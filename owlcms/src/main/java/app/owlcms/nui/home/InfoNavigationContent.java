/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.nui.home;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.github.appreciated.css.grid.GridLayoutComponent.AutoFlow;
import com.github.appreciated.css.grid.GridLayoutComponent.Overflow;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import app.owlcms.apputils.DebugUtils;
import app.owlcms.i18n.Translator;
import app.owlcms.init.OwlcmsFactory;
import app.owlcms.init.OwlcmsSession;
import app.owlcms.nui.shared.BaseNavigationContent;
import app.owlcms.nui.shared.NavigationPage;
import app.owlcms.nui.shared.OwlcmsLayout;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The Class HomeNavigationContent.
 */
/**
 * @author owlcms
 *
 */
@SuppressWarnings("serial")
@Route(value = "info", layout = OwlcmsLayout.class)
public class InfoNavigationContent extends BaseNavigationContent implements NavigationPage, HasDynamicTitle {

	final private static Logger logger = (Logger) LoggerFactory.getLogger(InfoNavigationContent.class);
	static {
		logger.setLevel(Level.INFO);
	}

	/**
	 * Navigation crudGrid.
	 *
	 * @param items the items
	 * @return the flexible crudGrid layout
	 */
	public static FlexibleGridLayout navigationGrid(Component... items) {
		FlexibleGridLayout layout = new FlexibleGridLayout();
		layout.withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("300px"), new Flex(1)))
		        .withAutoRows(new Length("1fr")).withItems(items).withGap(new Length("2vmin"))
		        .withOverflow(Overflow.AUTO).withAutoFlow(AutoFlow.ROW).withMargin(false).withPadding(true)
		        .withSpacing(false);
		layout.setSizeUndefined();
		layout.setWidth("80%");
		layout.setBoxSizing(BoxSizing.BORDER_BOX);
		return layout;
	}

	Map<String, List<String>> urlParameterMap = new HashMap<>();

	/**
	 * Instantiates a new main navigation content.
	 */
	public InfoNavigationContent() {
		VerticalLayout license = buildLicense();
		fillH(license, this);

		DebugUtils.gc();
	}

	/**
	 * @see com.vaadin.flow.router.HasDynamicTitle#getPageTitle()
	 */
	@Override
	public String getMenuTitle() {
		return Translator.translate("OWLCMS_Info");
	}

	/**
	 * @see com.vaadin.flow.router.HasDynamicTitle#getPageTitle()
	 */
	@Override
	public String getPageTitle() {
		return Translator.translate("ShortTitle.Info");
	}

	/**
	 * @see app.owlcms.apputils.queryparameters.FOPParameters#isIgnoreFopFromURL()
	 */
	@Override
	public boolean isIgnoreFopFromURL() {
		return true;
	}

	/**
	 * @see app.owlcms.nui.shared.BaseNavigationContent#createMenuBarFopField(java.lang.String, java.lang.String)
	 */
	@Override
	protected HorizontalLayout createMenuBarFopField(String label, String placeHolder) {
		return null;
	}

	private VerticalLayout buildLicense() {
		VerticalLayout license = new VerticalLayout();
		license.add(
		        sectionTitle(
		                Translator.translate("OwlcmsBuild", OwlcmsFactory.getVersion(),
		                        OwlcmsFactory.getBuildTimestamp())));
		license.add(sectionTitle(Translator.translate("CopyrightLicense")));
		addUL(license,
		        Translator.translate("Copyright2009") + LocalDate.now().getYear() + " " + Translator.translate("JFL"),
		        Translator.translate("LicenseUsed"));

		license.add(sectionTitle(Translator.translate("About.Community_Title")));
		addUL(license, Translator.translate("About.Community"));

		license.add(sectionTitle(Translator.translate("About.Bugs_Title")));
		addUL(license, Translator.translate("About.Bugs"));

		license.add(sectionTitle(Translator.translate("SourceDocumentation")));
		addUL(license,
		        Translator.translate("ProjectRepository"),
		        Translator.translate("Documentation"));

		license.add(sectionTitle(Translator.translate("Notes")));
		addUL(license, Translator.translate("TCRRCompliance") + Translator.translate("AtTimeOfRelease"),
		        Translator.translate("UseAtYourOwnRisk"));

		license.add(sectionTitle(Translator.translate("Credits")));
		addUL(license, Translator.translate("WrittenJFL"), Translator.translate("ThanksToAll"));

		license.add(sectionTitle(Translator.translate("Translation")));
		addUL(license,
		        Translator.translate("ThanksToTranslators") + translators(),
		        Translator.translate("TranslationDocumentation"));

		return license;
	}

	private Component sectionTitle(String translation) {
		H3 h3 = new H3(translation);
		h3.getStyle().set("margin-bottom", "-0.5em");
		h3.getStyle().set("margin-top", "0.5em");
		return h3;
	}

	private String translators() {
		Map<String, List<Locale>> translatorToLocales = new HashMap<>();
		for (Locale l : Translator.getAllAvailableLocales()) {
			String translator = Translator.translateNoInheritanceOrElseNull("Translator", l);
			if (translator != null) {
				List<Locale> list = translatorToLocales.get(translator);
				if (list == null) {
					list = new ArrayList<>();
					list.add(l);
					translatorToLocales.put(translator, list);
				} else {
					list.add(l);
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Entry<String, List<Locale>> entry : translatorToLocales.entrySet()) {
			if (!(sb.length() == 0)) {
				sb.append(", ");
			}
			sb.append(entry.getKey());
			sb.append(" (");
			sb.append(entry.getValue().stream().map(l -> l.getDisplayName(OwlcmsSession.getLocale()))
			        .collect(Collectors.joining(", ")));
			sb.append(")");
		}
		return sb.toString();
	}
}