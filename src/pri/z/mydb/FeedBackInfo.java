package pri.z.mydb;

public class FeedBackInfo {

	public int FeedBackDBID;//自增长
	public String FeedBackUserId;//反馈用户ID
	public String FeedBackContent;//反馈内容
	public String FeedBackTime;//反馈时间
	public FeedBackInfo(int feedBackDBID, String feedBackUserId,
			String feedBackContent, String feedBackTime) {
		super();
		FeedBackDBID = feedBackDBID;
		FeedBackUserId = feedBackUserId;
		FeedBackContent = feedBackContent;
		FeedBackTime = feedBackTime;
	}
	
	
}
