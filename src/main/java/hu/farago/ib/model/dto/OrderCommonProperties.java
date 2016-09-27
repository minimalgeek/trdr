package hu.farago.ib.model.dto;

import hu.farago.ib.strategy.Strategy;

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
	
}
