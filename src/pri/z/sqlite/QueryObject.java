package pri.z.sqlite;

public class QueryObject {

	String table;
	String[] columns;
	String selection;
	String[] selectionArgs;
	String groupBy;
	String having;
	String orderBy;
	public QueryObject(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		super();
		this.table = table;
		this.columns = columns;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.groupBy = groupBy;
		this.having = having;
		this.orderBy = orderBy;
	}
	
	
}
