package hu.farago.ib.model.dto.order;

import hu.farago.ib.order.strategy.enums.Strategy;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;

@Document(collection = "ib_order")
public class IBOrder {

	@Id
	private int orderId;

	private Strategy strategy;
	private DateTime openDate;
	private DateTime closeDate;
	private Double pnl;
	private String lastExecId;
	private Execution lastExecution;

	private Contract contract;
	private Order order;
	private OrderState orderState;

	public IBOrder() {
		
	}
	
	public IBOrder(int orderId, Contract contract, Order order,
			OrderState orderState, Strategy strategy) {
		super();
		this.orderId = orderId;
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
		this.strategy = strategy;
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

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
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

	public Double getPnl() {
		return pnl;
	}

	public void setPnl(Double pnl) {
		this.pnl = pnl;
	}

	public Execution getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Execution lastExecution) {
		this.lastExecution = lastExecution;
	}

	public String getLastExecId() {
		return lastExecId;
	}
	
	public void setLastExecId(String lastExecId) {
		this.lastExecId = lastExecId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
