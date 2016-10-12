package hu.farago.ib.service;

import hu.farago.ib.model.dao.IBOrderDAO;
import hu.farago.ib.model.dto.equity.EquityQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EquityService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IBOrderDAO orderDAO;
	
	public void getOrders(EquityQuery query) {
		LOGGER.info("getOrders: " + query.toString());
	}
	
}
