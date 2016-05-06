package pri.z.sqlite;

import pub.util.FormatTime;

public class QueryUserRequest {

	public static  void queryUser() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryUserData;
		String table = ContentValuesChange.UserInfo_Table;
		String[] columns = ContentValuesChange.contentQueryAllUserInfoBases();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	public static  void queryUserInMainLeft() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryUserInMainLeft;
		String table = ContentValuesChange.UserInfo_Table;
		String[] columns = ContentValuesChange.contentQueryAllUserInfoBases();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	
	public static  void queryActivity() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryActivityData;
		String table = ContentValuesChange.Activity_Table;
		String[] columns = ContentValuesChange.contentQueryAllActivityBasesAfterNow();
		String selection = ContentValuesChange.Key_ActivityStartTime +">=? ";
		String[] selectionArgs = new String[]{FormatTime.getFormatTime()};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	
//	public static  void queryHotActivity() {
//		// TODO Auto-generated method stub
//		SQLInfo sqlInfoQuery = new SQLInfo();
//		sqlInfoQuery.p = SQLiteProtocol.queryActivityData;
//		String table = ContentValuesChange.Activity_Table;
//		String[] columns = ContentValuesChange.contentQueryAllActivityBasesAfterNow();
//		String selection = ContentValuesChange.Key_ActivityStartTime +">=? ";
//		String[] selectionArgs = new String[]{FormatTime.getFormatTime()};
//		String groupBy = null;
//		String having = null;
//		String orderBy = null;
//		sqlInfoQuery.SQLInfo = new QueryObject(table, 
//				columns, selection, selectionArgs, groupBy, having, orderBy);
//		sqlSecurityThread.handleSQl(sqlInfoQuery);
//	}
	
	public static  void queryMoments() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryMomentData;
		String table = ContentValuesChange.Moment_Table;
		String[] columns = ContentValuesChange.contentQueryAllMomentBaseInfoTop10();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	public static  void queryRelationActivitysDataInHis() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryRelationActivitysDataInHis;
		String table = ContentValuesChange.RelationActivity_Table;
		String[] columns = ContentValuesChange.contentQueryAllRelationActivitys();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	public static  void queryRelationActivitysDataInMy() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryRelationActivitysDataInMy;
		String table = ContentValuesChange.RelationActivity_Table;
		String[] columns = ContentValuesChange.contentQueryAllRelationActivitys();
		String selection = ContentValuesChange.Key_RelationActivityStatus +"=?";
		String[] selectionArgs = new String[]{"-1"};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	public static  void qureyAllSearchDistances() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryAllSearchDistances;
		String table = ContentValuesChange.SearchDistance_Table;
		String[] columns = ContentValuesChange.contentQueryAllSearchDistances();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	public static  void qureyAllSearchDistancesInMainActivity() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.queryAllSearchDistancesInMainActivity;
		String table = ContentValuesChange.SearchDistance_Table;
		String[] columns = ContentValuesChange.contentQueryAllSearchDistances();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
	
	public static  void qureyAllPushMessages() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoQuery = new SQLInfo();
		sqlInfoQuery.p = SQLiteProtocol.qureyAllPushMessages;
		String table = ContentValuesChange.PushMessage_Table;
		String[] columns = ContentValuesChange.contentQueryAllPushMessages();
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		sqlInfoQuery.SQLInfo = new QueryObject(table, 
				columns, selection, selectionArgs, groupBy, having, orderBy);
		sqlSecurityThread.handleSQl(sqlInfoQuery);
	}
}
