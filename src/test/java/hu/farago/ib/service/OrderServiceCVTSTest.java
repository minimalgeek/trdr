package hu.farago.ib.service;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.strategy.enums.ActionType;

public class OrderServiceCVTSTest extends AbstractOrderServiceTest<CVTSOrder> {
	
	private Set<Integer> openedOrderIds = Sets.newHashSet();
	
	@Test
	public void placeOrderCVTSTest() throws InterruptedException {
		sendOrder();
		
		Thread.sleep(3000);
		
		assertEquals(4, openedOrderIds.size());
	}
	
	@Subscribe
	private void openOrder(IBOrder order) {
		openedOrderIds.add(order.getOrderId());
	}
	
//	@Subscribe
//	private void openOrder(IBOrderStatus status) {
//		//status.getAvgFillPrice();
//	}
	
	@Override
	protected CVTSOrder buildOrder() {
		CVTSOrder order = new CVTSOrder();
		
		order.setAction(ActionType.SELL);
		order.setHistoricalVolatility(10.0);
		order.setLimitPrice(130.0);
		order.setPreviousDayClosePrice(135.0);
		order.setTicker("AAPL");
		order.setStartDateTime(DateTime.now().minusDays(1));
		
		return order;
	}
}
