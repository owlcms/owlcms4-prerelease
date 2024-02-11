package app.owlcms.spreadsheet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.LoggerFactory;

import app.owlcms.components.GroupCategorySelectionMenu.TriConsumer;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
import app.owlcms.data.category.Category;
import app.owlcms.data.category.Participation;
import app.owlcms.data.competition.Competition;
import app.owlcms.data.group.Group;
import app.owlcms.data.group.GroupRepository;
import app.owlcms.data.jpa.JPAService;
import app.owlcms.data.platform.Platform;
import app.owlcms.data.platform.PlatformRepository;
import app.owlcms.i18n.Translator;
import app.owlcms.init.OwlcmsFactory;
import app.owlcms.utils.LoggerUtils;
import app.owlcms.utils.ResourceWalker;
import ch.qos.logback.classic.Logger;
import net.sf.jxls.reader.ReaderConfig;

public class NRegistrationFileProcessor implements IRegistrationFileProcessor {

	record AthleteInput(List<RAthlete> athletes) {
	};

	/* some setters must be called in a specific order; */
	private enum DelayedSetter {
		BIRTHDATE, BODYWEIGHT, QUALIFYING_TOTAL, GENDER, CATEGORY
	}
	static final String GROUPS_READER_SPEC = "/templates/registration/GroupsReader.xml";
	static final String REGISTRATION_READER_SPEC = "/templates/registration/RegistrationReader.xml";
	Integer[] delayedSetterColumns = new Integer[DelayedSetter.values().length];
	Logger logger = (Logger) LoggerFactory.getLogger(NRegistrationFileProcessor.class);
	public boolean keepParticipations;
	@SuppressWarnings("unchecked")
	TriConsumer<RAthlete, String, Cell>[] setterForColumn = new TriConsumer[25];
	FormulaEvaluator formulaEvaluator;
	DataFormatter formatter;

	private boolean createMissingGroups = true;

