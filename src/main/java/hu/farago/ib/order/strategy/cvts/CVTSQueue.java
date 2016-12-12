package hu.farago.ib.order.strategy.cvts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.Order;
import com.ib.client.Types.Action;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.market.TickPrice;
import hu.farago.ib.model.dto.order.AbstractStrategyOrderQueue;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.strategy.enums.ActionType;

/**
 * Listens to price changes. According to that, it triggers the limit orders.
 * 
 * @author neural
 */
@Component
public class CVTSQueue extends AbstractStrategyOrderQueue<CVTSOrder> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public CVTSQueue(EventBus eventBus, EWrapperImpl eWrapper) {
		super(eventBus, eWrapper);
	}

	@Subscribe
	private void consumePrice(TickPrice price) {
		LOGGER.info("Tick price arrived. ID: " + price.tickerId + "; price: " + price.price); 
		Order order = findByTickerId(price.tickerId);
		if (order != null && callback != null) {
			if (order.action() == Action.BUY && price.price >= order.lmtPrice()) {
				LOGGER.info("Buy triggered");
				applyCallbackAndRemoveOrders(price, order);
			} else if (order.action() == Action.SELL && price.price <= order.lmtPrice()) {
				LOGGER.info("Sell triggered");
				applyCallbackAndRemoveOrders(price, order);
			}
		}
	}

	private void applyCallbackAndRemoveOrders(TickPrice price, Order order) {
		callback.apply(order);
		Order removedOrder = removeByTickerId(price.tickerId);
		LOGGER.info("Removed order: " + removedOrder.toString());
	}
}
