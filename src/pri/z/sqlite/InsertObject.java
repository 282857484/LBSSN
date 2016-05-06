package pri.z.sqlite;

import android.content.ContentValues;

public class InsertObject {

	String table;
	String nullColumnHack;
	ContentValues values;
	public InsertObject(String table, String nullColumnHack,
			ContentValues values) {
		super();
		this.table = table;
		this.nullColumnHack = nullColumnHack;
		this.values = values;
	}
	
	
}
