//package pri.h.semap.overlayset;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.baidu.mapapi.cloud.CloudPoiInfo;
//import com.baidu.platform.comapi.basestruct.GeoPoint;
//
//public class ChangedPointToSet {
//
//	/**
//	 * 用于n个点间的合并
//	 * @param itemList 原始点集
//	 * @return List<OverlaySet>  点集列表
//	 */
//	public List<OverlaySet> go(List<CloudPoiInfo> itemList) {
//		List<OverlaySet> listOfOverlaySet = new ArrayList<OverlaySet>();
//		for(int fence = 0 ; fence < itemList.size() ; fence ++ )
//		{
//			OverlaySet overlaySet = new OverlaySet();
//			overlaySet.cloudPoiInfoSet = new ArrayList<CloudPoiInfo>();
//			overlaySet.cloudPoiInfoSet.add(itemList.get(fence));
//			overlaySet.point = new GeoPoint((int) (itemList.get(fence).latitude * 1E6) , (int) (itemList.get(fence).longitude * 1E6));
//			listOfOverlaySet.add(overlaySet);
//		}
//		for(int fence = 0 ; fence < itemList.size() ; fence ++ )
//		{
//			MinLength minlength = getMinLength(listOfOverlaySet);
//			if(minlength.minlength < getPointInMapMinLength(pri.h.semap.myMap.getMapZoomLevel()))
//			{
//				minlength.mergeTwoLeastOverlaySet(listOfOverlaySet);
//			}
//			else
//			{
//				break;
//			}
//		}
//		
//		return listOfOverlaySet;
//	}
//
//	
//
//
//
//	/**
//	 * 获取在此缩放条件下合并点集的最小距离
//	 * @param mapZoomLevel  缩放级别
//	 * @return
//	 */
//	private int getPointInMapMinLength(float mapZoomLevel) {
//		// 暂时只处理19级时的情况
//		return 19;
//	}
//
//
//	/**
//	 *  获取itemlist中最近的两个点与距离
//	 * @param listOfOverlaySet 点集列表
//	 * @return
//	 */
//	private MinLength getMinLength(List<OverlaySet> listOfOverlaySet) {
//		
//		
//		return null;
//	}
//
//}
