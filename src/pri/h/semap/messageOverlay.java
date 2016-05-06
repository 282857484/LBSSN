package pri.h.semap;

import java.util.List;

import pri.h.semap.overlayset.OverlaySet;
import pri.z.show.MomentDetail;
import pri.z.show.R;
import pri.z.utils.addressInfo;
import pub.httptransfer.SearchMomentDiscussByMessageId;
import pub.httptransfer.SearchOneMomentDetailById;
import pub.util.ImageManagerMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class messageOverlay extends ItemizedOverlay {

	MapView mapview = null;
	Context context = null;

	// List<CloudPoiInfo> itemList = new ArrayList<CloudPoiInfo>();
	// 原始点集
	List<CloudPoiInfo> itemList = null;
	// 近点合并后点集
	List<OverlaySet> listOfOverlaySet = null;

	public messageOverlay(Drawable defaultMaker, MapView mapView,
			Context context, int infoSize) {
		super(defaultMaker, mapView);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mapview = mapView;

		for (int fence = 0; fence < infoSize; fence++) {
			int resId = R.drawable.h_real_aplaca;
			String url = "";
			String ID = String.valueOf(itemList.get(fence).extras
					.get("univeralindex"));
			url = "http://" + addressInfo.localIP + ":" + addressInfo.visitPort
					+ "/" + addressInfo.visitFolderUserHeadXiaotu + "/" + ID
					+ ".png";
			// 这个应该需要修改ImageManagerMap文件，当图片返回设置最新加载的图片
			Drawable drawable = ImageManagerMap.from(context).displayImage(url,
					resId , this , fence);
//			getItem(fence).setMarker(drawable);
		}
		// getItem(0).setMarker(defaultMaker);

	}

	public boolean onTap(final int index) {

		pri.h.semap.myMap.extraShareString = (String) itemList.get(index).extras
				.get("broadcastcontent");

		// 确定点击了某点后的事件
		final LinearLayout broadcastscandialog = (LinearLayout) ((Activity) context)
				.getLayoutInflater().inflate(R.layout.h_broadcastscandialog,
						null);
		TextView title = (TextView) broadcastscandialog
				.findViewById(R.id.h_broadcasttitle);
		TextView broadcastcontent = (TextView) broadcastscandialog
				.findViewById(R.id.h_broadcastcontent);
		TextView broadcastwithtime = (TextView) broadcastscandialog
				.findViewById(R.id.h_broadcastwithtime);
		TextView univeralindex = (TextView) broadcastscandialog
				.findViewById(R.id.h_univeralindex);
		title.setText(itemList.get(index).address);
		broadcastcontent.setText(itemList.get(index).extras.get(
				"broadcastcontent").toString());
		broadcastwithtime.setText(itemList.get(index).extras.get(
				"uploadingtime").toString());
		univeralindex.setText(itemList.get(index).extras.get("univeralindex")
				.toString());
		pri.h.semap.myMap.contentbutton.setGravity(Gravity.CENTER);
		pri.h.semap.myMap.contentbutton.setText(itemList.get(index).extras.get(
				"broadcastcontent").toString());
		// pri.h.semap.myMap.contentbutton.setTextColor(0xA52A2A);
		GeoPoint thispoint = new GeoPoint(
				(int) (itemList.get(index).latitude * 1000000),
				(int) (itemList.get(index).longitude * 1000000));
		mapview.getController().animateTo(thispoint);
		pri.h.semap.myMap.pop.showPopup(pri.h.semap.myMap.contentbutton,
				thispoint, 32);
		// Toast.makeText(context, "onTap: " + index, Toast.LENGTH_LONG).show();
		pri.h.semap.myMap.contentbutton
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (pri.h.semap.myMap.pop != null) {
							pri.h.semap.myMap.pop.hidePop();
							mapview.removeView(pri.h.semap.myMap.contentbutton);
						}

						String messageId = String.valueOf(itemList.get(index).uid);
						new Thread(
								new SearchMomentDiscussByMessageId(messageId))
								.start();
						new Thread(new SearchOneMomentDetailById(messageId))
								.start();

						Intent intent = new Intent(context, MomentDetail.class);
						context.startActivity(intent);
						// ((myMap) context).finish();
						// new AlertDialog.Builder(context)
						// .setIcon(R.drawable.h_aplaca)
						// .setTitle(itemList.get(index).title)
						// .setView(broadcastscandialog)
						// .setNegativeButton("辱骂点这里",
						// new DialogInterface.OnClickListener() {
						//
						// @Override
						// public void onClick(
						// DialogInterface dialog,
						// int which) {
						//
						//
						// }
						//
						// })
						// .setPositiveButton("呵呵",
						// new DialogInterface.OnClickListener() {
						//
						// @Override
						// public void onClick(
						// DialogInterface dialog,
						// int which) {
						//
						// if (pri.h.semap.myMap.pop != null) {
						// pri.h.semap.myMap.pop
						// .hidePop();
						// mapview.removeView(pri.h.semap.myMap.contentbutton);
						// }
						// }
						// })
						// .setNeutralButton("泥马",
						// new DialogInterface.OnClickListener() {
						//
						// @Override
						// public void onClick(
						// DialogInterface dialog,
						// int which) {
						//
						// if (pri.h.semap.myMap.pop != null) {
						// pri.h.semap.myMap.pop
						// .hidePop();
						// mapview.removeView(pri.h.semap.myMap.contentbutton);
						// }
						// }
						// }).create().show();
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

	/**
	 * 将原始点集处理为合并点集
	 */
	// public boolean itemListChangeToListOfOverlaySet()
	// {
	// ChangedPointToSet CPTS = new ChangedPointToSet();
	// listOfOverlaySet = CPTS.go(itemList);
	// return true;
	// }
}
