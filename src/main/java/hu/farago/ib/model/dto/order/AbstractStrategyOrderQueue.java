package hu.farago.ib.model.dto.order;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.ib.client.Contract;
import com.ib.client.Order;

import hu.farago.ib.EWrapperImpl;

public abstract class AbstractStrategyOrderQueue<T extends AbstractStrategyOrder> {

	protected static class OrdersAndContract {
		public List<Order> orders;
		public Contract contract;

		public OrdersAndContract(List<Order> orders, Contract contract) {
			this.orders = orders;
			this.contract = contract;
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

	public void addOrder(List<Order> order, Contract contract) {
		int id = eWrapper.reqMktData(contract);
		tickerIdToOrderMap.put(id, new OrdersAndContract(order, contract));
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
		tickerIdToOrderMap.clear();
	}

	public void setOcp(OrderCommonProperties ocp) {
		this.ocp = ocp;
	}

}
