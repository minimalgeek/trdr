package hu.farago.ib.service;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.equity.EquityOfOrder;
import hu.farago.ib.model.dto.equity.EquityQuery;
import hu.farago.ib.model.dto.order.IBOrder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class EquityService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IBOrderDAO orderDAO;
	
	public List<EquityOfOrder> getOrders(EquityQuery query) {
		LOGGER.info("getOrders: " + query.toString());
		List<EquityOfOrder> ret = Lists.newArrayList();
		
		List<IBOrder> orders = orderDAO.findByStrategyAndOpenDateBetween(query.strategy, query.from, query.to);
		
		for (IBOrder ibOrder : orders) {
			EquityOfOrder e = new EquityOfOrder(ibOrder.getOrderId(), ibOrder.getOpenDate(), ibOrder.getCloseDate(), ibOrder.getPnl());
			ret.add(e);
		}
		
		return ret;
	}
	
}
