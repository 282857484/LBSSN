package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class addActivityDiscuss_C{

	/************必须项***********/
	private int p = protocolfromclient.addActivityDiscuss_C;
//	DiscussID
	private String ActivityID = "0"; // 活动账号
	private String UserID = "0"; // 用户账号
	private String UploadTime = "0";
	
	/************非必须项***********/
	public String ActivityName; // 活动姓名
	public String UserName; // 用户姓名
	public String DiscussContent; // 讨论内容
	public String Photo; // 留言图片
	public String PointDiscussID; // 留言指针
	public String ThisUserID; // 被留言人id
	public String getActivityID() {
		return ActivityID;
	}
	public void setActivityID(String activityID) {
		ActivityID = activityID;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getUploadTime() {
		return UploadTime;
	}
	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}
	/**
	 * 只可以在客户端设置与使用
	 */
	public void setStandardUploadTime() {
		UploadTime = FormatTime.getFormatTime();
	}
	public int getP() {
		return p;
	}

	
}
