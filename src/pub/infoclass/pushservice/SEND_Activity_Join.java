package pub.infoclass.pushservice;

import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;

public class SEND_Activity_Join {

	private int p = h_protocol_pusher.SEND_Activity_Join;
	private RelationSelectData rsd; // 关系ID
	private UserInfoSelectData uisd; // 申请人的详细信息
	private ActivitySelectData asd;
	public int getP() {
		return p;
	}
	public void setP(int p) {
		this.p = p;
	}
	public RelationSelectData getRsd() {
		return rsd;
	}
	public void setRsd(RelationSelectData rsd) {
		this.rsd = rsd;
	}
	public UserInfoSelectData getUisd() {
		return uisd;
	}
	public void setUisd(UserInfoSelectData uisd) {
		this.uisd = uisd;
	}
	public ActivitySelectData getAsd() {
		return asd;
	}
	public void setAsd(ActivitySelectData asd) {
		this.asd = asd;
	}
}
