package hu.farago.ib.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Formatters {

	// "20160118 23:59:59"
	private static final DateTimeFormatter TIMECONDITION = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	
	public static String formatToTimeCondition(DateTime dt) {
		return TIMECONDITION.print(dt);
	}
}
