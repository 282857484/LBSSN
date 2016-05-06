/**
 * 
 */
package pri.z.sqlite;

import pri.z.mydb.FeedBackInfo;
import pri.z.mydb.GradeInfo;
import pri.z.mydb.PushMessage;
import pri.z.mydb.RelationActivity;
import pri.z.mydb.SearchDistance;
import pri.z.utils.DiscussMoment;
import pri.z.utils.MomentBaseInfo;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.util.FormatTime;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 祝侦科
 *2014-3-8
 */
public class DBAdapter {
//	private DBAdapter(){}
	
	private static final String DB_NAME = "letsparty2014.db";//数据库名称
	private static final String Activity_Table = "activitybase";
	private static final String UserInfo_Table = "userinfobase";
	private static final String Moment_Table = "momentbaseinfo";
	private static final String Grade_Table = "grade";
//	private static final String DiscussMoment_Table = "discussmoment";
	private static final String SearchDistance_Table = "searchdistance";
	private static final String PushMessage_Table = "pushmessage";
	private static final String RelationActivity_Table = "relationactivity";
	private static final String FeedBack_Table = "feedback";
	
	private static final int DB_VERSION = 7;//当前数据库的版本号
	 
	//活动基本的信息：
	private static final String Key_ActivityDBID = "ActivityDBID";
	private static final String Key_ActivityID = "ActivityID";
	private static final String Key_BuildActivityUserID = "BuildActivityUserID";
	private static final String Key_ActivityManagerID = "ActivityManagerID";
	private static final String Key_UploadTime = "UploadTime";
	private static final String Key_IsDirectJoinIn = "IsDirectJoinIn";
	private static final String Key_ActivityFlag = "ActivityFlag";
	private static final String Key_ActivityMemberNumber = "ActivityMemberNumber";
	private static final String Key_ActivityMaxMemberNumber = "ActivityMaxMemberNumber";
	private static final String Key_ActivityDescribe = "ActivityDescribe";
	private static final String Key_ActivityName = "ActivityName";
	private static final String Key_ActivityStartTime = "ActivityStartTime";
	private static final String Key_ActivityEndTime = "ActivityEndTime";
	private static final String Key_ActivityHoldPlace = "ActivityHoldPlace";
	private static final String Key_HelpPhone = "HelpPhone";
	private static final String Key_ActivityBelongClass = "ActivityBelongClass";
	private static final String Key_ActivityTags = "ActivityTags";
	private static final String Key_ActivityOpinion = "ActivityOpinion";
	private static final String Key_ActivityAddress = "ActivityAddress";
	private static final String Key_ActivityLogo = "ActivityLogo";
	
	//用户资料的基本信息：
	private static final String Key_UserID = "UserID";//long
	private static final String Key_UserName = "UserName";
	private static final String Key_UserUploadTime = "UploadTime";
	private static final String Key_Code = "Code";
	private static final String Key_UserPhone = "UserPhone";
	private static final String Key_UserJoinActivity = "UserJoinActivity";
	private static final String Key_UserAttentionClass = "UserAttentionClass";
	private static final String Key_UserQQ = "UserQQ";
	private static final String Key_UserWeiChat = "UserWeiChat";
	private static final String Key_UserTags = "UserTags";
	private static final String Key_UserClass = "UserClass";
	private static final String Key_UserDescribe = "UserDescribe";
	private static final String Key_UserLevel = "UserLevel";
	private static final String Key_UserLogo = "UserLogo";
	private static final String Key_UserAge = "UserAge";
	private static final String Key_UserSex = "UserSex";
	private static final String Key_UserSchool = "UserSchool";
	private static final String Key_UserProfession = "UserProfession";
	private static final String Key_UserBirthday = "UserBirthday";
	private static final String Key_UserHome = "UserHome";
	
	//好友动态的字段
	private static final String Key_MomentId = "MomentId";
	private static final String Key_MomentMessageId ="MomentMessageId";
	private static final String Key_MomentUserId ="MomentUserId";
	private static final String Key_MomentUserName ="MomentUserName";
	private static final String Key_MomentUploadTime ="MomentUploadTime";
	private static final String Key_MomentContent ="MomentContent";
	private static final String Key_MomentPraiseNum ="MomentPraiseNum";
	private static final String Key_MomentCritizenNum ="MomentCritizenNum";
	private static final String Key_MomentMyUserId ="MomentMyUserId";
	private static final String Key_MomentMyUserName ="MomentMyUserName";
	private static final String Key_MomentHasOrNo ="MomentHasOrNo";
	//
	private static final String Key_MomentLocation ="MomentLocation";
	private static final String Key_MomentSex ="MomentSex";
	private static final String Key_MomentAge ="MomentAge";
	private static final String Key_MomentShowPhotos ="MomentShowPhotos";
	
	//活动评分的字段
	private static final String Key_GradeId = "GradeId";
	private static final String Key_GradePhone = "GradePhone";
	private static final String Key_GradeActId = "GradeActId";
	private static final String Key_GradeNum = "GradeNum";
	private static final String Key_GradeTime = "GradeTime";
	
	
	//动态评论的字段
	private static final String Key_DiscussId = "DiscussId";//自增长
	private static final String Key_DiscussUid = "DiscussUid";//评论在百度数据库的id
	private static final String Key_DiscussMessageId = "DiscussMessageId";//用户评论的动态Id
	private static final String Key_DiscussUserId = "DiscussUserId" ;//该评论的用户Id
	private static final String Key_DiscussUserName = "DiscussUserName";//该评论的用户昵称
	private static final String Key_DiscussPointId = "DiscussPointId";//指向Id
	private static final String Key_DiscussUploadTime = "DiscussUploadTime";//发表时间
	private static final String Key_DiscussContent = "DiscussContent";//评论内容
	
	//搜索范围的字段
	private static final String Key_SearchDistanceId = "DistanceId";//数据库中自增长的数据
	private static final String Key_SearchDistanceMomentDis = "DistanceMomentDis";//动态的范围
	private static final String Key_SearchDistanceActivityDis = "DistanceActivityDis";//活动的范围
	
	//推送消息
	private static final String Key_PushDBID = "PushDBID";//自增长，存放在数据库中
	private static final String Key_PushID = "PushID";//推送得到消息ID
	private static final String Key_PushUserID = "PushUserID";//要推送给的用户Id
	private static final String Key_PushMessage = "PushMessage";//推送信息
	private static final String Key_PushStatue = "PushStatue";//用户是否已读
	
	//活动Relation，方便消息的推送
	private static final String Key_RelationActivityDBID = "RelationActivityDBID";//自增长
	private static final String Key_RelationUserID = "RelationUserID";//用户ID
	private static final String Key_RelationActivityID = "RelationActivityID";//活动ID
	private static final String Key_RelationActivityName = "RelationActivityName";//活动名称
	private static final String Key_RelationActivityBuilderUserID = "RelationActivityBuilderUserID";//活动主办方ID
	private static final String Key_RelationActivityStartTime = "RelationActivityStartTime";//活动开始时间
	private static final String Key_RelationActivityEndTime = "RelationActivityEndTime";//活动结束时间
	private static final String Key_RelationActivityStartNotifyFlag = "RelationActivityStartNotifyFlag";//活动开始的通知标志：提前两小时，用于提醒用户参加：1为未通知，2为已通知
	private static final String Key_RelationActivityEndNotifyFlag = "RelationActivityEndNotifyFlag";//活动结束的通知标志：用户提醒用户评分：1为未通知，2为已通知
	private static final String Key_RelationActivityStatus = "RelationActivityStatus";//用户与活动的关系：(-1.用户主办;1.用户参加;2.用申请;3.活动方邀请;4.用户关注;5.未审核通过;6.被踢出用户)
	
	private static final String Key_FeedBackDBID = "FeedBackDBID";//自增长
	private static final String Key_FeedBackUserId = "FeedBackUserId";//反馈用户ID
	private static final String Key_FeedBackContent = "FeedBackContent";//反馈内容
	private static final String Key_FeedBackTime = "FeedBackTime";//反馈时间
	
	private static SQLiteDatabase db;
	private static Context context;
	private static DBOpenHelper dbOpenHelper;
	
	
	public  DBAdapter(Context _context) {
	    context = _context;
	  }
	public static DBAdapter dbAdapter = null;

	  /** Close the database */
	  public void close() {
		  if(dbOpenHelper != null){
			  dbOpenHelper.close();
			  dbOpenHelper = null;
		  }
		  if (db != null){
			  db.close();
			  db = null;
		  }
		}

