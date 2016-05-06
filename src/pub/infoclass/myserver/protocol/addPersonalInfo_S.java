package pub.infoclass.myserver.protocol;


public class addPersonalInfo_S {

	private int p = protocolfromserver.addPersonalInfo_S;
	
	private String ThisUserID = "0"; // 留言用户ID
	private String UploadTime = "0"; // 上传时间
	private String UserID = "0";
	/**
	 * 1.成功
	 * 2.失败
	 */
	private String Mark;
	private String Content;
	
	
	public String getThisUserID() {
		return ThisUserID;
	}


	public void setThisUserID(String thisUserID) {
		ThisUserID = thisUserID;
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


	public String getUserID() {
		return UserID;
	}


	public void setUserID(String userID) {
		UserID = userID;
	}
	
	
}
