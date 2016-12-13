package hu.farago.ib.order.strategy.cvts;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
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
import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.market.TickPrice;
import hu.farago.ib.model.dto.order.AbstractStrategyOrderQueue;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.strategy.enums.Strategy;

/**
 * Listens to price changes. According to that, it triggers the limit orders.
 * 
 * @author neural
 */
@Component
public class CVTSQueue extends AbstractStrategyOrderQueue<CVTSOrder> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IBOrderDAO ibOrderDAO;
	
	@Autowired
	public CVTSQueue(EventBus eventBus, EWrapperImpl eWrapper) {
		super(eventBus, eWrapper);
	}

	@Subscribe
	private void consumePrice(TickPrice price) {
		LOGGER.info("Tick price arrived. ID: " + price.tickerId + "; price: " + price.price); 
		OrdersAndContract oac = findByTickerId(price.tickerId);
		if (oac != null) {
			Order parent = (oac.orders).stream().filter((e) -> e.parentId() == 0).findFirst().get();
			if (parent.action() == Action.BUY && price.price <= parent.lmtPrice()) {
				LOGGER.info("Buy triggered");
				placeOrdersIfPossibleAndRemoveThemFromQueue(price, oac);
			} else if (parent.action() == Action.SELL && price.price >= parent.lmtPrice()) {
				LOGGER.info("Sell triggered");
				placeOrdersIfPossibleAndRemoveThemFromQueue(price, oac);
			}
		}
	}

	private void placeOrdersIfPossibleAndRemoveThemFromQueue(TickPrice price, OrdersAndContract oac) {
		// 0 means root order
		List<IBOrder> openedOrders = ibOrderDAO.findByStrategyAndParentOrderIdAndCloseDateIsNull(Strategy.CVTS, 0);

		if (openedOrders.size() < (int) ObjectUtils.defaultIfNull(ocp.maxOrders, 0)) {
			for (Order order : oac.orders) {
				eWrapper.placeOrder(order, oac.contract, Strategy.CVTS);
			}
			
			OrdersAndContract removedOrder = removeByTickerId(price.tickerId);
			LOGGER.info("Orders placed and removed from the queue: " + removedOrder.orders.toString());
		} else {
			LOGGER.info("The strategy reached the maximum amount of orders, order placement is not possible at this time");
		}
	}
	
	@Scheduled(cron="0 0 0 * * ?")
	private void clearAllAtMidnight() {
		removeAll();
	}
}
