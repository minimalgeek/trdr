package hu.farago.ib.model.dao;

import hu.farago.ib.model.dto.OpenOrder;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpenOrderDAO  extends MongoRepository<OpenOrder, Integer> {

}
