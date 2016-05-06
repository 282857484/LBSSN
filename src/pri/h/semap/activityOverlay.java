package pri.h.semap;

import java.util.List;

import pri.z.show.ActivityDetail;
import pri.z.show.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 活动覆盖物
 * 
 * @author 侯斌
 * 
 */
public class activityOverlay extends ItemizedOverlay {

	MapView mapview = null;
	Context context = null;
	List<CloudPoiInfo> itemList;

	public activityOverlay(Drawable defaultMaker, MapView mapView,
			Context context) {
		super(defaultMaker, mapView);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mapview = mapView;
	}

	public boolean onTap(final int index) {
		
		pri.h.semap.myMap.extraShareString = (String) itemList.get(index).extras.get("activity");
		pri.h.semap.myMap.contentbutton.setText(itemList.get(index).extras.get(
				"activity").toString());
		GeoPoint thispoint = new GeoPoint(
				(int) (itemList.get(index).latitude * 1000000),
				(int) (itemList.get(index).longitude * 1000000));
		mapview.getController().animateTo(thispoint);
		
		pri.h.semap.myMap.pop.showPopup(pri.h.semap.myMap.contentbutton,
				thispoint, 32);
		final LinearLayout activitydialog = (LinearLayout) ((Activity) context)
				.getLayoutInflater().inflate(R.layout.h_activity_scan_dialog,
						null);
		TextView activityname = (TextView) activitydialog
				.findViewById(R.id.h_activity_name);
		TextView activitytags = (TextView) activitydialog
				.findViewById(R.id.h_activity_tags);
		TextView activitycontent = (TextView) activitydialog
				.findViewById(R.id.h_activity_content);
		TextView activityhoster = (TextView) activitydialog
				.findViewById(R.id.h_activity_hoster);
		TextView activitylasttime = (TextView) activitydialog
				.findViewById(R.id.h_activity_lasttime);
		TextView activityaddress = (TextView) activitydialog
				.findViewById(R.id.h_activity_address);
		Button phonecall = (Button) activitydialog
				.findViewById(R.id.h_activity_mobliephonecall);
//		Button join = (Button) activitydialog
//				.findViewById(R.id.h_activity_joinin);
//		Button attention = (Button) activitydialog
//				.findViewById(R.id.h_activity_attention);
//		Button share = (Button) activitydialog
//				.findViewById(R.id.h_activity_share);

		activityname.setText(itemList.get(index).extras.get("activity")
				.toString());
		activitytags.setText(itemList.get(index).tags);
		activitycontent.setText(itemList.get(index).extras.get("describe")
				.toString());
		activityhoster.setText(itemList.get(index).extras.get("launchman")
				.toString());
		String date = itemList.get(index).extras.get("starttime").toString()
				+ "," + itemList.get(index).extras.get("endtime").toString();
		activitylasttime.setText(date);
		activityaddress.setText(itemList.get(index).address + "\n" + "距离您 : "
				+ itemList.get(index).distance);

		final String phonenumber = itemList.get(index).extras
				.get("mobilephone").toString();
		phonecall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phonenumber.length() < 5) {
					Toast.makeText(context, "商家预留电话无效", Toast.LENGTH_LONG)
							.show();
					;
				} else {
					Intent phoneIntent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"
									+ phonenumber));
					context.startActivity(phoneIntent);
				}
			}

		});

		pri.h.semap.myMap.contentbutton
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (pri.h.semap.myMap.pop != null) {
							pri.h.semap.myMap.pop
									.hidePop();
							mapview.removeView(pri.h.semap.myMap.contentbutton);
						}
						
						Intent intent = new Intent(context,
								ActivityDetail.class);
						String activityId  = String.valueOf(itemList.get(index).extras.get("univeralindex"));
						intent.putExtra("ActivityDetailActivityID", activityId);
						context.startActivity(intent);
//						((myMap) context).finish();
//						new AlertDialog.Builder(context)
//								.setIcon(R.drawable.h_aplaca)
//								.setTitle(itemList.get(index).title)
//								.setView(activitydialog)
//								.setNegativeButton("参加",
//										new DialogInterface.OnClickListener() {
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//
////												ApplyForJoinActivity_C ac = new ApplyForJoinActivity_C();
////												ac.setAccountNumber(SEMapApplication.AccountNumber);
////												ac.setActivityAccountNumber(itemList
////														.get(index).extras
////														.get("univeralindex").toString());
////												ac.setJoinWay(1);
////												List<Object> lo = new ArrayList<Object>();
////												lo.add(ac);
////
////												// 参加活动的信息类传送至服务器
////												Message msg = Message.obtain();
////												msg.replyTo = pri.h.semap.myMap.myMessenger;
////												msg.what = protocolfromclient1.ApplyForJoinActivity_C;
////												String timeStampString = ac.getTimeStamp();
////												Log.e("timeStampString :",
////														timeStampString);
////												msg.arg1 = Integer
////														.parseInt(timeStampString.substring(0, 7));
////												msg.arg2 = Integer
////														.parseInt(timeStampString.substring(7,15));
////												msg.obj = lo;
////												try {
////													pri.h.semap.myMap.remoteMessenger.send(msg);
////												} catch (RemoteException e) {
////													e.printStackTrace();
////												} 
//
//											}
//
//										})
//								.setPositiveButton("关注",
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//
////												ApplyForJoinActivity_C ac = new ApplyForJoinActivity_C();
////												ac.setAccountNumber(SEMapApplication.AccountNumber);
////												// 这里应该是toString而不是强制转换(String)
////												ac.setActivityAccountNumber(itemList.get(index).extras.get("univeralindex").toString());
////												ac.setJoinWay(2);
////
////												// 关注活动的信息类传送至服务器
////												List<Object> lo = new ArrayList<Object>();
////												lo.add(ac);
////
////												Message msg = Message.obtain();
////												msg.replyTo = pri.h.semap.myMap.myMessenger;
////												msg.what = protocolfromclient1.ApplyForJoinActivity_C;
////												String timeStampString = ac
////														.getTimeStamp();
////												Log.e("timeStampString :",
////														timeStampString);
////												msg.arg1 = Integer
////														.parseInt(timeStampString.substring(0, 7));
////												msg.arg2 = Integer
////														.parseInt(timeStampString.substring(7,15));
////												msg.obj = lo;
////												try {
////													pri.h.semap.myMap.remoteMessenger.send(msg);
////												} catch (RemoteException e) {
////													e.printStackTrace();
////												} 
//
//											}
//										})
//								.setNeutralButton("分享",
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//
//												Intent it = new Intent(Intent.ACTION_SEND);
//												it.putExtra(
//														Intent.EXTRA_TEXT,
//														"您的朋友与您分享一个活动: "
//																+ "\n"
//																+ "活动号 : "
//																+ itemList.get(index).extras
//																		.get("univeralindex"));
//												it.setType("text/plain");
//												context.startActivity(Intent.createChooser(it, "将短串分享到"));
//												
//											}
//										}).create().show();
					}
				});
		return true;
	}
	
	public boolean onTap(GeoPoint pt, MapView mMapView) {
		if (pri.h.semap.myMap.pop != null) {
			pri.h.semap.myMap.pop.hidePop();
			mMapView.removeView(pri.h.semap.myMap.contentbutton);
		}
		return false;
	}

	public boolean setListOfCloudPoiInfo(List<CloudPoiInfo> itemList) {
		this.itemList = itemList;
		return true;
	}
}
