/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.nui.preparation;

import java.util.Collection;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.vaadin.crudui.crud.CrudListener;
import org.vaadin.crudui.crud.impl.GridCrud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import app.owlcms.apputils.queryparameters.BaseContent;
import app.owlcms.data.platform.Platform;
import app.owlcms.data.platform.PlatformRepository;
import app.owlcms.i18n.Translator;
import app.owlcms.nui.crudui.OwlcmsComboBoxProvider;
import app.owlcms.nui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.nui.crudui.OwlcmsCrudGrid;
import app.owlcms.nui.crudui.OwlcmsGridLayout;
import app.owlcms.nui.lifting.TCContent;
import app.owlcms.nui.shared.OwlcmsContent;
import app.owlcms.nui.shared.OwlcmsLayout;
import app.owlcms.sound.Speakers;
import app.owlcms.utils.URLUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Class CategoryContent.
 *
 * Defines the toolbar and the table for editing data on categories.
 */
@SuppressWarnings("serial")
@Route(value = "preparation/platforms", layout = OwlcmsLayout.class)
public class PlatformContent extends BaseContent implements CrudListener<Platform>, OwlcmsContent {

	final static Logger logger = (Logger) LoggerFactory.getLogger(PlatformContent.class);
	static {
		logger.setLevel(Level.INFO);
	}
	private OwlcmsCrudFormFactory<Platform> editingFormFactory;
	private OwlcmsLayout routerLayout;

	/**
	 * Instantiates the Platform crudGrid.
	 */
	public PlatformContent() {
		OwlcmsCrudFormFactory<Platform> crudFormFactory = createFormFactory();
		GridCrud<Platform> crud = createGrid(crudFormFactory);
		// defineFilters(crudGrid);
		fillHW(crud, this);
	}

	@Override
	public Platform add(Platform domainObjectToAdd) {
		return this.editingFormFactory.add(domainObjectToAdd);
	}

	@Override
	public FlexLayout createMenuArea() {
		return new FlexLayout();
	}

	@Override
	public void delete(Platform domainObjectToDelete) {
		this.editingFormFactory.delete(domainObjectToDelete);
	}

	/**
	 * The refresh button on the toolbar
	 *
	 * @see org.vaadin.crudui.crud.CrudListener#findAll()
	 */
	@Override
	public Collection<Platform> findAll() {
		return PlatformRepository.findAll();
	}

	@Override
	public String getMenuTitle() {
		return Translator.translate("EditPlatforms");
	}

	/**
	 * @see com.vaadin.flow.router.HasDynamicTitle#getPageTitle()
	 */
	@Override
	public String getPageTitle() {
		return Translator.translate("Preparation_Platforms");
	}

	@Override
	public OwlcmsLayout getRouterLayout() {
		return this.routerLayout;
	}

	@Override
	public void setRouterLayout(OwlcmsLayout routerLayout) {
		this.routerLayout = routerLayout;
	}

	@Override
	public Platform update(Platform domainObjectToUpdate) {
		return this.editingFormFactory.update(domainObjectToUpdate);
	}

	/**
	 * The content and ordering of the editing form.
	 *
	 * @param crudFormFactory the factory that will create the form using this information
	 */
	protected void createFormLayout(OwlcmsCrudFormFactory<Platform> crudFormFactory) {
		crudFormFactory.setVisibleProperties("name", "soundMixerName");
		crudFormFactory.setFieldCaptions(Translator.translate("PlatformName"), Translator.translate("Speakers"));
		List<String> outputNames = Speakers.getOutputNames();
		outputNames.add(0, Translator.translate("UseBrowserSound"));
		crudFormFactory.setFieldProvider("soundMixerName", new OwlcmsComboBoxProvider<>(outputNames));
	}

	/**
	 * The columns of the crudGrid
	 *
	 * @param crudFormFactory what to call to create the form for editing an athlete
	 * @return
	 */
	protected GridCrud<Platform> createGrid(OwlcmsCrudFormFactory<Platform> crudFormFactory) {
		Grid<Platform> grid = new Grid<>(Platform.class, false);
		grid.getThemeNames().add("row-stripes");
		grid.addColumn(Platform::getName).setHeader(Translator.translate("Name"));
		grid.addColumn(Platform::getSoundMixerName).setHeader(Translator.translate("Speakers"));
		grid.addColumn(new ComponentRenderer<>(p -> {
			Button technical = openInNewTab(TCContent.class, Translator.translate("PlatesCollarBarbell"), p.getName());
			// prevent grid row selection from triggering
			technical.getElement().addEventListener("click", ignore -> {
			}).addEventData("event.stopPropagation()");
			return technical;
		})).setHeader(Translator.translate("PlatesCollarBarbell")).setWidth("0");

		GridCrud<Platform> crud = new OwlcmsCrudGrid<>(Platform.class, new OwlcmsGridLayout(Platform.class),
		        crudFormFactory, grid);
		crud.setCrudListener(this);
		crud.setClickRowToUpdate(true);
		return crud;
	}

	/**
	 * Define the form used to edit a given Platform.
	 *
	 * @return the form factory that will create the actual form on demand
	 */
	private OwlcmsCrudFormFactory<Platform> createFormFactory() {
		this.editingFormFactory = createPlatformEditingFactory();
		createFormLayout(this.editingFormFactory);
		return this.editingFormFactory;
	}

	/**
	 * Create the actual form generator with all the conversions and validations required
	 *
	 * {@link RegistrationContent#createAthleteEditingFormFactory} for example of redefinition of bindField
	 *
	 * @return the actual factory, with the additional mechanisms to do validation
	 */
	private OwlcmsCrudFormFactory<Platform> createPlatformEditingFactory() {
		return new PlatformEditingFormFactory(Platform.class);
	}

	private <T extends Component & HasUrlParameter<String>> String getWindowOpenerFromClass(Class<T> targetClass,
	        String parameter) {
		return "window.open('" + URLUtils.getUrlFromTargetClass(targetClass) + "?fop=" + parameter
		        + "','" + targetClass.getSimpleName() + "')";
	}

	private <T extends Component & HasUrlParameter<String>> Button openInNewTab(Class<T> targetClass,
	        String label, String parameter) {
		Button button = new Button(label);
		button.getElement().setAttribute("onClick", getWindowOpenerFromClass(targetClass, parameter));
		return button;
	}
}
