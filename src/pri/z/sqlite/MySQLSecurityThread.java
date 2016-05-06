package pri.z.sqlite;

import pri.z.mydb.PushMessage;
import pri.z.mydb.RelationActivity;
import pri.z.mydb.SearchDistance;
import pri.z.utils.MomentBaseInfo;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.UserInfoSelectData;
import android.content.Context;
import android.database.Cursor;

public class MySQLSecurityThread extends sqlSecurityThread {

	public MySQLSecurityThread(Context context) {
		super.setContext(context);
	}

	@Override
	protected long handleUPDATE(UpdateObject updateObj) {
		// TODO Auto-generated method stub
		super.open();
		long resultMark = db.update(updateObj.table, updateObj.values,
				updateObj.whereClause, updateObj.whereArgs);
		super.close();
		return resultMark;
	}

	@Override
	protected Object handleQUERY(QueryObject queryObj) {
		// TODO Auto-generated method stub
		super.open();
		Cursor resultSet = db.query(queryObj.table, queryObj.columns,
				queryObj.selection, queryObj.selectionArgs, queryObj.groupBy,
				queryObj.having, queryObj.orderBy);
		Object obj = null;
		if (queryObj.table.equals(ContentValuesChange.UserInfo_Table)) {
			UserInfoSelectData[] users = ContentValuesChange
					.ConvertToUserInfoSelectDatas(resultSet);
			obj = users;
		} else if (queryObj.table.equals(ContentValuesChange.Activity_Table)) {
			ActivitySelectData[] acts = ContentValuesChange
					.ConvertToActivitySelectDatas(resultSet);
			obj = acts;
		} else if (queryObj.table.equals(ContentValuesChange.Moment_Table)) {
			MomentBaseInfo[] moments = ContentValuesChange
					.ConvertToMomentBaseInfoAll(resultSet);
			obj = moments;
		}else if(queryObj.table.equals(ContentValuesChange.RelationActivity_Table)) {
			RelationActivity[] rels = ContentValuesChange
					.ConvertToRelationActivitys(resultSet);
			obj = rels;
		}else if(queryObj.table.equals(ContentValuesChange.SearchDistance_Table)) {
			SearchDistance[] searchs = ContentValuesChange
					.ConvertToSearchDistances(resultSet);
			obj = searchs;
		}else if(queryObj.table.equals(ContentValuesChange.PushMessage_Table)) {
			PushMessage[] searchs = ContentValuesChange
					.ConvertToPushMessages(resultSet);
			obj = searchs;
		}

		super.close();
		return obj;
	}

	@Override
	protected long handleDELETE(DeleteObject deleteObj) {
		// TODO Auto-generated method stub
		super.open();
		long resultMark = db.delete(deleteObj.table, deleteObj.whereClause,
				deleteObj.whereArgs);
		super.close();
		return resultMark;
	}

	@Override
	protected long handleINSERT(InsertObject insertObj) {
		// TODO Auto-generated method stub
		super.open();
		long resultMark = db.insert(insertObj.table, insertObj.nullColumnHack,
				insertObj.values);
		super.close();
		return resultMark;
	}

}
