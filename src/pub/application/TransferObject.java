package pub.application;

import java.util.List;

import com.baidu.mapapi.cloud.CloudPoiInfo;

public class TransferObject {

	/**
	 *  转移标记
	 *  1.展示点
	 *  2.从自己的位置到指定点
	 *  3.展示一组点
	 *  4.选择点
	 */
	private int mark = 0;
	// 1-展示点
	private CloudPoiInfo showCloudPoiInfo = null;
	// 2-路径规划终点
	private CloudPoiInfo routePlanPoiInfo = null;
	// 3-共享的展示点集
	private List<CloudPoiInfo> cloudPoiInfoList = null;
	// 4-选择点
	private Class<?> activityToIntent;
	
	public int getMark() {
		return mark;
		
	}
	public void setMark(int mark) {
		this.mark = mark;
	}

	public CloudPoiInfo getShowCloudPoiInfo() {
		return showCloudPoiInfo;
	}
	public void setShowCloudPoiInfo(CloudPoiInfo showCloudPoiInfo) {
		this.showCloudPoiInfo = showCloudPoiInfo;
	}
	public CloudPoiInfo getRoutePlanPoiInfo() {
		return routePlanPoiInfo;
	}
	public void setRoutePlanPoiInfo(CloudPoiInfo routePlanPoiInfo) {
		this.routePlanPoiInfo = routePlanPoiInfo;
	}
	public Class<?> getActivityToIntent() {
		return activityToIntent;
	}
	public void setActivityToIntent(Class<?> activityToIntent) {
		this.activityToIntent = activityToIntent;
	}
	public List<CloudPoiInfo> getCloudPoiInfoList() {
		return cloudPoiInfoList;
	}
	public void setCloudPoiInfoList(List<CloudPoiInfo> cloudPoiInfoList) {
		this.cloudPoiInfoList = cloudPoiInfoList;
	}

	
}
