package pri.h.uitool;

import java.util.List;

import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.NearbySearchInfo;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pub.application.SEMapApplication;
import android.content.Context;

public class SearchFreeInformation implements RadialMenuEntry {

	Context context = null;

	public SearchFreeInformation(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ShareMood";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "检索附近信息";
	}

	@Override
	public int getIcon() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<RadialMenuEntry> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void menuActiviated() {
		// TODO Auto-generated method stub
		// 此处数据链接至动态处

		NearbySearchInfo searchinfo = new NearbySearchInfo();
		searchinfo.ak = SEMapApplication.strkey;
		searchinfo.geoTableId = 49442;
		// 不知道经纬度是否是反的,这里的经纬度应该是currentpoint
		searchinfo.location = (String) (((double) pri.h.semap.myMap.currentpoint.getLongitudeE6()) / 1000000 + "," + ((double) pri.h.semap.myMap.currentpoint.getLatitudeE6()) / 1000000);
		searchinfo.radius = 3000;
		searchinfo.sortby = "uploadingtime:-1";
		searchinfo.tags = "";
		searchinfo.q = "";
		searchinfo.pageIndex = 0;
		searchinfo.pageSize = 10;
		// searchinfo.sn = "";

		CloudManager.getInstance().nearbySearch(searchinfo);
	}

}
