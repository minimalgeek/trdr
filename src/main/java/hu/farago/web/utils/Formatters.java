package hu.farago.web.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Formatters {

	private static NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
	private static DecimalFormat formatter = (DecimalFormat)nf;
	private static final DateTimeFormatter simpleYMD = DateTimeFormat
			.forPattern("yyyy.MM.dd").withZone(DateTimeZone.UTC);
	
	public static double parseDouble(String numb) throws ParseException {
		return formatter.parse(numb).doubleValue();
	}
	
	public static DateTime parseYYYY_MM_DD(String dateStr) {
		return simpleYMD.parseDateTime(dateStr);
	}
	
}
