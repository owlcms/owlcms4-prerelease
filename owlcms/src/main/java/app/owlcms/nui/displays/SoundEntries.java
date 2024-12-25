/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.nui.displays;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import app.owlcms.apputils.queryparameters.DisplayParametersReader;
import app.owlcms.fieldofplay.FieldOfPlay;
import app.owlcms.i18n.Translator;
import app.owlcms.init.OwlcmsSession;

public interface SoundEntries {

	public default void addSoundEntries(VerticalLayout layout, Component audioComponent,
	        DisplayParametersReader parentPage) {
		NativeLabel label = new NativeLabel(Translator.translate("DisplayParameters.SoundSettings"));
		FieldOfPlay fop = OwlcmsSession.getFop();
		if (fop != null) {
			if (fop.isEmitSoundsOnServer()) {
				label = new NativeLabel(Translator.translate("DisplayParameters.SoundsOnServer"));
				label.setWidth("25em");
				layout.add(label);
				return;
			}
		}

		boolean silentMode = parentPage.isSilenced();
		Button silentButton = new Button(Translator.translate("DisplayParameters.ClockSoundOff"),
		        new Icon(VaadinIcon.BELL_SLASH));
		Button soundButton = new Button(Translator.translate("DisplayParameters.ClockSoundOn"),
		        new Icon(VaadinIcon.BELL));

		RadioButtonGroup<Boolean> rbgroup = new RadioButtonGroup<>();
		rbgroup.setRequired(true);
		rbgroup.setLabel(null);
		rbgroup.setItems(Boolean.TRUE, Boolean.FALSE);
		rbgroup.setValue(silentMode);
		rbgroup.setRenderer(new ComponentRenderer<>((mn) -> mn ? silentButton : soundButton));
		rbgroup.addValueChangeListener(e -> {
			Boolean silenced = e.getValue();
			parentPage.switchSoundMode(silenced, true);
			// if (!silenced) {
			// SoundUtils.doEnableAudioContext(audioComponent.getElement());
			// }
		});

		boolean downSilentMode = parentPage.isDownSilenced();
		Button downSilencedButton = new Button(Translator.translate("DisplayParameters.DownSoundOff"),
		        new Icon(VaadinIcon.BELL_SLASH));
		Button downSoundButton = new Button(Translator.translate("DisplayParameters.DownSoundOn"),
		        new Icon(VaadinIcon.BELL));

		RadioButtonGroup<Boolean> rb2group = new RadioButtonGroup<>();
		rb2group.setRequired(true);
		rb2group.setLabel(null);
		rb2group.setItems(Boolean.TRUE, Boolean.FALSE);
		rb2group.setValue(downSilentMode);
		rb2group.setRenderer(new ComponentRenderer<>((mn) -> mn ? downSilencedButton : downSoundButton));
		rb2group.addValueChangeListener(e -> {
			Boolean downSilenced = e.getValue();
			parentPage.switchDownMode(downSilenced, true);
			// if (!downSilenced) {
			// SoundUtils.doEnableAudioContext(audioComponent.getElement());
			// }
		});
		rb2group.setHelperText(Translator.translate("DisplayParameters.SoundHelper"));

		layout.add(label);
		layout.add(rbgroup, rb2group);
	}

}
