package pri.z.sqlite;

import java.util.Map;

import pri.z.main.MainActivity;
import pri.z.mydb.PushMessage;
import pri.z.mydb.RelationActivity;
import pri.z.show.MyMessage;
import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.infoclass.pushservice.h_protocol_pusher;
import pub.netservice.GsonInstance;
import pub.util.FormatTime;
import pub.util.notification;
import pub.util.timeCha;
import android.content.ContentValues;
import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

public class CheckPushMessageNow extends Thread{
	
	String TAG = "哈哈哈哈";
	
	Context mContext;
	public CheckPushMessageNow(Context mContext){
		this.mContext = mContext;
	}
	
	@Override
	public void run(){
		Looper.prepare();
		while(true){
			CheckPushMessage();
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		Looper.loop();
	}
	
	
	/**
	 * 查询是否有用户的活动消息需要推送
	 * 1:活动开始前一小时通知；活动结束后通知评分
	 */
	private void CheckPushMessage() {
		// TODO Auto-generated method stub
		Map<String,Object> mapPushs = SEMapApplication.MapPushMessage;
		if(mapPushs == null )
			return;
		if(mapPushs.size() <= 0)
			return;
		 
//		//UI更新   0915测试
//		Message msg2 = Message.obtain();
//		msg2.what = z_baiduprotocol.noticeRedPointPro;
//		msg2.obj = 12;
//		try {
//			if(MainActivity.myMessenger != null)
//				MainActivity.myMessenger.send(msg2);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//得到这里的pushID
		int myPushID = 0;
		if(mapPushs != null && mapPushs.size() > 0){
			myPushID = mapPushs.size()*(-2);
		}
		
		int number = 0;
		//查看是否有用户没有读到的消息
		for(int index = 0;index < mapPushs.size();index ++){
			PushMessage push = (PushMessage)mapPushs.get("MapPushMessage"+index);
			if(push.PushStatue.equals("1"))
				number++;
		}
		//UI更新
		Message msg = Message.obtain();
		msg.what = z_baiduprotocol.noticeRedPointPro;
		msg.obj = number;
		try {
			if(MainActivity.myMessenger != null)
				MainActivity.myMessenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/****推送活动开始和结束的信息***/
//		DBAdapter dbAdapterPush = new DBAdapter(mContext);
//		dbAdapterPush.open();
//		RelationActivity[] res = dbAdapterPush.queryAllRelationActivitys();
//		dbAdapterPush.close();
		Map<String,Object> mapRels = SEMapApplication.MapRelationActivity;
		if(mapRels == null )
			return;
		if(mapRels.size() <= 0)
		for(int index = 0;index < mapRels.size();index++){
			RelationActivity re =  (RelationActivity)mapPushs.get("MapRelationActivity"+index);
			if(re.RelationActivityStartNotifyFlag.equals("1")){
				//要开始的通知
				long cha = timeCha.TimeCha(FormatTime.getFormatTime()
						, re.RelationActivityStartTime);
				if(cha > 0 && cha < 60*60*1000){
					notification.showActivityStartInHourNotify(MyMessage.class , re);
					//修改本地该数据为已通知
//					DBAdapter dbAdapterStart = new DBAdapter(mContext);
//					dbAdapterStart.open();
//					dbAdapterStart.updateOneRelationActivityStart(re.RelationActivityID);
//					dbAdapterStart.close();
					
					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValuesUpdate = ContentValuesChange
							.contentUpdateOneRelationActivityStart();
					String whereClause = ContentValuesChange.Key_RelationActivityID +"=? "; 
					String[] whereArgs = new String[]{re.RelationActivityID};
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.RelationActivity_Table,newValuesUpdate, whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);
					
					//将消息加入到PushMessage里面
					re.p = h_protocol_pusher.Z_Send_ActivityStart;
					String json = GsonInstance.getG().toJson(re);
					int pushID = myPushID-1;
					PushMessage push = new PushMessage(0, pushID+"","", json, "1");
//					dbAdapterStart.insertPushMessage(push);
//					dbAdapterStart.close();
					
					ContentValues newValues = ContentValuesChange
							.contentInsertPushMessage(push);
					SQLInfo sqlInfo = new SQLInfo();
					sqlInfo.p = SQLiteProtocol.insertCommonData;
					sqlInfo.SQLInfo = new InsertObject(
							ContentValuesChange.PushMessage_Table, null, newValues);
					sqlSecurityThread.handleSQl(sqlInfo);
					
					
				}
			}
			
			if(re.RelationActivityEndNotifyFlag.equals("1")){
				//已经结束的通知
				long cha2 = pub.util.timeCha.TimeCha(FormatTime.getFormatTime()
						, re.RelationActivityEndTime) ;
				if(cha2 < 0){
					if(re.RelationActivityBuilderUserID.equals(re.RelationUserID)){
						//如果是群主，那么就不推送活动结束的消息
						return;
					}
					notification.showActivityEndGradeNotify(MyMessage.class , re);
					//修改本地该数据为已通知
//					DBAdapter dbAdapterEnd = new DBAdapter(mContext);
//					dbAdapterEnd.open();
//					dbAdapterEnd.updateOneRelationActivityEnd(re.RelationActivityID);
					
					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValuesUpdate = ContentValuesChange
							.contentUpdateOneRelationActivityEnd();
					String whereClause = ContentValuesChange.Key_RelationActivityID +"=? "; 
					String[] whereArgs = new String[]{re.RelationActivityID};
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.RelationActivity_Table,newValuesUpdate, whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);
					
					//将消息加入到PushMessage里面
					re.p = h_protocol_pusher.Z_Send_ActivityEnd;
					String json = GsonInstance.getG().toJson(re);
					int pushID = myPushID-2;
					PushMessage push = new PushMessage(0, pushID+"","", json, "1");
//					dbAdapterEnd.insertPushMessage(push);
//					dbAdapterEnd.close();
					
					ContentValues newValues = ContentValuesChange
							.contentInsertPushMessage(push);
					SQLInfo sqlInfo = new SQLInfo();
					sqlInfo.p = SQLiteProtocol.insertCommonData;
					sqlInfo.SQLInfo = new InsertObject(
							ContentValuesChange.PushMessage_Table, null, newValues);
					sqlSecurityThread.handleSQl(sqlInfo);
					
				}
			}
			
		}
		
	}
}
