package hu.farago.ib.model.dto.order.strategy;

import java.math.BigInteger;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.order.strategy.enums.Strategy;

/**
 * This class holds the data pasted from an Excel sheet
 * 
 * @author Balázs
 *
 */
@Document(collection = "cvts_order")
public class CVTSOrder extends AbstractStrategyOrder {

	@Id
	private BigInteger id;
	
	private ActionType action;
	private double previousDayClosePrice;
	private double limitPrice;
	private double historicalVolatility;
	
	public CVTSOrder() {
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
	
	public BigInteger getId() {
		return id;
	}
	
	public void setId(BigInteger id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public Strategy strategy() {
		return Strategy.CVTS;
	}

}
