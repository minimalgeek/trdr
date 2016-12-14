package hu.farago.ib.utils;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.farago.AbstractRootTest;

public class HolidaysTest extends AbstractRootTest {

	private static final DateTimeFormatter PATTERN = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	@Autowired
	private Holidays holidays;
	
	@Test
	public void testMonday() {
		DateTime dt = DateTime.parse("2016-12-12", PATTERN);
		DateTime prev = holidays.previousTradeDay(dt);
		
		Assert.assertTrue(DateUtils.isSameDay(prev.toDate(), DateTime.parse("2016-12-09", PATTERN).toDate()));
	}
	
	@Test
	public void testChristmas() {
		DateTime dt = DateTime.parse("2014-12-26", PATTERN);
		DateTime prev = holidays.previousTradeDay(dt);
		
		Assert.assertTrue(DateUtils.isSameDay(prev.toDate(), DateTime.parse("2014-12-24", PATTERN).toDate()));
	}
	
}
