package pri.h.uitool;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;

public class PersonInfo implements RadialMenuEntry {

	private Context context;

	public PersonInfo(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "个人信息";
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

		
	}

	
}
