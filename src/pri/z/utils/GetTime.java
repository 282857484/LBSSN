package pri.z.utils;

public class GetTime {

	/**
	 * 转化15位的时间
	 * @param time
	 * @return
	 */
	public static String getTime(String time){
		if(time.length()<15)
			return "unknown time";
		String year = time.substring(0, 4);
		String month = time.substring(4, 6);
		String day = time.substring(6, 8);
		String hour = time.substring(8, 10);
		String minute = time.substring(10, 12);
		return year+"-"+month+"-"+day+" "+hour+":"+minute;
	}
	
	public  static String getNoYearTime(String time){
		if(time.length()<15)
			return "unknown time";
//		String year = time.substring(0, 4);
		String month = time.substring(4, 6);
		String day = time.substring(6, 8);
		String hour = time.substring(8, 10);
		String minute = time.substring(10, 12);
		return month+"-"+day+" "+hour+":"+minute;
	}
	
	public  static String getNoYearChineseTime(String time){
		if(time.length()<15)
			return "unknown time";
//		String year = time.substring(0, 4);
		
		String month = time.substring(4, 6);
		int IntMonth = Integer.valueOf(month);
		
		String day = time.substring(6, 8);
		int IntDay = Integer.valueOf(day);
		String hour = time.substring(8, 10);
		String minute = time.substring(10, 12);
		return IntMonth+"月"+IntDay+"日 "+hour+":"+minute;
	}
	
	/**
	 * 得到用户的生日：1995-08-08
	 * @param time
	 * @return
	 */
	public  static String getBirthday(String time){
		if(time.length()<15)
			return "unknown time";
		String year = time.substring(0, 4);
		String month = time.substring(4, 6);
		String day = time.substring(6, 8);
		return year+"-"+month+"-"+day;
	}
	
	/**
	 * 转化12位的时间
	 * @param time
	 * @return
	 */
	public static String getTime2(String time){
		if(time.length()<12)
			return "unknown time";
		String year = time.substring(0, 4);
		String month = time.substring(4, 6);
		String day = time.substring(6, 8);
		String hour = time.substring(8, 10);
		String minute = time.substring(10, 12);
		return year+"-"+month+"-"+day+" "+hour+":"+minute;
	}
	
	/**
	 * 将秒钟转化为分钟
	 * @param millsTime
	 * @return
	 */
	public static String getMinutesTime(int secondTime){
		String time = "";
		if(secondTime <= 0)
			return time;
		//1小时以内
		if(secondTime <= 3600){
			int myTime = (int)(secondTime/60);
			return "约 "+myTime+"分钟";
		}
		else{
			int myHour = (int)(secondTime/3600);
			int myMinute = (secondTime-myHour*3600)/60;
			return "约 "+myHour+"小时 "+myMinute+"分钟";
		}
	}
	
	/**
	 * 将米转化为千米
	 * @param mi
	 * @return
	 */
	public static  double ChangeMiToKM(int mi){
		double km = 0;
		if(mi <= 0)
			return 0;
		else if(mi <= 100)
			return 0.1;
		else{
			double mimi = Double.valueOf(""+mi);
			km = mimi/1000;
		}
		return km;
	}
}
