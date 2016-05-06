package pri.z.sqlite;

import android.content.ContentValues;

public class UpdateObject {

	String table;
	ContentValues values;
	String whereClause;
	String[] whereArgs;
	public UpdateObject(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		super();
		this.table = table;
		this.values = values;
		this.whereClause = whereClause;
		this.whereArgs = whereArgs;
	}
	
	
}
