package pri.h.semap;

import java.util.List;

import android.content.Context;
import android.widget.LinearLayout;
import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;

public class choiceCancel implements RadialMenuEntry {

	public choiceCancel(Context context) {
		// TODO Auto-generated constructor stub
		// do nothing ...
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "choiceCancel";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "取消";
	}

	@Override
	public int getIcon() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<RadialMenuEntry> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void menuActiviated() {
		// TODO Auto-generated method stub
		((LinearLayout) pri.h.semap.myMap.PieMenu.getParent()).removeView(pri.h.semap.myMap.PieMenu);
	}

}
