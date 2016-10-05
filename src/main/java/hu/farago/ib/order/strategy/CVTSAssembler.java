package hu.farago.ib.order.strategy;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.ib.order.OrderUtils;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.utils.Formatters;
import hu.farago.web.component.order.dto.CVTSOrder;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderCondition;
import com.ib.client.OrderConditionType;
import com.ib.client.TimeCondition;
import com.ib.client.Types.TimeInForce;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;

@Component
public class CVTSAssembler implements IOrderAssembler<CVTSOrder> {

	@Value("${trdr.strategy.cvts.stopLossTarget}")
	private double stopLossTarget;
	
	@Value("${trdr.strategy.cvts.profitTarget.firstDay}")
	private double profitTargetFirstDay;

	@Value("${trdr.strategy.cvts.profitTarget.remainingDay}")
	private double profitTargetRemainingDay;
	
	private HolidayManager holidayManager = HolidayManager.getInstance(HolidayCalendar.NYSE);

	@Override
	public Contract buildContract(CVTSOrder so, OrderCommonProperties ocp) {
		Contract contract = new Contract();
		contract.conid(0);
		contract.symbol(so.getTicker());
		contract.secType(ocp.getSecType());
		contract.currency(ocp.getCurrency());
		contract.exchange(ocp.getExchange());
		contract.primaryExch(ocp.getPrimaryExchange());
		return contract;
	}

	@Override
	public List<Order> buildOrders(CVTSOrder so, OrderCommonProperties ocp,
			int parentOrderId) {

		List<Order> retList = Lists.newArrayList();
		
		Double position = ocp.getPositionSize() * ocp.getTargetVolatility()
				/ so.getHistoricalVolatility();
		Double quantity = position / so.getPreviousDayClosePrice();
		
		final ActionType action = so.getAction();
		final double limitPrice = so.getLimitPrice();
		final String switchActionStr = OrderUtils.switchActionStr(action);

		// This will be our main or "parent" order
		Order parent = new Order();
		parent.orderId(parentOrderId);
		parent.action(action.name());
		parent.orderType(ocp.getOrderType());
		parent.totalQuantity(quantity.intValue());
		parent.lmtPrice(limitPrice);
		parent.faProfile(ocp.getFaProfile());
		// The parent and children orders will need this attribute set to false
		// to prevent accidental executions.
		// The LAST CHILD will have it set to true.
		parent.transmit(false);
		retList.add(parent);

		double takeProfitLimitPrice = 0, remainingTakeProfitLimitPrice = 0, stopLossPrice = 0;
		if (action == ActionType.BUY) {
			takeProfitLimitPrice = limitPrice
					* (1.0 + profitTargetFirstDay / 100.0);
			remainingTakeProfitLimitPrice = limitPrice
					* (1.0 + profitTargetRemainingDay / 100.0);
			stopLossPrice = limitPrice * (1.0 - stopLossTarget / 100.0);
		} else {
			takeProfitLimitPrice = limitPrice
					* (1.0 - profitTargetFirstDay / 100);
			remainingTakeProfitLimitPrice = limitPrice
					* (1.0 - profitTargetRemainingDay / 100.0);
			stopLossPrice = limitPrice * (1.0 + stopLossTarget / 100.0);
		}

		Order takeProfit = new Order();
		takeProfit.orderId(parent.orderId() + 1);
		takeProfit.action(switchActionStr);
		takeProfit.orderType(ocp.getOrderType());
		takeProfit.totalQuantity(quantity.intValue());
		takeProfit.lmtPrice(takeProfitLimitPrice);
		takeProfit.parentId(parentOrderId);
		takeProfit.transmit(false);
		takeProfit.faProfile(ocp.getFaProfile());
		retList.add(takeProfit);
		
		DateTime startDT = DateTime.now().withTimeAtStartOfDay();
		boolean allTakeProfitSet = false;
		int nrOfTakeProfitOrders = 0;
        
		while (!allTakeProfitSet) {
        	startDT = startDT.plusDays(1);
        	if (holidayManager.isHoliday(startDT.toCalendar(Locale.US))) {
        		continue;
        	}
        	
        	TimeCondition timeConditionStart = (TimeCondition)OrderCondition.create(OrderConditionType.Time);
            timeConditionStart.isMore(true);
            timeConditionStart.time(Formatters.formatToTimeCondition(startDT));
            timeConditionStart.conjunctionConnection(true);
            
            TimeCondition timeConditionEnd = (TimeCondition)OrderCondition.create(OrderConditionType.Time);
            timeConditionEnd.isMore(false);
            timeConditionEnd.time(Formatters.formatToTimeCondition(startDT.plusDays(1)));
            timeConditionEnd.conjunctionConnection(true);
            
            Order timedTakeProfit = new Order();
            timedTakeProfit.orderId(parent.orderId() + 2 + nrOfTakeProfitOrders);
            timedTakeProfit.action(switchActionStr);
            timedTakeProfit.orderType(ocp.getOrderType());
            timedTakeProfit.totalQuantity(quantity.intValue());
            timedTakeProfit.lmtPrice(remainingTakeProfitLimitPrice);
            timedTakeProfit.parentId(parentOrderId);
            timedTakeProfit.transmit(false);
            timedTakeProfit.faProfile(ocp.getFaProfile());
            timedTakeProfit.tif(TimeInForce.GTC);
    		
            timedTakeProfit.conditionsCancelOrder(true);
            timedTakeProfit.conditions().add(timeConditionStart);
            timedTakeProfit.conditions().add(timeConditionEnd);
    		
    		retList.add(timedTakeProfit);
    		
    		nrOfTakeProfitOrders++;
    		if (nrOfTakeProfitOrders == ocp.barStop - 1) {
    			allTakeProfitSet = true;
    		}
        }

		
		Order stopLoss = new Order();
		stopLoss.orderId(parent.orderId() + 1 + ocp.barStop);
		stopLoss.action(switchActionStr);
		stopLoss.orderType("STP");
		// Stop trigger price
		stopLoss.auxPrice(stopLossPrice);
		stopLoss.totalQuantity(quantity.intValue());
		stopLoss.parentId(parentOrderId);
		// In this case, the low side order will be the last child being sent.
		// Therefore, it needs to set this attribute to true
		// to activate all its predecessors
		stopLoss.transmit(true);
		stopLoss.faProfile(ocp.getFaProfile());
		retList.add(stopLoss);

		return retList;
	}
}
