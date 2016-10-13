package hu.farago.ib.order;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.order.strategy.enums.Strategy;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.CommissionReport;
import com.ib.client.Execution;

@Component
public class IBOrderSaver {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private IBOrderDAO ooDAO;

	@PostConstruct
	public void init() {
		eventBus.register(this);
	}

	@Subscribe
	public void orderChanged(IBOrder ibOrder) {
		IBOrder older = ooDAO.findOne(ibOrder.getOrderId());
		String algoId = ibOrder.getOrder().algoId();
		if (older != null) {
			ibOrder.setStrategy(older.getStrategy());
			ibOrder.setOpenDate(older.getOpenDate());
		} else {
			if (StringUtils.isNotEmpty(algoId)) {
				ibOrder.setStrategy(Strategy.valueOf(algoId));
			}
			ibOrder.setOpenDate(DateTime.now());
		}

		ooDAO.save(ibOrder);
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
