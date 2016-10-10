package hu.farago.ib.service.order;

import static org.junit.Assert.assertEquals;
import hu.farago.ib.model.dto.IBOrder;
import hu.farago.ib.model.dto.IBOrderStatus;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.web.component.order.dto.CVTSOrder;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

public class OrderServiceCVTSTest extends AbstractOrderServiceTest<CVTSOrder> {

	@Autowired
	private OrderService orderService;
	
	private List<IBOrder> openedOrders = Lists.newArrayList();
	
	@Test
	public void placeOrderCVTSTest() throws InterruptedException {
		sendOrder();
		
		Thread.sleep(3000);
		
		assertEquals(5, openedOrders.size());
	}
	
	@Subscribe
	private void openOrder(IBOrder order) {
		openedOrders.add(order);
	}
	
	@Subscribe
	private void openOrder(IBOrderStatus status) {
		status.getAvgFillPrice();
	}
	
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
