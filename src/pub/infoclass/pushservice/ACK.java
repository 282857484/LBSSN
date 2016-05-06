package pub.infoclass.pushservice;

public class ACK {

	private int p = h_protocol_pusher.ACK;
	private String UserID;
	private String PushID;
	public ACK(String userID, String pushID) {
		super();
		UserID = userID;
		PushID = pushID;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getPushID() {
		return PushID;
	}

	public void setPushID(String pushID) {
		PushID = pushID;
	}

}
