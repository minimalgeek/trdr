package hu.farago.ib.utils;

import java.time.DayOfWeek;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;

@Component
public class Holidays {

	@SuppressWarnings("deprecation")
	private HolidayManager holidayManagerNYSE = HolidayManager
			.getInstance(HolidayCalendar.NYSE);
	@SuppressWarnings("deprecation")
	private HolidayManager holidayManagerUS = HolidayManager
			.getInstance(HolidayCalendar.UNITED_STATES);
	
	public synchronized boolean isHolidayOrWeekend(DateTime dateTime) {
		return holidayManagerNYSE.isHoliday(dateTime.toGregorianCalendar())
				|| holidayManagerUS.isHoliday(dateTime.toGregorianCalendar())
				|| dateTime.getDayOfWeek() == DayOfWeek.SATURDAY.getValue()
				|| dateTime.getDayOfWeek() == DayOfWeek.SUNDAY.getValue();
	}
	
	public synchronized DateTime previousTradeDay(DateTime dateTime) {
		DateTime ret = dateTime;
		do {
			ret = ret.minusDays(1);
		} while (isHolidayOrWeekend(ret));
		
		return ret;
	}
	
}
