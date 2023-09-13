/*******************************************************************************
 * Copyright (c) 2009-2023 Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.displays.attemptboard;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

import app.owlcms.init.OwlcmsSession;
import app.owlcms.nui.displayselection.AthleteFacingAttemptBoardPage;
import app.owlcms.nui.displayselection.AttemptBoardPage;
import app.owlcms.nui.displayselection.SoundEntries;

@SuppressWarnings({ "serial", "deprecation" })
@Tag("attempt-board-template")
@JsModule("./components/AttemptBoard.js")
@JsModule("./components/AudioContext.js")
@JsModule("./components/TimerElement.js")
@JsModule("./components/DecisionElement.js")
@CssImport(value = "./styles/shared-styles.css")
@CssImport(value = "./styles/plates.css")

public class AthleteFacingAttemptBoard extends AttemptBoard implements SoundEntries {

	private AttemptBoardPage wrapper;

	public AthleteFacingAttemptBoard(AthleteFacingAttemptBoardPage athleteFacingAttemptBoardWrapper) {
		super();
		setPublicFacing(false);
		this.wrapper = athleteFacingAttemptBoardWrapper;
	}

	@Override
	public String getPageTitle() {
		return getTranslation("AttemptAF") + OwlcmsSession.getFopNameIfMultiple();
	}

	@Override
	public AttemptBoardPage getWrapper() {
		return wrapper;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see app.owlcms.displays.attemptboard.AttemptBoard#onAttach(com.vaadin.flow. component.AttachEvent)
	 */
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		decisions.setPublicFacing(false);
	}

}
