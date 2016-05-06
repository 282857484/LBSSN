package pri.z.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class UtilsTrans {
	/**
	 * string的长度为15位 可以转为long 不可转为int
	 * 得到两个小时以后的时间戳
	 * @return
	 */
	public static String getFormatTimeBefore1Hour() {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 
				c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY)-1, 
				c.get(Calendar.MINUTE));
		StringBuilder formatTime = new StringBuilder();
		formatTime.append(c.get(Calendar.YEAR));
		int month = c.get(Calendar.MONTH) + 1;
		if (month < 10)
			formatTime.append("0" + String.valueOf(month));
		else
			formatTime.append(String.valueOf(month));
		int day = c.get(Calendar.DAY_OF_MONTH);
		if (day < 10)
			formatTime.append("0" + String.valueOf(day));
		else
			formatTime.append(String.valueOf(day));
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if (hour < 10)
			formatTime.append("0" + String.valueOf(hour));
		else
			formatTime.append(String.valueOf(hour));
		int minute = c.get(Calendar.MINUTE);
		if (minute < 10)
			formatTime.append("0" + String.valueOf(minute));
		else
			formatTime.append(String.valueOf(minute));
		int second = c.get(Calendar.MILLISECOND);
		if (second < 10) {
			formatTime.append("00" + String.valueOf(second));
		} else if ((second >= 10) && (second < 100)) {
			formatTime.append("0" + String.valueOf(second));
		} else {
			formatTime.append(String.valueOf(second));
		}
		return formatTime.toString();
	}
	/**
	 * string的长度为15位 可以转为long 不可转为int
	 * 得到两个小时以后的时间戳
	 * @return
	 */
	public static String getFormatTimeIn1Hour() {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 
				c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY)+1, c.get(Calendar.MINUTE));
		StringBuilder formatTime = new StringBuilder();
		formatTime.append(c.get(Calendar.YEAR));
		int month = c.get(Calendar.MONTH) + 1;
		if (month < 10)
			formatTime.append("0" + String.valueOf(month));
		else
			formatTime.append(String.valueOf(month));
		int day = c.get(Calendar.DAY_OF_MONTH);
		if (day < 10)
			formatTime.append("0" + String.valueOf(day));
		else
			formatTime.append(String.valueOf(day));
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if (hour < 10)
			formatTime.append("0" + String.valueOf(hour));
		else
			formatTime.append(String.valueOf(hour));
		int minute = c.get(Calendar.MINUTE);
		if (minute < 10)
			formatTime.append("0" + String.valueOf(minute));
		else
			formatTime.append(String.valueOf(minute));
		int second = c.get(Calendar.MILLISECOND);
		if (second < 10) {
			formatTime.append("00" + String.valueOf(second));
		} else if ((second >= 10) && (second < 100)) {
			formatTime.append("0" + String.valueOf(second));
		} else {
			formatTime.append(String.valueOf(second));
		}
		return formatTime.toString();
	}
	
	/**
	 * 判断str在list的下标
	 * @param lists
	 * @param str
	 * @return
	 */
	public static boolean IfExitThisNumber(List<Integer> lists,int str){
		boolean flag = false;
		if(lists == null)
			return false;
		if(lists.size() <= 0)
			return false;
		
		for(int index=0;index<lists.size();index++){
			if(str == lists.get(index)){
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 判断str在list的下标
	 * @param lists
	 * @param str
	 * @return
	 */
	public static int getStringIndexInList(List<String> lists,String str){
		int flag = -1;
		if(lists == null)
			return -1;
		if(lists.size() <= 0)
			return -1;
		
		for(int index=0;index<lists.size();index++){
			if(str.equals(lists.get(index))){
				flag = index;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 判断list中是否含有重复值
	 * @param lists
	 * @param str
	 * @return
	 */
	public static boolean IfExitStringInLists(List<String> lists,String str){
		boolean flag = false;
		if(lists == null)
			return false;
		if(lists.size() <= 0)
			return false;
		
		for(int index=0;index<lists.size();index++){
			if(str.equals(lists.get(index))){
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 在全部的评论回复中筛选包含指定messageid的List
	 * 
	 * @param messageId
	 *            ：筛选条件
	 * @param lists
	 *            ：全部List
	 * @return
	 */
	public static List<DiscussMoment> getDiscussMomentByMessageId(
			String messageId, List<DiscussMoment> lists) {
		List<DiscussMoment> myList = new ArrayList<DiscussMoment>();
		for (int index = 0; index < lists.size(); index++) {
			if (messageId.equals(lists.get(index).DiscussMessageId)) {
				myList.add(lists.get(index));
			}
		}
		return myList;
	}

	/**
	 * 判断加到评论List上的数据有没有重复
	 * @param myList
	 * @param myDis
	 * @return：重复：true；无重复：false
	 */
	public static  boolean RemoveDuplicateDiscuss(List<DiscussMoment> myList,
			DiscussMoment myDis) {
		boolean flag = false;
		if (myList.size() > 0) {
			for (int index = 0; index < myList.size(); index++) {
				boolean flag1 = myDis.DiscussContent
						.equals(myList.get(index).DiscussContent);
				boolean flag2 = myDis.DiscussUploadTime.equals(myList
						.get(index).DiscussUploadTime);

				if (flag1 && flag2) {
					flag = true;
				}
			}
		}

		return flag;
	}
	
	/**
	 * 该字符是否含有表情:不可用
	 * @param str
	 * @return
	 */
	public static boolean IsUTF8(String str){
		if(str == null)
			return true;
		if(str.trim().equals(""))
			return true;
		
		//汉字，数字，字母
		String patternHanzi = "[\u4e00-\u9fa5\\w]+";
		Pattern pattern = Pattern.compile(patternHanzi);
		boolean flag = pattern.matcher(str).matches();
		
		return flag;
	}
	
	
	public static boolean IfHasExpression(String str){
		if(str == null)
			return false;
		if(str.length() <= 0)
			return false;
		
		String asas = "\\";
		
		return str.contains(asas);
	}
	
	public  static String getStatusMean(String status) {
		// TODO Auto-generated method stub
		int Status = Integer.valueOf(status);
		switch(Status) {
		case 1:
			return "参加活动";
		case 2:
			return "申请参加活动";
		case 3:
			return "邀请您参加活动";
		case 4:
			return "关注活动";
		default:
			return "unknown packet";
		}
	}

	/**
	 * 截取名字名字，长度超过五位的
	 * @param username
	 * @return
	 */
	public static String getUserName(String username){
		if(username.length() > 7){
			username = username.substring(0, 7);
			username += "...";
		}
		return username;
	}
	
	/**
	 * 截取活动名字，长度超过五位的
	 * @param username
	 * @return
	 */
	public static String getActivityName(String activityname){
		if(activityname.length() > 8){
			activityname = activityname.substring(0, 8);
			activityname += "...";
		}
		return activityname;
	}
	
	/**
	 * 截取活动名字，长度超过五位的
	 * @param username
	 * @return
	 */
	public static String getUserDescCribe(String descCribe){
		if(descCribe.length() > 15){
			descCribe = descCribe.substring(0, 15);
			descCribe += "...";
		}
		return descCribe;
	}
	
	/**
	 * 将B转化为最多以为小数的MB值
	 * @param citySizeB
	 * @return
	 */
	public static String ChangeBToMB(int citySizeB){
		String str = "";
		double citySizeM = ((double)citySizeB)/(1024*1024);
		String sss = String.valueOf(citySizeM);
		str = sss;
		int index = sss.indexOf(".");
		if(sss.length() > index+2){
			str = sss.substring(0, index+2);
		}
		return str;
	}
	/**
	 * 将Bitmap转化为Drawable
	 * @param bitmap
	 * @return
	 */
	public static Drawable ChangeBitMapToDrawable(Bitmap bitmap){
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		return bd;
	}
}
