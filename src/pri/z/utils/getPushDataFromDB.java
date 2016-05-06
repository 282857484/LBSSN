package pri.z.utils;

import java.util.Map;

import pri.z.mydb.PushMessage;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import pub.application.SEMapApplication;
import android.content.ContentValues;

public class getPushDataFromDB {

	
	/**
	 * 判定手机数据是否含有改pushId
	 * @param pushID
	 * @return
	 */
	public boolean IfContainPushID(String pushID){
		boolean flag = false;
		
//		DBAdapter dbAdapter = new DBAdapter(mContext);
//		dbAdapter.open();
//		PushMessage[] pushes = dbAdapter.queryAllPushMessages();
//		//关闭数据库连接
//		dbAdapter.close();
		
		Map<String,Object> maps =   SEMapApplication.MapPushMessage;
		if(maps == null)
			return false;
		if(maps.size() <= 0)
			return false;
		PushMessage[] pushes = new PushMessage[maps.size()];
		for(int i=0;i<maps.size();i++){
			pushes[i] = (PushMessage) maps.get("MapPushMessage"+i);
		}
		
		if(pushes == null)
			return false;
		
		if(pushes.length <= 0)
			return false;
		for(int index = 0 ;index <pushes.length;index++){
			if(pushes[index].PushID.equals(pushID)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 插入数据到数据库中
	 * @param push
	 * @return
	 */
	public void  insertPushMessage(PushMessage push){
//		DBAdapter dbAdapter = new DBAdapter(mContext);
//		dbAdapter.open();
//		
//		long count = dbAdapter.insertPushMessage(push);
//		//关闭数据库连接
//		dbAdapter.close();
		
		ContentValues newValues = ContentValuesChange
				.contentInsertPushMessage(push);
		SQLInfo sqlInfo = new SQLInfo();
		sqlInfo.p = SQLiteProtocol.insertCommonData;
		sqlInfo.SQLInfo = new InsertObject(
				ContentValuesChange.PushMessage_Table, null, newValues);
		sqlSecurityThread.handleSQl(sqlInfo);
		
	}
	
	
}
