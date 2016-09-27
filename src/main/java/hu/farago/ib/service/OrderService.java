package hu.farago.ib.service;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.strategy.Strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.UIScope;

@Component
@UIScope
public class OrderService {

	@Autowired
	private EWrapperImpl wrapper;
	
	@Autowired
	private OrderCommonPropertiesDAO ocpDAO;
	
	public void saveOrModifyOcp(OrderCommonProperties ocp) {
		OrderCommonProperties oldOcp = ocpDAO.findOne(ocp.id);
		if (oldOcp != null) {
			ocpDAO.delete(oldOcp);
		}
		ocpDAO.save(ocp);
	}
	
	public OrderCommonProperties loadOcp(Strategy strategy) {
		return ocpDAO.findOne(strategy);
	}
	
//	public void placeOrder(OrderContract oc) {
//		wrapper.getClientSocket().placeOrder(wrapper.getCurrentOrderId() + 1, 
//				buildIBContract(oc), buildIBOrder(oc));
//	}
//	
//	private Contract buildIBContract(OrderContract oc) {
//		Contract contract = new Contract();
//		contract.symbol(oc.getSymbol());
//		contract.secType(oc.getSecType());
//		contract.currency(oc.getCurrency());
//		contract.exchange(oc.getExchange());
//		contract.primaryExch(oc.getPrimaryExchange());
//		return contract;
//	}
//	
//	private Order buildIBOrder(OrderContract oc) {
//		Order order = new Order();
//		order.action(oc.getAction());
//		order.orderType(oc.getOrderType());
//		order.totalQuantity(oc.getTotalQuantity());
//		order.lmtPrice(oc.getLimitPrice());
//		return order;
//	}
}
