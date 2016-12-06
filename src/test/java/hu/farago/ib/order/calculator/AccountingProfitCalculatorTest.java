package hu.farago.ib.order.calculator;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.ib.client.Order;
import com.ib.client.Types.Action;

import hu.farago.AbstractRootTest;
import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.IBOrderStatus;

public class AccountingProfitCalculatorTest extends AbstractRootTest {

	@Autowired
	private AccountingProfitCalculator calculator;
	
	@Autowired
	private IBOrderDAO ibOrderDAO;
	
	@Autowired
	private EventBus eventBus;
	
	private IBOrder testOrder;
	private IBOrder parentOrder;
	
	@Before
	public void before() {
		ibOrderDAO.deleteAll();

		parentOrder = new IBOrder();

		parentOrder.setOrder(new Order());
		parentOrder.getOrder().totalQuantity(20.0);
		parentOrder.setOrderId(10);
		parentOrder.getOrder().action(Action.BUY);
		parentOrder.getOrder().totalQuantity(20);
		
		IBOrderStatus status = new IBOrderStatus();
		status.setOrderId(10);
		status.setAvgFillPrice(100.0);
		status.setStatus("Filled");
		
		testOrder = new IBOrder();
		testOrder.setOrder(new Order());
		testOrder.setOrderId(11);
		testOrder.setParentOrderId(10);
		testOrder.getOrder().action(Action.SELL);
		
		IBOrderStatus status2 = new IBOrderStatus();
		status2.setOrderId(11);
		status2.setAvgFillPrice(103.0);
		status2.setStatus("Filled");

		eventBus.post(parentOrder);
		eventBus.post(testOrder);
		eventBus.post(status);
		eventBus.post(status2);
	}
	
	@After
	public void after() {
		ibOrderDAO.deleteAll();
	}
	
	@Test
	public void calculateProfitTest() throws InterruptedException {
		Thread.sleep(1000);
		calculator.calculateProfit(testOrder);
		
		IBOrder fetchedParentOrder = ibOrderDAO.findOne(10);
		Assert.assertEquals(60.0, fetchedParentOrder.getCalculatedAccountingProfit(), 0.1);
	}
	
}
