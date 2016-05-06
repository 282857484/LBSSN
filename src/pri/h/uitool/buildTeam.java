package pri.h.uitool;

import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pri.z.build.ShareMoment;
import pri.z.show.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class buildTeam implements RadialMenuEntry {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "buildTeam";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "分享动态";
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
		Intent intent = new Intent(context, ShareMoment.class);
		context.startActivity(intent);
//		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.z_push_left_in,
				R.anim.z_push_left_out);
		
		/**
		 * 这里创建活动信息dialog
		 */
//		ScrollView buildteamForm = (ScrollView) ((Activity) context)
//				.getLayoutInflater().inflate(R.layout.h_buildteam_dialog, null);
//		new AlertDialog.Builder(context)
//				.setIcon(R.drawable.g_pois)
//				.setTitle("组建队伍")
//				.setView(buildteamForm)
//				.setPositiveButton("组建",
//						new android.content.DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
////								Message msg = Message.obtain();
////								msg.what = protocolwithbaidustore.baidutableteamPOI;
////								msg.replyTo = pri.h.semap.myMap.myMessenger;
//
////								creatPoi creatbroadcast = new creatPoi();
//
//								// 自定义k-v对集合在百度云存储出现一些小问题
////								Map<String, Object> customvalue = new HashMap<String, Object>();
//
//								
//							
//
//								
//								/*customvalue.put("mobilephone",
//										SEMapApplication.getPhone());
//								
//								customvalue
//										.put("univeralindex",
//												SEMapApplication.AccountNumber
//														+ SEMapApplication.buildTeamNumber);
//								
//								String formatTime = FormatTime.getFormatTime();
//								customvalue.put("uploadingtime", formatTime);
//								creatbroadcast.setColumnkey(customvalue);
//								creatbroadcast.setCoord_type(3);
//								creatbroadcast.setGeotable_id(String
//										.valueOf(protocolwithbaidustore.baidutableteamPOI));
//								creatbroadcast
//										.setLatitude((double) (pri.h.semap.myMap.locationdata.latitude));
//								creatbroadcast
//										.setLongitude((double) (pri.h.semap.myMap.locationdata.longitude));
//
//								msg.obj = creatbroadcast;*/
////								try {
////									pri.h.semap.myMap.remoteMessenger.send(msg);
////								} catch (RemoteException e) {
////									// TODO Auto-generated catch block
////									e.printStackTrace();
////								}
//							}
//
//						})
//				.setNegativeButton("取消",
//						new android.content.DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method
//								// stub
//
//							}
//
//						}
//
//				).create().show();
	}

	private Context context;

	public buildTeam(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}

}
