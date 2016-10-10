package hu.farago.ib.model.dto.market;

import org.joda.time.DateTime;

public class StockQueryDTO {

	public String ticker;
	public DateTime from;
	public DateTime to;
	
	public StockQueryDTO(String ticker, DateTime from, DateTime to) {
		super();
		this.ticker = ticker;
		this.from = from;
		this.to = to;
	}
	
	
}
