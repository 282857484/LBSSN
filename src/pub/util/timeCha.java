package pub.util;

import java.util.Calendar;


public class timeCha {

	private static String TimeStart = "-1";
	private static String TimeEnd = "-1";

	// public static void main(String[] args) {
	// TimeStart = FormatTime.getFormatTime();
	// TimeEnd = "201408311005142";
	//
	// System.out.println("TimeStart = " + TimeStart);
	// System.out.println("TimeEnd   = " + TimeEnd);
	// System.out.println(TimeCha(TimeStart,TimeEnd));
	// System.out.println(MillisToWord(TimeCha(TimeStart,TimeEnd)));
	// }

	/**
	 * TimeEnd 减去 TimeStart
	 * 
	 * @param TimeStart
	 * @param TimeEnd
	 * @return
	 */
	public static long TimeCha(String TimeStart, String TimeEnd) {

		return TimeToMillis(TimeEnd) - TimeToMillis(TimeStart);
	}

	public static String MillisToWord(long millis) {
		StringBuilder sb = new StringBuilder();
		final long OnDayInMinllis = 1000 * 60 * 60 * 24;
		final long OnHourInMinllis = 1000 * 60 * 60;
		final long OnMinuteInMillis = 1000 * 60;
		// System.out.println("aaa:  " + (OnDayInMinllis * 31));
		if (millis > (OnDayInMinllis * 365)) {
			return (millis / (OnDayInMinllis * 365)) + "年前 ";
		} else if (millis > (OnDayInMinllis * 31)) {
			return (millis / (OnDayInMinllis * 31)) + "个月前 ";
		} else if (millis > OnDayInMinllis) {
			return millis / OnDayInMinllis + "天前 ";
		} else if (millis > OnHourInMinllis) {
			return millis / OnHourInMinllis + "小时前 ";
		} else if (millis > OnMinuteInMillis) {
			return millis / OnMinuteInMillis + "分钟前 ";
		} else {
			return "刚刚 ";
		}
	}

	/**
	 * 用在活动显示时间为：2小时后
	 * 
	 * @param millis
	 * @return
	 */
	public static String MillisToWordInActivity(long millis) {
		StringBuilder sb = new StringBuilder();
		final long OnDayInMinllis = 1000 * 60 * 60 * 24;
		final long OnHourInMinllis = 1000 * 60 * 60;
		final long OnMinuteInMillis = 1000 * 60;
		// System.out.println("aaa:  " + (OnDayInMinllis * 31));
		// 得到间隔的分钟数
		long abc = 0;
		if (millis > OnMinuteInMillis) {
			abc = millis / OnMinuteInMillis;
		}
		int day = (int) (abc / (60 * 24));
		int hourAbc = (int) (abc - day * (60 * 24));
		int hour = (int) (hourAbc / 60);
		int minute = (int) (hourAbc - hour * 60);

		if (day >= 1) {
			return day + "天" + hour + "小时" + minute + "分钟 后";
		} else if (hour >= 1) {
			return hour + "小时" + minute + "分钟 后";
		} else if (minute > 0) {
			return minute + "分钟 后";
		} else if (minute > -60) {
			return "进行中";
		} else {
			return "已过时";
		}
	}

	/**
	 * 15位标准时间转换为毫秒数
	 * 
	 * @param time
	 * @return
	 */
	public static long TimeToMillis(String time) {
		Calendar timeCalendar = Calendar.getInstance();
		long Millis;
		int startyear = Integer.valueOf(time.substring(0, 4));
		int startmonth = Integer.valueOf(time.substring(4, 6));
		int startday = Integer.valueOf(time.substring(6, 8));
		int starthour = Integer.valueOf(time.substring(8, 10));
		int startminute = Integer.valueOf(time.substring(10, 12));
		// int startms = Integer.valueOf(time.substring(12));
		timeCalendar.set(startyear, startmonth - 1, startday - 1, starthour,
				startminute);
		Millis = timeCalendar.getTimeInMillis();
		return Millis;
	}

}
