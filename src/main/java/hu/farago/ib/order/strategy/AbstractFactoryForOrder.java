package hu.farago.ib.order.strategy;

import org.springframework.data.mongodb.repository.MongoRepository;

import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.model.dto.order.OrderCommonProperties;

public abstract class AbstractFactoryForOrder<T extends AbstractStrategyOrder> {

	public abstract IOrderAssembler<T> getAssembler();
	public abstract AbstractStrategyOrderQueue<T> getQueue(OrderCommonProperties ocp);
	public abstract MongoRepository<T, ?> getRepository();
	
}
