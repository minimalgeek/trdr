package hu.farago.ib.order.strategy.cvts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import hu.farago.ib.model.dao.CVTSOrderDAO;
import hu.farago.ib.model.dto.order.AbstractStrategyOrderQueue;
import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.AbstractFactoryForOrder;
import hu.farago.ib.order.strategy.IOrderAssembler;

@Component
public class CVTSFactory extends AbstractFactoryForOrder<CVTSOrder> {

	@Autowired
	private CVTSAssemblerSimple cvtsAssembler;
	
	@Autowired
	private CVTSQueue queue;
	
	@Autowired
	private CVTSOrderDAO dao;
	
	@Override
	public IOrderAssembler<CVTSOrder> getAssembler() {
		return cvtsAssembler;
	}

	@Override
	public AbstractStrategyOrderQueue<CVTSOrder> getQueue(OrderCommonProperties ocp) {
		queue.setOcp(ocp);
		return queue;
	}
	
	@Override
	public MongoRepository<CVTSOrder, ?> getRepository() {
		return dao;
	}

}
