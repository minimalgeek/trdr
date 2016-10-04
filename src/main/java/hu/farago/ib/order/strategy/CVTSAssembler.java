package hu.farago.ib.order.strategy;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.order.OrderUtils;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.web.component.order.dto.CVTSOrder;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ib.client.Contract;
import com.ib.client.Order;

@Component
public class CVTSAssembler implements IOrderAssembler<CVTSOrder> {

	@Value("${trdr.strategy.cvts.stopLossTarget}")
	private double stopLossTarget;
	
	@Value("${trdr.strategy.cvts.profitTarget.firstDay}")
	private double profitTargetFirstDay;

	@Value("${trdr.strategy.cvts.profitTarget.remainingDay}")
	private double profitTargetRemainingDay;

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

		Double position = ocp.getPositionSize() * ocp.getTargetVolatility()
				/ so.getHistoricalVolatility();
		Double quantity = position / so.getPreviousDayClosePrice();
		
		ActionType action = so.getAction();
		double limitPrice = so.getLimitPrice();

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

		double takeProfitLimitPrice = 0, stopLossPrice = 0;
		if (action == ActionType.BUY) {
			takeProfitLimitPrice = limitPrice
					* (1.0 + profitTargetFirstDay / 100.0);
			stopLossPrice = limitPrice * (1.0 - stopLossTarget / 100.0);
		} else {
			takeProfitLimitPrice = limitPrice
					* (1.0 - profitTargetFirstDay / 100);
			stopLossPrice = limitPrice * (1.0 + stopLossTarget / 100.0);
		}

		Order takeProfit = new Order();
		takeProfit.orderId(parent.orderId() + 1);
		takeProfit.action(OrderUtils.switchActionStr(action));
		takeProfit.orderType(ocp.getOrderType());
		takeProfit.totalQuantity(quantity.intValue());
		takeProfit.lmtPrice(takeProfitLimitPrice);
		takeProfit.parentId(parentOrderId);
		takeProfit.transmit(false);
		takeProfit.faProfile(ocp.getFaProfile());

		Order stopLoss = new Order();
		stopLoss.orderId(parent.orderId() + 2);
		stopLoss.action(OrderUtils.switchActionStr(action));
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

		return Lists.newArrayList(parent, takeProfit, stopLoss);

		// Order order = new Order();
		//
		// order.action(so.getAction().name());
		// order.orderType(ocp.getOrderType());
		//
		// Double position =
		// ocp.getPositionSize()*ocp.getTargetVolatility()/so.getHistoricalVolatility();
		// Double quantity = position/so.getPreviousDayClosePrice();
		//
		// order.totalQuantity(quantity.intValue());
		// order.lmtPrice(so.getLimitPrice());
		// order.faProfile(ocp.getFaProfile());
		// return Lists.newArrayList(order);
	}
}
