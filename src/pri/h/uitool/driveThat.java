package pri.h.uitool;

import java.util.List;

import pri.h.semap.myMap;
import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pub.application.SEMapApplication;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class driveThat implements RadialMenuEntry {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "driveThat";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "驾车路线";
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
		/**
		 * 这里进行汽车的dialog回调
		 */
		final MKPlanNode startNode = new MKPlanNode();
		final MKPlanNode endNode = new MKPlanNode();

		startNode.pt = new GeoPoint(
				(int) (SEMapApplication.locationdata.latitude * 1000000),
				(int) (SEMapApplication.locationdata.longitude * 1000000));
		endNode.pt = pri.h.semap.myMap.currentpoint;
		pri.h.semap.myMap.mapsearch.drivingSearch(myMap.myCity, startNode, myMap.myCity,
				endNode);
		if (myMap.PieMenu != null)
			((LinearLayout) myMap.PieMenu.getParent()).removeView(myMap.PieMenu);
		Toast.makeText(context, "正在查询...", Toast.LENGTH_SHORT).show();
	}
	
	private Context context;
	
	public driveThat(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}

}
