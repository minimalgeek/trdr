package hu.farago.ib.model.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.order.strategy.enums.Strategy;

public interface IBOrderDAO extends MongoRepository<IBOrder, Integer>, QueryDslPredicateExecutor<IBOrder> {

	List<IBOrder> findByStrategyAndCloseDateIsNull(Strategy strategy);
	List<IBOrder> findByStrategyAndParentOrderIdAndCloseDateIsNull(Strategy strategy, Integer parentOrderId);
	List<IBOrder> findByParentOrderId(Integer parentOrderId);
	
}
