package pri.h.semap.overlayset;

import java.util.List;

public class MinLength {
	
	int minlength = 999999;
//	OverlaySet cpi1 , cpi2;
	int overlaySet1 , overlaySet2;

	public void mergeTwoLeastOverlaySet(List<OverlaySet> listOfOverlaySet)
	{
		listOfOverlaySet.get(overlaySet1).cloudPoiInfoSet.addAll(listOfOverlaySet.get(overlaySet2).cloudPoiInfoSet);
		listOfOverlaySet.get(overlaySet1).countMiddelAndUpdateGeoPoint();
		listOfOverlaySet.remove(overlaySet2);
	}
}
