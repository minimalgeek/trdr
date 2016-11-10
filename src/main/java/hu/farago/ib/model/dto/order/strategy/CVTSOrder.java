package hu.farago.ib.model.dto.order.strategy;

import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.order.strategy.enums.Strategy;

/**
 * This class holds the data pasted from an Excel sheet
 * 
 * @author Balázs
 *
 */
public class CVTSOrder extends AbstractStrategyOrder {

	private ActionType action;
	private double previousDayClosePrice;
	private double limitPrice;
	private double historicalVolatility;

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
