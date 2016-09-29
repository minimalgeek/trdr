package hu.farago.ib.model.dto;

import org.springframework.core.style.ToStringCreator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

@Document(collection = "ib_opened_order")
public class OpenOrder {
	
	@Id
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

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public OrderState getOrderState() {
		return orderState;
	}

	public void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	@Override
	public String toString() {
		ToStringCreator tsc = new ToStringCreator(this);
		tsc.append("Order id", orderId).append("Contract", contract.toString())
				.append("Order", order.toString())
				.append("Order state", orderState.toString());
		return tsc.toString();
	}
}
