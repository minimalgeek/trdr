package hu.farago.ib.order.strategy.cvts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.Order;
import com.ib.client.Types.Action;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.market.TickPrice;
import hu.farago.ib.model.dto.order.AbstractStrategyOrderQueue;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;

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
		List<Order> orders = findByTickerId(price.tickerId);
		if (orders != null && callback != null) {
			Order parent = orders.stream().filter((e) -> e.parentId() == 0).findFirst().get();
			if (parent.action() == Action.BUY && price.price <= parent.lmtPrice()) {
				LOGGER.info("Buy triggered");
				applyCallbackAndRemoveOrders(price, orders);
			} else if (parent.action() == Action.SELL && price.price >= parent.lmtPrice()) {
				LOGGER.info("Sell triggered");
				applyCallbackAndRemoveOrders(price, orders);
			}
		}
	}
	
	@Scheduled(cron="0 0 0 * * ?")
	private void clearAllAtMidnight() {
		removeAll();
	}
}
