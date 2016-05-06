package pri.h.uitool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import android.content.Context;

public class BuildSomething implements RadialMenuEntry {

	@Override
	public String getName() {
		return "buildSomething";
	}

	@Override
	public String getLabel() {
		return "发起";
	}

	@Override
	public int getIcon() {
		return 0;
	}

	private List<RadialMenuEntry> children = null;

	@Override
	public List<RadialMenuEntry> getChildren() {
		children = new ArrayList<RadialMenuEntry>(Arrays.asList(
				new buildActivity(context), new buildTeam(context)));
		return children;
	}

	@Override
	public void menuActiviated() {

	}

	private Context context;

	public BuildSomething(Context context) {
		this.context = context;
	}

}
