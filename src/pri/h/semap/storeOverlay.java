package pri.h.semap;

import java.util.ArrayList;
import java.util.List;

import pub.application.SEMapApplication;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class storeOverlay extends ItemizedOverlay{
	public final static int baiduPOITag = 999999;
	
	MapView mapview = null;
	Context context = null;
	List<MKPoiInfo> itemList = new ArrayList<MKPoiInfo>();
	
	public List<MKPoiInfo> getItemList() {
		return itemList;
	}

	public void setItemList(List<MKPoiInfo> itemList) {
		this.itemList = itemList;
	}

	public storeOverlay(Drawable defaultMaker, MapView mapView, Context context) {
		
		super(defaultMaker, mapView);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mapview = mapView;
	}
	
	public boolean onTap(int index)
	{
		
//		pri.h.semap.myMap.extraShareString = itemList.get(index).title;
//		return true;
		MKPoiInfo info = itemList.get(index);
		GeoPoint thispoint = itemList.get(index).pt;
		myMap.currentpoint = thispoint;
		mapview.getController().animateTo(thispoint);
		Messenger messenger = SEMapApplication.currentMessenger;
		Message msg = Message.obtain();
		msg.what = baiduPOITag;
		msg.obj = info;
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean onTap(GeoPoint pt, MapView mMapView)
	{
		
		return false;
	}
	
	
}
