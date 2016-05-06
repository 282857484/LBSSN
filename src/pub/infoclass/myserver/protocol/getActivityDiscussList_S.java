package pub.infoclass.myserver.protocol;

public class getActivityDiscussList_S {

	private int p = protocolfromserver.getActivityDiscussList_S;

	private String ActivityID = "0";
	private String UserID = "0";
	private String UploadTime = "0";
	private String PageSize = "100"; // 集合大小
	private String PageIndex = "0";
	private String Mark = "0";
	private String Content = "0";

	private String ActivityDiscussList;

	public String getActivityID() {
		return ActivityID;
	}

	public void setActivityID(String activityID) {
		ActivityID = activityID;
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

	public String getActivityDiscussList() {
		return ActivityDiscussList;
	}

	public void setActivityDiscussList(String activityDiscussList) {
		ActivityDiscussList = activityDiscussList;
	}

	public int getP() {
		return p;
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

}
