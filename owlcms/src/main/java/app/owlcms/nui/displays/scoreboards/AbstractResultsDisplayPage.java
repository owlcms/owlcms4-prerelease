package app.owlcms.nui.displays.scoreboards;

import java.text.DecimalFormat;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;

import app.owlcms.apputils.queryparameters.DisplayParameters;
import app.owlcms.data.config.Config;
import app.owlcms.displays.scoreboard.Results;
import app.owlcms.nui.displays.AbstractDisplayPage;
import app.owlcms.nui.displays.SoundEntries;

/**
 * Wrapper class to wrap a board as navigable page, to store the board display options, and to present an option editing
 * dialog.
 *
 * @author jflamy
 *
 */
@SuppressWarnings("serial")

public abstract class AbstractResultsDisplayPage extends AbstractDisplayPage
        implements SoundEntries, DisplayParameters, HasDynamicTitle {

	private Results board;

	// FIXME: normalize where the parameter values from the dialog are stored
	private Double emFontSize;
	private boolean showLeaders;
	private boolean showRecords;
	private Double teamWidth;
	
	final private DecimalFormat df = new DecimalFormat("0.000");

	@Override
	public void addDialogContent(Component page, VerticalLayout vl) {
		addSoundEntries(vl, page, (DisplayParameters) page);
	}

	public void doChangeAbbreviated() {
		if (isAbbreviatedName()) {
			updateURLLocation(getLocationUI(), getLocation(), DisplayParameters.ABBREVIATED, "true");
		} else {
			updateURLLocation(getLocationUI(), getLocation(), ABBREVIATED, null);
		}
	}

	public void doChangeEmSize() {
		String formattedEm = null;
		if (emFontSize != null) {
			formattedEm = df.format(emFontSize);
			board.getElement().setProperty("sizeOverride", " --tableFontSize:" + formattedEm + "rem;");
		}
		updateURLLocation(getLocationUI(), getLocation(), FONTSIZE,
		        emFontSize != null ? formattedEm : null);
	}

	public void doChangeTeamWidth() {
		String formattedTW = null;

		if (teamWidth != null) {
			formattedTW = df.format(teamWidth);
			board.getElement().setProperty("twOverride", "--nameWidth: 1fr; --clubWidth:" + formattedTW + "em;");
		}
		updateURLLocation(getLocationUI(), getLocation(), TEAMWIDTH, teamWidth != null ? formattedTW : null);
	}

	@Override
	public Double getEmFontSize() {
		if (emFontSize == null) {
			return 1.2;
		}
		return emFontSize;
	}

	@Override
	public Double getTeamWidth() {
		if (teamWidth == null) {
			return 12.0D;
		}
		return teamWidth;
	}

	public final boolean isShowLeaders() {
		return showLeaders;
	}

	public final boolean isShowRecords() {
		return showRecords;
	}

	public final void setBoard(Results board) {
		this.board = board;
	}

	@Override
	public void setDownSilenced(boolean silenced) {
		board.getDecisions().setSilenced(silenced);
		this.downSilenced = silenced;
	}

	@Override
	public void setEmFontSize(Double emFontSize) {
		this.emFontSize = emFontSize;
		doChangeEmSize();
	}

	@Override
	public void setLeadersDisplay(boolean showLeaders) {
		checkVideo(Config.getCurrent().getParamStylesDir() + "/video/results.css", routeParameter, this);
		this.showLeaders = showLeaders;
		this.getElement().setProperty("showLeaders", showLeaders);
	}

	@Override
	public void setRecordsDisplay(boolean showRecords) {
		this.showRecords = showRecords;
		this.getElement().setProperty("showRecords", showRecords);
	}

	public final void setShowLeaders(boolean showLeaders) {
		this.showLeaders = showLeaders;
	}

	public final void setShowRecords(boolean showRecords) {
		this.showRecords = showRecords;
	}

	/**
	 * @see app.owlcms.apputils.queryparameters.ContentParameters#setSilenced(boolean)
	 */
	@Override
	public void setSilenced(boolean silenced) {
		board.getTimer().setSilenced(silenced);
		board.getBreakTimer().setSilenced(silenced);
		this.silenced = silenced;
	}

	@Override
	public void switchEmFontSize(Component target, Double emFontSize, boolean updateURL) {
		setEmFontSize(emFontSize);
		doChangeEmSize();
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		logger.warn("AbstractAttemptBoardPage onAttach");
		super.onAttach(attachEvent);
		openDialog(getDialog());
	}

	private void checkVideo(String string, String routeParameter,
	        AbstractResultsDisplayPage abstractResultsDisplayPage) {
		// FIXME checkVideo
	}

}