	public NRegistrationFileProcessor() {
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#adjustParticipations()
	 */
	@Override
	public void adjustParticipations() {
		if (!keepParticipations) {
			AthleteRepository.resetParticipations();
		}
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#cleanMessage(java.lang.String)
	 */
	@Override
	public String cleanMessage(String localizedMessage) {
		localizedMessage = localizedMessage.replace("Can't read cell ", "");
		String cell = localizedMessage.substring(0, localizedMessage.indexOf(" "));
		String ss = "spreadsheet";
		int ix = localizedMessage.indexOf(ss) + ss.length();
		localizedMessage = localizedMessage.substring(ix);
		if (localizedMessage.trim().contentEquals("text")) {
			localizedMessage = "Empty or invalid.";
		}
		String cleanMessage = Translator.translate("Cell") + " " + cell + ": " + localizedMessage;
		return cleanMessage;
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#doProcessAthletes(java.io.InputStream, boolean,
	 *      java.util.function.Consumer, java.lang.Runnable)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int doProcessAthletes(InputStream inputStream, boolean dryRun, Consumer<String> errorConsumer,
	        Runnable displayUpdater) {

		try (InputStream xlsInputStream = inputStream) {
			inputStream.reset();
			RCompetition c = new RCompetition();
			RCompetition.resetActiveCategories();
			RCompetition.resetActiveGroups();
			RCompetition.resetAthleteToEligibles();
			RCompetition.resetAthleteToTeams();

			List<RAthlete> athletes = new ArrayList<>();
			AthleteInput athleteInput;
			try (Workbook workbook = WorkbookFactory.create(xlsInputStream)) {
				formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				formatter = new DataFormatter();
				athleteInput = readAthletes(workbook, c, errorConsumer);
			} catch (IOException | EncryptedDocumentException e) {
				errorConsumer.accept(e.getLocalizedMessage());
				LoggerUtils.logError(logger, e);
				return 0;
			}

			// get back the updated athletes
			athletes = athleteInput.athletes;
			// if exact matches were found for categories, the processing for eligibility
			// has been done, and we keep the eligibilities exactly as in the file.
			keepParticipations = athletes.stream()
			        .filter(r -> r.getAthlete().getEligibleCategories() != null).findFirst()
			        .isPresent();

			logger.info(Translator.translate("DataRead") + " " + athletes.size() + " athletes");
			if (dryRun) {
				return athletes.size();
			}

			if (athletes.size() > 0) {
				updateAthletes(errorConsumer, c, athletes);
				appendErrors(displayUpdater, errorConsumer);
			} else {
				errorConsumer.accept(Translator.translate("NoAthletes"));
				displayUpdater.run();
			}
			return athletes.size();
		} catch (IOException e) {
			LoggerUtils.stackTrace(e);
			LoggerUtils.logError(logger, e);
		}
		return 0;
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#doProcessGroups(java.io.InputStream, boolean,
	 *      java.util.function.Consumer, java.lang.Runnable)
	 */
	@Override
	public int doProcessGroups(InputStream inputStream, boolean dryRun, Consumer<String> errorConsumer,
	        Runnable displayUpdater) {
		try (InputStream xmlInputStream = ResourceWalker.getResourceAsStream(GROUPS_READER_SPEC)) {
			inputStream.reset();
			ReaderConfig readerConfig = ReaderConfig.getInstance();
			readerConfig.setUseDefaultValuesForPrimitiveTypes(true);
			readerConfig.setSkipErrors(true);

			try (InputStream xlsInputStream = inputStream) {
				List<RGroup> groups = new ArrayList<>();

				Map<String, Object> beans = new HashMap<>();
				beans.put("groups", groups);

				// logger.info(getTranslation("ReadingData_"));
				logger.info("Read {} groups.", groups.size());
				if (!dryRun) {
					updatePlatformsAndGroups(groups);
				}

				// FIXME real errors
				appendErrors(displayUpdater, errorConsumer);
				return groups.size();
			} catch (IOException e) {
				LoggerUtils.logError(logger, e);
			}
		} catch (IOException e1) {
			LoggerUtils.logError(logger, e1);
		}
		return 0;
	}

	public boolean isCreateMissingGroups() {
		return createMissingGroups;
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#resetAthletes()
	 */
	@Override
	public void resetAthletes() {
		// delete all athletes and groups (naive version).
		JPAService.runInTransaction(em -> {
			List<Athlete> athletes = AthleteRepository.doFindAll(em);
			for (Athlete a : athletes) {
				em.remove(a);
			}
			em.flush();
			return null;
		});
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#resetGroups()
	 */
	@Override
	public void resetGroups() {
		// delete all athletes and groups (naive version).
		JPAService.runInTransaction(em -> {
			List<Group> oldGroups = GroupRepository.doFindAll(em);
			for (Group g : oldGroups) {
				em.remove(g);
			}
			em.flush();
			return null;
		});
	}

	public void setCreateMissingGroups(boolean createMissingGroups) {
		this.createMissingGroups = createMissingGroups;
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#updateAthletes(java.util.function.Consumer,
	 *      app.owlcms.spreadsheet.RCompetition, java.util.List)
	 */
	@Override
	public void updateAthletes(Consumer<String> errorConsumer, RCompetition c, List<RAthlete> athletes) {
		JPAService.runInTransaction(em -> {
			// Competition curC = Competition.getCurrent();
			try {

				// updateCompetitionInfo(c, em, curC);

				// Create the new athletes.
				athletes.stream().forEach(r -> {
					Athlete athlete = r.getAthlete();
					//logger.debug("merging {}", athlete.getShortName());
					em.merge(athlete);
				});
				em.flush();
			} catch (Exception e) {
				LoggerUtils.stackTrace(e);
				errorConsumer.accept(e.toString());
			}

			return null;
		});

		JPAService.runInTransaction(em -> {
			AthleteRepository.findAll().stream().forEach(a2 -> {
				LinkedHashSet<Category> eligibles = (LinkedHashSet<Category>) RCompetition
				        .getAthleteToEligibles()
				        .get(a2.getId());
				LinkedHashSet<Category> teams = (LinkedHashSet<Category>) RCompetition
				        .getAthleteToTeams()
				        .get(a2.getId());
				if (eligibles != null) {
					Category first = eligibles.stream().findFirst().orElse(null);
					a2.setCategory(first);
					// logger.debug("setting eligibility {} {}", a2.getShortName(), eligibles);
					a2.setEligibleCategories(eligibles);
					List<Participation> participations2 = a2.getParticipations();
					for (Participation p : participations2) {
						if (teams.contains(p.getCategory())) {
							p.setTeamMember(true);
						} else {
							logger.info("Excluding {} as team member for {}", a2.getShortName(),
							        p.getCategory().getComputedCode());
							p.setTeamMember(false);
						}
					}
					// logger.debug("participations {} {}", a2.getShortName(), a2.getParticipations());
					em.merge(a2);
				}
			});
			em.flush();
			return null;
		});
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#updatePlatformsAndGroups(java.util.List)
	 */
	@Override
	public void updatePlatformsAndGroups(List<RGroup> groups) {
		Set<String> futurePlatforms = groups.stream().map(RGroup::getPlatform).filter(p -> (p != null && !p.isBlank()))
		        .collect(Collectors.toSet());

		String defaultPlatformName = OwlcmsFactory.getDefaultFOP().getName();
		if (futurePlatforms.isEmpty()) {
			// keep the current default if no group is linked to a platform.
			futurePlatforms.add(defaultPlatformName);
		}
		logger.debug("to be kept if present: {}", futurePlatforms);

		PlatformRepository.deleteUnusedPlatforms(futurePlatforms);
		PlatformRepository.createMissingPlatforms(groups);

		// recompute the available platforms, unregister the existing FOPs, etc.
		OwlcmsFactory.initDefaultFOP();
		String newDefault = OwlcmsFactory.getDefaultFOP().getName();

		JPAService.runInTransaction(em -> {
			groups.stream().forEach(g -> {
				String platformName = g.getPlatform();
				Group group = g.getGroup();
				if (platformName == null || platformName.isBlank()) {
					platformName = newDefault;
				}
				logger.info("setting platform '{}' for group {}", platformName, g.getGroupName());
				Platform op = PlatformRepository.findByName(platformName);
				group.setPlatform(op);
				em.merge(group);
			});
			em.flush();
			return null;
		});

		groups.stream().forEach(g -> {
			logger.debug("group {} weighIn {} competition {}", g.getGroup(), g.getWeighinTime(),
			        g.getCompetitionTime());
		});
	}

	/**
	 * @see app.owlcms.spreadsheet.IRegistrationFileProcessor#appendErrors(java.lang.Runnable,
	 *      java.util.function.Consumer, net.sf.jxls.reader.XLSReadStatus)
	 */
	private void appendErrors(Runnable displayUpdater, Consumer<String> errorAppender) {
		displayUpdater.run();
	}

	private String cellToString(Cell cell) {
		switch (cell.getCellType()) {
			case FORMULA:
				return formatter.formatCellValue(cell, formulaEvaluator);
			default:
				return formatter.formatCellValue(cell);
		}
	}

	private void processException(RAthlete a, String s, Cell c, Exception e, Consumer<String> errorConsumer) {
		errorConsumer.accept(c.getAddress() + " " + e.getLocalizedMessage() + System.lineSeparator());
		// logger.error("{} {}", c.getAddress(), e.toString());
		// if (e instanceof InvocationTargetException) {
		LoggerUtils.logError(logger, e);
		// }
	}

	private AthleteInput readAthletes(Workbook workbook, RCompetition rComp, Consumer<String> errorConsumer) {
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();
		List<RAthlete> athletes = new LinkedList<>();
		int iRow = 0;

		rows: while (rowIterator.hasNext()) {
			int iColumn = 0;
			Row row = rowIterator.next();
			if (iRow == 0) {
				//logger.debug("creating header");
				// header, create map.
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = cell.getStringCellValue();
					String trim = cellValue.trim();
					//logger.trace("cell {} {} {} {}", iColumn, iRow, cell.getAddress(), trim);

					if (trim.contentEquals(Translator.translate("Membership"))) {
						setterForColumn[iColumn] = (a, s, c) -> {
							a.setMembership(s);
						};
					} else if (trim.contentEquals(Translator.translate("Card.lotNumber"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setLotNumber(s);
						});
					} else if (trim.contentEquals(Translator.translate("LastName"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setLastName(s);
						});
					} else if (trim.contentEquals(Translator.translate("FirstName"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setFirstName(s);
						});
					} else if (trim.contentEquals(Translator.translate("Scoreboard.Team"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setTeam(s);
						});
					} else if (trim.contentEquals(Translator.translate("Registration.birth"))) {
						delayedSetterColumns[DelayedSetter.BIRTHDATE.ordinal()] = iColumn;
						setterForColumn[iColumn] = ((a, s, c) -> {
							try {
								a.setFullBirthDate(s);
							} catch (Exception e) {
								processException(a, s, c, e, errorConsumer);
							}
						});
					} else if (trim.contentEquals("M/F")) {
						delayedSetterColumns[DelayedSetter.GENDER.ordinal()] = iColumn;
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setGender(s);
						});
					} else if (trim.contentEquals(Translator.translate("Card.category"))) {
						delayedSetterColumns[DelayedSetter.CATEGORY.ordinal()] = iColumn;
						setterForColumn[iColumn] = ((a, s, c) -> {
							try {
								a.setCategory(s);
							} catch (Exception e) {
								processException(a, s, c, e, errorConsumer);
							}
						});
					} else if (trim.contentEquals(Translator.translate("Scoreboard.BodyWeight"))) {
						delayedSetterColumns[DelayedSetter.BODYWEIGHT.ordinal()] = iColumn;
						setterForColumn[iColumn] = ((a, s, c) -> {
							try {
								if (s == null || s.isBlank()) {
									return;
								}
								double d = Double.parseDouble(s);
								a.setBodyWeight(d);
							} catch (Exception e) {
								processException(a, s, c, e, errorConsumer);
							}
						});
					} else if (trim.contentEquals(Translator.translate("Results.Snatch") + " "
					        + Translator.translate("Results.Declaration_abbrev"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setSnatch1Declaration(s);
						});
					} else if (trim.contentEquals(Translator.translate("Results.CJ_abbrev") + " "
					        + Translator.translate("Results.Declaration_abbrev"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setCleanJerk1Declaration(s);
						});
					} else if (trim.contentEquals(Translator.translate("Group"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							try {
								a.setGroup(s);
							} catch (Exception e) {
								if (isCreateMissingGroups()) {
									Group g = GroupRepository.add(new Group(s));
									rComp.addGroup(g);
									try {
										a.setGroup(s);
									} catch (Exception e1) {
										processException(a, s, c, e, errorConsumer);
									}
								} else {
									processException(a, s, c, e, errorConsumer);
								}
							}
						});
					} else if (trim.contentEquals(Translator.translate("Card.entryTotal"))) {
						delayedSetterColumns[DelayedSetter.QUALIFYING_TOTAL.ordinal()] = iColumn;
						setterForColumn[iColumn] = ((a, s, c) -> {
							try {
								//logger.debug("qualifying total {}", s);
								int i = Integer.parseInt(s);
								a.setQualifyingTotal(i);
							} catch (Exception e) {
								processException(a, s, c, e, errorConsumer);
							}
						});
					} else if (trim.contentEquals(Translator.translate("Coach"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setCoach(s);
						});
					} else if (trim.contentEquals(Translator.translate("Custom1.Title"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setCustom1(s);
						});
					} else if (trim.contentEquals(Translator.translate("Custom2.Title"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setCustom2(s);
						});
					} else if (trim.contentEquals(Translator.translate("Registration.FederationCodesShort"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setFederationCodes(s);
						});
					} else if (trim.contentEquals(Translator.translate("PersonalBestSnatch"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setPersonalBestSnatch(s);
						});
					} else if (trim.contentEquals(Translator.translate("PersonalBestCleanJerk"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setPersonalBestCleanJerk(s);
						});
					} else if (trim.contentEquals(Translator.translate("PersonalBestTotal"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setPersonalBestTotal(s);
						});
					} else if (trim.contentEquals(Translator.translate("SubCategory"))) {
						setterForColumn[iColumn] = ((a, s, c) -> {
							a.setSubCategory(s);
						});
					} else {
						errorConsumer
						        .accept(Translator.translate("Registration.UnknownColumnHeader", trim) + " " + trim);
					}
					iColumn++;
				}
			} else {
				RAthlete ra = new RAthlete();
				Iterator<Cell> cellIterator = row.cellIterator();

				// first pass, memorize values for setters that need to be called in a specific order
				// call the other setters.
				String[] delayedSetterValues = new String[DelayedSetter.values().length];
				Cell[] delayedSetterCells = new Cell[DelayedSetter.values().length];
				iColumn = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = cellToString(cell);
					if (iColumn == 0) {
						String trim = cellValue.trim();
						if (trim.isBlank()) {
							break rows;
						}
					}
					int delayedOrder = ArrayUtils.indexOf(delayedSetterColumns, iColumn);
					if (delayedOrder < 0) {
						setterForColumn[iColumn].accept(ra, cellValue.trim(), cell);
					} else {
						delayedSetterValues[delayedOrder] = cellValue.trim();
						delayedSetterCells[delayedOrder] = cell;
					}
					iColumn++;
				}

				// second pass, call the delayed setters in the correct order.
				for (int delayedOrder = 0; delayedOrder < DelayedSetter.values().length; delayedOrder++) {
					Integer setterColumn = delayedSetterColumns[delayedOrder];
					setterForColumn[setterColumn].accept(ra, delayedSetterValues[delayedOrder],
					        delayedSetterCells[delayedOrder]);
				}
				athletes.add(ra);
			}

			iRow++;
		}
		return new AthleteInput(athletes);
	}

	@SuppressWarnings("unused")
	private void updateCompetitionInfo(RCompetition c, EntityManager em, Competition curC)
	        throws IllegalAccessException, InvocationTargetException {
		Competition rCompetition = c.getCompetition();
		// save some properties from current database that do not appear on spreadheet
		rCompetition.setEnforce20kgRule(curC.isEnforce20kgRule());
		rCompetition.setUseBirthYear(curC.isUseBirthYear());
		rCompetition.setMasters(curC.isMasters());

		// update the current competition with the new properties read from spreadsheet
		BeanUtils.copyProperties(curC, rCompetition);
		// update in database and set current to result of JPA merging.
		Competition.setCurrent(em.merge(curC));
	}
}