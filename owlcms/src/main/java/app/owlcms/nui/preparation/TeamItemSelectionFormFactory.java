/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/
package app.owlcms.nui.preparation;

import java.util.Collection;

import org.vaadin.crudui.crud.CrudOperation;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;

import app.owlcms.data.athlete.Athlete;
import app.owlcms.data.athlete.AthleteRepository;
import app.owlcms.data.team.TeamTreeItem;
import app.owlcms.nui.crudui.OwlcmsCrudFormFactory;
import app.owlcms.nui.shared.CustomFormFactory;
import app.owlcms.nui.shared.IAthleteEditing;
import app.owlcms.nui.shared.NAthleteRegistrationFormFactory;
import app.owlcms.spreadsheet.PAthlete;

@SuppressWarnings("serial")

public class TeamItemSelectionFormFactory
        extends OwlcmsCrudFormFactory<TeamTreeItem>
        implements CustomFormFactory<TeamTreeItem> {

	private NAthleteRegistrationFormFactory acff;

	public TeamItemSelectionFormFactory(Class<TeamTreeItem> domainType, IAthleteEditing origin) {
		super(domainType);
		this.acff = new NAthleteRegistrationFormFactory(Athlete.class, null, null);
	}

	@Override
	public TeamTreeItem add(TeamTreeItem athlete) {
		this.acff.add(athlete.getAthlete());
		return null;
	}

	@Override
	public Binder<TeamTreeItem> buildBinder(CrudOperation operation, TeamTreeItem doNotUse) {
		// will not be used because we delegate
		return null;
	}

	@Override
	public String buildCaption(CrudOperation operation, TeamTreeItem aFromDb) {
		return this.acff.buildCaption(operation, aFromDb.getAthlete());
	}

	@Override
	public Component buildFooter(CrudOperation operation, TeamTreeItem unused,
	        ComponentEventListener<ClickEvent<Button>> cancelButtonClickListener,
	        ComponentEventListener<ClickEvent<Button>> unused2, ComponentEventListener<ClickEvent<Button>> unused3,
	        boolean shortcutEnter, Button... buttons) {
		return this.acff.buildFooter(operation, null, cancelButtonClickListener, null, null, shortcutEnter, buttons);
	}

	@Override
	public Component buildNewForm(CrudOperation operation, TeamTreeItem aFromDb, boolean readOnly,
	        ComponentEventListener<ClickEvent<Button>> cancelButtonClickListener,
	        ComponentEventListener<ClickEvent<Button>> updateButtonClickListener,
	        ComponentEventListener<ClickEvent<Button>> deleteButtonClickListener, Button... buttons) {
		Athlete athlete = aFromDb.getAthlete();
		if (athlete instanceof PAthlete) {
			// get the original athlete
			athlete = ((PAthlete) athlete)._getAthlete();
		}
		return this.acff.buildNewForm(operation, athlete, readOnly, cancelButtonClickListener,
		        updateButtonClickListener, deleteButtonClickListener, buttons);
	}

	@Override
	public Button buildOperationButton(CrudOperation operation, TeamTreeItem domainObject,
	        ComponentEventListener<ClickEvent<Button>> callBack) {
		return this.acff.buildOperationButton(operation, domainObject.getAthlete(), callBack);
	}

	// @Override
	// public TextField defineOperationTrigger(CrudOperation operation, TeamTreeItem domainObject,
	// ComponentEventListener<ClickEvent<Button>> action) {
	// return acff.defineOperationTrigger(operation, domainObject.getAthlete(), action);
	// }

	@Override
	public void delete(TeamTreeItem notUsed) {
		AthleteRepository.delete(notUsed.getAthlete());
	}

	@Override
	public Collection<TeamTreeItem> findAll() {
		// not used
		return null;
	}

	@Override
	public boolean setErrorLabel(BinderValidationStatus<?> validationStatus, boolean updateFieldStatus) {
		return this.acff.setErrorLabel(validationStatus, updateFieldStatus);
	}

	@Override
	public TeamTreeItem update(TeamTreeItem athleteFromDb) {
		AthleteRepository.save(athleteFromDb.getAthlete());
		return athleteFromDb;
	}

}