	  /** Open the database */
	  public void open() throws SQLiteException {  
		  dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
		  try {
			  db = dbOpenHelper.getWritableDatabase();
		  }
		  catch (SQLiteException ex) {
			  db = dbOpenHelper.getReadableDatabase();
		  }	  
		}
	  
	
	  /**
	   * 根据活动ID更改信息：这里只做为测试，没有实际用途
	   *@author 祝侦科
	   *2014-5-22
	   * @param id
	   * @param data
	   * @return:影响的行数
	   */
//	  public int updateOneActivityByID(ActivitySelectData base){
//		  ContentValues newValues = new ContentValues();
//		//ID为自增长的不要添加
////		  newValues.put(Key_ActivityDBID, base.getActivityID());
//		    newValues.put(Key_ActivityID, base.getActivityID());
//		    newValues.put(Key_BuildActivityUserID, base.getBuildActivityUserID());
//		    newValues.put(Key_ActivityManagerID, base.getActivityManagerID());
//		    newValues.put(Key_UploadTime, base.getUploadTime());
//		    newValues.put(Key_IsDirectJoinIn, base.getIsDirectJoinIn());
//		    newValues.put(Key_ActivityFlag, base.getActivityFlag());
//		    newValues.put(Key_ActivityMemberNumber, base.getActivityMemberNumber());
//		    newValues.put(Key_ActivityMaxMemberNumber, base.getActivityMaxMemberNumber());
//		    newValues.put(Key_ActivityDescribe, base.getActivityDescribe());
//		    newValues.put(Key_ActivityName, base.getActivityName());
//		    newValues.put(Key_ActivityStartTime, base.getActivityStartTime());
//		    newValues.put(Key_ActivityEndTime, base.getActivityEndTime());
//		    newValues.put(Key_ActivityHoldPlace, base.getActivityHoldPlace());
//		    newValues.put(Key_HelpPhone, base.getHelpPhone());
//		    newValues.put(Key_ActivityBelongClass, base.getActivityBelongClass());
//		    newValues.put(Key_ActivityTags, base.getActivityTags());
//		    newValues.put(Key_ActivityOpinion, base.getActivityOpinion());
//		    newValues.put(Key_ActivityAddress, base.getActivityAddress());
//		    newValues.put(Key_ActivityLogo, base.getActivityLogo());
//		   return db.update(Activity_Table, newValues, Key_ActivityID +"=? ", new String[]{base.getActivityID()+""});
//	  }
	  
//	  public int updateOneMomentByMessageId(int messageId,String PraiseNum,String CriticizenNum,String HasOrNo){
//		  ContentValues newValues = new ContentValues();
//		    newValues.put(Key_MomentPraiseNum, PraiseNum);
//		    newValues.put(Key_MomentCritizenNum, CriticizenNum);
//		    newValues.put(Key_MomentHasOrNo, HasOrNo);
//		    
//		   return db.update(Moment_Table, newValues, Key_MomentMessageId +"=? ", new String[]{String.valueOf(messageId)});
//	  }
	  
//	  public int updateOneSearchDistance(SearchDistance myDistanceUpdate){
//		  ContentValues newValues = new ContentValues();
//		    newValues.put(Key_SearchDistanceActivityDis, myDistanceUpdate.DistanceActivityDis);
//		    newValues.put(Key_SearchDistanceMomentDis, myDistanceUpdate.DistanceMomentDis);
//		    
//		   return db.update(SearchDistance_Table, newValues, 
//				   Key_SearchDistanceId +"=? ", 
//				   new String[]{String.valueOf(myDistanceUpdate.DistanceId)});
//	  }
	  
//	  public int updateOneMomentByMessageId(MomentBaseInfo info){
//		  ContentValues newValues = new ContentValues();
////		    newValues.put(Key_MomentId, info.MomentId);
//		    newValues.put(Key_MomentMessageId, info.MessageId);
//		    newValues.put(Key_MomentUserId, info.UserId);
//		    newValues.put(Key_MomentUserName, info.UserName);
//		    newValues.put(Key_MomentUploadTime, info.UploadTime);
//		    newValues.put(Key_MomentContent, info.MomentContent);
//		    newValues.put(Key_MomentPraiseNum, info.PraiseNum);
//		    newValues.put(Key_MomentCritizenNum, info.CritizenNum);
//		    newValues.put(Key_MomentMyUserId, info.MyUserId);
//		    newValues.put(Key_MomentMyUserName, info.MyUserName);
//		  //不能修改是否赞的数据，否则就会刻意无数次点赞
////		    newValues.put(Key_MomentHasOrNo, info.HasOrNo);
//		    newValues.put(Key_MomentLocation, info.Location);
//		    newValues.put(Key_MomentSex, info.Sex);
//		    newValues.put(Key_MomentAge, info.Age);
//		    newValues.put(Key_MomentShowPhotos, info.ShowPhotos);
//		    
//		    
//		   return db.update(Moment_Table, newValues, Key_MomentMessageId +"=? ", new String[]{String.valueOf(info.MessageId)});
//	  }
	  
	  
//	  /**改变是否已读的状态**/
//	  public int updateOnePushMessageByPushId(String pushId){
//		  ContentValues newValues = new ContentValues();
//		  newValues.put(Key_PushStatue, "2");//标记为已读
//		  
//		  return db.update(PushMessage_Table, newValues, Key_PushID +"=? ", new String[]{pushId});
//	  }
	  
//	  /**改变是否已读的状态**/
//	  public int updateAllPushMessageByPushId(){
//		  ContentValues newValues = new ContentValues();
//		  newValues.put(Key_PushStatue, "2");//标记为已读
//		  
//		  return db.update(PushMessage_Table, newValues, null, null);
//	  }
	  
//	  /**活动开始时间的通知是否已经送到的状态**/
//	  public int updateOneRelationActivityStart(String ActivityId){
//		  ContentValues newValues = new ContentValues();
//		  newValues.put(Key_RelationActivityStartNotifyFlag, "2");//标记为已读
//		  
//		  return db.update(RelationActivity_Table, newValues, Key_RelationActivityID +"=? ", new String[]{ActivityId});
//	  }
	  
