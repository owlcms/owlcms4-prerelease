/*******************************************************************************
 * Copyright © 2009-present Jean-Fran�ois Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.components.elements;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;

import app.owlcms.nui.shared.SafeEventBusRegistration;
import ch.qos.logback.classic.Logger;

/**
 * ExplicitDecision display element.
 */
@SuppressWarnings({ "serial", "deprecation" })
@Tag("beep-element")
@JsModule("./components/BeepElement.js")
public class BeepElement extends LitTemplate
        implements SafeEventBusRegistration {

	public void beep() {
		Logger logger = (Logger) LoggerFactory.getLogger(BeepElement.class);
		logger.debug("calling beep");
		getElement().setProperty("doBeep", true);
	}

	public void reset() {
		Logger logger = (Logger) LoggerFactory.getLogger(BeepElement.class);
		logger.debug("calling reset");
		getElement().setProperty("doBeep", false);
	}

	/*
	 * @see com.vaadin.flow.component.Component#onAttach(com.vaadin.flow.component. AttachEvent)
	 */
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
	}
}
