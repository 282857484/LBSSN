package pub.infoclass.lbsinfo;

public class activityBaseInfoCustomColumn {

	public String uploadingtime; // 上传时间
	public String univeralindex; // 通用索引
	public String describe; // 活动描述
	public String activity; // 活动名称
	public String launchman; // 发起人
	public String mobilephone; // 联系电话	
	public String continuetime; // 活动持续时间	
	public String starttime; // 活动开始时间
	public String endtime; // 活动结束时间
	public String single; // 单一标识
	public String activityopinion; // 活动评分
	public String activityalwaysjoinperson; // 活动已参加人数
	public String managerid; // 	活动管理员ID
	public String maxmembernumber; // 活动最大参加人数
	public String activitylogo; // 	活动LOGO
	public String activityholdplace; // 活动举办地
	public activityBaseInfoCustomColumn(String uploadingtime,
			String univeralindex, String describe, String activity,
			String launchman, String mobilephone, String continuetime,
			String starttime, String endtime, String single,
			String activityopinion, String activityalwaysjoinperson,
			String managerid, String maxmembernumber, String activitylogo,
			String activityholdplace) {
		super();
		this.uploadingtime = uploadingtime;
		this.univeralindex = univeralindex;
		this.describe = describe;
		this.activity = activity;
		this.launchman = launchman;
		this.mobilephone = mobilephone;
		this.continuetime = continuetime;
		this.starttime = starttime;
		this.endtime = endtime;
		this.single = single;
		this.activityopinion = activityopinion;
		this.activityalwaysjoinperson = activityalwaysjoinperson;
		this.managerid = managerid;
		this.maxmembernumber = maxmembernumber;
		this.activitylogo = activitylogo;
		this.activityholdplace = activityholdplace;
	}

	
}
