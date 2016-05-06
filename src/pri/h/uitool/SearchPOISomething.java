package pri.h.uitool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import android.content.Context;

public class SearchPOISomething implements RadialMenuEntry {

	@Override
	public String getName() {
		return "SearchPOISomething";
	}

	@Override
	public String getLabel() {
		return "强撸搜索";
	}

	@Override
	public int getIcon() {
		return 0;
	}

	private List<RadialMenuEntry> children = null;

	@Override
	public List<RadialMenuEntry> getChildren() {
		children = new ArrayList<RadialMenuEntry>(Arrays.asList(
				 new searchMessage(context), new searchActivity(context)));
		return children;
	}

	@Override
	public void menuActiviated() {

	}

	private Context context;

	public SearchPOISomething(Context context) {
		this.context = context;
	}

}
