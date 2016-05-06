package pri.h.semap;

import java.util.ArrayList;

import pri.z.show.R;
import pri.z.utils.UtilsTrans;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;

public class OfflineDemo extends Activity implements MKOfflineMapListener {
	
	private MapView mMapView = null;
	private MKOfflineMap mOffline = null;
//	private TextView cidView;
	private TextView stateView;
	private EditText cityNameView;
	private MapController mMapController = null;
	
	private static int cityID;
	/**
	 * 已下载的离线地图信息列表
	 */
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private LocalMapAdapter lAdapter = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.h_activity_offline);
        mMapView = new MapView(this);
        mMapController = mMapView.getController();
        
        mOffline = new MKOfflineMap();    
        /**
         * 初始化离线地图模块,MapControler可从MapView.getController()获取
         */
        mOffline.init(mMapController, this);
        initView();
                
	}
	
	ArrayList<String> hotCities;
	ArrayList<String> allCities;
	ImageButton imgBtnSearch;
//	ArrayList<MKOLSearchRecord> records1;
	ArrayList<MKOLSearchRecord> records2;
	private void initView(){
		
//		cidView       = (TextView)findViewById(R.id.h_offline_cityid);
		cityNameView  = (EditText)findViewById(R.id.h_offline_city);
		stateView     = (TextView)findViewById(R.id.h_offline_state);
		imgBtnSearch = (ImageButton)findViewById(R.id.h_offline_search);
		cityNameView.setText(pri.h.semap.myMap.myCity);
		imgBtnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String cityName = cityNameView.getText().toString().trim();
				if(cityName.equals("")){
					Toast.makeText(getBaseContext(), "请输入", 1).show();
					return;
				}
				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(imgBtnSearch.getWindowToken(), 0);
				ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityName);
				if(records == null){
					Toast.makeText(OfflineDemo.this, "未搜索到 "+cityNameView.getText().toString(), 1).show();
					return;
				}
				if(records.size() <= 0){
					Toast.makeText(OfflineDemo.this, "未搜索到 "+cityNameView.getText().toString(), 1).show();
					return;
				}
				cityID = records.get(0).cityID;
				if (records == null || records.size() != 1)
					return;
//				cidView.setText(String.valueOf(records.get(0).cityID));
				
				myStart(records.get(0).cityID,records.get(0).cityName);
			}
		});
		ListView hotCityList = (ListView)findViewById(R.id.h_offline_hotcitylist);
//		ListView allCityList = (ListView)findViewById(R.id.h_offline_allcitylist);
		
        hotCities = new ArrayList<String>();
        allCities = new ArrayList<String>();
        
        //获取热闹城市列表
//        records1 = mOffline.getHotCityList();
        //获取所有支持离线地图的城市
        records2 = mOffline.getOfflineCityList();
        
        if (records2 != null) {
        	for (MKOLSearchRecord r : records2 ){
        		 hotCities.add(r.cityName+"   --"+ this.formatDataSize(r.size));
        	}
        }
//        if (records2 != null) {
//        	for (MKOLSearchRecord r : records2 ){
//        		 allCities.add(r.cityName+"("+r.cityID+")"+"   --"+ this.formatDataSize(r.size));
//        	}
//        }
        
//        ListAdapter hAdapter = (ListAdapter)new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,hotCities); 
//        hotCityList.setAdapter(hAdapter);
//        ListAdapter aAdapter = (ListAdapter)new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,allCities); 
//        allCityList.setAdapter(aAdapter);
        
        
        BaseAdapter ba = new BaseAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return records2.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				
				
				View view = convertView;// 当前的View
				ViewHolderCity holder;
				final int myPosition = position;
				if (view == null) {
					view = LayoutInflater.from(OfflineDemo.this).inflate(
							R.layout.z_item_offlinemap, parent, false);
					holder = new ViewHolderCity();
					holder.tvCityName = (TextView) view
							.findViewById(R.id.z_itemOffLineMapCityName);
					holder.imgDownload = (ImageView) view
							.findViewById(R.id.z_itemOffLineMapDownLoad);
					holder.tvCitySize = (TextView) view
							.findViewById(R.id.z_itemOffLineMapCitySize);
					view.setTag(holder);
				} else {
					holder = (ViewHolderCity) view.getTag();
				}
//				holder.tvCityName.setText(hotCities.get(position));
				holder.tvCityName.setText(records2.get(myPosition).cityName);
				holder.tvCitySize.setText(UtilsTrans.ChangeBToMB(records2.get(myPosition).size)+" M");
				holder.imgDownload.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						myStart(null, records2.get(myPosition).cityID,records2.get(myPosition).cityName);
					}
				});
				return view;
			}
        	
        };
        hotCityList.setAdapter(ba);
