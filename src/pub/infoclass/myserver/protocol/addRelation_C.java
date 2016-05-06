package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class addRelation_C {

	private int p = protocolfromclient.addRelation_C;
	
	/************必须项***********/
//	private String RelationID; // 关系ID
	private String ActivityID = "0"; // 活动ID
	private String UserID = "0"; // 用户ID
	private String UploadTime = "0";
	private String Status = "0"; // (1.用户参加;2.用户申请;3.活动方邀请;4.用户关注)
//	public String getRelationID() {
//		return RelationID;      
//	}
//	public void setRelationID(String relationID) {
//		RelationID = relationID;
//	}
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
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
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

	/************非必须项***********/
	
	
}
