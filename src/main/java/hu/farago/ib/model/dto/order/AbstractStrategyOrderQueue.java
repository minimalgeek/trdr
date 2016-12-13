package hu.farago.ib.model.dto.order;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.ib.client.Contract;
import com.ib.client.Order;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.market.TickPrice;

public abstract class AbstractStrategyOrderQueue<T extends AbstractStrategyOrder> {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private Map<Integer, List<Order>> tickerIdToOrderMap;
	protected EWrapperImpl eWrapper;
	protected EventBus eventBus;
	protected Function<List<Order>, Void> callback;
	
	public AbstractStrategyOrderQueue(EventBus eventBus, EWrapperImpl eWrapper) {
		this.eWrapper = eWrapper;
		this.eventBus = eventBus;
		this.eventBus.register(this);
		
		this.tickerIdToOrderMap = Maps.newConcurrentMap();
	}
	
	public void addCallback(Function<List<Order>, Void> callback) {
		this.callback = callback;
	}
	
	public void addOrder(List<Order> order, Contract contract) {
		int id = eWrapper.reqMktData(contract);
		tickerIdToOrderMap.put(id, order);
	}
	
	public List<Order> findByTickerId(Integer tickerId) {
		return tickerIdToOrderMap.get(tickerId);
	}
	
	public List<Order> removeByTickerId(Integer tickerId) {
		eWrapper.cancelMktData(tickerId);
		return tickerIdToOrderMap.remove(tickerId);
	}
	
	protected void applyCallbackAndRemoveOrders(TickPrice price, List<Order> order) {
		callback.apply(order);
		List<Order> removedOrder = removeByTickerId(price.tickerId);
		LOGGER.info("Removed order: " + removedOrder.toString());
	}
	
	public void removeAll() {
		for (Integer key : tickerIdToOrderMap.keySet()) {
			removeByTickerId(key);
		}
		tickerIdToOrderMap.clear();
	}

}
