package pri.h.semap.overlayset;

import java.util.List;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class OverlaySet {
	
	public GeoPoint point;
	public List<CloudPoiInfo> cloudPoiInfoSet;
	
	/**
	 * 计算中点并更新
	 * @return
	 */
	public GeoPoint countMiddelAndUpdateGeoPoint()
	{
		double latitudeS = 0;
		double longitudeS = 0;
		for(int fence = 0 ; fence < cloudPoiInfoSet.size() ; fence ++ )
		{
			latitudeS = latitudeS + cloudPoiInfoSet.get(fence).latitude;
			longitudeS = longitudeS + cloudPoiInfoSet.get(fence).longitude;
		}
		
		GeoPoint pt = new GeoPoint( ((int) (latitudeS*1E6)) / cloudPoiInfoSet.size()  , ((int)(longitudeS*1E6)) / cloudPoiInfoSet.size() );
		return pt;
	}
	

}