	  /**活动结束后的评分的通知是否送到的状态**/
//	  public int updateOneRelationActivityEnd(String ActivityId){
//		  ContentValues newValues = new ContentValues();
//		  newValues.put(Key_RelationActivityEndNotifyFlag, "2");//标记为已读
//		  
//		  return db.update(RelationActivity_Table, newValues, Key_RelationActivityID +"=? ", new String[]{ActivityId});
//	  }
	  
//	  /**活动与用户的关系状态**/
//	  public int updateOneRelationActivityStatus(String ActivityId,String Status){
//		  ContentValues newValues = new ContentValues();
//		  newValues.put(Key_RelationActivityStatus, Status);//标记为已读
//		  
//		  return db.update(RelationActivity_Table, newValues, Key_RelationActivityID +"=? ", new String[]{ActivityId});
//	  }
	  
//	  public int updateOneUserInfoByID(String phone,UserInfoSelectData info){
//		  ContentValues newValues = new ContentValues();
//		  newValues.put(Key_UserID, info.getUserID());
//		    newValues.put(Key_UserName, info.getUserName());
//		    newValues.put(Key_UserUploadTime, info.getUploadTime());
//		    newValues.put(Key_Code, info.getCode());
//		    newValues.put(Key_UserPhone, info.getUserPhone());
//		    newValues.put(Key_UserJoinActivity, info.getUserJoinActivity());
//		    newValues.put(Key_UserAttentionClass, info.getUserAttentionClass());
//		    newValues.put(Key_UserQQ, info.getUserQQ());
//		    newValues.put(Key_UserWeiChat, info.getUserWeiChat());
//		    newValues.put(Key_UserTags, info.getUserTags());
//		    newValues.put(Key_UserClass, info.getUserClass());
//		    newValues.put(Key_UserDescribe, info.getUserDescribe());
//		    newValues.put(Key_UserLevel, info.getUserLevel());
//		    newValues.put(Key_UserLogo, info.getUserLogo());
//		    newValues.put(Key_UserAge, info.getUserAge());
//		    newValues.put(Key_UserSex, info.getUserSex());
//		    newValues.put(Key_UserSchool, info.getSchool());
//		    newValues.put(Key_UserProfession, info.getProfession());
//		    newValues.put(Key_UserBirthday, info.getBirthday());
//		    newValues.put(Key_UserHome, info.getHome());
//		    
//		   return db.update(UserInfo_Table, newValues, Key_UserPhone +"=? ", new String[]{phone});
//	  }
	  
	  
	  /**
	   * 添加活动类信息到我的手机数据库中
	   *@author 祝侦科
	   *2014-5-21 
	   * @param base
	   * @return
	   */
//	  public long insertActivityBase(ActivitySelectData base) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
////		    newValues.put(Key_ActivityDBID, base.ActivityID);
//		    newValues.put(Key_ActivityID, base.getActivityID());
//		    newValues.put(Key_BuildActivityUserID, base.getBuildActivityUserID());
//		    newValues.put(Key_ActivityManagerID, base.getActivityManagerID());
//		    newValues.put(Key_UploadTime, base.getUploadTime());
//		    newValues.put(Key_IsDirectJoinIn, base.getIsDirectJoinIn());
//		    newValues.put(Key_ActivityFlag, base.getActivityFlag());
//		    newValues.put(Key_ActivityMemberNumber, base.getActivityMemberNumber());
//		    newValues.put(Key_ActivityMaxMemberNumber, base.getActivityMaxMemberNumber());
//		    newValues.put(Key_ActivityDescribe, base.getActivityDescribe());
//		    newValues.put(Key_ActivityName, base.getActivityName());
//		    newValues.put(Key_ActivityStartTime, base.getActivityStartTime());
//		    newValues.put(Key_ActivityEndTime, base.getActivityEndTime());
//		    newValues.put(Key_ActivityHoldPlace, base.getActivityHoldPlace());
//		    newValues.put(Key_HelpPhone, base.getHelpPhone());
//		    newValues.put(Key_ActivityBelongClass, base.getActivityBelongClass());
//		    newValues.put(Key_ActivityTags, base.getActivityTags());
//		    newValues.put(Key_ActivityOpinion, base.getActivityOpinion());
//		    newValues.put(Key_ActivityAddress, base.getActivityAddress());
//		    newValues.put(Key_ActivityLogo, base.getActivityLogo());
//		    
//		    
//		    return db.insert(Activity_Table, null, newValues);
//	 }
	  
//	  public long insertUserInfoBase(UserInfoSelectData info) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
////		    newValues.put(Key_UserID, info.UserID);
//		    newValues.put(Key_UserName, info.getUserName());
//		    newValues.put(Key_UserUploadTime, info.getUploadTime());
//		    newValues.put(Key_Code, info.getCode());
//		    newValues.put(Key_UserPhone, info.getUserPhone());
//		    newValues.put(Key_UserJoinActivity, info.getUserJoinActivity());
//		    newValues.put(Key_UserAttentionClass, info.getUserAttentionClass());
//		    newValues.put(Key_UserQQ, info.getUserQQ());
//		    newValues.put(Key_UserWeiChat, info.getUserWeiChat());
//		    newValues.put(Key_UserTags, info.getUserTags());
//		    newValues.put(Key_UserClass, info.getUserClass());
//		    newValues.put(Key_UserDescribe, info.getUserDescribe());
//		    newValues.put(Key_UserLevel, info.getUserLevel());
//		    newValues.put(Key_UserLogo, info.getUserLogo());
//		    newValues.put(Key_UserAge, info.getUserAge());
//		    newValues.put(Key_UserSex, info.getUserSex());
//		    newValues.put(Key_UserSchool, info.getSchool());
//		    newValues.put(Key_UserProfession, info.getProfession());
//		    newValues.put(Key_UserBirthday, info.getBirthday());
//		    newValues.put(Key_UserHome, info.getHome());
//		    
//		    return db.insert(UserInfo_Table, null, newValues);
//	 }
	  
	  
//	  public long insertMomentBaseInfo(MomentBaseInfo info) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
////		    newValues.put(Key_MomentId, info.MomentId);
//		    newValues.put(Key_MomentMessageId, info.MessageId);
//		    newValues.put(Key_MomentUserId, info.UserId);
//		    newValues.put(Key_MomentUserName, info.UserName);
//		    newValues.put(Key_MomentUploadTime, info.UploadTime);
//		    newValues.put(Key_MomentContent, info.MomentContent);
//		    newValues.put(Key_MomentPraiseNum, info.PraiseNum);
//		    newValues.put(Key_MomentCritizenNum, info.CritizenNum);
//		    newValues.put(Key_MomentMyUserId, info.MyUserId);
//		    newValues.put(Key_MomentMyUserName, info.MyUserName);
//		    newValues.put(Key_MomentHasOrNo, info.HasOrNo);
//		    newValues.put(Key_MomentLocation, info.Location);
//		    newValues.put(Key_MomentSex, info.Sex);
//		    newValues.put(Key_MomentAge, info.Age);
//		    newValues.put(Key_MomentShowPhotos, info.ShowPhotos);
//		    
//		    return db.insert(Moment_Table, null, newValues);
//	 }
	  
//	  public long insertGradeInfo(GradeInfo info) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
//		    newValues.put(Key_GradePhone, info.getGradePhone());
//		    newValues.put(Key_GradeActId, info.getGradeActId());
//		    newValues.put(Key_GradeNum, info.getGradeNum());
//		    newValues.put(Key_GradeTime, info.getGradeTime());
//		    
//		    return db.insert(Grade_Table, null, newValues);
//	 }
	  
//	  public long insertFeedBackInfo(FeedBackInfo info) {
//		  ContentValues newValues = new ContentValues();
//		  //ID为自增长的不要添加
////		  newValues.put(Key_FeedBackDBID, info.FeedBackDBID);
//		  newValues.put(Key_FeedBackUserId, info.FeedBackUserId);
//		  newValues.put(Key_FeedBackContent, info.FeedBackContent);
//		  newValues.put(Key_FeedBackTime, info.FeedBackTime);
//		  
//		  return db.insert(FeedBack_Table, null, newValues);
//	  }

//	  public long insertDiscussMoment(DiscussMoment info) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
////		    newValues.put(Key_DiscussId, info.DiscussId);
//		    newValues.put(Key_DiscussUid, info.DiscussUid);
//		    newValues.put(Key_DiscussMessageId, info.DiscussMessageId);
//		    newValues.put(Key_DiscussUserId, info.DiscussUserId);
//		    newValues.put(Key_DiscussUserName, info.DiscussUserName);
//		    newValues.put(Key_DiscussPointId, info.DiscussPointId);
//		    newValues.put(Key_DiscussUploadTime, info.DiscussUploadTime);
//		    newValues.put(Key_DiscussContent, info.DiscussContent);
//		    
//		    return db.insert(DiscussMoment_Table, null, newValues);
//	 }
	  
//	  public long insertRelationActivity(RelationActivity info) {
//		  ContentValues newValues = new ContentValues();
//		  //ID为自增长的不要添加
////		   newValues.put(Key_RelationActivityDBID, info.RelationActivityDBID);
//		  newValues.put(Key_RelationUserID, info.RelationUserID);
//		  newValues.put(Key_RelationActivityID, info.RelationActivityID);
//		  newValues.put(Key_RelationActivityName, info.RelationActivityName);
//		  newValues.put(Key_RelationActivityBuilderUserID, info.RelationActivityBuilderUserID);
//		  newValues.put(Key_RelationActivityStartTime, info.RelationActivityStartTime);
//		  newValues.put(Key_RelationActivityEndTime, info.RelationActivityEndTime);
//		  newValues.put(Key_RelationActivityStartNotifyFlag, info.RelationActivityStartNotifyFlag);
//		  newValues.put(Key_RelationActivityEndNotifyFlag, info.RelationActivityEndNotifyFlag);
//		  newValues.put(Key_RelationActivityStatus, info.RelationActivityStatus);
//		  
//		  return db.insert(RelationActivity_Table, null, newValues);
//	  }
//	  public long insertSearchDistance(SearchDistance info) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
////		    newValues.put(Key_SearchDistanceId, info.DistanceId);
//		    newValues.put(Key_SearchDistanceMomentDis, info.DistanceMomentDis);
//		    newValues.put(Key_SearchDistanceActivityDis, info.DistanceActivityDis);
//		    
//		    return db.insert(SearchDistance_Table, null, newValues);
//	 }
	  
//	  public long insertPushMessage(PushMessage info) {
//		    ContentValues newValues = new ContentValues();
//		    //ID为自增长的不要添加
////		    newValues.put(Key_PushDBID, info.PushDBID);
//		    newValues.put(Key_PushID, info.PushID);
//		    newValues.put(Key_PushUserID, info.PushUserID);
//		    newValues.put(Key_PushMessage, info.PushMessage);
//		    newValues.put(Key_PushStatue, info.PushStatue);
//		    
//		    return db.insert(PushMessage_Table, null, newValues);
//	 }
	  /**
	   * 查询所有的活动信息
	   *@author 祝侦科
	   *2014-5-22
	   * @return
	   */
//	  public ActivitySelectData[] queryAllActivityBases() {  
//		  Cursor results =  db.query(Activity_Table, new String[] { 
//				  Key_ActivityID, Key_BuildActivityUserID, Key_ActivityManagerID, Key_UploadTime,
//				  Key_IsDirectJoinIn, Key_ActivityFlag, Key_ActivityMemberNumber, Key_ActivityMaxMemberNumber,
//				  Key_ActivityDescribe, Key_ActivityName, Key_ActivityStartTime, Key_ActivityEndTime,
//				  Key_ActivityHoldPlace, Key_HelpPhone, Key_ActivityBelongClass, Key_ActivityTags,
//				  Key_ActivityOpinion, Key_ActivityAddress, Key_ActivityLogo}, 
//				  null, null, null, null, null);
//		  return ConvertToActivitySelectDatas(results);   
//	  }
	  /**
	   * 查询所有的活动信息
	   *@author 祝侦科
	   *2014-5-22
	   * @return
	   */
//	  public ActivitySelectData[] queryAllActivityBasesAfterNow() {  
//		  Cursor results =  db.query(Activity_Table, new String[] { 
//				  Key_ActivityID, Key_BuildActivityUserID, Key_ActivityManagerID, Key_UploadTime,
//				  Key_IsDirectJoinIn, Key_ActivityFlag, Key_ActivityMemberNumber, Key_ActivityMaxMemberNumber,
//				  Key_ActivityDescribe, Key_ActivityName, Key_ActivityStartTime, Key_ActivityEndTime,
//				  Key_ActivityHoldPlace, Key_HelpPhone, Key_ActivityBelongClass, Key_ActivityTags,
//				  Key_ActivityOpinion, Key_ActivityAddress, Key_ActivityLogo}, 
//				  Key_ActivityStartTime +">=? ", new String[]{FormatTime.getFormatTime()}, null, null, null);
//		  return ConvertToActivitySelectDatas(results);   
//	  }
	  
