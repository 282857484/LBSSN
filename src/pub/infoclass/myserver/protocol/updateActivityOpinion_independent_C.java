package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class updateActivityOpinion_independent_C {
	private int p = protocolfromclient.updateActivityOpinion_independent_C;
	private String UserID = "0";
	private String ActivityID = "0"; // 活动账户
	private String Opinion = "0"; // 评分
	private String UploadTime = "0";

	
	public updateActivityOpinion_independent_C() {
		super();
	}

	public updateActivityOpinion_independent_C(String userID,
			String activityID, String opinion, String uploadTime) {
		super();
		UserID = userID;
		ActivityID = activityID;
		Opinion = opinion;
		UploadTime = uploadTime;
	}

	public String getActivityID() {
		return ActivityID;
	}

	public void setActivityID(String activityID) {
		ActivityID = activityID;
	}

	public String getOpinion() {
		return Opinion;
	}

	public void setOpinion(String opinion) {
		Opinion = opinion;
	}

	public int getP() {
		return p;
	}

	public String getUploadTime() {
		return UploadTime;
	}

	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}
	
	/**
	 * 只可以在客户端设置与使用
	 */
	public void setStandardUploadTime() {
		UploadTime = FormatTime.getFormatTime();
	}

}
