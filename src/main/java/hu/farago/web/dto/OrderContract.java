package hu.farago.web.dto;

import com.ib.client.OrderType;
import com.ib.client.Types.SecType;

public class OrderContract extends Contract {

	private String action;
	private OrderType orderType;
	private double totalQuantity;
	private double limitPrice;
	
	public OrderContract() {
		this.setSymbol("AAPL");
		this.setSecType(SecType.STK);
		this.setCurrency("USD");
		this.setExchange("SMART");
		this.setPrimaryExchange("ISLAND");
		
		this.setAction("BUY");
		this.setOrderType(OrderType.LMT);
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public OrderType getOrderType() {
		return orderType;
	}
	
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double limitPrice) {
		this.limitPrice = limitPrice;
	}

}
