package hu.farago.ib.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Formatters {

	// "20160118 23:59:59"
	private static final DateTimeFormatter TIMECONDITION = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
	private static final DecimalFormat TWO_DECIMALS = new DecimalFormat("###.##", DecimalFormatSymbols.getInstance(Locale.US)); 
	
	public static String formatToTimeCondition(DateTime dt) {
		return TIMECONDITION.print(dt);
	}
	
	public static Double formatToTwoDecimalPlaces(Double numb) {
		return Double.valueOf(TWO_DECIMALS.format(numb));
	}
}
