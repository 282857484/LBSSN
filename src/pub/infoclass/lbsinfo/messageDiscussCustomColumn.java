package pub.infoclass.lbsinfo;

public class messageDiscussCustomColumn {

	public String messageid; // 广播ID(从属)
	public String userid; // 用户ID(从属)
	public String pointdiscussid; // 指向ID(messageid)
	public String uploadtime; // 指向ID(messageid)
	public String username; // 用户名
	public String discusscontent; // 讨论内容
	public String photo; // 图像
	public messageDiscussCustomColumn(String messageid, String userid,
			String pointdiscussid, String uploadtime, String username,
			String discusscontent, String photo) {
		super();
		this.messageid = messageid;
		this.userid = userid;
		this.pointdiscussid = pointdiscussid;
		this.uploadtime = uploadtime;
		this.username = username;
		this.discusscontent = discusscontent;
		this.photo = photo;
	}
	
	

}
