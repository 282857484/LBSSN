package pub.infoclass.pushservice;


import java.util.List;

import pub.infoclass.db.PushMessageSelectData;

public class PushMessageListObject {
	private int p = h_protocol_pusher.PushMessageListObject;
	private List<PushMessageSelectData> pmsdList;
	public List<PushMessageSelectData> getPmsdList() {
		return pmsdList;
	}
	public void setPmsdList(List<PushMessageSelectData> pmsdList) {
		this.pmsdList = pmsdList;
	}
	public int getP() {
		return p;
	}
	public void setP(int p) {
		this.p = p;
	}

}
