package hu.farago.ib.model.dao;

import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.order.strategy.enums.Strategy;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IBOrderDAO  extends MongoRepository<IBOrder, Integer> {

	List<IBOrder> findByStrategyAndOpenDateBetween(Strategy strategy, DateTime begin, DateTime end);
	IBOrder findByLastExecId(String execId);
	List<IBOrder> findByStrategyAndCloseDateIsNull(Strategy strategy);
	List<IBOrder> findByStrategyAndParentOrderIdAndCloseDateIsNull(Strategy strategy, int parentOrderId);
	List<IBOrder> findByParentOrderId(int parentOrderId);
	
}
