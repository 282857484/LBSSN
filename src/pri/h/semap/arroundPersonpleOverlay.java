package pri.h.semap;

import java.util.LinkedList;
import java.util.List;

import pri.h.semap.myMap.Close;
import pri.h.uitool.RadialMenuWidget;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class arroundPersonpleOverlay extends ItemizedOverlay{
	
	List<CloudPoiInfo> itemList = new LinkedList<CloudPoiInfo>();
	Context context;
	MapView mapView = null;
	
	public arroundPersonpleOverlay(Drawable defaultMaker, MapView mapView, Context context) {
		super(defaultMaker, mapView);
		// TODO Auto-generated constructor stub
		this.mapView = mapView;
		this.context = context;
	}
	
	public boolean onTap(int index)
	{
		CloudPoiInfo cloudPoiInfo = itemList.get(index);
		
		//此处处理点击POI事件
		pri.h.semap.myMap.PieMenu = new RadialMenuWidget(mapView.getContext());
		int xLayoutSize = pri.h.semap.myMap.whatisthislayout.getWidth();
		int yLayoutSize = pri.h.semap.myMap.whatisthislayout.getHeight();
		
		pri.h.semap.myMap.PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
		pri.h.semap.myMap.PieMenu.setHeader(
				"纬度:" + (double) pri.h.semap.myMap.currentpoint.getLatitudeE6() / 1000000
						+ "精度:"
						+ (double) pri.h.semap.myMap.currentpoint.getLongitudeE6()
						/ 1000000, 20);

		pri.h.semap.myMap.PieMenu.setIconSize(15, 30);
		pri.h.semap.myMap.PieMenu.setTextSize(13);
		
		/**
		 * 这里需要补全!
		 */
//		com.example.semap.myMap.PieMenu.setCenterCircle();
		
		return true;
	}
	
	/**
	 * 此处处理未点到POI的事件
	 */
	public boolean onTap(GeoPoint pt, MapView mMapView)
	{
		
		return false;
	}
	
	public boolean setListOfCloudPoiInfo(List<CloudPoiInfo> item)
	{
		this.itemList = item;
		return true;
	}
	
	/**
	 * 点聚集问题?
	 * @呵呵 还未解决...
	 */
}
