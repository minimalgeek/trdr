package hu.farago.ib.order.strategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.Contract;
import com.ib.client.Order;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.ConnectAck;
import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.order.strategy.enums.Strategy;

public abstract class AbstractStrategyOrderQueue<T extends AbstractStrategyOrder> {

	public static class QueueChanged {
		public Strategy strategy;
		
		public QueueChanged() {
		}
		
		public QueueChanged(Strategy strategy) {
			this.strategy = strategy;
		}
	}
	
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
	private List<OrdersAndContract> triggeredOrderList;
	protected OrderCommonProperties ocp;
	protected EWrapperImpl eWrapper;
	protected EventBus eventBus;

	public AbstractStrategyOrderQueue(EventBus eventBus, EWrapperImpl eWrapper) {
		this.eWrapper = eWrapper;
		this.eventBus = eventBus;
		this.eventBus.register(this);

		this.tickerIdToOrderMap = Maps.newConcurrentMap();
		this.triggeredOrderList = Lists.newCopyOnWriteArrayList();
	}

	public void addOrder(OrdersAndContract oac) {
		int id = eWrapper.reqMktData(oac.contract);
		tickerIdToOrderMap.put(id, oac);
		fireChangeEvent(oac);
	}

	public OrdersAndContract findByTickerId(Integer tickerId) {
		return tickerIdToOrderMap.get(tickerId);
	}

	public OrdersAndContract removeByTickerIdAndMarkAsPlaced(Integer tickerId) {
		OrdersAndContract ret = removeByTickerId(tickerId);
		triggeredOrderList.add(ret);
		fireChangeEvent(ret);
		return ret;
	}
	
	public OrdersAndContract removeByTickerId(Integer tickerId) {
		eWrapper.cancelMktData(tickerId);
		OrdersAndContract ret = tickerIdToOrderMap.remove(tickerId);
		fireChangeEvent(ret);
		return ret;
	}

	public void removeAllFromQueue() {
		for (Integer key : tickerIdToOrderMap.keySet()) {
			removeByTickerId(key);
		}
		// not necessary!
		tickerIdToOrderMap.clear();
	}
	
	public void removeAllFromQueueAndTriggeredOrders() {
		removeAllFromQueue();
		triggeredOrderList.clear();
	}

	public List<T> getAbstractOrders() {
		List<T> ret = tickerIdToOrderMap.entrySet().stream().map(x -> x.getValue().abstractOrder)
				.collect(Collectors.toList());
		return ret;
	}
	
	public List<T> getTriggeredOrders() {
		return triggeredOrderList.stream().map(x -> x.abstractOrder).collect(Collectors.toList());
	}

	@Subscribe
	private void clientReconnected(ConnectAck ack) {
		for (Integer key : tickerIdToOrderMap.keySet()) {
			OrdersAndContract oac = findByTickerId(key);
			addOrder(oac);
			removeByTickerId(key);
		}
	}
	
	private void fireChangeEvent(OrdersAndContract oac) {
		eventBus.post(new QueueChanged(oac.abstractOrder.strategy()));
	}

	public void setOcp(OrderCommonProperties ocp) {
		this.ocp = ocp;
	}

}
