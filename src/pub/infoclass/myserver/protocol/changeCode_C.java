package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;

public class changeCode_C {

	private int p = protocolfromclient.changeCode_C;
	private String UserID = "0";
	private String Code = "0";
	private String NewCode = "0";
	private String UploadTime = "0";
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getNewCode() {
		return NewCode;
	}
	public void setNewCode(String newCode) {
		NewCode = newCode;
	}
	public String getUploadTime() {
		return UploadTime;
	}
	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}
	public int getP() {
		return p;
	}
	
	/**
	 * 只可以在客户端设置与使用
	 */
	public void setStandardUploadTime() {
		UploadTime = FormatTime.getFormatTime();
	}
	@Override
	public String toString() {
		return "changeCode_C [p=" + p + ", UserID=" + UserID + ", Code=" + Code
				+ ", NewCode=" + NewCode + ", UploadTime=" + UploadTime + "]";
	}

}
