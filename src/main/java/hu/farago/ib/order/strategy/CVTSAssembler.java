package hu.farago.ib.order.strategy;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.order.IOrderAssembler;
import hu.farago.web.component.order.dto.CVTSOrder;

import org.springframework.stereotype.Component;

import com.ib.client.Contract;
import com.ib.client.Order;

@Component
public class CVTSAssembler implements IOrderAssembler<CVTSOrder> {
	
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
	public Order buildOrder(CVTSOrder so, OrderCommonProperties ocp) {
		Order order = new Order();
		
		order.action(so.getAction().name());
		order.orderType(ocp.getOrderType());
		
		Double position = ocp.getPositionSize()*ocp.getTargetVolatility()/so.getHistoricalVolatility();
		Double quantity = position/so.getPreviousDayClosePrice();
		
		order.totalQuantity(quantity.intValue());
		order.lmtPrice(so.getLimitPrice());
		order.faProfile(ocp.getFaProfile());
		return order;
	}

}
