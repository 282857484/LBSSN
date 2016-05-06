package pri.h.semap;

import pri.h.semap.myMap.Close;
import pri.h.uitool.BuildSomething;
import pri.h.uitool.RadialMenuWidget;
import pri.h.uitool.SearchPOISomething;
import pri.h.uitool.ShareLocationInfo;
import pri.h.uitool.placeRadialMenuEntryRouteSearch;
import pub.application.SEMapApplication;
import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;

/**
 * ItemizedOverlay!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * @author 侯斌
 *
 */
public class baiduPOIOverlay extends PoiOverlay {
	public final static int baiduPOITag = 999999;
	
	MKSearch mSearch;
	Context context;
	
	public baiduPOIOverlay(Activity activity, MapView mapView, MKSearch search) {
		super(activity, mapView);
		this.mSearch = search;
		this.context = activity;
		
	}
	
	@Override
	protected boolean onTap(int index) {
		
		super.onTap(index);
		MKPoiInfo info = getPoi(index);
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
	
//	public boolean setData(ArrayList<MKPoiInfo> arrayListMKPoiInfo)
//	{
//		this.arraylistMKPoiInfo = arrayListMKPoiInfo;
//		return true;
//	}
//	protected boolean onTap() {
//		super.onTap();
//		MKPoiInfo info = getPoi();
//		if (info.hasCaterDetails) {
//			mSearch.poiDetailSearch(info.uid);
//		}
//		return true;
//	}
}
