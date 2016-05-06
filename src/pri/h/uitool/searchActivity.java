package pri.h.uitool;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.NearbySearchInfo;

import android.content.Context;
import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pri.z.main.MainActivity;
import pub.application.SEMapApplication;
import pub.util.FormatTime;

public class searchActivity implements RadialMenuEntry {

	
	private Context context;

	public searchActivity(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "活动";
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
		NearbySearchInfo searchinfo = new NearbySearchInfo();
		searchinfo.ak = SEMapApplication.strkey;
		searchinfo.geoTableId = 49436;
		// 不知道经纬度是否是反的,这里的经纬度应该是currentpoint
		searchinfo.location = (String) (((double) pri.h.semap.myMap.currentpoint
				.getLongitudeE6()) / 1000000 + "," + ((double) pri.h.semap.myMap.currentpoint
				.getLatitudeE6()) / 1000000);
		searchinfo.radius = MainActivity.RadiusInMap;
		searchinfo.sortby = "uploadingtime:-1";
		searchinfo.tags = "";
		searchinfo.q = "";
		searchinfo.pageIndex = 0;
		searchinfo.pageSize = 10;
		searchinfo.filter = "starttime:"+FormatTime.getFormatTime()+",299912312359999";
		// searchinfo.sn = "";

		CloudManager.getInstance().nearbySearch(searchinfo);
	}

}