	  /**
	   * 查询数据库的前十条活动数据
	   * @return
	   */
//	  public ActivitySelectData[] queryTopTenActivityBases() {
//		  Cursor results = db.query(Activity_Table, new String[] { 
//				  Key_ActivityID, Key_BuildActivityUserID, Key_ActivityManagerID, Key_UploadTime,
//				  Key_IsDirectJoinIn, Key_ActivityFlag, Key_ActivityMemberNumber, Key_ActivityMaxMemberNumber,
//				  Key_ActivityDescribe, Key_ActivityName, Key_ActivityStartTime, Key_ActivityEndTime,
//				  Key_ActivityHoldPlace, Key_HelpPhone, Key_ActivityBelongClass, Key_ActivityTags,
//				  Key_ActivityOpinion, Key_ActivityAddress, Key_ActivityLogo}, 
//				  null, null, null, null, null,String.valueOf(10));
//		  return ConvertToActivitySelectDatas(results);   
//	  }
	  
//	  public MomentBaseInfo[] queryAllMomentBaseInfo() {  
//		  Cursor results =  db.query(Moment_Table, new String[] { 
//				  Key_MomentId, Key_MomentMessageId, Key_MomentUserId, Key_MomentUserName,
//				  Key_MomentUploadTime, Key_MomentContent,Key_MomentPraiseNum, Key_MomentCritizenNum, 
//				  Key_MomentMyUserId,Key_MomentMyUserName, Key_MomentHasOrNo,
//				  Key_MomentLocation,Key_MomentSex,Key_MomentAge,Key_MomentShowPhotos}, 
//				  null, null, null, null, null);
//		  return ConvertToMomentBaseInfo(results);   
//	  }
//	  public MomentBaseInfo[] queryAllMomentBaseInfoTop10() {  
//		  Cursor results =  db.query(Moment_Table, new String[] { 
//				  Key_MomentId, Key_MomentMessageId, Key_MomentUserId, Key_MomentUserName,
//				  Key_MomentUploadTime, Key_MomentContent,Key_MomentPraiseNum, Key_MomentCritizenNum, 
//				  Key_MomentMyUserId,Key_MomentMyUserName, Key_MomentHasOrNo,
//				  Key_MomentLocation,Key_MomentSex,Key_MomentAge,Key_MomentShowPhotos}, 
//				  null, null, null, null, null);
//		  return ConvertToMomentBaseInfoTop10(results);   
//	  }
	  
//	  public UserInfoSelectData[] queryOneUserInfoBase(String phone) {  
//		  Cursor results =  db.query(UserInfo_Table, new String[] { 
//				  Key_UserID, Key_UserName, Key_UserUploadTime, Key_Code,
//				  Key_UserPhone, Key_UserJoinActivity, Key_UserAttentionClass, Key_UserQQ,
//				  Key_UserWeiChat, Key_UserTags, Key_UserClass, Key_UserDescribe,
//				  Key_UserLevel, Key_UserLogo,Key_UserAge,
//				  Key_UserSex,Key_UserSchool,Key_UserProfession,
//				  Key_UserBirthday,Key_UserHome},
//				  Key_UserPhone +"=? ", new String[]{phone}, null, null, null);
//		  return ConvertToUserInfoSelectDatas(results);   
//	  }
	  
	  public MomentBaseInfo[] queryOneMomentBaseInfo(String messageId) {  
		  Cursor results =  db.query(Moment_Table, new String[] { 
				  Key_MomentId, Key_MomentMessageId, Key_MomentUserId, Key_MomentUserName,
				  Key_MomentUploadTime, Key_MomentContent,Key_MomentPraiseNum, Key_MomentCritizenNum, 
				  Key_MomentMyUserId,Key_MomentMyUserName, Key_MomentHasOrNo,
				  Key_MomentLocation,Key_MomentSex,Key_MomentAge,Key_MomentShowPhotos}, 
				  Key_MomentMessageId +"=? ", new String[]{messageId}, null, null, null);
		  return ConvertToMomentBaseInfo(results);   
	  }
	  
	  
//	  public DiscussMoment[] queryOneDiscussMomentInfo(int discussMessageId) {  
//		  Cursor results =  db.query(DiscussMoment_Table, new String[] { 
//				  Key_DiscussId, Key_DiscussUid,Key_DiscussMessageId, 
//				  Key_DiscussUserId, Key_DiscussUserName,Key_DiscussPointId, 
//				  Key_DiscussUploadTime, Key_DiscussContent}, 
//				  Key_DiscussMessageId +"=? ", new String[]{String.valueOf(discussMessageId)}, null, null, null);
//		  return ConvertToDiscussMoments(results);   
//	  }
	  
//	  public RelationActivity[] queryOneRelationActivityInfo(String ActivityID) {  
//		  Cursor results =  db.query(RelationActivity_Table, new String[] { 
//				  Key_RelationActivityDBID, Key_RelationUserID,Key_RelationActivityID, 
//				  Key_RelationActivityName, Key_RelationActivityBuilderUserID,Key_RelationActivityStartTime, 
//				  Key_RelationActivityEndTime, Key_RelationActivityStartNotifyFlag,Key_RelationActivityEndNotifyFlag,
//				  Key_RelationActivityStatus}, 
//				  Key_RelationActivityID +"=? ", new String[]{ActivityID}, null, null, null);
//		  return ConvertToRelationActivitys(results);   
//	  }
	  
	  
//	  public PushMessage[] queryOnePushMessageInfo(String pushId) {  
//		  Cursor results =  db.query(PushMessage_Table, new String[] { 
//				  Key_PushDBID, Key_PushID,Key_PushUserID, 
//				  Key_PushMessage,Key_PushStatue}, 
//				  Key_PushID +"=? ", new String[]{pushId}, null, null, null,null);
//		  return ConvertToPushMessages(results);   
//	  }
	  
//	  public UserInfoSelectData[] queryAllUserInfoBases() {  
//		  Cursor results =  db.query(UserInfo_Table, new String[] { 
//				  Key_UserID, Key_UserName, Key_UserUploadTime, Key_Code,
//				  Key_UserPhone, Key_UserJoinActivity, Key_UserAttentionClass, Key_UserQQ,
//				  Key_UserWeiChat, Key_UserTags, Key_UserClass, Key_UserDescribe,
//				  Key_UserLevel, Key_UserLogo,Key_UserAge,
//				  Key_UserSex,Key_UserSchool,Key_UserProfession,
//				  Key_UserBirthday,Key_UserHome}, 
//				  null, null, null, null, null);
//		  return ConvertToUserInfoSelectDatas(results);   
//	  }
	 
//	  public GradeInfo[] queryAllGradeInfos() {  
//		  Cursor results =  db.query(Grade_Table, new String[] { 
//				  Key_GradeId, Key_GradePhone,Key_GradeActId, 
//				  Key_GradeNum, Key_GradeTime}, 
//				  null, null, null, null, null);
//		  return ConvertToGradeInfos(results);   
//	  }
	  
//	  public FeedBackInfo[] queryAllFeedBackInfos() {  
//		  Cursor results =  db.query(FeedBack_Table, new String[] { 
//				  Key_FeedBackDBID, Key_FeedBackUserId,
//				  Key_FeedBackContent, Key_FeedBackTime}, 
//				  null, null, null, null, null);
//		  return ConvertToFeedBackInfos(results);   
//	  }
	  
