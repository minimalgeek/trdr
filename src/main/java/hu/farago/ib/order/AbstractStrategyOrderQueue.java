package hu.farago.ib.order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.Contract;
import com.ib.client.Order;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.ConnectAck;
import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.OrderCommonProperties;

public abstract class AbstractStrategyOrderQueue<T extends AbstractStrategyOrder> {

	public class OrdersAndContract {

		public T abstractOrder;
		public List<Order> orders;
		public Contract contract;

		public OrdersAndContract(List<Order> orders, Contract contract, T abstractOrder) {
			this.orders = orders;
			this.contract = contract;
			this.abstractOrder = abstractOrder;
		}

	}

	private Map<Integer, OrdersAndContract> tickerIdToOrderMap;
	protected OrderCommonProperties ocp;
	protected EWrapperImpl eWrapper;
	protected EventBus eventBus;

	public AbstractStrategyOrderQueue(EventBus eventBus, EWrapperImpl eWrapper) {
		this.eWrapper = eWrapper;
		this.eventBus = eventBus;
		this.eventBus.register(this);

		this.tickerIdToOrderMap = Maps.newConcurrentMap();
	}

	public void addOrder(OrdersAndContract oac) {
		int id = eWrapper.reqMktData(oac.contract);
		tickerIdToOrderMap.put(id, oac);
	}

	public OrdersAndContract findByTickerId(Integer tickerId) {
		return tickerIdToOrderMap.get(tickerId);
	}

	public OrdersAndContract removeByTickerId(Integer tickerId) {
		eWrapper.cancelMktData(tickerId);
		return tickerIdToOrderMap.remove(tickerId);
	}

	public void removeAll() {
		for (Integer key : tickerIdToOrderMap.keySet()) {
			removeByTickerId(key);
		}
		// not necessary, just in case we clear it :)
		tickerIdToOrderMap.clear();
	}

	public List<T> getAbstractOrders() {
		List<T> ret = tickerIdToOrderMap.entrySet().stream().map(x -> x.getValue().abstractOrder)
				.collect(Collectors.toList());
		return ret;
	}

	@Subscribe
	private void clientReconnected(ConnectAck ack) {
		for (Integer key : tickerIdToOrderMap.keySet()) {
			OrdersAndContract oac = findByTickerId(key);
			addOrder(oac);
			removeByTickerId(key);
		}
	}

	public void setOcp(OrderCommonProperties ocp) {
		this.ocp = ocp;
	}

}
