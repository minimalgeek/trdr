package hu.farago.ib.order.strategy.cvts;

import java.time.DayOfWeek;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderCondition;
import com.ib.client.OrderConditionType;
import com.ib.client.OrderType;
import com.ib.client.TimeCondition;
import com.ib.client.Types.TimeInForce;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.OrderUtils;
import hu.farago.ib.order.strategy.IOrderAssembler;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.utils.Formatters;

@Component
public class CVTSAssemblerSimple implements IOrderAssembler<CVTSOrder> {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

//	@Value("${trdr.strategy.cvts.stopLossTarget}")
//	private double stopLossTarget;
//
//	@Value("${trdr.strategy.cvts.profitTarget.firstDay}")
//	private double profitTargetFirstDay;
//
//	@Value("${trdr.strategy.cvts.profitTarget.remainingDay}")
//	private double profitTargetRemainingDay;

	@Autowired
	private EWrapperImpl eWrapper;
	
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
	public List<Order> buildOrders(CVTSOrder so, OrderCommonProperties ocp) {

		List<Order> retList = Lists.newArrayList();

		Double position = ocp.getPositionSize() * ocp.getTargetVolatility()
				/ so.getHistoricalVolatility();
		Double quantity = Formatters.formatToTwoDecimalPlaces(position
				/ so.getPreviousDayClosePrice());

		final ActionType action = so.getAction();
		final double limitPrice = so.getLimitPrice();
		final String switchActionStr = OrderUtils.switchActionStr(action);
		double takeProfitLimitPrice = 0, remainingTakeProfitLimitPrice = 0, stopLossPrice = 0;
		if (action == ActionType.BUY) {
			takeProfitLimitPrice = limitPrice
					* (1.0 + ocp.firstDayTarget / 100.0);
			remainingTakeProfitLimitPrice = limitPrice
					* (1.0 + ocp.remainingDaysTarget / 100.0);
			stopLossPrice = limitPrice * (1.0 - ocp.stopLossTarget / 100.0);
		} else {
			takeProfitLimitPrice = limitPrice
					* (1.0 - ocp.firstDayTarget / 100);
			remainingTakeProfitLimitPrice = limitPrice
					* (1.0 - ocp.remainingDaysTarget / 100.0);
			stopLossPrice = limitPrice * (1.0 + ocp.stopLossTarget / 100.0);
		}
		takeProfitLimitPrice = Formatters.formatToTwoDecimalPlaces(takeProfitLimitPrice);
		remainingTakeProfitLimitPrice = Formatters.formatToTwoDecimalPlaces(remainingTakeProfitLimitPrice);
		stopLossPrice = Formatters.formatToTwoDecimalPlaces(stopLossPrice);

		// This will be our main or "parent" order
		int parentOrderId = eWrapper.nextOrderId();
		Order parent = new Order();
		parent.orderId(parentOrderId);
		parent.action(action.name());
		parent.orderType(ocp.getOrderType());
		parent.totalQuantity(quantity.intValue());
		parent.lmtPrice(limitPrice);
		parent.faProfile(ocp.getFaProfile());
		parent.tif(TimeInForce.DAY);
		parent.transmit(false);
		retList.add(parent);

		Order takeProfit = new Order();
		takeProfit.orderId(eWrapper.nextOrderId());
		takeProfit.action(switchActionStr);
		takeProfit.orderType(ocp.getOrderType()); // LMT?
		takeProfit.totalQuantity(quantity.intValue());
		takeProfit.lmtPrice(takeProfitLimitPrice);
		takeProfit.parentId(parentOrderId);
		takeProfit.transmit(false);
		takeProfit.faProfile(ocp.getFaProfile());
		takeProfit.tif(TimeInForce.GTC);
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

		Order timedTakeProfit = new Order();
		timedTakeProfit.orderId(eWrapper.nextOrderId());
		timedTakeProfit.action(switchActionStr);
		timedTakeProfit.orderType(ocp.getOrderType()); // LMT?
		timedTakeProfit.totalQuantity(quantity.intValue());
		timedTakeProfit.lmtPrice(remainingTakeProfitLimitPrice);
		timedTakeProfit.parentId(parentOrderId);
		timedTakeProfit.transmit(false);
		timedTakeProfit.faProfile(ocp.getFaProfile());
		timedTakeProfit.tif(TimeInForce.GTC);
		timedTakeProfit.conditionsCancelOrder(false);
		timedTakeProfit.conditionsIgnoreRth(true);
		timedTakeProfit.conditions().add(buildTimeCondition(startDT, true));
		retList.add(timedTakeProfit);

		Order nBarStop = new Order();
		nBarStop.orderId(eWrapper.nextOrderId());
		nBarStop.action(switchActionStr);
		nBarStop.orderType(OrderType.MKT);
		nBarStop.totalQuantity(quantity.intValue());
		nBarStop.parentId(parentOrderId);
		nBarStop.transmit(false);
		nBarStop.faProfile(ocp.getFaProfile());
		nBarStop.tif(TimeInForce.GTC);
		nBarStop.conditionsCancelOrder(false);
		nBarStop.conditionsIgnoreRth(true);
		nBarStop.conditions().add(buildTimeCondition(endDT, true));
		retList.add(nBarStop);

		Order stopLoss = new Order();
		stopLoss.orderId(eWrapper.nextOrderId());
		stopLoss.action(switchActionStr);
		stopLoss.orderType(OrderType.STP);
		// Stop trigger price
		stopLoss.auxPrice(stopLossPrice);
		stopLoss.totalQuantity(quantity.intValue());
		stopLoss.parentId(parentOrderId);
		stopLoss.transmit(true);
		stopLoss.faProfile(ocp.getFaProfile());
		stopLoss.tif(TimeInForce.GTC);
		retList.add(stopLoss);

		return retList;
	}

	private TimeCondition buildTimeCondition(DateTime startDT, boolean isMore) {
		TimeCondition timeCondition = (TimeCondition) OrderCondition
				.create(OrderConditionType.Time);
		timeCondition.isMore(isMore);
		timeCondition.time(Formatters.formatToTimeCondition(startDT));
		timeCondition.conjunctionConnection(true);
		return timeCondition;
	}

	private boolean isHolidayOrWeekend(DateTime dateTime) {
		return holidayManagerNYSE.isHoliday(dateTime.toGregorianCalendar())
				|| holidayManagerUS.isHoliday(dateTime.toGregorianCalendar())
				|| dateTime.getDayOfWeek() == DayOfWeek.SATURDAY.getValue()
				|| dateTime.getDayOfWeek() == DayOfWeek.SUNDAY.getValue();
	}
}
