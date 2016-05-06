package pub.infoclass.myserver.protocol;

import pub.util.FormatTime;


public class CheckVersionAndOtherInfo_C {

	private int p = protocolfromclient.CheckVersionAndOtherInfo_C;
	private String UserID = "0";
	private String UploadTime = "0";
	private String Version = "0";
	private String MacAddress = "0";
	private String UUID = "0";
	public void setStandardUploadTime() {
		UploadTime = FormatTime.getFormatTime();
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public int getP() {
		return p;
	}
	public String getUploadTime() {
		return UploadTime;
	}
	public void setUploadTime(String uploadTime) {
		UploadTime = uploadTime;
	}
	public String getMacAddress() {
		return MacAddress;
	}
	public void setMacAddress(String macAddress) {
		MacAddress = macAddress;
	}
	
	
}
