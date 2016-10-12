package hu.farago.ib.service;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.market.StockQuery;
import hu.farago.ib.utils.Formatters;

import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ib.client.Contract;
import com.ib.client.Types.SecType;

@Component
public class StockPriceService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EWrapperImpl wrapper;
	
	public void getStockPrices(StockQuery query) {
		LOGGER.info("getStockPrices");
		wrapper.resetOhlcList();
		
		String queryTime = Formatters.formatToTimeCondition(query.to);
		int days = Days.daysBetween(query.from.toLocalDate(), query.to.toLocalDate()).getDays();
		
		Contract contract = new Contract();
		contract.conid(0);
		contract.symbol(query.ticker);
		// TODO extract it!
		contract.secType(SecType.STK);
		contract.currency("USD");
		contract.exchange("SMART");
		contract.primaryExch("NASDAQ");
		
		wrapper.getClientSocket().reqHistoricalData(wrapper.nextTickerId(), contract, queryTime, days + " D", "1 day", "MIDPOINT", 1, 2, null);
	}
	
}
