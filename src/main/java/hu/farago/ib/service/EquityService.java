package hu.farago.ib.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.equity.EquityOfOrder;
import hu.farago.ib.model.dto.equity.EquityQuery;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.QIBOrder;

@Component
public class EquityService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IBOrderDAO orderDAO;

	public List<EquityOfOrder> getOrders(EquityQuery query) {
		LOGGER.info("getOrders: " + query.toString());
		List<EquityOfOrder> ret = Lists.newArrayList();

		QIBOrder qIBOrder = new QIBOrder("order");
		Predicate predicate = null;
		if (query.from == null && query.to == null) {
			predicate = qIBOrder.strategy.eq(query.strategy);
		} else if (query.from == null && query.to != null) {
			predicate = qIBOrder.openDate.loe(query.to).and(qIBOrder.strategy.eq(query.strategy));
		} else if (query.from != null && query.to == null) {
			predicate = qIBOrder.openDate.goe(query.from).and(qIBOrder.strategy.eq(query.strategy));
		} else {
			predicate = qIBOrder.openDate.loe(query.to).and(qIBOrder.openDate.goe(query.from)).and(qIBOrder.strategy.eq(query.strategy));
		}
		Iterable<IBOrder> orders = orderDAO.findAll(predicate, qIBOrder.orderId.asc());

		for (IBOrder ibOrder : orders) {
			EquityOfOrder e = new EquityOfOrder(ibOrder.getOrderId(), ibOrder.getContract().symbol(),
					ibOrder.getOrder().action().name(), ibOrder.getOpenDate(), ibOrder.getCloseDate(), ibOrder.getPnl(),
					ibOrder.getParentOrderId(),
					ibOrder.getLastOrderStatus() != null ? ibOrder.getLastOrderStatus().getStatus() : "Unknown");
			ret.add(e);
		}

		return ret;
	}

}
