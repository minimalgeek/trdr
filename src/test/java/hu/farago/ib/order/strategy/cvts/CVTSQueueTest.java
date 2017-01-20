package hu.farago.ib.order.strategy.cvts;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.OrderStatus;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.market.TickPrice;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.ib.service.AbstractOrderServiceTest;

public class CVTSQueueTest extends AbstractOrderServiceTest<CVTSOrder> {

	@Mock
	private EWrapperImpl eWrapperImpl;

	@Autowired
	private IBOrderDAO ibOrderDao;
	
	@InjectMocks
	@Autowired
	private CVTSQueue queue;
	
	@Before
	public void before2() {

		OrderState state = mock(OrderState.class);
		when(state.status()).thenReturn(OrderStatus.PreSubmitted);
		
		Answer<Void> answer = new Answer<Void>() {
			int counter = 1;
			
			public Void answer(InvocationOnMock invocation) {
				eventBus.post(new IBOrder(counter,
						invocation.getArgumentAt(1, Contract.class),
						invocation.getArgumentAt(0, Order.class), 
						state, 
						invocation.getArgumentAt(2, Strategy.class),
						counter == 1 ? 0 : 1
						));
				counter++;
				return null;
			}
		};
		doAnswer(answer).when(eWrapperImpl).placeOrder(any(Order.class), any(Contract.class), any(Strategy.class));
		doAnswer(answer).when(eWrapperImpl).placeOrderWithParentId(any(Order.class), any(Contract.class), any(Strategy.class), any(Integer.class));
		
		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				eventBus.post(new TickPrice(
						invocation.getArgumentAt(0, Integer.class), 
						invocation.getArgumentAt(1, Integer.class), 
						invocation.getArgumentAt(2, Double.class), 
						invocation.getArgumentAt(3, Integer.class)));
				return null;
			}
		}).when(eWrapperImpl).tickPrice(any(Integer.class), any(Integer.class), any(Double.class), any(Integer.class));

		ibOrderDao.deleteAll();
		eWrapperImpl.resetTickerId();
	}

	@Test
	public void testQueuePlacement() throws InterruptedException {
		sendOrder();

		Thread.sleep(500);

		eWrapperImpl.tickPrice(0, 0, 20.06, 1);
		eWrapperImpl.tickPrice(0, 0, 20.07, 1);

		Thread.sleep(500);

		List<IBOrder> openedOrders = ibOrderDao.findByStrategyAndParentOrderIdAndCloseDateIsNull(Strategy.CVTS, 0);
		Assert.assertEquals(0, openedOrders.size());

		Thread.sleep(500);

		eWrapperImpl.tickPrice(0, 0, 20.04, 1);
		eWrapperImpl.tickPrice(0, 0, 20.03, 1);

		Thread.sleep(500);

		openedOrders = ibOrderDao.findByStrategyAndParentOrderIdAndCloseDateIsNull(Strategy.CVTS, 0);
		Assert.assertEquals(1, openedOrders.size());
	}

	@Override
	protected CVTSOrder buildOrder() {
		CVTSOrder orderToPlace = new CVTSOrder();

		orderToPlace.setAction(ActionType.BUY);
		orderToPlace.setHistoricalVolatility(12.34);
		orderToPlace.setLimitPrice(20.05);
		orderToPlace.setPreviousDayClosePrice(19.34);
		orderToPlace.setStartDateTime(DateTime.now().minusDays(1));
		orderToPlace.setTicker("AAPL");

		return orderToPlace;
	}
	
	@Test
	public void isTradingTimeTest() {
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2017, 1, 1, 16, 30).getMillis()); // trading time in UTC+1
		
		Assert.assertTrue(queue.isTradingTime());
		
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2017, 1, 1, 10, 30).getMillis()); // not trading time in UTC+1
		
		Assert.assertFalse(queue.isTradingTime());
		
		DateTimeUtils.setCurrentMillisSystem();
	}

}
