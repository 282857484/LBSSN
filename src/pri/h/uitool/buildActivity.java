package pri.h.uitool;

import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pri.z.build.BuildActivity;
import android.content.Context;
import android.content.Intent;

public class buildActivity implements RadialMenuEntry {

	private Context context;

	// FormatTime ft = new FormatTime();

	public buildActivity(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "buildActivity";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "发起活动";
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
		
		Intent intent = new Intent(context, BuildActivity.class);
		context.startActivity(intent);
	}
}



// if(year > thisyear)
// {
// activityStartTime.append(year);
// if((monthOfYear == 1) && (thismonthOfYear == 12))
// {
// activityStartTime.append(monthOfYear);
// if((31 - thisdayOfMonth + dayOfMonth) <11)
// {
//
// if(dayOfMonth < 10)
// {
// activityStartTime.append("0" + dayOfMonth);
// }
// else
// {
// activityStartTime.append(dayOfMonth);
// }
// }
// else{
// Toast.makeText(context, "选择日期有误", Toast.LENGTH_LONG).show();
// }
// }
// else{
// Toast.makeText(context, "选择日期有误", Toast.LENGTH_LONG).show();
// }
// } else if(year == thisyear) {
// activityStartTime.append(year);
// if(thismonthOfYear < monthOfYear) {
// if(monthOfYear < 10)
// {
// activityStartTime.append("0" + monthOfYear);
// if(dayOfMonth < 10)
// {
// activityStartTime.append("0" + dayOfMonth);
// }
// else
// {
// activityStartTime.append(dayOfMonth);
// }
// }
// else
// {
// activityStartTime.append(monthOfYear);
// if(dayOfMonth < 10)
// {
// activityStartTime.append("0" + dayOfMonth);
// }
// else
// {
// activityStartTime.append(dayOfMonth);
// }
// }
// } else if(thismonthOfYear == monthOfYear) {
// activityStartTime.append(monthOfYear);
// } else {
//
// }
// } else {
// Toast.makeText(context, "选择日期有误", Toast.LENGTH_LONG).show();
// }
