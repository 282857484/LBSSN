package pri.h.uitool;

import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import android.content.Context;

public class ShareLocationInfo implements RadialMenuEntry{

	@Override
	public String getName() {
		return "ShareLocationInfo";
	}

	@Override
	public String getLabel() {
		return "分享位置";
	}

	@Override
	public int getIcon() {
		return 0;
	}

	@Override
	public List<RadialMenuEntry> getChildren() {
		return null;
	}

	@Override
	public void menuActiviated() {
		pri.h.semap.myMap.isshareaddress = true;
		pri.h.semap.myMap.mapsearch.reverseGeocode(pri.h.semap.myMap.currentpoint);
		
	}
	
	private Context context;
	
	public ShareLocationInfo(Context context) {
		this.context = context;
	}

}
