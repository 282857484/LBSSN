package pri.h.semap;

import java.util.Calendar;

public class CreateActivity_information {
	
//	private int p = protocolfromclient1
	
	private String account;
	
	private  Calendar start_calendar,end_calendar;
	
	private double  longitude,latitude;//经度，纬度
	
	private String hostaccount;//主办人账号
	
	private String hostcompany;//主办单位
	
	private String description;//活动描述
	
	private String tag;//兴趣关键码
	
	private boolean visiable;//成员位置是否可见
	
	private String theame;//主题
	
	private String type;//类型
	
	private boolean open;//是否公开活动信息
	
	private boolean cardallowed;//是否允许发放名片
	
	private String remarks;//备注

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	

	

	public String getHostaccount() {
		return hostaccount;
	}

	public void setHostaccount(String hostaccount) {
		this.hostaccount = hostaccount;
	}

	public String getHostcompany() {
		return hostcompany;
	}

	public void setHostcompany(String hostcompany) {
		this.hostcompany = hostcompany;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public boolean isVisiable() {
		return visiable;
	}

	public void setVisiable(boolean visiable) {
		this.visiable = visiable;
	}

	public String getTheame() {
		return theame;
	}

	public void setTheame(String theame) {
		this.theame = theame;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isCardallowed() {
		return cardallowed;
	}

	public void setCardallowed(boolean cardallowed) {
		this.cardallowed = cardallowed;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Calendar getStart_calendar() {
		return start_calendar;
	}

	public void setStart_calendar(Calendar start_calendar) {
		this.start_calendar = start_calendar;
	}

	public Calendar getEnd_calendar() {
		return end_calendar;
	}

	public void setEnd_calendar(Calendar end_calendar) {
		this.end_calendar = end_calendar;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
