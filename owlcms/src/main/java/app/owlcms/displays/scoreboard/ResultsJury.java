/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.displays.scoreboard;

import app.owlcms.nui.displays.scoreboards.WarmupNoLeadersPage;

@SuppressWarnings("serial")
public class ResultsJury extends ResultsNoLeaders {

	public ResultsJury(WarmupNoLeadersPage page) {
		super(page);
	}

	@Override
	public boolean isJury() {
		return true;
	}

}
