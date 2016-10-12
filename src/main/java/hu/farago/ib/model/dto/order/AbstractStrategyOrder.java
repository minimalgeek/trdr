package hu.farago.ib.model.dto.order;

import hu.farago.ib.order.strategy.enums.Strategy;

public abstract class AbstractStrategyOrder {

	protected String ticker;
	
	public abstract Strategy strategy();
	
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
}
