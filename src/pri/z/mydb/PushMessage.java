package pri.z.mydb;

public class PushMessage {
	//后来的规范的
	public int PushDBID;//自增长，存放在数据库中
	public String PushID;//推送得到消息ID
	public String PushUserID;//要推送给的用户Id
	public String PushMessage;//推送信息
	public String PushStatue;//用户是否已读：1表示未读，2表示已读
	
	public PushMessage(){
		
	}


	public PushMessage(int pushDBID, String pushID, String pushUserID,
			String pushMessage, String pushStatue) {
		super();
		PushDBID = pushDBID;
		PushID = pushID;
		PushUserID = pushUserID;
		PushMessage = pushMessage;
		PushStatue = pushStatue;
	}


	@Override
	public String toString() {
		return "PushMessage [PushDBID=" + PushDBID + ", PushID=" + PushID
				+ ", PushUserID=" + PushUserID + ", PushMessage=" + PushMessage
				+ ", PushStatue=" + PushStatue + "]";
	}


	
	
}
