package pri.z.utils;

import java.io.Serializable;


public class DiscussMoment  implements Serializable{

	public int DiscussId;//自增长
	public String DiscussUid;//评论在百度数据库的id:唯一
	public String DiscussMessageId;//用户评论的动态Id
	public String DiscussUserId;//该评论的用户Id
	public String DiscussUserName;//该评论的用户昵称
	public String DiscussPointId;//指向Id
	public String DiscussUploadTime;//发表时间
	public String DiscussContent;//评论内容
	public DiscussMoment() {
		super();
	}
	public DiscussMoment(int discussId, String discussUid,
			String discussMessageId, String discussUserId,
			String discussUserName, String discussPointId,
			String discussUploadTime, String discussContent) {
		super();
		DiscussId = discussId;
		DiscussUid = discussUid;
		DiscussMessageId = discussMessageId;
		DiscussUserId = discussUserId;
		DiscussUserName = discussUserName;
		DiscussPointId = discussPointId;
		DiscussUploadTime = discussUploadTime;
		DiscussContent = discussContent;
	}
	public int getDiscussId() {
		return DiscussId;
	}
	public void setDiscussId(int discussId) {
		DiscussId = discussId;
	}
	public String getDiscussUid() {
		return DiscussUid;
	}
	public void setDiscussUid(String discussUid) {
		DiscussUid = discussUid;
	}
	public String getDiscussMessageId() {
		return DiscussMessageId;
	}
	public void setDiscussMessageId(String discussMessageId) {
		DiscussMessageId = discussMessageId;
	}
	public String getDiscussUserId() {
		return DiscussUserId;
	}
	public void setDiscussUserId(String discussUserId) {
		DiscussUserId = discussUserId;
	}
	public String getDiscussUserName() {
		return DiscussUserName;
	}
	public void setDiscussUserName(String discussUserName) {
		DiscussUserName = discussUserName;
	}
	public String getDiscussPointId() {
		return DiscussPointId;
	}
	public void setDiscussPointId(String discussPointId) {
		DiscussPointId = discussPointId;
	}
	public String getDiscussUploadTime() {
		return DiscussUploadTime;
	}
	public void setDiscussUploadTime(String discussUploadTime) {
		DiscussUploadTime = discussUploadTime;
	}
	public String getDiscussContent() {
		return DiscussContent;
	}
	public void setDiscussContent(String discussContent) {
		DiscussContent = discussContent;
	}
	@Override
	public String toString() {
		return "DiscussMoment [DiscussId=" + DiscussId + ", DiscussUid="
				+ DiscussUid + ", DiscussMessageId=" + DiscussMessageId
				+ ", DiscussUserId=" + DiscussUserId + ", DiscussUserName="
				+ DiscussUserName + ", DiscussPointId=" + DiscussPointId
				+ ", DiscussUploadTime=" + DiscussUploadTime
				+ ", DiscussContent=" + DiscussContent + "]";
	}
	
	
	
}
