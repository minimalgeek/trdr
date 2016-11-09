package hu.farago.ib.service;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.ib.order.strategy.CVTSAssembler;
import hu.farago.ib.order.strategy.enums.Strategy;

import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
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

	@Autowired
	private IBOrderDAO ibOrderDAO;

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

		OrderCommonProperties ocp = loadOcp(strat);
		if (ocp == null) {
			eventBus.post(new IBError(strat.name()
					+ " strategy's common properties are empty"));
			return;
		}

		// 0 means root order
		List<IBOrder> openedOrders = ibOrderDAO
				.findByStrategyAndParentOrderIdAndCloseDateIsNull(strat, 0);

		if (openedOrders.size() >= ocp.maxOrders) {
			eventBus.post(new IBError(strat.name()
					+ " strategy's max opened position limit exceeded: "
					+ ocp.maxOrders));
			return;
		}

		IOrderAssembler<T> orderAssembler = getAssembler(strat);

		Contract contract = orderAssembler.buildContract(so, ocp);
		for (Order order : orderAssembler.buildOrders(so, ocp,
				wrapper.nextOrderId())) {
			wrapper.placeOrder(order, contract, strat);
		}

		// the parameter has no effects
		wrapper.getClientSocket().reqIds(0);
	}

	public <T extends AbstractStrategyOrder> void placeOrders(
			List<T> convertedItems) {
		for (T order : convertedItems) {
			if (DateUtils.isSameDay(order.getStartDateTime().toDate(), DateTime.now().toDate())) {
				placeOrder(order);
			} else {
				eventBus.post(new IBError("Start date is not today for the '" + order.getTicker() + "' order"));
			}
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
