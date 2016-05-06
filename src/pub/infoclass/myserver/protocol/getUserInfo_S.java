package pub.infoclass.myserver.protocol;
public class getUserInfo_S {

	private int p = protocolfromserver.getUserInfo_S;
	
	private String UserID = "0"; // 用户ID
	private String UploadTime = "0"; // 用户名称
	private String PageSize = "10";
	private String PageIndex = "0";
	private String Mark = "0";
	private String Content = "0";
	
	private String UserInfoList;

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

	public String getUserInfoList() {
		return UserInfoList;
	}

	public void setUserInfoList(String userInfoList) {
		UserInfoList = userInfoList;
	}

	public int getP() {
		return p;
	}
	
	
}
