package hu.farago.ib.service;

import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.ib.client.Contract;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dao.OrderCommonPropertiesDAO;
import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.AbstractStrategyOrderQueue;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.order.AbstractFactoryForOrder;
import hu.farago.ib.order.strategy.IOrderAssembler;
import hu.farago.ib.order.strategy.cvts.CVTSFactory;
import hu.farago.ib.order.strategy.enums.Strategy;

@Component
public class OrderService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EventBus eventBus;

	@Autowired
	private EWrapperImpl wrapper;

	@Autowired
	private OrderCommonPropertiesDAO ocpDAO;

	// strategy factories
	@Autowired
	private CVTSFactory cvtsFactory;

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

		if (!candidateStartDateIsYesterday(so)) {
			eventBus.post(new IBError("Start date is not yesterday for the '" + so.getTicker() + "' order"));
			return;
		}

		Strategy strat = so.strategy();
		AbstractFactoryForOrder<T> factory = getFactory(strat);

		OrderCommonProperties ocp = loadOcp(strat);
		if (ocp == null) {
			eventBus.post(new IBError(strat.name() + " strategy's common properties are empty"));
			return;
		}

		IOrderAssembler<T> orderAssembler = factory.getAssembler();
		AbstractStrategyOrderQueue<T> queue = factory.getQueue(ocp);
		
		// save it, build contract from it, and put orders into a queue for further processing
		factory.getRepository().save(so);
		Contract contract = orderAssembler.buildContract(so, ocp);
		queue.addOrder(orderAssembler.buildOrders(so, ocp), contract);

		wrapper.reqIds();
	}

	public <T extends AbstractStrategyOrder> void placeOrders(List<T> convertedItems) {
		for (T order : convertedItems) {
			placeOrder(order);
		}
	}

	private <T extends AbstractStrategyOrder> boolean candidateStartDateIsYesterday(T order) {
		return DateUtils.isSameDay(order.getStartDateTime().toDate(), DateTime.now().minusDays(1).toDate());
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractStrategyOrder> AbstractFactoryForOrder<T> getFactory(Strategy strat) {
		switch (strat) {
		case CVTS:
			return (AbstractFactoryForOrder<T>) cvtsFactory;
		default:
			return null;
		}
	}

	public void reqAllOpenOrders() {
		wrapper.reqAllOpenOrders();
	}

	public void reqExecutions() {
		wrapper.reqExecutions();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractStrategyOrder> List<T> listOrders(Strategy strat) {
		return (List<T>) getFactory(strat).getRepository().findAll();
	}
}
