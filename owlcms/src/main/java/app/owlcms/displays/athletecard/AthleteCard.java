/*******************************************************************************
 * Copyright © 2009-present Jean-Fran�ois Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.displays.athletecard;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import app.owlcms.apputils.queryparameters.FOPParametersReader;
import app.owlcms.data.agegroup.AgeGroup;
import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.category.Category;
import app.owlcms.data.group.Group;
import app.owlcms.fieldofplay.FieldOfPlay;
import app.owlcms.i18n.Translator;
import app.owlcms.init.OwlcmsFactory;
import app.owlcms.nui.shared.RequireLogin;
import app.owlcms.nui.shared.SafeEventBusRegistration;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * Attempt board.
 */
@SuppressWarnings({ "serial", "deprecation" })
@Tag("athlete-card-template")
@JsModule("./components/AthleteCard.js")
@CssImport(value = "./styles/shared-styles.css")
@Route("weighin/AthleteCard")

public class AthleteCard extends LitTemplate
        implements FOPParametersReader, SafeEventBusRegistration, HasDynamicTitle, RequireLogin {

	final private static Logger logger = (Logger) LoggerFactory.getLogger(AthleteCard.class);
	final private static Logger uiEventLogger = (Logger) LoggerFactory.getLogger("UI" + logger.getName());

	static {
		logger.setLevel(Level.INFO);
		uiEventLogger.setLevel(Level.INFO);
	}
	private Athlete athlete;
	private Location location;
	private UI locationUI;
	Map<String, List<String>> urlParameterMap = new HashMap<>();
	private FieldOfPlay fop;
	private Group group;
	private QueryParameters defaultParameters;
	private String routeParameter;

	/**
	 * Instantiates a new attempt board.
	 */
	public AthleteCard() {
		OwlcmsFactory.waitDBInitialized();
	}

	@Override
	public QueryParameters getDefaultParameters() {
		return this.defaultParameters;
	}

	@Override
	final public FieldOfPlay getFop() {
		return this.fop;
	}

	@Override
	final public Group getGroup() {
		return this.group;
	}

	@Override
	final public Location getLocation() {
		return this.location;
	}

	@Override
	final public UI getLocationUI() {
		return this.locationUI;
	}

	@Override
	public String getPageTitle() {
		return Translator.translate("AthleteCard");
	}

	@Override
	public String getRouteParameter() {
		return this.routeParameter;
	}

	@Override
	final public Map<String, List<String>> getUrlParameterMap() {
		return this.urlParameterMap;
	}

	@Override
	public boolean isShowInitialDialog() {
		return false;
	}

	@Override
	public void setDefaultParameters(QueryParameters qp) {
		this.defaultParameters = qp;
	}

	@Override
	final public void setFop(FieldOfPlay fop) {
		this.fop = fop;
	}

	@Override
	final public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	final public void setLocation(Location location) {
		this.location = location;

	}

	@Override
	final public void setLocationUI(UI locationUI) {
		this.locationUI = locationUI;
	}

	@Override
	public void setRouteParameter(String routeParameter) {
		this.routeParameter = routeParameter;
	}

	@Override
	final public void setUrlParameterMap(Map<String, List<String>> parametersMap) {
		this.urlParameterMap = removeDefaultValues(parametersMap);
	}

	public int zeroIfInvalid(String v) {
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/*
	 * @see com.vaadin.flow.component.Component#onAttach(com.vaadin.flow.component. AttachEvent)
	 */
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		init();

		getElement().setProperty("fullName", this.athlete.getFullName());
		getElement().setProperty("team", this.athlete.getTeam());
		getElement().setProperty("bodyWeight", String.format("%.2f", this.athlete.getBodyWeight()));
		AgeGroup ageGroup = this.athlete.getAgeGroup();
		getElement().setProperty("ageGroup", ageGroup != null ? ageGroup.getName() : "");
		getElement().setProperty("ageDivision",
		        ageGroup != null ? ageGroup.getChampionship().translate() : "");
		Integer yearOfBirth = this.athlete.getYearOfBirth();
		if (yearOfBirth != null && yearOfBirth > 1900) {
			getElement().setProperty("birth", yearOfBirth.toString());
		} else {
			getElement().setProperty("birth", "");
		}
		Integer lotNumber = this.athlete.getLotNumber();
		if (lotNumber != null && lotNumber > 0) {
			getElement().setProperty("lotNumber", lotNumber.toString());
		} else {
			getElement().setProperty("lotNumber", "");
		}
		Integer startNumber = this.athlete.getStartNumber();
		if (startNumber != null && startNumber > 0) {
			getElement().setProperty("startNumber", startNumber.toString());
		} else {
			getElement().setProperty("startNumber", "");
		}
		Group group = this.athlete.getGroup();
		if (group != null && group != null) {
			getElement().setProperty("group", group.getName());
		} else {
			getElement().setProperty("group", "");
		}
		Category category = this.athlete.getCategory();
		if (category != null) {
			getElement().setProperty("category", category.getNameWithAgeGroup());
		} else {
			getElement().setProperty("category", "");
		}
		String snatch1Declaration = this.athlete.getSnatch1Declaration();
		if (snatch1Declaration != null && zeroIfInvalid(snatch1Declaration) > 0) {
			getElement().setProperty("snatch1Declaration", snatch1Declaration);
		} else {
			getElement().setProperty("snatch1Declaration", "");
		}
		String cleanJerk1Declaration = this.athlete.getCleanJerk1Declaration();
		if (cleanJerk1Declaration != null && zeroIfInvalid(cleanJerk1Declaration) > 0) {
			getElement().setProperty("cleanJerk1Declaration", cleanJerk1Declaration);
		} else {
			getElement().setProperty("cleanJerk1Declaration", "");
		}
		Integer entryTotal = this.athlete.getEntryTotal();
		if (entryTotal != null && entryTotal > 0) {
			getElement().setProperty("entryTotal", entryTotal.toString());
		} else {
			getElement().setProperty("entryTotal", "");
		}
	}

	protected void setTranslationMap() {
		JsonObject translations = Json.createObject();
		Enumeration<String> keys = Translator.getKeys();
		while (keys.hasMoreElements()) {
			String curKey = keys.nextElement();
			if (curKey.startsWith("Card.")) {
				translations.put(curKey.replace("Card.", ""), Translator.translate(curKey));
			}
		}
		this.getElement().setPropertyJson("t", translations);
	}

	private void init() {
		getElement().executeJs("document.querySelector('html').setAttribute('theme', 'light');");
		setTranslationMap();

		Button button = new Button(Translator.translate("Print"));
		button.setThemeName("primary success");
		button.getElement().setAttribute("onClick", "window.print()");
		HorizontalLayout banner = new HorizontalLayout(button);
		banner.setJustifyContentMode(JustifyContentMode.END);
		banner.setPadding(true);
		banner.setClassName("printing");
		getElement().getParent().appendChild(banner.getElement());
	}

}
