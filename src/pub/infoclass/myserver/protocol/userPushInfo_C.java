package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class userPushInfo_C{

	private int p = protocolfromclient.userPushInfo_C;
	private String UploadTime = "0";
	private String UserID = "0";
	private String ThisUserID = "0";
	private String mbi = "0" ;// 
	public int getP() {
		return p;
	}

	public String getUploadTime() {
		return UploadTime;
	}
	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}
	public void setStandardUploadTime() {
		// TODO Auto-generated method stub
		UploadTime = FormatTime.getFormatTime();
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getThisUserID() {
		return ThisUserID;
	}
	public void setThisUserID(String thisUserID) {
		ThisUserID = thisUserID;
	}
	public String getMbi() {
		return mbi;
	}
	public void setMbi(String mbi) {
		this.mbi = mbi;
	}
	@Override
	public String toString() {
		return "userPushInfo_C [p=" + p + ", UploadTime=" + UploadTime
				+ ", UserID=" + UserID + ", ThisUserID=" + ThisUserID
				+ ", mbi=" + mbi + "]";
	}

	
	
	
	
}
