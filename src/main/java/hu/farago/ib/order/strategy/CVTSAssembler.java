package hu.farago.ib.order.strategy;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.ib.order.OrderUtils;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.utils.Formatters;
import hu.farago.web.component.order.dto.CVTSOrder;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderCondition;
import com.ib.client.OrderConditionType;
import com.ib.client.TagValue;
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

	@SuppressWarnings("deprecation")
	private HolidayManager holidayManagerNYSE = HolidayManager
			.getInstance(HolidayCalendar.NYSE);
	@SuppressWarnings("deprecation")
	private HolidayManager holidayManagerUS = HolidayManager
			.getInstance(HolidayCalendar.UNITED_STATES);

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

		// This will be our main or "parent" order
		Order parent = new Order();
		parent.orderId(parentOrderId);
		parent.action(action.name());
		parent.orderType(ocp.getOrderType());
		parent.totalQuantity(quantity.intValue());
		parent.lmtPrice(limitPrice);
		parent.faProfile(ocp.getFaProfile());
		parent.tif(TimeInForce.GTC);
		// The parent and children orders will need this attribute set to false
		// to prevent accidental executions.
		// The LAST CHILD will have it set to true.
		parent.transmit(false);
		retList.add(parent);

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

		DateTime startDT = DateTime.now().withTimeAtStartOfDay().plusDays(1);
		DateTime endDT = startDT; // DateTime objects are immutable
		int nrOfRunningDays = 0;

		while (nrOfRunningDays < ocp.barStop - 1) {
			if (isHolidayOrWeekend(startDT)) {
				startDT = startDT.plusDays(1);
				endDT = startDT;
				continue;
			}
			endDT = endDT.plusDays(1);
			if (isHolidayOrWeekend(endDT)) {
				continue;
			}

			nrOfRunningDays++;
		}

		TimeCondition timeConditionStart = buildTimeCondition(startDT, true);
		TimeCondition timeConditionEnd = buildTimeCondition(endDT, false);

		Order timedTakeProfit = new Order();
		timedTakeProfit.orderId(parent.orderId() + 2);
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

		Order vwapClose = new Order();
		fillVwapParams(vwapClose, 0.2, "09:00:00 CET", "16:00:00 CET", true,
				true);
		vwapClose.orderId(parent.orderId() + 3);
		vwapClose.action(switchActionStr);
		vwapClose.orderType("MKT");
		vwapClose.totalQuantity(quantity.intValue());
		vwapClose.parentId(parentOrderId);
		vwapClose.transmit(false);
		vwapClose.faProfile(ocp.getFaProfile());
		vwapClose.tif(TimeInForce.GTC);
		vwapClose.conditionsCancelOrder(true);
		vwapClose.conditions().add(buildTimeCondition(endDT, true));
		retList.add(vwapClose);

		Order stopLoss = new Order();
		stopLoss.orderId(parent.orderId() + 4);
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
		stopLoss.tif(TimeInForce.GTC);
		retList.add(stopLoss);

		return retList;
	}

	private TimeCondition buildTimeCondition(DateTime startDT, boolean isMore) {
		TimeCondition timeConditionStart = (TimeCondition) OrderCondition
				.create(OrderConditionType.Time);
		timeConditionStart.isMore(isMore);
		timeConditionStart.time(Formatters.formatToTimeCondition(startDT));
		timeConditionStart.conjunctionConnection(true);
		return timeConditionStart;
	}

	private boolean isHolidayOrWeekend(DateTime dateTime) {
		return holidayManagerNYSE.isHoliday(dateTime.toGregorianCalendar())
				|| holidayManagerUS.isHoliday(dateTime.toGregorianCalendar())
				|| dateTime.getDayOfWeek() == DayOfWeek.SATURDAY.getValue()
				|| dateTime.getDayOfWeek() == DayOfWeek.SUNDAY.getValue();
	}

	private void fillVwapParams(Order baseOrder, double maxPctVol,
			String startTime, String endTime, boolean allowPastEndTime,
			boolean noTakeLiq) {
		baseOrder.algoStrategy("Vwap");
		baseOrder.algoParams(new ArrayList<TagValue>());
		baseOrder.algoParams().add(
				new TagValue("maxPctVol", String.valueOf(maxPctVol)));
		baseOrder.algoParams().add(new TagValue("startTime", startTime));
		baseOrder.algoParams().add(new TagValue("endTime", endTime));
		baseOrder.algoParams().add(
				new TagValue("allowPastEndTime", allowPastEndTime ? "1" : "0"));
		baseOrder.algoParams().add(
				new TagValue("noTakeLiq", noTakeLiq ? "1" : "0"));
	}
}
