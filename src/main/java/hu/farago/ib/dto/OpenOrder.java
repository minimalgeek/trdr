package hu.farago.ib.dto;

import org.springframework.core.style.ToStringCreator;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

public class OpenOrder {
	private int orderId;
	private Contract contract;
	private Order order;
	private OrderState orderState;
	public OpenOrder(int orderId, Contract contract, Order order,
			OrderState orderState) {
		super();
		this.orderId = orderId;
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
	}
	
	@Override
	public String toString() {
		ToStringCreator tsc = new ToStringCreator(this);
		tsc.append("Order id", orderId)
			.append("Contract", contract.toString())
			.append("Order", order.toString())
			.append("Order state", orderState.toString());
		return tsc.toString();
	}
}
