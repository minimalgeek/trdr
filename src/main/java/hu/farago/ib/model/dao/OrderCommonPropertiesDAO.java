package hu.farago.ib.model.dao;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.strategy.enums.Strategy;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderCommonPropertiesDAO extends MongoRepository<OrderCommonProperties, Strategy> {

}
