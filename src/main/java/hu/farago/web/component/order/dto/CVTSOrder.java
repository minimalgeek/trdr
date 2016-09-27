package hu.farago.web.component.order.dto;

import hu.farago.ib.strategy.ActionType;
import hu.farago.ib.strategy.Strategy;

import org.joda.time.DateTime;

/**
 * This class holds the data pasted from an Excel sheet
 * 
 * @author Balázs
 *
 */
public class CVTSOrder extends Order {

	private String ticker;
	private DateTime startDateTime;
	private ActionType action;
	private double previousDayClosePrice;
	private double limitPrice;
	private double historicalVolatility;

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public DateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(DateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public double getPreviousDayClosePrice() {
		return previousDayClosePrice;
	}

	public void setPreviousDayClosePrice(double previousDayClosePrice) {
		this.previousDayClosePrice = previousDayClosePrice;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double limitPrice) {
		this.limitPrice = limitPrice;
	}

	public double getHistoricalVolatility() {
		return historicalVolatility;
	}

	public void setHistoricalVolatility(double historicalVolatility) {
		this.historicalVolatility = historicalVolatility;
	}

	@Override
	public Strategy strategy() {
		return Strategy.CVTS;
	}

}
