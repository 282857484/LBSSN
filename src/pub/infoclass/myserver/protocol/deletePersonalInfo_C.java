package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class deletePersonalInfo_C {

	private int p = protocolfromclient.deletePersonalInfo_C;

	private String UserDiscussID = "0"; // 指定留言项
	private String UserID = "0"; // 指定用户ID
	private String UploadTime = "0";

	public String getUserDiscussID() {
		return UserDiscussID;
	}

	public void setUserDiscussID(String userDiscussID) {
		UserDiscussID = userDiscussID;
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
