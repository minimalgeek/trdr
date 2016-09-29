package hu.farago.ib.order;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.web.component.order.dto.StrategyOrder;

import com.ib.client.Contract;
import com.ib.client.Order;

public interface IOrderAssembler<T extends StrategyOrder> {
	public Contract buildContract(T so, OrderCommonProperties ocp);
	public Order buildOrder(T so, OrderCommonProperties ocp);
}
