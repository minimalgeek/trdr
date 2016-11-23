package hu.farago.ib.model.dto.equity;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import hu.farago.ib.order.strategy.enums.Strategy;

public class EquityQuery {

	public DateTime from;
	public DateTime to;
	@NotNull
	public Strategy strategy;

//	@AssertTrue(message = "'From' must be before 'To'")
//	public boolean isValidRange() {
//		if (from == null || to == null) {
//			return true;
//		}
//		return from.compareTo(to) <= 0;
//	}

	public DateTime getFrom() {
		return from;
	}

	public void setFrom(DateTime from) {
		this.from = from;
	}

	public DateTime getTo() {
		return to;
	}

	public void setTo(DateTime to) {
		this.to = to;
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
