package pri.h.semap;

import pri.z.show.R;
import pri.z.utils.GetTime;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

public class showWayPlanDetail extends Activity {

	private int error = 0;
	private int searchwayplantype;
	private MKDrivingRouteResult drivingrouteresult = null;
	private MKTransitRouteResult transitrouteresult = null;
	private MKWalkingRouteResult walkingrouteresult = null;

	ListView detailShowListView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.h_activity_show_way_plan_detail);
		searchwayplantype = intent.getIntExtra("WayPlanType", 0);
		if (searchwayplantype == 0) {
			this.drivingrouteresult = pri.h.semap.myMap.drivingrouteresult;
			if(this.drivingrouteresult == null)
			{
				Toast.makeText(this, "没有查到可用数据", Toast.LENGTH_LONG).show();
				return ;
			}
		} else if (searchwayplantype == 1) {
			this.transitrouteresult = pri.h.semap.myMap.transitrouteresult;
			if(this.transitrouteresult == null)
			{
				Toast.makeText(this, "没有查到可用数据", Toast.LENGTH_LONG).show();
				return ;
			}
		} else if (searchwayplantype == 2) {
			this.walkingrouteresult = pri.h.semap.myMap.walkingrouteresult;
			if(this.walkingrouteresult == null)
			{
				Toast.makeText(this, "没有查到可用数据", Toast.LENGTH_LONG).show();
				return ;
			}
		} else {
			Log.e("哈哈哈哈", "searchwayplantype出错了!!!!");
		}
		
		initStartEndPoi();

		initFindComponent();

		setContent();
	}

	private void initStartEndPoi() {
		// TODO Auto-generated method stub
		TextView tvStartPoi = (TextView)findViewById(R.id.z_wayDetailStartPoi);
		TextView tvEndPoi = (TextView)findViewById(R.id.z_wayDetailEndPoi);
		//起点
		if(pri.h.semap.myMap.myAddress != null){
			tvStartPoi.setText(" "+pri.h.semap.myMap.myAddress);
		}else{
			tvStartPoi.setText(" ");
		}
		//终点
		if(pri.h.semap.myMap.currentaddress != null){
			tvEndPoi.setText(" "+pri.h.semap.myMap.currentaddress );
		}else{
			tvEndPoi.setText(" ");
		}
		
	}

	private void initFindComponent() {
		detailShowListView = (ListView) findViewById(R.id.h_way_plan_detail_list);
		detailShowListView.setAdapter(new detailShowListviewLisener());
	}

	private void setContent() {

	}

	public class detailShowListviewLisener implements ListAdapter {

		@Override
		public int getCount() {
			// 这里到底该不该 "+1"
			if (searchwayplantype == 0) {
				return drivingrouteresult.getNumPlan() ;
			}
			if (searchwayplantype == 1) {
				return transitrouteresult.getNumPlan() ;
			}
			if (searchwayplantype == 2) {
				return walkingrouteresult.getNumPlan() ;
			}
			error = 1;
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (searchwayplantype == 0) {
				return drivingrouteresult.getPlan(position);
			}
			if (searchwayplantype == 1) {
				return transitrouteresult.getPlan(position);
			}
			if (searchwayplantype == 2) {
				return walkingrouteresult.getPlan(position);
			}
			error = 1;
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = getLayoutInflater();
				view = inflater.inflate(R.layout.h_listview_show_way_plan_detail,
						null);
				holder.detailTextView = (TextView) view
						.findViewById(R.id.h_listview_show_way_detail_text);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			//驾车
			if (searchwayplantype == 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("总路线: ");
				sb.append(drivingrouteresult.getPlan(position).getNumRoutes() + "\n");
				sb.append("距离: ");
				sb.append(drivingrouteresult.getPlan(position).getDistance() +" 米"+ "\n");
//				for (int fence1 = 0; fence1 < drivingrouteresult.getPlan(position).getNumRoutes(); fence1++) {
					sb.append("路线" + (position+1)+ "\n");
					for (int fence2 = 0; fence2 < drivingrouteresult.getPlan(position).getRoute(position).getNumSteps(); fence2++) {
						sb.append("\t第" + (fence2+1) + "步");
						sb.append("\t\t" + drivingrouteresult.getPlan(position).getRoute(position).getStep(fence2).getContent() + "\n");
					}
//				}
				holder.detailTextView.setTextSize(16);
				holder.detailTextView.setText(sb.toString());
			}
			//公交
			if (searchwayplantype == 1) {
				StringBuilder sb = new StringBuilder();
				sb.append("第" + (position+1) + "种方案 \n");
				sb.append("距离: ");
				sb.append(GetTime.ChangeMiToKM(transitrouteresult.getPlan(position).getDistance())+" 千米");
				sb.append("\n");
//				for(int fence1 = 0; fence1 < transitrouteresult.getNumPlan(); fence1 ++)
//				{
					
					for(int fence2 = 0; fence2 < transitrouteresult.getPlan(position).getNumLines() ; fence2 ++)// 公交线路数
						sb.append("乘坐车次:"+transitrouteresult.getPlan(position).getLine(fence2).getTitle()+"\n详细信息:" + transitrouteresult.getPlan(position).getLine(fence2).getTip() + "\n");
					for(int fence2 = 0; fence2 < transitrouteresult.getPlan(position).getNumRoute() ; fence2 ++)// 步行线路数
						for(int fence3 = 0; fence3 < transitrouteresult.getPlan(position).getRoute(fence2).getNumSteps(); fence3 ++)
							sb.append("*" + transitrouteresult.getPlan(position).getRoute(fence2).getStep(fence3).getContent() + "*");
					
//					Toast.makeText(showWayPlanDetail.this,"mEndCityList :" + transitrouteresult.getAddrResult().mEndCityList + "mEndCityList" + transitrouteresult.getAddrResult().mEndCityList + "mStartCityList" + transitrouteresult.getAddrResult().mStartCityList + "mStartPoiList" + transitrouteresult.getAddrResult().mStartPoiList + "mWpCityList" + transitrouteresult.getAddrResult().mWpCityList + "mWpPoiList" + transitrouteresult.getAddrResult().mWpPoiList, Toast.LENGTH_LONG).show();
					
					sb.append("时间: ");
					sb.append(GetTime.getMinutesTime(transitrouteresult.getPlan(position).getTime()));
//				}
				holder.detailTextView.setTextSize(16);
				holder.detailTextView.setText(sb.toString());
			}
			
			//步行
			if (searchwayplantype == 2) {
				StringBuilder sb = new StringBuilder();
				sb.append("总路线: ");
				sb.append(walkingrouteresult.getPlan(position).getNumRoutes() + "\n");
				sb.append("距离: ");
				sb.append(walkingrouteresult.getPlan(position).getDistance()+" 米");
				sb.append("\n");
				// sb.append(drivingrouteresult.getPlan(position).getRoute(position).)
//				for (int fence1 = 0; fence1 < walkingrouteresult.getPlan(position).getNumRoutes(); fence1++) {
					sb.append("路线" + (position+1)+"\n");
					for (int fence2 = 0; fence2 < walkingrouteresult.getPlan(position).getRoute(position).getNumSteps(); fence2++) {
						sb.append("\t第" + fence2 + "步");
						sb.append("\t\t" + walkingrouteresult.getPlan(position).getRoute(position).getStep(fence2).getContent() + "\n");
					}
//				}
				holder.detailTextView.setTextSize(16);
				holder.detailTextView.setText(sb.toString());
			}

			return view;
		}

		public final class ViewHolder {

			public TextView detailTextView;
		}

		@Override
		public int getViewTypeCount() {
			// 这里到底该不该 "+1"
			if (searchwayplantype == 0) {
				return drivingrouteresult.getNumPlan() ;
			}
			if (searchwayplantype == 1) {
				return transitrouteresult.getNumPlan() ;
			}
			if (searchwayplantype == 2) {
				return walkingrouteresult.getNumPlan() ;
			}

			error = 1;
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return false;
		}

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}
