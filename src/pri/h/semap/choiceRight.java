package pri.h.semap;

import java.util.List;

import android.content.Context;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;

public class choiceRight implements RadialMenuEntry {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "choiceRight";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "确认";
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
		((myMap) context).finish();
		
	}

	private Context context;
	
	public choiceRight(Context context) {
		this.context = context;
	}
}
