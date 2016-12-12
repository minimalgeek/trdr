package hu.farago.ib.order.strategy.cvts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.farago.ib.model.dto.order.AbstractStrategyOrderQueue;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.AbstractFactoryForOrder;
import hu.farago.ib.order.strategy.IOrderAssembler;

@Component
public class CVTSFactory extends AbstractFactoryForOrder<CVTSOrder> {

	@Autowired
	private CVTSAssemblerSimple cvtsAssembler;
	
	@Autowired
	private CVTSQueue queue;
	
	@Override
	public IOrderAssembler<CVTSOrder> getAssembler() {
		return cvtsAssembler;
	}

	@Override
	public AbstractStrategyOrderQueue<CVTSOrder> getQueue() {
		return queue;
	}

}
