package hu.farago.ib.service.order;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.ib.order.strategy.CVTSAssembler;
import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.web.component.order.dto.StrategyOrder;

import java.util.List;

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

		IOrderAssembler<T> oa = getAssembler(strat);
		wrapper.getClientSocket().placeOrder(wrapper.nextOrderId(),
				oa.buildContract(so, ocp), oa.buildOrder(so, ocp));
	}
	
	public <T extends StrategyOrder> void placeOrders(List<T> convertedItems) {
		for(T order : convertedItems) {
			placeOrder(order);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends StrategyOrder> IOrderAssembler<T> getAssembler(Strategy strat) {
		switch (strat) {
		case CVTS:
			return (IOrderAssembler<T>) cvtsAssembler;
		default:
			return null;
		}
	}
}