	  public GradeInfo[] queryOneGradeInfos(String ActivityID) {  
		  Cursor results =  db.query(Grade_Table, new String[] { 
				  Key_GradeId, Key_GradePhone,Key_GradeActId, 
				  Key_GradeNum, Key_GradeTime}, 
				  Key_GradeActId +"=?", new String[]{ActivityID}, null, null, null);
		  return ConvertToGradeInfos(results);   
	  }
	  
	  
//	  public DiscussMoment[] queryAllDiscussMoments() {  
//		  Cursor results =  db.query(DiscussMoment_Table, new String[] { 
//				  Key_DiscussId, Key_DiscussUid,Key_DiscussMessageId, 
//				  Key_DiscussUserId, Key_DiscussUserName,Key_DiscussPointId, 
//				  Key_DiscussUploadTime, Key_DiscussContent}, 
//				  null, null, null, null, null);
//		  return ConvertToDiscussMoments(results);   
//	  }
	  
//	  public RelationActivity[] queryAllRelationActivitys() {  
//		  Cursor results =  db.query(RelationActivity_Table, new String[] { 
//				  Key_RelationActivityDBID, Key_RelationUserID,Key_RelationActivityID, 
//				  Key_RelationActivityName, Key_RelationActivityBuilderUserID,Key_RelationActivityStartTime, 
//				  Key_RelationActivityEndTime, Key_RelationActivityStartNotifyFlag,Key_RelationActivityEndNotifyFlag,
//				  Key_RelationActivityStatus},
//				  null, null, null, null, null);
//		  return ConvertToRelationActivitys(results);   
//	  }
	  /**
	   * 得到用户主办的活动，用户主办的活动的statue为-1
	   * @return
	   */
//	  public RelationActivity[] queryAllMyHostRelationActivitys() {  
//		  Cursor results =  db.query(RelationActivity_Table, new String[] { 
//				  Key_RelationActivityDBID, Key_RelationUserID,Key_RelationActivityID, 
//				  Key_RelationActivityName, Key_RelationActivityBuilderUserID,Key_RelationActivityStartTime, 
//				  Key_RelationActivityEndTime, Key_RelationActivityStartNotifyFlag,Key_RelationActivityEndNotifyFlag,
//				  Key_RelationActivityStatus},
//				  Key_RelationActivityStatus +"=?", new String[]{"-1"}, null, null, null,null);
//		  return ConvertToRelationActivitys(results);   
//	  }
	  
//	  public SearchDistance[] queryAllSearchDistances() {  
//		  Cursor results =  db.query(SearchDistance_Table, new String[] { 
//				  Key_SearchDistanceId, Key_SearchDistanceMomentDis, 
//				  Key_SearchDistanceActivityDis}, 
//				  null, null, null, null, null);
//		  return ConvertToSearchDistances(results);   
//	  }
	  
//	  public PushMessage[] queryAllPushMessages() {  
//		  Cursor results =  db.query(PushMessage_Table, new String[] { 
//				  Key_PushDBID, Key_PushID,Key_PushUserID, 
//				  Key_PushMessage,Key_PushStatue}, 
//				  null, null, null, null, null);
//		  return ConvertToPushMessages(results);   
//	  }
	  /**
	   * 判断动态数据表中是否存在Key_MomentMessageId = messageId的值
	   * @param messageId：
	   * @return：true：存在，FALSE：不存在
	   */
//	  public boolean judgeIfExistInMomentBase(String messageId){
//		  boolean flag = false;
//		  Cursor results =  db.query(Moment_Table, new String[] { 
//				  Key_MomentId, Key_MomentMessageId, Key_MomentUserId, Key_MomentUserName,
//				  Key_MomentUploadTime, Key_MomentContent,Key_MomentPraiseNum, Key_MomentCritizenNum, 
//				  Key_MomentMyUserId,Key_MomentMyUserName, Key_MomentHasOrNo,
//				  Key_MomentLocation,Key_MomentSex,Key_MomentAge,Key_MomentShowPhotos}, 
//				  Key_MomentMessageId +"=? ", new String[]{messageId}, null, null, null);
//		  if(results != null && results.getCount() > 0){
////			  Log.v("哈哈哈哈", "-----results.getCount()-------"+results.getCount()+"----------messageId-----"+messageId);
//			  flag = true;
//		  }
//		  results.close();
//		  return flag;
//	  }
	  
