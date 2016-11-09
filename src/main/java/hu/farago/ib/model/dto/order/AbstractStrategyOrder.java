package hu.farago.ib.model.dto.order;

import org.joda.time.DateTime;

import hu.farago.ib.order.strategy.enums.Strategy;

public abstract class AbstractStrategyOrder {

	protected DateTime startDateTime;
	protected String ticker;
	
	public abstract Strategy strategy();
	
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
	
}
