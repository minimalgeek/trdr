package hu.farago.ib.service.order;

import java.util.List;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.dto.IBError;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.order.OrderAssembler;
import hu.farago.ib.order.strategy.CVTSAssembler;
import hu.farago.ib.strategy.enums.Strategy;
import hu.farago.web.component.order.dto.CVTSOrder;
import hu.farago.web.component.order.dto.StrategyOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.UIScope;

@Component
@UIScope
public class OrderService {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private EWrapperImpl wrapper;

	@Autowired
	private OrderCommonPropertiesDAO ocpDAO;

	// Strategy assemblers
	@Autowired
	private CVTSAssembler cvtsAssembler;

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

	public <T extends StrategyOrder> void placeOrder(T so) {
		Strategy strat = so.strategy();

		OrderCommonProperties ocp = ocpDAO.findOne(strat);
		if (ocp == null) {
			eventBus.post(new IBError(strat.name()
					+ " strategy's common properties are empty!"));
		}

		OrderAssembler<T> oa = getAssembler(strat);
		wrapper.getClientSocket().placeOrder(wrapper.getCurrentOrderId() + 1,
				oa.buildContract(so, ocp), oa.buildOrder(so, ocp));
	}
	
	public <T extends StrategyOrder> void placeOrders(List<T> convertedItems) {
		for(T order : convertedItems) {
			placeOrder(order);
		}
	}


	private <T extends StrategyOrder> OrderAssembler<T> getAssembler(Strategy strat) {
		switch (strat) {
		case CVTS:
			return (OrderAssembler<T>) cvtsAssembler;
		default:
			return null;
		}
	}

	// public void placeOrder(OrderContract oc) {
	// wrapper.getClientSocket().placeOrder(wrapper.getCurrentOrderId() + 1,
	// buildIBContract(oc), buildIBOrder(oc));
	// }
	//
	// private Contract buildIBContract(OrderContract oc) {
	// Contract contract = new Contract();
	// contract.symbol(oc.getSymbol());
	// contract.secType(oc.getSecType());
	// contract.currency(oc.getCurrency());
	// contract.exchange(oc.getExchange());
	// contract.primaryExch(oc.getPrimaryExchange());
	// return contract;
	// }
	//
	// private Order buildIBOrder(OrderContract oc) {
	// Order order = new Order();
	// order.action(oc.getAction());
	// order.orderType(oc.getOrderType());
	// order.totalQuantity(oc.getTotalQuantity());
	// order.lmtPrice(oc.getLimitPrice());
	// return order;
	// }
}
