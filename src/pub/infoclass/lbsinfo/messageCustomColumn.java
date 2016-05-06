package pub.infoclass.lbsinfo;

public class messageCustomColumn {

	public String uploadingtime; // 上传时间
	public String broadcastcontent; // 广播内容
	public String univeralindex; // 通用索引
	public String single; // 单一标识
	public String photo1; // 图像1
	public String photo2; // 图像2
	public String photo3; // 图像3
	public String praisenumber; // 赞
	public String criticizenumber; // 贬
	public String username; // 用户名称
	public messageCustomColumn(String uploadingtime, String broadcastcontent,
			String univeralindex, String single, String photo1, String photo2,
			String photo3, String praisenumber, String criticizenumber,
			String username) {
		super();
		this.uploadingtime = uploadingtime;
		this.broadcastcontent = broadcastcontent;
		this.univeralindex = univeralindex;
		this.single = single;
		this.photo1 = photo1;
		this.photo2 = photo2;
		this.photo3 = photo3;
		this.praisenumber = praisenumber;
		this.criticizenumber = criticizenumber;
		this.username = username;
	}

	
}
