package pub.infoclass.myserver.protocol;

public class Validate_S {
	private int p = protocolfromserver.Validate_S;
	
	private String UserID = "0"; //用户ID
	private String UploadTime = "0"; //上传时间戳
	
	private String sendId = "0";
	private String sendPhone = "0";//手机号
	private String sendCode = "0";//验证码
	/**
	 * 发送标记:1:注册请求 2:找回密码请求 3:发送注册验证码  4:发送找回密码验证码
	 */
	private int sendMark = 0; //必须
	
	private String Mark = "0";//验证成功或失败的标记 1:成功 2 :失败
	private String Content = "0";
	
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
	public String getSendId() {
		return sendId;
	}
	public void setSendId(String sendId) {
		this.sendId = sendId;
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
	public int getSendMark() {
		return sendMark;
	}
	public void setSendMark(int sendMark) {
		this.sendMark = sendMark;
	}
	
	
}
