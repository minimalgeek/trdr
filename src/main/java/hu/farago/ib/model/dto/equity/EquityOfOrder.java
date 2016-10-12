package hu.farago.ib.model.dto.equity;

import org.joda.time.DateTime;

public class EquityOfOrder {

	public int orderId;
	public DateTime openDate;
	public DateTime closeDate;
	public double profitAndLoss;

	public EquityOfOrder(int orderId, DateTime openDate, DateTime closeDate,
			double profitAndLoss) {
		this.orderId = orderId;
		this.openDate = openDate;
		this.closeDate = closeDate;
		this.profitAndLoss = profitAndLoss;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
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

	public double getProfitAndLoss() {
		return profitAndLoss;
	}

	public void setProfitAndLoss(double profitAndLoss) {
		this.profitAndLoss = profitAndLoss;
	}

}
