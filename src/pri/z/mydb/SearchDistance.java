package pri.z.mydb;

import java.io.Serializable;

public class SearchDistance implements Serializable{

	public int DistanceId;//自增长的列
	public String DistanceMomentDis;//动态搜索范围
	public String DistanceActivityDis;//活动搜索范围
	public SearchDistance(){
		
	}
	public SearchDistance(int distanceId,
			String distanceMomentDis, String distanceActivityDis) {
		super();
		DistanceId = distanceId;
		DistanceMomentDis = distanceMomentDis;
		DistanceActivityDis = distanceActivityDis;
	}
	
	
	
}
