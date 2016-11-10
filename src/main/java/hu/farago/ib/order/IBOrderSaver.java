package hu.farago.ib.order;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.IBOrderStatus;

import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.CommissionReport;
import com.ib.client.Execution;

@Component
public class IBOrderSaver {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EventBus eventBus;

	@Autowired
	private IBOrderDAO ooDAO;

	private static final List<String> CLOSE_ORDER_STATUS = Lists.newArrayList("ApiCanceled", "Cancelled", "Filled");
	
	@PostConstruct
	public void init() {
		eventBus.register(this);
	}
	
	@Subscribe
	public void orderChanged(IBOrder ibOrder) {
		IBOrder older = ooDAO.findOne(ibOrder.getOrderId());
		if (older == null) {
			ibOrder.setOpenDate(DateTime.now());
			if (ibOrder.getParentOrderId() == null) {
				ibOrder.setParentOrderId(ibOrder.getOrder().parentId());
			}
			ooDAO.save(ibOrder);
		} else {
			older.setOrder(ibOrder.getOrder());
			older.setContract(ibOrder.getContract());
			older.setOrderState(ibOrder.getOrderState());
			
			updateCloseIfNecessary(ibOrder.getOrderState().status().name(), older);
			
			ooDAO.save(older);
		}
	}
	
	@Subscribe
	public void orderStatusUpdated(IBOrderStatus ibOrderStatus) {
		LOGGER.info("orderStatusUpdated: " + ibOrderStatus.toString());
		
		IBOrder older = ooDAO.findOne(ibOrderStatus.getOrderId());
		
		if (older != null) {
			older.setLastOrderStatus(ibOrderStatus);
			
			updateCloseIfNecessary(ibOrderStatus.getStatus(), older);
			ooDAO.save(older);
		}
	}

	private void updateCloseIfNecessary(String status,
			IBOrder older) {
		if (CLOSE_ORDER_STATUS.contains(status)) {
			older.setCloseDate(DateTime.now());
		}
	}
	
	@Subscribe
	public void execution(Execution exec) {
		IBOrder older = ooDAO.findOne(exec.orderId());
		if (older != null) {
			older.setLastExecution(exec);
			older.setLastExecId(exec.execId());
			ooDAO.save(older);
		}
	}
	
	@Subscribe
	public void commission(CommissionReport commission) {
		IBOrder older = ooDAO.findByLastExecId(commission.m_execId);
		if (older != null) {
			older.setPnl(commission.m_realizedPNL);
			ooDAO.save(older);
		}
	}

}
