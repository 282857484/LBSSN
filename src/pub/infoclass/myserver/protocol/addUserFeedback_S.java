package pub.infoclass.myserver.protocol;
public class addUserFeedback_S {

	private int p = protocolfromserver.addUserFeedback_S;
	private String UserID  = "0"; 
	private String UploadTime = "0";
	private String Mark = "0";
	private String Content = "0";
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
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
