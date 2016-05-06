package pub.infoclass.myserver.protocol;
public class addActivityDiscuss_S {

	private int p = protocolfromserver.addActivityDiscuss_S;
	private String DiscussID = "0";
	private String UserID = "0";
	private String ActivityID = "0";
	private String UploadTime = "0";
	/**
	 * 1.成功
	 * 2.失败
	 */
	private String Mark;
	private String Content;
	
	
	public String getDiscussID() {
		return DiscussID;
	}
	public void setDiscussID(String discussID) {
		DiscussID = discussID;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getActivityID() {
		return ActivityID;
	}
	public void setActivityID(String activityID) {
		ActivityID = activityID;
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
	public String getUploadTime() {
		return UploadTime;
	}
	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}
	
}
