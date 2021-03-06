package hu.farago.ib.service;

import hu.farago.AbstractRootTest;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.service.OrderService;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.ib.client.OrderType;
import com.ib.client.Types.SecType;

public abstract class AbstractOrderServiceTest<T extends AbstractStrategyOrder> extends AbstractRootTest {
	
	@Autowired
    @InjectMocks
	protected OrderService orderService;
	@Autowired
	protected OrderCommonPropertiesDAO ocpDAO;
	@Autowired
	protected EventBus eventBus;
	
	protected T order;
	
	protected abstract T buildOrder();
	
	protected void sendOrder() {
		this.orderService.placeOrder(order);
	}
	
	@Before
	public void before() {
		eventBus.register(this);
		order = buildOrder();
		
		OrderCommonProperties ocp = new OrderCommonProperties();
		
		ocp.id = order.strategy();
		ocp.secType = SecType.STK;
		ocp.currency = "USD";
		ocp.exchange = "SMART";
		ocp.primaryExchange = "NASDAQ";
		ocp.orderType = OrderType.LMT;
		ocp.positionSize = 1000.0;
		ocp.targetVolatility = 10.0;
		ocp.faProfile = "Proba01";
		ocp.barStop = 5;
		
		ocp.firstDayTarget = 2.5;
		ocp.remainingDaysTarget = 1.5;
		ocp.stopLossTarget = 10.0;
		ocp.maxOrders = 20;
		
		orderService.saveOrModifyOcp(ocp);
	}
	
}
