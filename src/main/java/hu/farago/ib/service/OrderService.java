package hu.farago.ib.service;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.ib.order.strategy.CVTSAssembler;
import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.web.component.order.dto.AbstractStrategyOrder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.ib.client.Contract;
import com.ib.client.Order;

@Component
public class OrderService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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

	public <T extends AbstractStrategyOrder> void placeOrder(T so) {
		Strategy strat = so.strategy();

		OrderCommonProperties ocp = ocpDAO.findOne(strat);
		if (ocp == null) {
			eventBus.post(new IBError(strat.name()
					+ " strategy's common properties are empty!"));
		}

		IOrderAssembler<T> orderAssembler = getAssembler(strat);

		Contract contract = orderAssembler.buildContract(so, ocp);
		for (Order order : orderAssembler.buildOrders(so, ocp, wrapper.nextOrderId())) {
			wrapper.getClientSocket().placeOrder(order.orderId(), contract,
					order);
		}
	}

	public <T extends AbstractStrategyOrder> void placeOrders(
			List<T> convertedItems) {
		for (T order : convertedItems) {
			placeOrder(order);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractStrategyOrder> IOrderAssembler<T> getAssembler(
			Strategy strat) {
		switch (strat) {
		case CVTS:
			return (IOrderAssembler<T>) cvtsAssembler;
		default:
			return null;
		}
	}

	public void reqAllOpenOrders() {
		LOGGER.info("reqAllOpenOrders");
		wrapper.getClientSocket().reqAllOpenOrders();
	}
}
