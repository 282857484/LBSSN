package pri.h.uitool;

import java.util.List;

import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pri.z.show.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.ScrollView;

public class searchBaiduPOI implements RadialMenuEntry{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "searchBaiduPOI";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "地理信息";
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

	EditText cityInfo;
	EditText poiInfo;
	@Override
	public void menuActiviated() {
		// TODO Auto-generated method stub
		final ScrollView poiSearchForm = (ScrollView) ((Activity) context).getLayoutInflater()
				.inflate(R.layout.h_poisearch_dialog, null);

		cityInfo = (EditText) poiSearchForm
				.findViewById(R.id.h_poicityname);
		poiInfo = (EditText) poiSearchForm
				.findViewById(R.id.h_poiname);
		
		cityInfo.setText(pri.h.semap.myMap.myCity);
		
		new AlertDialog.Builder(context)
				.setIcon(R.drawable.z_luxianicon)
				.setTitle("poi查询")
				.setView(poiSearchForm)
				.setPositiveButton(
						"查询",
						new android.content.DialogInterface.OnClickListener() {
							/**
							 * 请求信息，并更新到后台
							 */
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stub
								int typesearch = 0;

								SearchButtonProcess(typesearch);
							}

							private void SearchButtonProcess(
									int typesearch) {
								// TODO Auto-generated method
								// stub
								pri.h.semap.myMap.route = null;
								pri.h.semap.myMap.routeOverlay = null;
								pri.h.semap.myMap.transitOverlay = null;
								// 在MKSearch里面设置pre、nextbutton的可视
								
								pri.h.semap.myMap.mapsearch.poiSearchInCity(cityInfo
										.getText().toString(), poiInfo
										.getText().toString());

							}

						})
				.setNegativeButton(
						"取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stub
							}

						}

				).create().show();
	}
	
	private Context context;
	
	public searchBaiduPOI(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}

}
