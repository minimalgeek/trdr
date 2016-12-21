package hu.farago.ib.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.IBOrderStatus;
import hu.farago.ib.model.dto.order.IBOrderStatusWrap;

@Component
public class IBOrderStatusWrapBuilder {

	private EventBus eventBus;
	private IBOrderDAO ibOrderDAO;
	
	@Autowired
	public IBOrderStatusWrapBuilder(EventBus eventBus, IBOrderDAO ibOrderDAO) {
		this.eventBus = eventBus;
		this.ibOrderDAO = ibOrderDAO;
		this.eventBus.register(this);
	}
	
	@Subscribe
	private void buildAndSendIBOrderStatusWrap(IBOrderStatus orderStatus) {
		IBOrder order = ibOrderDAO.findOne(orderStatus.getOrderId());
		IBOrderStatusWrap wrap = new IBOrderStatusWrap(orderStatus, order.getOrder(), order.getContract());
		eventBus.post(wrap);
	}
	
}
