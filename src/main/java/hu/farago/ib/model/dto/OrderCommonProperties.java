package hu.farago.ib.model.dto;

import hu.farago.ib.order.strategy.enums.Strategy;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ib.client.OrderType;
import com.ib.client.Types.SecType;

@Document(collection = "order_common_properties")
public class OrderCommonProperties {

	@Id
	public Strategy id;

	public SecType secType;
	public String currency;
	public String exchange;
	public String primaryExchange;
	public OrderType orderType;
	public Double positionSize;
	public Double targetVolatility;

	public Strategy getId() {
		return id;
	}

	public void setId(Strategy id) {
		this.id = id;
	}

	public SecType getSecType() {
		return secType;
	}

	public void setSecType(SecType secType) {
		this.secType = secType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getPrimaryExchange() {
		return primaryExchange;
	}

	public void setPrimaryExchange(String primaryExchange) {
		this.primaryExchange = primaryExchange;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public Double getPositionSize() {
		return positionSize;
	}

	public void setPositionSize(Double positionSize) {
		this.positionSize = positionSize;
	}

	public Double getTargetVolatility() {
		return targetVolatility;
	}

	public void setTargetVolatility(Double targetVolatility) {
		this.targetVolatility = targetVolatility;
	}
	
}
