package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class Validate_C {

	private int p = protocolfromclient.Validate_C;
	
	private String UserID = "0"; //用户ID //必须
	private String UploadTime = "0"; //上传时间戳 //必须
	
	private String sendId = "0";
	private String sendPhone = "0";//手机号 //必须
	private String sendCode = "0";//验证码 //必须
	
	/**
	 * 发送标记:1:注册请求 2:找回密码请求 3:发送注册验证码  4:发送找回密码验证码
	 */
	private int sendMark = 0; //必须

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
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
	
	public String getSendPhone() {
		return sendPhone;
	}

	public void setSendPhone(String sendPhone) {
		this.sendPhone = sendPhone;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public int getSendMark() {
		return sendMark;
	}

	public void setSendMark(int sendMark) {
		this.sendMark = sendMark;
	}

	public String getSendId() {
		return sendId;
	}

	public void setSendId(String sendId) {
		this.sendId = sendId;
	}
	
	
	
}
