package hu.farago.web.component.order.dto;

import hu.farago.ib.order.strategy.enums.Strategy;

public abstract class StrategyOrder {

	protected String ticker;
	
	public abstract Strategy strategy();
	
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
}
