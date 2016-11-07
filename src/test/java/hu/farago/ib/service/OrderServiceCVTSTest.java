package hu.farago.ib.service;

import static org.junit.Assert.assertEquals;
import hu.farago.ib.model.dto.order.CVTSOrder;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.order.strategy.enums.ActionType;

import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

public class OrderServiceCVTSTest extends AbstractOrderServiceTest<CVTSOrder> {

	@Autowired
	private OrderService orderService;
	
	private Set<Integer> openedOrderIds = Sets.newHashSet();
	
	@Test
	public void placeOrderCVTSTest() throws InterruptedException {
		sendOrder();
		
		Thread.sleep(3000);
		
		assertEquals(5, openedOrderIds.size());
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
		
		return order;
	}
}