	  private ActivitySelectData[] ConvertToActivitySelectDatas(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if ((resultCounts == 0) || (!cursor.moveToFirst())){
			  return null;
		  }
		  ActivitySelectData[] bases = new ActivitySelectData[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  String StractivityID = cursor.getString(cursor.getColumnIndex(Key_ActivityID));
			  long activityID = Long.valueOf(StractivityID);
			  String StrbuildActivityUserID = cursor.getString(cursor.getColumnIndex(Key_BuildActivityUserID));
			  long buildActivityUserID = Long.valueOf(StrbuildActivityUserID);
			  String StractivityManagerID = cursor.getString(cursor.getColumnIndex(Key_ActivityManagerID));
			  long activityManagerID = Long.valueOf(StractivityManagerID);
			  
			  String uploadTime = cursor.getString(cursor.getColumnIndex(Key_UploadTime));
			  String isDirectJoinIn = cursor.getString(cursor.getColumnIndex(Key_IsDirectJoinIn));
			  String activityFlag = cursor.getString(cursor.getColumnIndex(Key_ActivityFlag));
			  String activityMemberNumber = cursor.getString(cursor.getColumnIndex(Key_ActivityMemberNumber));
			  String activityMaxMemberNumber = cursor.getString(cursor.getColumnIndex(Key_ActivityMaxMemberNumber));
			  String activityDescribe = cursor.getString(cursor.getColumnIndex(Key_ActivityDescribe));
			  String activityName = cursor.getString(cursor.getColumnIndex(Key_ActivityName));
			  String activityStartTime = cursor.getString(cursor.getColumnIndex(Key_ActivityStartTime));
			  String activityEndTime = cursor.getString(cursor.getColumnIndex(Key_ActivityEndTime));
			  String activityHoldPlace = cursor.getString(cursor.getColumnIndex(Key_ActivityHoldPlace));
			  String helpPhone = cursor.getString(cursor.getColumnIndex(Key_HelpPhone));
			  String activityBelongClass = cursor.getString(cursor.getColumnIndex(Key_ActivityBelongClass));
			  String activityTags = cursor.getString(cursor.getColumnIndex(Key_ActivityTags));
			  String activityOpinion = cursor.getString(cursor.getColumnIndex(Key_ActivityOpinion));
			  String activityAddress = cursor.getString(cursor.getColumnIndex(Key_ActivityAddress));
			  String activityLogo = cursor.getString(cursor.getColumnIndex(Key_ActivityLogo));
			  //赋值
			  bases[i] = new ActivitySelectData(activityID, buildActivityUserID, activityManagerID, uploadTime, isDirectJoinIn, activityFlag, activityMemberNumber, activityMaxMemberNumber, activityDescribe, activityName, activityStartTime, activityEndTime, activityHoldPlace, helpPhone, activityBelongClass, activityTags, activityOpinion, activityAddress, activityLogo);
			  cursor.moveToNext();
		  }	 
		  cursor.close();
		  return bases; 
	  }
	  
	  
	  private UserInfoSelectData[] ConvertToUserInfoSelectDatas(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  UserInfoSelectData[] infos = new UserInfoSelectData[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  long userID = cursor.getInt(cursor.getColumnIndex(Key_UserID));
			  String userName = cursor.getString(cursor.getColumnIndex(Key_UserName));
			  String uploadTime = cursor.getString(cursor.getColumnIndex(Key_UserUploadTime));
			  String code = cursor.getString(cursor.getColumnIndex(Key_Code));
			  String userPhone = cursor.getString(cursor.getColumnIndex(Key_UserPhone));
			  String userJoinActivity = cursor.getString(cursor.getColumnIndex(Key_UserJoinActivity));
			  String userAttentionClass = cursor.getString(cursor.getColumnIndex(Key_UserAttentionClass));
			  String userQQ = cursor.getString(cursor.getColumnIndex(Key_UserQQ));
			  String userWeiChat = cursor.getString(cursor.getColumnIndex(Key_UserWeiChat));
			  String userTags = cursor.getString(cursor.getColumnIndex(Key_UserTags));
			  String userClass = cursor.getString(cursor.getColumnIndex(Key_UserClass));
			  String userDescribe = cursor.getString(cursor.getColumnIndex(Key_UserDescribe));
			  String userLevel = cursor.getString(cursor.getColumnIndex(Key_UserLevel));
			  String userLogo = cursor.getString(cursor.getColumnIndex(Key_UserLogo));
			  String userAge = cursor.getString(cursor.getColumnIndex(Key_UserAge));
			  String userSex = cursor.getString(cursor.getColumnIndex(Key_UserSex));
			  String school = cursor.getString(cursor.getColumnIndex(Key_UserSchool));
			  String profession = cursor.getString(cursor.getColumnIndex(Key_UserProfession));
			  String birthday = cursor.getString(cursor.getColumnIndex(Key_UserBirthday));
			  String home = cursor.getString(cursor.getColumnIndex(Key_UserHome));
			  //赋值
			  infos[i] = new UserInfoSelectData(userID, userName, uploadTime, 
					  code, userPhone, userJoinActivity, userAttentionClass, 
					  userQQ, userWeiChat, userTags, userClass, userDescribe, 
					  userLevel, userLogo, userAge, 
					  userSex, school, profession, 
					  birthday, home);
			  cursor.moveToNext();
		  }	  
		  cursor.close();
		  return infos; 
	  }
	  
	  
	  private MomentBaseInfo[] ConvertToMomentBaseInfo(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  MomentBaseInfo[] infos = new MomentBaseInfo[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  int  momentId = cursor.getInt(cursor.getColumnIndex(Key_MomentId));
			  String messageId = cursor.getString(cursor.getColumnIndex(Key_MomentMessageId));
			  String userId = cursor.getString(cursor.getColumnIndex(Key_MomentUserId));
			  String userName = cursor.getString(cursor.getColumnIndex(Key_MomentUserName));
			  String uploadTime = cursor.getString(cursor.getColumnIndex(Key_MomentUploadTime));
			  String momentContent = cursor.getString(cursor.getColumnIndex(Key_MomentContent));
			  String praiseNum = cursor.getString(cursor.getColumnIndex(Key_MomentPraiseNum));
			  String critizenNum = cursor.getString(cursor.getColumnIndex(Key_MomentCritizenNum));
			  String myUserId = cursor.getString(cursor.getColumnIndex(Key_MomentMyUserId));
			  String myUserName = cursor.getString(cursor.getColumnIndex(Key_MomentMyUserName));
			  String hasOrNo = cursor.getString(cursor.getColumnIndex(Key_MomentHasOrNo));
			  String location = cursor.getString(cursor.getColumnIndex(Key_MomentLocation));
			  String sex = cursor.getString(cursor.getColumnIndex(Key_MomentSex));
			  String age = cursor.getString(cursor.getColumnIndex(Key_MomentAge));
			  String showPhotos = cursor.getString(cursor.getColumnIndex(Key_MomentShowPhotos));
			  //赋值
			  infos[i] = new MomentBaseInfo(momentId, messageId, userId,
					  userName, uploadTime, momentContent, praiseNum, 
					  critizenNum, myUserId, myUserName, hasOrNo, location,
					  sex, age, showPhotos);
			  cursor.moveToNext();
		  }	  
		  cursor.close();
		  return infos; 
	  }
	  
	  /**
	   * 得到本地动态的最后十条
	   * @param cursor
	   * @return
	   */
	  private MomentBaseInfo[] ConvertToMomentBaseInfoTop10(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  
		  /***多余十条**/
		  if(resultCounts > 10){
			  MomentBaseInfo[] infos = new MomentBaseInfo[10];
			  //i保存cursor的下标，index保存infos数组的下标
			  for (int i = resultCounts,index = 0 ; index  < 10; i--,index ++){
				  //取值
				  int  momentId = cursor.getInt(cursor.getColumnIndex(Key_MomentId));
				  String messageId = cursor.getString(cursor.getColumnIndex(Key_MomentMessageId));
				  String userId = cursor.getString(cursor.getColumnIndex(Key_MomentUserId));
				  String userName = cursor.getString(cursor.getColumnIndex(Key_MomentUserName));
				  String uploadTime = cursor.getString(cursor.getColumnIndex(Key_MomentUploadTime));
				  String momentContent = cursor.getString(cursor.getColumnIndex(Key_MomentContent));
				  String praiseNum = cursor.getString(cursor.getColumnIndex(Key_MomentPraiseNum));
				  String critizenNum = cursor.getString(cursor.getColumnIndex(Key_MomentCritizenNum));
				  String myUserId = cursor.getString(cursor.getColumnIndex(Key_MomentMyUserId));
				  String myUserName = cursor.getString(cursor.getColumnIndex(Key_MomentMyUserName));
				  String hasOrNo = cursor.getString(cursor.getColumnIndex(Key_MomentHasOrNo));
				  String location = cursor.getString(cursor.getColumnIndex(Key_MomentLocation));
				  String sex = cursor.getString(cursor.getColumnIndex(Key_MomentSex));
				  String age = cursor.getString(cursor.getColumnIndex(Key_MomentAge));
				  String showPhotos = cursor.getString(cursor.getColumnIndex(Key_MomentShowPhotos));
				  //赋值
				  
				  infos[index] = new MomentBaseInfo(momentId, messageId, userId,
						  userName, uploadTime, momentContent, praiseNum, 
						  critizenNum, myUserId, myUserName, hasOrNo, location,
						  sex, age, showPhotos);
				  cursor.moveToNext();
			  }	  
			  cursor.close();
			  return infos; 
		  }
		  /***少于十条**/
		  else{
			  MomentBaseInfo[] infos2 = new MomentBaseInfo[resultCounts];
			//i保存cursor的下标，index保存infos数组的下标
			  for (int i = resultCounts -1,index = 0 ; i >=0 ; i--,index ++){
				  //取值
				  int  momentId = cursor.getInt(cursor.getColumnIndex(Key_MomentId));
				  String messageId = cursor.getString(cursor.getColumnIndex(Key_MomentMessageId));
				  String userId = cursor.getString(cursor.getColumnIndex(Key_MomentUserId));
				  String userName = cursor.getString(cursor.getColumnIndex(Key_MomentUserName));
				  String uploadTime = cursor.getString(cursor.getColumnIndex(Key_MomentUploadTime));
				  String momentContent = cursor.getString(cursor.getColumnIndex(Key_MomentContent));
				  String praiseNum = cursor.getString(cursor.getColumnIndex(Key_MomentPraiseNum));
				  String critizenNum = cursor.getString(cursor.getColumnIndex(Key_MomentCritizenNum));
				  String myUserId = cursor.getString(cursor.getColumnIndex(Key_MomentMyUserId));
				  String myUserName = cursor.getString(cursor.getColumnIndex(Key_MomentMyUserName));
				  String hasOrNo = cursor.getString(cursor.getColumnIndex(Key_MomentHasOrNo));
				  String location = cursor.getString(cursor.getColumnIndex(Key_MomentLocation));
				  String sex = cursor.getString(cursor.getColumnIndex(Key_MomentSex));
				  String age = cursor.getString(cursor.getColumnIndex(Key_MomentAge));
				  String showPhotos = cursor.getString(cursor.getColumnIndex(Key_MomentShowPhotos));
				  //赋值
				  infos2[index] = new MomentBaseInfo(momentId, messageId, userId,
						  userName, uploadTime, momentContent, praiseNum, 
						  critizenNum, myUserId, myUserName, hasOrNo, location,
						  sex, age, showPhotos);
				  cursor.moveToNext();
			  }	
			  cursor.close();
			  return infos2; 
		  }
		  
		  
	  }
	  
