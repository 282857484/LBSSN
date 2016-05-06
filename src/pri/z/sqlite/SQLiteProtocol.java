package pri.z.sqlite;

public interface SQLiteProtocol {

	int insertCommonData = 3000;
	int deleteCommonData = 3001;
	int updateCommonData = 3002;
	
	/**
	 * 查询用户信息
	 */
	int queryUserData = 3003;
	
	/**
	 * 查询保存的活动集合
	 */
	int queryActivityData = 3004;
	
	/**
	 * 查询保存的动态信息集合：最新的十条
	 */
	int queryMomentData = 3005;
	
	/**
	 * 在历史活动中查找相关活动
	 */
	int queryRelationActivitysDataInHis = 3006;
	
	/**
	 * 在历史活动中查找相关活动
	 */
	int queryRelationActivitysDataInMy = 3007;
	
	
	/**
	 *搜索范围 
	 */
	int queryAllSearchDistances = 3008;
	/**
	 *搜索范围 
	 */
	int queryAllSearchDistancesInMainActivity = 3009;
	
	/**
	 *推送消息
	 */
	int qureyAllPushMessages = 3010;
	
	/**
	 *在侧栏发送用户信息
	 */
	int queryUserInMainLeft = 3011;
}
