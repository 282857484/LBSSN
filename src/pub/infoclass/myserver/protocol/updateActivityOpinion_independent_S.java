package pub.infoclass.myserver.protocol;
public class updateActivityOpinion_independent_S {

	private int p = protocolfromserver.updateActivityOpinion_independent_S;
	private String UserID = "0";
	private String ActivityID = "0"; // 活动账号
	private String UploadTime = "0";
	private String GotGrade = "0"; // 取得的总分数
	private String TotalFullGrade = "0"; // 总人数
	private String Mark = "0";
	private String Content = "0";

	public updateActivityOpinion_independent_S() {
		super();
	}

	public updateActivityOpinion_independent_S(String userID,
			String activityID, String uploadTime, String gotGrade,
			String totalFullGrade, String mark, String content) {
		super();
		UserID = userID;
		ActivityID = activityID;
		UploadTime = uploadTime;
		GotGrade = gotGrade;
		TotalFullGrade = totalFullGrade;
		Mark = mark;
		Content = content;
	}

	public String getActivityID() {
		return ActivityID;
	}

	public void setActivityID(String activityID) {
		ActivityID = activityID;
	}

	public String getGotGrade() {
		return GotGrade;
	}

	public void setGotGrade(String gotGrade) {
		GotGrade = gotGrade;
	}

	public String getTotalFullGrade() {
		return TotalFullGrade;
	}

	public void setTotalFullGrade(String totalFullGrade) {
		TotalFullGrade = totalFullGrade;
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

}
