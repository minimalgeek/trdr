package hu.farago.ib.order;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.ib.client.CommissionReport;
import com.ib.client.Execution;
import com.ib.client.Order;

import hu.farago.AbstractRootTest;
import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;

public class IBOrderSaverTest extends AbstractRootTest {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private IBOrderDAO ibOrderDAO;

	@Before
	public void sendEventsToTest() {
		ibOrderDAO.deleteAll();

		IBOrder ibOrder = new IBOrder();
		ibOrder.setOrder(new Order());
		ibOrder.getOrder().totalQuantity(20.0);
		ibOrder.setOrderId(10);

		eventBus.post(ibOrder);

		Execution execution = new Execution();
		execution.avgPrice(13.3);
		execution.cumQty(10);
		execution.execId("15");
		execution.orderId(10);
		execution.price(13.5);
		execution.shares(15.0);
		execution.time("Whatever");

		eventBus.post(execution);

		CommissionReport commission = new CommissionReport();

		commission.m_execId = "15";
		commission.m_realizedPNL = 123.45;

		eventBus.post(commission);
		
		execution = new Execution();
		execution.avgPrice(13.2);
		execution.cumQty(10);
		execution.execId("16");
		execution.orderId(10);
		execution.price(13.5);
		execution.shares(15.0);
		execution.time("Whatever2");

		eventBus.post(execution);

		commission = new CommissionReport();

		commission.m_execId = "16";
		commission.m_realizedPNL = 123.55;

		eventBus.post(commission);
		
		// fire a not binding execution and commission report!
		execution = new Execution();
		execution.avgPrice(123.45);
		execution.cumQty(321432);
		execution.execId("17");
		execution.orderId(11);
		execution.price(34.22);
		execution.shares(10.0);
		execution.time("Whatever3");

		eventBus.post(execution);
		
		commission = new CommissionReport();

		commission.m_execId = "17";
		commission.m_realizedPNL = 200.0;

		eventBus.post(commission);
	}

	@After
	public void deleteAll() {
		ibOrderDAO.deleteAll();
	}

	@Test
	public void commissionAndExecutionSaveTest() throws InterruptedException {
		Thread.sleep(1000);

		IBOrder order = ibOrderDAO.findOne(10);
		Assert.assertNotNull(order);
		Assert.assertEquals(2, order.getExecutions().size());
		Assert.assertEquals(247.0, order.getExecutions().stream().mapToDouble((e) -> e.realizedPNL).sum(), 0.5);
	}

}
