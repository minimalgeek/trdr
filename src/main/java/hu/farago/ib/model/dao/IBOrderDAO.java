package hu.farago.ib.model.dao;

import hu.farago.ib.model.dto.IBOrder;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IBOrderDAO  extends MongoRepository<IBOrder, Integer> {

}
