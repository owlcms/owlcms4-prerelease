package app.owlcms.nui.displays.scoreboards;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import app.owlcms.apputils.queryparameters.DisplayParameters;
import app.owlcms.apputils.queryparameters.SoundParameters;
import app.owlcms.data.config.Config;
import app.owlcms.displays.scoreboard.ResultsRankingOrder;
import app.owlcms.init.OwlcmsSession;
import ch.qos.logback.classic.Logger;

@SuppressWarnings("serial")
@Route("displays/publicRankingOrder")

public class PublicRankingOrderPage extends PublicBoardPage {

	Logger logger = (Logger) LoggerFactory.getLogger(PublicRankingOrderPage.class);

	public PublicRankingOrderPage() {
		// intentionally empty. superclass will call init() as required.
	}

	@Override
	public String getPageTitle() {
		return getTranslation("Scoreboard.RankingOrder") + OwlcmsSession.getFopNameIfMultiple();
	}

	@Override
	protected void init() {
		var board = new ResultsRankingOrder();
		this.setBoard(board);
		board.setLeadersDisplay(true);
		board.setRecordsDisplay(true);
		this.addComponent(board);
		this.ui = UI.getCurrent();

		setDefaultParameters(QueryParameters.simple(Map.of(
		        SoundParameters.SILENT, "true",
		        SoundParameters.DOWNSILENT, "true",
		        DisplayParameters.DARK, "true",
		        DisplayParameters.LEADERS, "true",
		        DisplayParameters.RECORDS, "true",
		        DisplayParameters.ABBREVIATED,
		        Boolean.toString(Config.getCurrent().featureSwitch("shortScoreboardNames")))));
	}

}