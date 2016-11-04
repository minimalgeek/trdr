package hu.farago.ib.model.dto.equity;

import org.joda.time.DateTime;

public class EquityOfOrder {

	public Integer orderId;
	public DateTime openDate;
	public DateTime closeDate;
	public Double profitAndLoss;

	public EquityOfOrder(Integer orderId, DateTime openDate,
			DateTime closeDate, Double profitAndLoss) {
		this.orderId = orderId;
		this.openDate = openDate;
		this.closeDate = closeDate;
		this.profitAndLoss = profitAndLoss;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public DateTime getOpenDate() {
		return openDate;
	}

	public void setOpenDate(DateTime openDate) {
		this.openDate = openDate;
	}

	public DateTime getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(DateTime closeDate) {
		this.closeDate = closeDate;
	}

	public Double getProfitAndLoss() {
		return profitAndLoss;
	}

	public void setProfitAndLoss(Double profitAndLoss) {
		this.profitAndLoss = profitAndLoss;
	}

}
