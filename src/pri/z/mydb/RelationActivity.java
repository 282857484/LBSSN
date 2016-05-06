package pri.z.mydb;

/**
 * 本地保存用户参加/关注的活动
 * Time:2014/08/21
 * @author zhuzhenke
 *
 */
public class RelationActivity {

	public int RelationActivityDBID;//自增长
	public String RelationUserID;//用户ID
	public String RelationActivityID;//活动ID
	public String RelationActivityName;//活动名称
	public String RelationActivityBuilderUserID;//活动主办方ID
	public String RelationActivityStartTime;//活动开始时间
	public String RelationActivityEndTime;//活动结束时间
	public String RelationActivityStartNotifyFlag;//活动开始的通知标志：提前两小时，用于提醒用户参加：1为未通知，2为已通知
	public String RelationActivityEndNotifyFlag;//活动结束的通知标志：用户提醒用户评分：1为未通知，2为已通知
	public String RelationActivityStatus;//用户与活动的关系：(-1.用户主办;1.用户参加;2.用申请;3.活动方邀请;4.用户关注;5.未审核通过;6.被踢出用户) 
	public int p;//用在推送消息的时候用到，其他的都不用
	public RelationActivity() {
		super();
	}
	public RelationActivity(int relationActivityDBID, String relationUserID,
			String relationActivityID, String relationActivityName,
			String relationActivityBuilderUserID,
			String relationActivityStartTime, String relationActivityEndTime,
			String relationActivityStartNotifyFlag,
			String relationActivityEndNotifyFlag, String relationActivityStatus) {
		super();
		RelationActivityDBID = relationActivityDBID;
		RelationUserID = relationUserID;
		RelationActivityID = relationActivityID;
		RelationActivityName = relationActivityName;
		RelationActivityBuilderUserID = relationActivityBuilderUserID;
		RelationActivityStartTime = relationActivityStartTime;
		RelationActivityEndTime = relationActivityEndTime;
		RelationActivityStartNotifyFlag = relationActivityStartNotifyFlag;
		RelationActivityEndNotifyFlag = relationActivityEndNotifyFlag;
		RelationActivityStatus = relationActivityStatus;
	}


	
	
}
