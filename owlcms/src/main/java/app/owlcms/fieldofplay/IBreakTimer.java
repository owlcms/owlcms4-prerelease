/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.fieldofplay;

import java.time.LocalDateTime;

public interface IBreakTimer extends IProxyTimer {

	Integer getBreakDuration();

	@Override
	boolean isIndefinite();

	void setBreakDuration(Integer breakDuration);

	void setEnd(LocalDateTime targetTime);

	void setIndefinite();

	void setOrigin(Object origin);

	@Override
	void setTimeRemaining(int intValue, boolean indefinite);
}