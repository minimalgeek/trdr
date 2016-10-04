package hu.farago.ib.order;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.web.component.order.dto.AbstractStrategyOrder;

import java.util.List;

import com.ib.client.Contract;
import com.ib.client.Order;

public interface IOrderAssembler<T extends AbstractStrategyOrder> {
	public Contract buildContract(T so, OrderCommonProperties ocp);
	public List<Order> buildOrders(T so, OrderCommonProperties ocp, int parentOrderId);
}
