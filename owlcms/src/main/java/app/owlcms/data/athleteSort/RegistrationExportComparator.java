/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.data.athleteSort;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.competition.Competition;

/**
 * This comparator is used for the technical meeting sheet. It is based on the registration category
 *
 * @author jflamy
 *
 */
public class RegistrationExportComparator extends AbstractLifterComparator implements Comparator<Athlete> {

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Athlete lifter1, Athlete lifter2) {
		int compare = 0;

		compare = compareAgeGroup(lifter1, lifter2);
		if (compare != 0) {
			return Competition.getCurrent().isMasters() ? -compare : compare;
		}

		// takes into account platform and group name so that groups are not mixed
		// together
		compare = compareGroup(lifter1, lifter2);
		if (compare != 0) {
			return compare;
		}

		compare = ObjectUtils.compare(lifter1.getCategory(), lifter2.getCategory(), true); // null weighed after
		if (compare != 0) {
			return compare;
		}

		compare = compareEntryTotal(lifter1, lifter2);
		if (compare != 0) {
			return compare;
		}

		compare = compareLotNumber(lifter1, lifter2);
		if (compare != 0) {
			return compare;
		}

		compare = compareLastName(lifter1, lifter2);
		if (compare != 0) {
			return compare;
		}

		compare = compareFirstName(lifter1, lifter2);
		if (compare != 0) {
			return compare;
		}

		return compare;
	}

}
