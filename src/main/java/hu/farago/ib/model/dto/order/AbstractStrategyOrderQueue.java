package hu.farago.ib.model.dto.order;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.ib.client.Contract;
import com.ib.client.Order;

import hu.farago.ib.EWrapperImpl;

public abstract class AbstractStrategyOrderQueue<T extends AbstractStrategyOrder> {
	
	private Map<Integer, Order> tickerIdToOrderMap;
	protected EWrapperImpl eWrapper;
	protected EventBus eventBus;
	protected Function<Order, Void> callback;
	
	public AbstractStrategyOrderQueue(EventBus eventBus, EWrapperImpl eWrapper) {
		this.eWrapper = eWrapper;
		this.eventBus = eventBus;
		this.eventBus.register(this);
		
		this.tickerIdToOrderMap = Maps.newConcurrentMap();
	}
	
	public void addCallback(Function<Order, Void> callback) {
		this.callback = callback;
	}
	
	public void addOrder(Order order, Contract contract) {
		int id = eWrapper.reqMarketData(contract);
		tickerIdToOrderMap.put(id, order);
	}
	
	public Order findByTickerId(Integer tickerId) {
		return tickerIdToOrderMap.get(tickerId);
	}
	
	public Order removeByTickerId(Integer tickerId) {
		eWrapper.cancelMktData(tickerId);
		return tickerIdToOrderMap.remove(tickerId);
	}
	
	public void removeAll() {
		tickerIdToOrderMap.clear();
	}

}
