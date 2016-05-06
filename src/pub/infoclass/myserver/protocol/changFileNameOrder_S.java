package pub.infoclass.myserver.protocol;

public class changFileNameOrder_S {

	private int p = protocolfromserver.changFileNameOrder_S;
	private String UserID = "0";
	private String UploadTime = "0";
	private String Mark = "0";
	private String Content = "0";
	
	@Override
	public String toString() {
		return "changFileNameOrder_S [p=" + p + ", UserID=" + UserID
				+ ", UploadTime=" + UploadTime + ", Mark=" + Mark
				+ ", Content=" + Content + "]";
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
