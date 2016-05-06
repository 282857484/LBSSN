package pub.infoclass.myserver.protocol;

public class deletePersonalInfo_S {

	private int p = protocolfromserver.deletePersonalInfo_S;

	private String UserDiscussID = "0"; // 指定留言项
	private String UserID = "0"; // 指定用户ID
	private String UploadTime = "0";
	private String Mark = "0";
	private String Content = "0";

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

	public String getMark() {
		return Mark;
	}

	public void setMark(String mark) {
		Mark = mark;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public int getP() {
		return p;
	}

}
