package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class deleteActivityDiscuss_C {

	private int p = protocolfromclient.deleteActivityDiscuss_C;
	/*********************/
	private String DiscussID = "0";
	private String ActivityID = "0";
	private String UserID = "0";
	private String UploadTime = "0";
	public String getDiscussID() {
		return DiscussID;
	}
	public void setDiscussID(String discussID) {
		DiscussID = discussID;
	}
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
