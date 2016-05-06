package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class deleteUserInfo_C {

	private int p = protocolfromclient.deleteUserInfo_C;
	private String UserID = "0"; // 用户ID
	private String UploadTime = "0"; // 用户名称

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
