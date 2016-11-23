package hu.farago.ib.service;

import java.util.List;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.farago.AbstractRootTest;
import hu.farago.ib.model.dto.equity.EquityOfOrder;
import hu.farago.ib.model.dto.equity.EquityQuery;
import hu.farago.ib.order.strategy.enums.Strategy;

public class EquityServiceTest extends AbstractRootTest {

	@Autowired
	private EquityService equityService;
	
	@Test
	public void getOrdersTest1() {
		EquityQuery eq = new EquityQuery();
		eq.strategy = Strategy.CVTS;
		
		List<EquityOfOrder> ret = equityService.getOrders(eq);
		assertNotNull(ret);
	}
	
	@Test
	public void getOrdersTest2() {
		EquityQuery eq = new EquityQuery();
		eq.strategy = Strategy.CVTS;
		eq.from = DateTime.now().minusYears(2);
		
		List<EquityOfOrder> ret = equityService.getOrders(eq);
		assertNotNull(ret);
	}
	
	@Test
	public void getOrdersTest3() {
		EquityQuery eq = new EquityQuery();
		eq.strategy = Strategy.CVTS;
		eq.from = DateTime.now().minusYears(2);
		eq.to = DateTime.now().plusYears(2);
		
		List<EquityOfOrder> ret = equityService.getOrders(eq);
		assertNotNull(ret);
	}
	
}
