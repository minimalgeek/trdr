package hu.farago.ib.model.dto.order;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;

import hu.farago.ib.order.strategy.enums.Strategy;

@Document(collection = "ib_order")
public class IBOrder {

	@Id
	private int orderId;

	private Strategy strategy;
	private DateTime openDate;
	private DateTime closeDate;
	private Integer parentOrderId;

	// sent by openOrder()
	private Contract contract;
	private Order order;
	private OrderState orderState;
	
	// sent by orderStatus()	
	private IBOrderStatus lastOrderStatus;

	public class ExecutionAndCommission {
		public int orderId;
		public String execId;
		public double avgPrice;
		public double price;
		public double shares;
		public String time;
		public int cumQty;
		public double realizedPNL;
	}
	
	// sent by execDetails() and commissionReport()
	private List<ExecutionAndCommission> executions = Lists.newArrayList();
	
	public IBOrder() {
	}
	
	public IBOrder(int orderId, Contract contract, Order order,
			OrderState orderState, Strategy strategy, Integer parentOrderId) {
		super();
		this.orderId = orderId;
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
		this.strategy = strategy;
		this.parentOrderId = parentOrderId;
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

	public IBOrderStatus getLastOrderStatus() {
		return lastOrderStatus;
	}
	
	public void setLastOrderStatus(IBOrderStatus lastOrderStatus) {
		this.lastOrderStatus = lastOrderStatus;
	}
	
	public List<ExecutionAndCommission> getExecutions() {
		return executions;
	}
	
	public void setExecutions(List<ExecutionAndCommission> executions) {
		this.executions = executions;
	}
		
	public Integer getParentOrderId() {
		return parentOrderId;
	}
	
	public void setParentOrderId(Integer parentOrderId) {
		this.parentOrderId = parentOrderId;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
