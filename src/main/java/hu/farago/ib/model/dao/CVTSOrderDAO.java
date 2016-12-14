package hu.farago.ib.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import hu.farago.ib.model.dto.order.strategy.CVTSOrder;

public interface CVTSOrderDAO extends MongoRepository<CVTSOrder, Integer> {

}
