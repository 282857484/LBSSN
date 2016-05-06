package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class getPersonalInfo_C {

	private int p = protocolfromclient.getPersonalInfo_C;

	private String UserDiscussID = "0"; // 用户发布信息
	private String UserID = "0"; // 被留言用户ID
	private String ThisUserID = "0"; // 留言用户ID
	private String UploadTime = "0"; // 上传时间
	private String PageSize = "10"; // 默认10个
	private String PageIndex = "0"; // 初始为第0页

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

	public String getPageSize() {
		return PageSize;
	}

	public void setPageSize(String pageSize) {
		PageSize = pageSize;
	}

	public String getPageIndex() {
		return PageIndex;
	}

	public void setPageIndex(String pageIndex) {
		PageIndex = pageIndex;
	}

	public int getP() {
		return p;
	}

	public String getThisUserID() {
		return ThisUserID;
	}

	public void setThisUserID(String thisUserID) {
		ThisUserID = thisUserID;
	}

}