	  private GradeInfo[] ConvertToGradeInfos(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  GradeInfo[] infos = new GradeInfo[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  int  GradeId = cursor.getInt(cursor.getColumnIndex(Key_GradeId));
			  String GradePhone = cursor.getString(cursor.getColumnIndex(Key_GradePhone));
			  String GradeActId = cursor.getString(cursor.getColumnIndex(Key_GradeActId));
			  String GradeNum = cursor.getString(cursor.getColumnIndex(Key_GradeNum));
			  String GradeTime = cursor.getString(cursor.getColumnIndex(Key_GradeTime));
			  
			  //赋值
			  infos[i] = new GradeInfo(GradeId, GradePhone, GradeActId, GradeNum, GradeTime);
			  cursor.moveToNext();
		  }	  
		  cursor.close();
		  return infos; 
	  }
	  
	  private FeedBackInfo[] ConvertToFeedBackInfos(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  FeedBackInfo[] infos = new FeedBackInfo[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  int  feedBackDBID = cursor.getInt(cursor.getColumnIndex(Key_FeedBackDBID));
			  String feedBackUserId = cursor.getString(cursor.getColumnIndex(Key_FeedBackUserId));
			  String feedBackContent = cursor.getString(cursor.getColumnIndex(Key_FeedBackContent));
			  String feedBackTime = cursor.getString(cursor.getColumnIndex(Key_FeedBackTime));
			  
			  //赋值
			  infos[i] = new FeedBackInfo(feedBackDBID, feedBackUserId,
					  feedBackContent, feedBackTime);
			  cursor.moveToNext();
		  }	  
		  cursor.close();
		  return infos; 
	  }
	  
	  private DiscussMoment[] ConvertToDiscussMoments(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  DiscussMoment[] infos = new DiscussMoment[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  int  discussId = cursor.getInt(cursor.getColumnIndex(Key_DiscussId));
			  String discussUid = cursor.getString(cursor.getColumnIndex(Key_DiscussUid));
			  String discussMessageId = cursor.getString(cursor.getColumnIndex(Key_DiscussMessageId));
			  String discussUserId = cursor.getString(cursor.getColumnIndex(Key_DiscussUserId));
			  String discussUserName = cursor.getString(cursor.getColumnIndex(Key_DiscussUserName));
			  String discussPointId = cursor.getString(cursor.getColumnIndex(Key_DiscussPointId));
			  String discussUploadTime = cursor.getString(cursor.getColumnIndex(Key_DiscussUploadTime));
			  String discussContent = cursor.getString(cursor.getColumnIndex(Key_DiscussContent));
			  
			  //赋值
			  infos[i] = new DiscussMoment(discussId, discussUid, discussMessageId, 
					  discussUserId, discussUserName, discussPointId, discussUploadTime, discussContent);
//			  Log.v("哈哈哈哈", "-------手机数据库---DiscussMoment--------"+infos[i].toString());
			  cursor.moveToNext();
		  }	 
		  cursor.close();
		  return infos; 
	  }
	  
	  private RelationActivity[] ConvertToRelationActivitys(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  RelationActivity[] infos = new RelationActivity[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  int  relationActivityDBID = cursor.getInt(cursor.getColumnIndex(Key_RelationActivityDBID));
			  String relationUserID = cursor.getString(cursor.getColumnIndex(Key_RelationUserID));
			  String relationActivityID = cursor.getString(cursor.getColumnIndex(Key_RelationActivityID));
			  String relationActivityName = cursor.getString(cursor.getColumnIndex(Key_RelationActivityName));
			  String relationActivityBuilderUserID = cursor.getString(cursor.getColumnIndex(Key_RelationActivityBuilderUserID));
			  String relationActivityStartTime = cursor.getString(cursor.getColumnIndex(Key_RelationActivityStartTime));
			  String relationActivityEndTime = cursor.getString(cursor.getColumnIndex(Key_RelationActivityEndTime));
			  String relationActivityStartNotifyFlag = cursor.getString(cursor.getColumnIndex(Key_RelationActivityStartNotifyFlag));
			  String relationActivityEndNotifyFlag = cursor.getString(cursor.getColumnIndex(Key_RelationActivityEndNotifyFlag));
			  String relationActivityStatus = cursor.getString(cursor.getColumnIndex(Key_RelationActivityStatus));
			  //赋值
			  infos[i] = new RelationActivity(relationActivityDBID, relationUserID, relationActivityID, 
					  relationActivityName, relationActivityBuilderUserID, relationActivityStartTime,
					  relationActivityEndTime, relationActivityStartNotifyFlag, relationActivityEndNotifyFlag,
					  relationActivityStatus);
			  cursor.moveToNext();
		  }	 
		  cursor.close();
		  return infos; 
	  }
	  
	  private SearchDistance[] ConvertToSearchDistances(Cursor cursor){
		  if(cursor == null){
			  return null;
		  }
		  int resultCounts = cursor.getCount();
		  if (resultCounts == 0 || !cursor.moveToFirst()){
			  return null;
		  }
		  SearchDistance[] infos = new SearchDistance[resultCounts];
		  for (int i = 0 ; i<resultCounts; i++){
			  //取值
			  int  distanceId = cursor.getInt(cursor.getColumnIndex(Key_SearchDistanceId));
			  String distanceMomentDis = cursor.getString(cursor.getColumnIndex(Key_SearchDistanceMomentDis));
			  String distanceActivityDis = cursor.getString(cursor.getColumnIndex(Key_SearchDistanceActivityDis));
			  
			  //赋值
			  infos[i] = new SearchDistance(distanceId, distanceMomentDis, distanceActivityDis);
			  cursor.moveToNext();
		  }	  
//		  cursor.close();
		  return infos; 
	  }
//	  private PushMessage[] ConvertToPushMessages(Cursor cursor){
//		  if(cursor == null){
//			  return null;
//		  }
//		  int resultCounts = cursor.getCount();
//		  if (resultCounts == 0 || !cursor.moveToFirst()){
//			  return null;
//		  }
//		  PushMessage[] infos = new PushMessage[resultCounts];
//		  for (int i = 0 ; i<resultCounts; i++){
//			  //取值
//			  int  pushDBID = cursor.getInt(cursor.getColumnIndex(Key_PushDBID));
//			  String pushID = cursor.getString(cursor.getColumnIndex(Key_PushID));
//			  String pushUserID = cursor.getString(cursor.getColumnIndex(Key_PushUserID));
//			  String pushMessage = cursor.getString(cursor.getColumnIndex(Key_PushMessage));
//			  String pushStatue = cursor.getString(cursor.getColumnIndex(Key_PushStatue));
//			  
//			  //赋值
//			  PushMessage pp =  new PushMessage();
//			  pp.PushDBID = pushDBID;
//			  pp.PushID = pushID;
//			  pp.PushUserID = pushUserID;
//			  pp.PushMessage = pushMessage;
//			  pp.PushStatue = pushStatue;
//			  infos[i] = pp;
//			  cursor.moveToNext();
//		  }	  
//		  cursor.close();
//		  return infos; 
//	  }
	  
	  /**
	   * 根据活动号删除保存自己参加的活动
	   *@author 祝侦科
	   *2014-5-22
	   * @param ActivityID
	   * @return:0:没有删除，其他的是返回删除的行数
	   */
//	  public long deleteOneActivity(int ActivityID) {
//		  return db.delete(Activity_Table,  Key_ActivityID + "=?" , new String[]{String.valueOf(ActivityID)});
//	  }
//	  public long deleteOneUserInfo(int UserID) {
//		  return db.delete(UserInfo_Table,  Key_UserID + "=?" , new String[]{String.valueOf(UserID)});
//	  }
//	  public long deleteOnePushMessage(String PushID) {
//		  return db.delete(PushMessage_Table,  Key_PushID + "=?" , new String[]{PushID});
//	  }
//	  public long deleteOneMomentBaseInfo(int messageId) {
//		  return db.delete(Moment_Table,  Key_MomentMessageId + "=?" , new String[]{String.valueOf(messageId)});
//	  }
	  	  
//	  public long deleteAllActivitys() {
//		  return db.delete(Activity_Table, null, null);
//	  }
	  
	  //删除活动已经过期了的数据
//	  public long deleteAllActivitysBeforeNow() {
//		  return db.delete(Activity_Table, Key_ActivityStartTime +"<=? ", new String[]{FormatTime.getFormatTime()});
//	  }
	  
	  /**
	   * 删除用户举办的活动
	   * @return
	   */
//	  public long deleteUserHostRelationActivitys() {
//		  return db.delete(RelationActivity_Table, Key_RelationActivityStatus +"=? ", new String[]{"-1"});
//	  }

