package pri.h.semap;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class teammateOverlay extends ItemizedOverlay{
	
	Context context = null;
	MapView mapview = null;
	List<CloudPoiInfo> itemList = new ArrayList<CloudPoiInfo>();
	
	public teammateOverlay(Drawable defaultMaker, MapView mapView, Context context) {
		super(defaultMaker, mapView);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mapview = mapView;
	}
	
	public boolean onTap(int index)
	{
		
		return true;
	}
	
	public boolean onTap(GeoPoint pt, MapView mMapView)
	{
		
		return false;
	}

	
	public boolean setListOfCloudPoiInfo(List<CloudPoiInfo> itemList) {
		this.itemList = itemList;
		return true;
	}
}
