package hu.farago.web.dto;

import com.ib.client.Types.SecType;

public class Contract {

	private String symbol;
	private SecType secType;
	private String currency;
	private String exchange;
	private String primaryExchange;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public SecType getSecType() {
		return secType;
	}

	public void setSecType(SecType secType) {
		this.secType = secType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getPrimaryExchange() {
		return primaryExchange;
	}

	public void setPrimaryExchange(String primaryExchange) {
		this.primaryExchange = primaryExchange;
	}

}
