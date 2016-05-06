package pub.infoclass.pushservice;

import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.updateActivityOpinion_independent_C;
import pub.infoclass.myserver.protocol.updateActivityOpinion_independent_S;

public class SEND_UserOpinion {

	private int p = h_protocol_pusher.SEND_UserOpinion;
	private updateActivityOpinion_independent_C uaoic; // 成员用户发送的信息包
	private updateActivityOpinion_independent_S uaois; // 成员用户接受的信息包
	private UserInfoSelectData uisd; //评分人信息
	private ActivitySelectData asd;
	
	public updateActivityOpinion_independent_C getUaoic() {
		return uaoic;
	}
	public void setUaoic(updateActivityOpinion_independent_C uaoic) {
		this.uaoic = uaoic;
	}
	public updateActivityOpinion_independent_S getUaois() {
		return uaois;
	}
	public void setUaois(updateActivityOpinion_independent_S uaois) {
		this.uaois = uaois;
	}
	public int getP() {
		return p;
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
