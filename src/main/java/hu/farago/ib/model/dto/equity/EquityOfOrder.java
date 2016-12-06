package hu.farago.ib.model.dto.equity;

import org.joda.time.DateTime;

public class EquityOfOrder {

	public Integer orderId;
	public String ticker;
	public String action;
	public DateTime openDate;
	public DateTime closeDate;
	public Double profitAndLoss;
	public Integer parentOrderId;
	public String status;
	public Double accountingPNL;

	public EquityOfOrder(Integer orderId, String ticker, String action, DateTime openDate,
			DateTime closeDate, Double profitAndLoss, Integer parentOrderId, String status, Double accountingPNL) {
		this.orderId = orderId;
		this.ticker = ticker;
		this.action = action;
		this.openDate = openDate;
		this.closeDate = closeDate;
		this.profitAndLoss = profitAndLoss != null && profitAndLoss != Double.MAX_VALUE ? profitAndLoss : 0.0;
		this.parentOrderId = parentOrderId;
		this.status = status;
		this.accountingPNL = accountingPNL;
	}
	
}
