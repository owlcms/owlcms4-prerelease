/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.nui.shared;

import org.slf4j.LoggerFactory;
import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.grid.Grid;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.init.OwlcmsSession;
import app.owlcms.nui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.nui.crudui.OwlcmsCrudGrid;
import app.owlcms.nui.crudui.OwlcmsGridLayout;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * This class attempts to locate the selected athlete from the in the lifting order.
 *
 * <p>
 * This is a workaround for two issues:
 * <li>Why does getValue() return a different object than that in the lifting order (initialization issue?)
 * <li>Why do we have to get the same object anyway (spurious comparison with == instead of getId() or .equals)
 * </p>
 *
 * @author Jean-François Lamy
 */
@SuppressWarnings("serial")
public class AthleteCrudGrid extends OwlcmsCrudGrid<Athlete> {

	Athlete match = null;
	final private Logger logger = (Logger) LoggerFactory.getLogger(AthleteCrudGrid.class);

	{
		this.logger.setLevel(Level.INFO);
	}

	public AthleteCrudGrid(Class<Athlete> domainType, OwlcmsGridLayout crudLayout,
	        OwlcmsCrudFormFactory<Athlete> owlcmsCrudFormFactory, Grid<Athlete> grid) {
		super(domainType, crudLayout, owlcmsCrudFormFactory, grid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.vaadin.crudui.crud.impl.GridCrud#updateButtonClicked()
	 */
	@Override
	protected void updateButtonClicked() {
		Athlete domainObject = this.grid.asSingleSelect().getValue();
		Athlete sought = domainObject;
		// if available we want the exact object from the lifting order and not a copy
		OwlcmsSession.withFop((fop) -> {
			Long id = sought.getId();
			found: for (Athlete a : fop.getLiftingOrder()) {
				this.logger.debug("checking for {} : {} {}", id, a, a.getId());
				if (a.getId().equals(id)) {
					this.match = a;
					break found;
				}
			}
		});
		this.logger.trace("domainObject = {} {}", (domainObject != this.match ? "!!!!" : ""), domainObject, this.match);
		if (this.match != null) {
			domainObject = this.match;
		}

		// show both an update and a delete button.
		this.showForm(CrudOperation.UPDATE, domainObject, false, this.savedMessage, null);
	}
}
