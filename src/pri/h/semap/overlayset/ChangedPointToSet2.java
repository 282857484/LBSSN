package pri.h.semap.overlayset;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class ChangedPointToSet2 {
	/**
	 * 用于n个点间的合并
	 * @param itemList 原始点集
	 * @return List<OverlaySet>  点集列表
	 */
	public List<OverlaySet> go(List<CloudPoiInfo> itemList) {
		List<OverlaySet> listOfOverlaySet = new ArrayList<OverlaySet>();
		for(int fence = 0 ; fence < itemList.size() ; fence ++ )
		{
			OverlaySet overlaySet = new OverlaySet();
			overlaySet.cloudPoiInfoSet = new ArrayList<CloudPoiInfo>();
			overlaySet.cloudPoiInfoSet.add(itemList.get(fence));
			overlaySet.point = new GeoPoint((int) (itemList.get(fence).latitude * 1E6) , (int) (itemList.get(fence).longitude * 1E6));
			listOfOverlaySet.add(overlaySet);
		}
		
		
		return listOfOverlaySet;
	}
}