//        allCityList.setAdapter(ba);
        
        LinearLayout cl = (LinearLayout)findViewById(R.id.h_offline_citylist_layout);
		LinearLayout lm = (LinearLayout)findViewById(R.id.h_offline_localmap_layout);
		lm.setVisibility(View.GONE);
		cl.setVisibility(View.VISIBLE);
		
		//获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if ( localMapList == null ){
            localMapList = new ArrayList<MKOLUpdateElement>();	
        }
        
        ListView localMapListView = (ListView)findViewById(R.id.h_offline_localmaplist);
        lAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(lAdapter);
		
	}
	
	private class ViewHolderCity {
		public TextView tvCityName;
		public ImageView imgDownload;
		public TextView tvCitySize;
	}
	/**
	 * 切换至城市列表
	 * @param view
	 */
	public void clickCityListButton(View view){
		LinearLayout cl = (LinearLayout)findViewById(R.id.h_offline_citylist_layout);
		LinearLayout lm = (LinearLayout)findViewById(R.id.h_offline_localmap_layout);
		lm.setVisibility(View.GONE);
		cl.setVisibility(View.VISIBLE);
		
	}
	/**
	 * 切换至下载管理列表
	 * @param view
	 */
	public void clickLocalMapListButton(View view){
		LinearLayout cl = (LinearLayout)findViewById(R.id.h_offline_citylist_layout);
		LinearLayout lm = (LinearLayout)findViewById(R.id.h_offline_localmap_layout);
		lm.setVisibility(View.VISIBLE);
		cl.setVisibility(View.GONE);
	}
	
	
	
	/**
	 * 开始下载
	 * @param view
	 */
	public void start(View view,String cityName){
//		int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.start(cityID);
		clickLocalMapListButton(null);
		Toast.makeText(this,  "开始下载 "+cityName+" 离线地图 ", Toast.LENGTH_SHORT).show();
	}
	
	public void myStart(View view, int cityId,String cityName) {
		mOffline.start(cityId);
		clickLocalMapListButton(null);
		Toast.makeText(this, "开始下载 "+cityName+" 离线地图 ", Toast.LENGTH_SHORT).show();
	}
	public void myStart(int cityId,String cityName) {
		mOffline.start(cityId);
		clickLocalMapListButton(null);
		Toast.makeText(this, "开始下载 "+cityName+" 离线地图 ", Toast.LENGTH_SHORT).show();
	}
	/**
	 * 暂停下载
	 * @param view
	 */
	public void stop(View view,String cityName){
//		cityID = Integer.parseInt(cidView.getText().toString());
		mOffline.pause(cityID);
		Toast.makeText(this, "开始下载 "+cityName+" 离线地图 ", Toast.LENGTH_SHORT)
		          .show();
	}
	/**
	 * 删除离线地图
	 * @param view
	 */
	public void remove(View view,String cityName){
//		int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.remove(cityID);
		Toast.makeText(this, "删除 "+cityName+" 离线地图", Toast.LENGTH_SHORT)
		          .show();
	}
	/**
	 * 从SD卡导入离线地图安装包
	 * @param view
	 */
    public void importFromSDCard(View view){
		int num = mOffline.scan();
		String msg = "";
		if ( num == 0){
			msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
		}
		else{
		   msg = String.format("成功导入 %d 个离线包，可以在下载管理查看",num);	
		}
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
    /**
     * 更新状态显示 
     */
    public void updateView(){
    	localMapList = mOffline.getAllUpdateInfo();
        if ( localMapList == null ){
            localMapList = new ArrayList<MKOLUpdateElement>();	
        }
    	lAdapter.notifyDataSetChanged();
    }
	
	

	@Override
    protected void onPause() {
//		int cityid = Integer.parseInt(cidView.getText().toString());
//		mOffline.pause(cityid);
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    
    public  String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
    	/**
    	 * 退出时，销毁离线地图模块
    	 */
//        mOffline.destroy();
//        mMapView.destroy();
        super.onDestroy();
    }

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			{
				MKOLUpdateElement update = mOffline.getUpdateInfo(state);
				//处理下载进度更新提示
				if ( update != null ){
				    stateView.setText(String.format("%s : %d%%", update.cityName, update.ratio));
				    updateView();
				}
			}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			//有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
		    // 版本更新提示
            //	MKOLUpdateElement e = mOffline.getUpdateInfo(state);
			
			break;
		}
		 
	}
	/**
	 * 离线地图管理列表适配器
	 */
	public class LocalMapAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
		    view = View.inflate(OfflineDemo.this, R.layout.h_offline_localmap_list,null);
		    initViewItem(view,e);
			return view;
		}
		
		void initViewItem(View view , final MKOLUpdateElement e){
//			Button display = (Button)view.findViewById(R.id.h_offline_display);
			Button remove  = (Button)view.findViewById(R.id.h_offline_remove); 
			TextView title = (TextView)view.findViewById(R.id.h_offline_title);
			TextView update = (TextView)view.findViewById(R.id.h_offline_update);
			TextView ratio = (TextView)view.findViewById(R.id.h_offline_ratio);
			ratio.setText(e.ratio+"%");
			title.setText(e.cityName);
			if ( e.update){
				update.setText("可更新");
			}
			else{
				update.setText("最新");
			}
//			if ( e.ratio != 100 ){
//				display.setEnabled(false);
//			}
//			else{
//				display.setEnabled(true);
//			}
			remove.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {				
					 mOffline.remove(e.cityID);
                     updateView();
				}
			});
//			display.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent();
//					intent.putExtra("x", e.geoPt.getLongitudeE6() );
//					intent.putExtra("y", e.geoPt.getLatitudeE6());
//					intent.setClass(OfflineDemo.this, myMap.class);
//					startActivity(intent);
//				}
//			});
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