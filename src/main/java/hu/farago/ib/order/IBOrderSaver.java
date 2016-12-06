package hu.farago.ib.order;

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
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.model.dto.order.IBOrder.ExecutionAndCommission;
import hu.farago.ib.model.dto.order.IBOrderStatus;
import hu.farago.ib.model.dto.order.QIBOrder;
import hu.farago.ib.model.dto.order.QIBOrder_ExecutionAndCommission;
import hu.farago.ib.order.calculator.AccountingProfitCalculator;

@Component
public class IBOrderSaver {

	private static final String CANCELLED = "Cancelled";
	private static final String API_CANCELED = "ApiCanceled";
	private static final String FILLED = "Filled";

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EventBus eventBus;

	@Autowired
	private IBOrderDAO ooDAO;
	
	@Autowired
	private AccountingProfitCalculator profitCalculator;

	private static final List<String> CLOSE_ORDER_STATUS = Lists.newArrayList(API_CANCELED, CANCELLED, FILLED);
	
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
			LOGGER.info("Closing IBOrder: " + older.getOrderId());
			older.setCloseDate(DateTime.now());
			profitCalculator.calculateProfit(older);
		}
	}
	
	@Subscribe
	public void execution(Execution exec) {
		QIBOrder qIBOrder = QIBOrder.iBOrder;
		Predicate ibPredicate = qIBOrder.orderId.eq(exec.orderId()).and(qIBOrder.executions.any().execId.eq(exec.execId()));
		
		if (ooDAO.count(ibPredicate) == 0) {
			IBOrder order = ooDAO.findOne(qIBOrder.orderId.eq(exec.orderId()));
			if (order != null) {
				ExecutionAndCommission ec = order.new ExecutionAndCommission();
				ec.avgPrice = exec.avgPrice();
				ec.cumQty = exec.cumQty();
				ec.execId = exec.execId();
				ec.orderId = exec.orderId();
				ec.price = exec.price();
				ec.shares = exec.shares();
				ec.time = exec.time();
				order.getExecutions().add(ec);
				if (exec.cumQty() >= order.getOrder().totalQuantity()) {
					updateCloseIfNecessary(FILLED, order);
				}
				ooDAO.save(order);
			}
		}
	}
	
	@Subscribe
	public void commission(CommissionReport commission) {
		QIBOrder qIBOrder = QIBOrder.iBOrder;
		Predicate ibPredicate = qIBOrder.executions.any().execId.eq(commission.m_execId);
		
		IBOrder order = ooDAO.findOne(ibPredicate);
		
		if (order != null) {
			ExecutionAndCommission eac = order.getExecutions().stream().filter((ex) -> ex.execId.equals(commission.m_execId)).findFirst().get();
			eac.realizedPNL = commission.m_realizedPNL;
			ooDAO.save(order);
		}
	}
}
