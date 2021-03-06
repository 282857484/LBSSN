package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class deleteRelation_C {

	private int p = protocolfromclient.deleteRelation_C;

	private String RelationID = "0";
	private String ActivityID = "0";
	private String SearchUserID = "0";
	private String UserID = "0";
	private String UploadTime = "0";

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

	/**
	 * 只可以在客户端设置与使用
	 */
	public void setStandardUploadTime() {
		UploadTime = FormatTime.getFormatTime();
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

	public String getSearchUserID() {
		return SearchUserID;
	}

	public void setSearchUserID(String searchUserID) {
		SearchUserID = searchUserID;
	}

}
