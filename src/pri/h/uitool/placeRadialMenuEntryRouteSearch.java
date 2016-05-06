package pri.h.uitool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import android.content.Context;

public class placeRadialMenuEntryRouteSearch implements RadialMenuEntry {

	@Override
	public String getName() {
		return "placeRadialMenuEntryRouteSearch";
	}

	@Override
	public String getLabel() {
		return "路线规划";
	}

	@Override
	public int getIcon() {
		return 0;
	}

	private List<RadialMenuEntry> children = null;

	@Override
	public List<RadialMenuEntry> getChildren() {
		children = new ArrayList<RadialMenuEntry>(Arrays.asList(new walkThat(
				context), new driveThat(context), new busThat(context)));
		return children;
	}

	@Override
	public void menuActiviated() {
		// 监听回调事件

	}

	private Context context;

	public placeRadialMenuEntryRouteSearch(Context context) {
		this.context = context;
	}

}
