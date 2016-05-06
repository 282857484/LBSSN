package pub.infoclass.myserver.protocol;

public class deleteRelation_S {

	private int p = protocolfromclient.deleteRelation_C;

	private String RelationID = "0";
	private String ActivityID = "0";
	private String UserID = "0";
	private String UploadTime = "0";
	private String Mark = "0";
	private String Content = "0";
	public String getRelationID() {
		return RelationID;
	}
	public void setRelationID(String relationID) {
		RelationID = relationID;
	}
	public String getActivityID() {
		return ActivityID;
	}
	public void setActivityID(String activityID) {
		ActivityID = activityID;
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