	  /**
	   * 删除用户关联的活动
	   * @return
	   */
//	  public long deleteUserRelationActivitysOne() {
//		  return db.delete(RelationActivity_Table, Key_RelationActivityStatus +"=? ", new String[]{"1"});
//	  }
	  /**
	   * 删除用户关联的活动
	   * @return
	   */
//	  public long deleteUserRelationActivitysThree() {
//		  return db.delete(RelationActivity_Table, Key_RelationActivityStatus +"=? ", new String[]{"3"});
//	  }
	  /**
	   * 删除用户关联的活动
	   * @return
	   */
//	  public long deleteUserRelationActivitysFour() {
//		  return db.delete(RelationActivity_Table, Key_RelationActivityStatus +"=? ", new String[]{"4"});
//	  }
	  
//	  public long deleteAllUserInfoBases() {
//		  return db.delete(UserInfo_Table, null, null);
//	  }
	  
//	  public long deleteAllMomentBaseInfos() {
//		  return db.delete(Moment_Table, null, null);
//	  }
	  
//	  public long deleteAllGradeInfos() {
//		  return db.delete(Grade_Table, null, null);
//	  }
	  
//	  public long deleteAllFeedBackInfos() {
//		  return db.delete(FeedBack_Table, null, null);
//	  }
	  
//	  public long deleteAllDiscussMoments() {
//		  return db.delete(DiscussMoment_Table, null, null);
//	  }
//	  public long deleteAllRelationActivitys() {
//		  return db.delete(RelationActivity_Table, null, null);
//	  }
//	  public long deleteAllSearchDistances() {
//		  return db.delete(SearchDistance_Table, null, null);
//	  }
	  
//	  public long deleteAllPushMessages() {
//		  return db.delete(PushMessage_Table, null, null);
//	  }
	  
	  
	  
		/** 静态Helper类，用于建立、更新和打开数据库*/
	  public  static class DBOpenHelper extends SQLiteOpenHelper {

		  public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		    super(context, name, factory, version);
		  }
		  private static final String Create_ActivityBase = "create table " + 
				  Activity_Table + " (" + Key_ActivityDBID + " integer primary key autoincrement, " 
				  +Key_ActivityID+ " varchar unique, "
				    + Key_BuildActivityUserID+ " varchar, " + Key_ActivityManagerID+ " varchar,"
				    + Key_UploadTime+ " varchar,"+ Key_IsDirectJoinIn+ " varchar,"+ Key_ActivityFlag+ " varchar,"
				    + Key_ActivityMemberNumber+ " varchar,"+ Key_ActivityMaxMemberNumber+ " varchar,"+ Key_ActivityDescribe+ " varchar,"
				    + Key_ActivityName+ " varchar,"+ Key_ActivityStartTime+ " varchar,"+ Key_ActivityEndTime+ " varchar,"
				    + Key_ActivityHoldPlace+ " varchar,"+ Key_HelpPhone+ " varchar,"+ Key_ActivityBelongClass+ " varchar,"
				    + Key_ActivityTags+ " varchar,"+ Key_ActivityOpinion+ " varchar,"+ Key_ActivityAddress+ " varchar,"
				    +Key_ActivityLogo + " varchar);";
		  private static final String Create_UserInfoBase= "create table " + 
				  UserInfo_Table + " (" + Key_UserID + " integer primary key autoincrement, " 
				    + Key_UserName+ " varchar,"+ Key_UserUploadTime+ " varchar,"+ Key_Code+ " varchar,"
				    + Key_UserPhone+ " varchar,"+ Key_UserJoinActivity+ " varchar,"+ Key_UserAttentionClass+ " varchar,"
				    + Key_UserQQ+ " varchar,"+ Key_UserWeiChat+ " varchar,"+ Key_UserTags+ " varchar,"
				    + Key_UserClass+ " varchar,"+ Key_UserDescribe+ " varchar,"+ Key_UserLevel+ " varchar,"
				    + Key_UserLogo+ " varchar,"+ Key_UserAge+ " varchar,"+ Key_UserSex+ " varchar,"
				    + Key_UserSchool+ " varchar,"+ Key_UserProfession+ " varchar,"+ Key_UserBirthday+ " varchar,"
				    +Key_UserHome + " varchar);";
		  
		  
		  private static final String Create_MomentBaseInfo= "create table " + 
				  Moment_Table + " (" + Key_MomentId + " integer primary key autoincrement, " 
				    + Key_MomentMessageId+ " varchar unique,"+ Key_MomentUserId+ " varchar,"+ Key_MomentUserName+ " varchar,"
				    + Key_MomentUploadTime+ " varchar,"+ Key_MomentContent+ " varchar,"+ Key_MomentPraiseNum+ " varchar,"
				    + Key_MomentCritizenNum+ " varchar,"+ Key_MomentMyUserId+ " varchar,"+ Key_MomentMyUserName+ " varchar,"
				    + Key_MomentHasOrNo+ " varchar,"+ Key_MomentLocation+ " varchar,"+ Key_MomentSex+ " varchar,"
				    + Key_MomentAge+ " varchar,"+Key_MomentShowPhotos + " varchar);";
		  
		  private static final String Create_GradeInfo= "create table " + 
				  Grade_Table + " (" + Key_GradeId + " integer primary key autoincrement, " 
				    + Key_GradePhone+ " varchar,"+ Key_GradeActId+ " varchar,"+ Key_GradeNum+ " varchar,"
				    +Key_GradeTime + " varchar);";
		  
//		  private static final String Create_DiscussMomentInfo= "create table " + 
//				  DiscussMoment_Table + " (" + Key_DiscussId + " integer primary key autoincrement, " 
//				    + Key_DiscussUid+ " varchar unique,"+ Key_DiscussMessageId+ " varchar,"+ Key_DiscussUserId+ " varchar,"
//				    + Key_DiscussUserName+ " varchar,"+ Key_DiscussPointId+ " varchar,"+ Key_DiscussUploadTime+ " varchar,"
//				    +Key_DiscussContent + " varchar);";
		  
		  private static final String Create_SearchDistanceInfo= "create table " + 
				  SearchDistance_Table + " (" + Key_SearchDistanceId + " integer primary key autoincrement, " 
				    + Key_SearchDistanceMomentDis+ " varchar,"
				    +Key_SearchDistanceActivityDis + " varchar);";
		  
		  private static final String Create_PushMessageInfo= "create table " + 
				  PushMessage_Table + " (" + Key_PushDBID + " integer primary key autoincrement, " 
				  + Key_PushID+ " varchar unique,"+ Key_PushUserID+ " varchar,"+ Key_PushMessage+ " varchar,"
				  +Key_PushStatue + " varchar);";
		  
		  private static final String Create_RelationActivityInfo= "create table " + 
				  RelationActivity_Table + " (" + Key_RelationActivityDBID + " integer primary key autoincrement, " 
				  + Key_RelationActivityID+ " varchar unique,"+ Key_RelationUserID+ " varchar,"+ Key_RelationActivityName+ " varchar,"
				  + Key_RelationActivityBuilderUserID+ " varchar,"+ Key_RelationActivityStartTime+ " varchar,"
				  + Key_RelationActivityEndTime+ " varchar,"+ Key_RelationActivityStartNotifyFlag+ " varchar,"
				  + Key_RelationActivityEndNotifyFlag+ " varchar,"
				  +Key_RelationActivityStatus + " varchar);";
		  
		  private static final String Create_FeedBackInfo= "create table " + 
				  FeedBack_Table + " (" + Key_FeedBackDBID + " integer primary key autoincrement, " 
				  + Key_FeedBackUserId+ " varchar,"+ Key_FeedBackContent+ " varchar,"
				  +Key_FeedBackTime + " varchar);";
		  
		  @Override
		  public void onCreate(SQLiteDatabase _db) {
			  
			_db.execSQL(Create_ActivityBase);
			_db.execSQL(Create_UserInfoBase);
			_db.execSQL(Create_MomentBaseInfo);
			_db.execSQL(Create_GradeInfo);
//			_db.execSQL(Create_DiscussMomentInfo);
			_db.execSQL(Create_SearchDistanceInfo);
			_db.execSQL(Create_PushMessageInfo);
			_db.execSQL(Create_RelationActivityInfo);
			_db.execSQL(Create_FeedBackInfo);
		  }

		  @Override
		  public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {		    
//		    _db.execSQL("DROP TABLE IF EXISTS " + INFO_TABLE);
//		    _db.execSQL("DROP TABLE IF EXISTS " + Activity_Table);
//		    _db.execSQL("DROP TABLE IF EXISTS "+ UserInfo_Table);
		    onCreate(_db);
		  }
		}

}
