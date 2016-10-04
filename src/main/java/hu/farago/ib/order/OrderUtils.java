package hu.farago.ib.order;

import hu.farago.ib.order.strategy.enums.ActionType;

public class OrderUtils {

	public static String switchActionStr(ActionType action) {
		return switchAction(action).name();
	}

	public static ActionType switchAction(ActionType action) {
		return action == ActionType.BUY ? ActionType.SELL : ActionType.BUY;
	}
	
}
