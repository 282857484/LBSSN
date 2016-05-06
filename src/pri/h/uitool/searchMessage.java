package pri.h.uitool;

import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pri.z.main.MainActivity;
import pub.application.SEMapApplication;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.NearbySearchInfo;

public class searchMessage implements RadialMenuEntry {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "searchMessage";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "动态";
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
		searchinfo.geoTableId = 49442;
		// searchinfo.filter = "time:20130801,20140801";
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
		// searchinfo.sn = "";

		CloudManager.getInstance().nearbySearch(searchinfo);

		// ScrollView searchactivityForm = (ScrollView) ((Activity) context)
		// .getLayoutInflater().inflate(R.layout.searchmessage_dialog,
		// null);
		// new AlertDialog.Builder(context)
		// .setIcon(R.drawable.composer_camera)
		// .setTitle("")
		// .setView(searchactivityForm)
		// .setPositiveButton("",
		// new android.content.DialogInterface.OnClickListener() {
		//
		// /**
		// * 请求信息，并更新到后台
		// */
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method
		// // stub
		//
		// /**
		// * 半径检索 这里只是举个例子 用的时候还需要进行修改 这些信息要从dialog中取出即可
		// *
		// */
		// // LocalSearchInfo searchinfo =
		// // new LocalSearchInfo();
		// // BoundSearchInfo searchinfo =
		// // new BoundSearchInfo();
		// NearbySearchInfo searchinfo = new NearbySearchInfo();
		// searchinfo.ak = SEMapApplication.strkey;
		// searchinfo.geoTableId = 49442;
		// // searchinfo.filter = "time:20130801,20140801";
		// // searchinfo.location = "116.403689,39.914957";
		// //不知道经纬度是否是反的
		// searchinfo.location = (String)
		// ((double)com.example.semap.myMap.locationdata.latitude + "," +
		// (double)com.example.semap.myMap.locationdata.longitude);
		// searchinfo.radius = 10000;
		// searchinfo.sortby = "";
		// searchinfo.tags = "";
		// searchinfo.q = "";
		// searchinfo.pageIndex = 0;
		// searchinfo.pageSize = 10;
		// // searchinfo.sn = "";
		//
		// CloudManager.getInstance().nearbySearch(
		// searchinfo);
		//
		// }
		//
		// })
		// .setNegativeButton("",
		// new android.content.DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method
		// // stub
		//
		// }
		//
		// }
		//
		// ).create().show();
	}

	private Context context;

	public searchMessage(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}
}
