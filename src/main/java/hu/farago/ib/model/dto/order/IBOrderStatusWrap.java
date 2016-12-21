package hu.farago.ib.model.dto.order;

import com.ib.client.Contract;
import com.ib.client.Order;

public class IBOrderStatusWrap {

	private IBOrderStatus orderStatus;
	private Order order;
	private Contract contract;

	public IBOrderStatusWrap(IBOrderStatus orderStatus, Order order, Contract contract) {
		super();
		this.orderStatus = orderStatus;
		this.order = order;
		this.contract = contract;
	}
	
	public int getOrderId() {
		return order.orderId();
	}
	
	public String getTicker() {
		return contract.symbol();
	}
	
	public String getAction() {
		return order.getAction();
	}
	
	public double getLimitPrice() {
		return order.lmtPrice();
	}
	
	public String getStatus() {
		return orderStatus.getStatus();
	}
	
	public double getFilled() {
		return orderStatus.getFilled();
	}

	public double getRemaining() {
		return orderStatus.getRemaining();
	}

	public double getAvgFillPrice() {
		return orderStatus.getAvgFillPrice();
	}
	
	public int getParentId() {
		return orderStatus.getParentId();
	}
	
	public String getWhyHeld() {
		return orderStatus.getWhyHeld();
	}
	
}
