/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.nui.lifting;

import java.util.List;

import app.owlcms.data.athlete.Athlete;

public interface NextAthleteAble {

	List<Athlete> getAthletes();

	Athlete getNextAthlete(Athlete current);

	Athlete getPreviousAthlete(Athlete current);

}
