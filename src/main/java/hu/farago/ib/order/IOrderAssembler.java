package hu.farago.ib.order;

import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.OrderCommonProperties;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.Order;

public interface IOrderAssembler<T extends AbstractStrategyOrder> {
	public Contract buildContract(T so, OrderCommonProperties ocp);
	public List<Order> buildOrders(T so, OrderCommonProperties ocp);
}
