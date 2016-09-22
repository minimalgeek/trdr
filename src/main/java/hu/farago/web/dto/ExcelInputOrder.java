package hu.farago.web.dto;

import org.joda.time.DateTime;

/**
 * This class holds the data pasted from an Excel sheet
 * 
 * @author Balázs
 *
 */
public class ExcelInputOrder {

	public enum ActionType {
		BUY, SELL
	}

	private String ticker;
	private DateTime startDateTime;
	private String strategy;
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

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
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

}
