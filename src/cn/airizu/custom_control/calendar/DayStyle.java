package cn.airizu.custom_control.calendar;

import java.util.Calendar;

public class DayStyle {
	public final static String[] vecStrWeekDayNames = getWeekNames();
	
	private static String[] getWeekNames() {
		String[] vec = new String[10];
		
		vec[Calendar.SUNDAY] = "周日";
		vec[Calendar.MONDAY] = "周一";
		vec[Calendar.TUESDAY] = "周二";
		vec[Calendar.WEDNESDAY] = "周三";
		vec[Calendar.THURSDAY] = "周四";
		vec[Calendar.FRIDAY] = "周五";
		vec[Calendar.SATURDAY] = "周六";
		
		return vec;
	}
	
	public static String getWeekName(int weekKey) {
		return vecStrWeekDayNames[weekKey];
	}
	
	public static int getWeekKeyAfterAdjust(int currentlyWeekKey, int iFirstDayOfWeek) {
		int weekKeyForAdjust = -1;
		
		if (iFirstDayOfWeek == Calendar.MONDAY) {
			weekKeyForAdjust = currentlyWeekKey + Calendar.MONDAY;
			
			if (weekKeyForAdjust > Calendar.SATURDAY) {
				weekKeyForAdjust = Calendar.SUNDAY;
			}
		}
		
		if (iFirstDayOfWeek == Calendar.SUNDAY) {
			weekKeyForAdjust = currentlyWeekKey + Calendar.SUNDAY;
		}
		
		return weekKeyForAdjust;
	}
}
