package hu.farago.ib.model.dao;

import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.order.strategy.enums.Strategy;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderCommonPropertiesDAO extends MongoRepository<OrderCommonProperties, Strategy> {

}
