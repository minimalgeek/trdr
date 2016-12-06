package hu.farago.ib.order.calculator;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ib.client.Types.Action;
import com.querydsl.core.types.Predicate;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.IBOrderStatus;
import hu.farago.ib.model.dto.order.QIBOrder;

@Component
public class AccountingProfitCalculator {
	
	@Autowired
	private IBOrderDAO orderDAO;

	public void calculateProfit(IBOrder closingOrder) {
		if (closingOrder.getCloseDate() != null && 
			closingOrder.getParentOrderId() != null && 
			closingOrder.getParentOrderId() != 0) {
			
			QIBOrder qOrder = QIBOrder.iBOrder;
			Predicate pred = qOrder.orderId.eq(closingOrder.getParentOrderId());
			
			IBOrder ocaOrderParent = orderDAO.findOne(pred);
			
			if (ocaOrderParent != null) {
				Double pnl = null;
				if (ocaOrderParent.getOrder().action() == Action.BUY) {
					pnl = (validPrice(closingOrder) - validPrice(ocaOrderParent)) * validQuantity(ocaOrderParent);
				} else if (ocaOrderParent.getOrder().action() == Action.SELL) {
					pnl = (validPrice(ocaOrderParent) - validPrice(closingOrder)) * validQuantity(ocaOrderParent);
				}
				
				if (pnl != null && pnl != 0.0) {
					ocaOrderParent.setCalculatedAccountingProfit(pnl);
					orderDAO.save(ocaOrderParent);
				}
			}
		}
	}

	private double validPrice(IBOrder order) {
		IBOrderStatus stat = order.getLastOrderStatus();
		
		if (stat != null && stat.getStatus().equals("Filled")) {
			return order.getLastOrderStatus().getAvgFillPrice();
		} else if (CollectionUtils.isNotEmpty(order.getExecutions())) {
			return order.getExecutions().stream().mapToDouble((e) -> e.avgPrice).average().getAsDouble();
		}
		return 0.0;
	}
	
	private double validQuantity(IBOrder order) {
		if (order.getOrder().totalQuantity() != Double.MAX_VALUE) {
			return order.getOrder().totalQuantity();
		} else if (CollectionUtils.isNotEmpty(order.getExecutions())) {
			return order.getExecutions().stream().mapToDouble((e) -> e.shares).sum();
		}
		
		return 0.0;
	}
}
