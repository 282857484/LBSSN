package pri.z.sqlite;

public class DeleteObject {

	String table;
	String whereClause; 
	String[] whereArgs;
	public DeleteObject(String table, String whereClause, String[] whereArgs) {
		super();
		this.table = table;
		this.whereClause = whereClause;
		this.whereArgs = whereArgs;
	}
	
	
}
