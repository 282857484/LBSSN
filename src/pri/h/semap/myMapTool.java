//package pri.h.semap;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import net.micode.notes.ui.NotesListActivity;
//import net.simonvt.menudrawer.samples.R;
//import pri.h.semap.chooseview.ExpandTabView;
//import pri.h.semap.chooseview.ViewMiddle;
//import pri.h.uitool.BuildSomething;
//import pri.h.uitool.Configure;
//import pri.h.uitool.MyLocationNote;
//import pri.h.uitool.PersonInfo;
//import pri.h.uitool.RadialMenuWidget;
//import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
//import pri.h.uitool.SearchPOISomething;
//import pri.h.uitool.ShareLocationInfo;
//import pri.h.uitool.StartBoardcase;
//import pri.h.uitool.placeRadialMenuEntryRouteSearch;
//import pub.application.SEMapApplication;
//import pub.httptransfer.HTTPResponceInfo;
//import pub.infoclass.myserver.protocol.protocolwithbaidustore;
//import pub.netservice.NetService;
//import pub.uitool.fourbutton.MyAnimations;
//import pub.util.FormatTime;
//import pub.util.ImageManager2;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Service;
//import android.content.ComponentName;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.Messenger;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioGroup;
//import android.widget.RadioGroup.OnCheckedChangeListener;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.mapapi.cloud.CloudListener;
//import com.baidu.mapapi.cloud.CloudManager;
//import com.baidu.mapapi.cloud.CloudPoiInfo;
//import com.baidu.mapapi.cloud.CloudSearchResult;
//import com.baidu.mapapi.cloud.DetailSearchResult;
//import com.baidu.mapapi.map.LocationData;
//import com.baidu.mapapi.map.MKEvent;
//import com.baidu.mapapi.map.MKMapTouchListener;
//import com.baidu.mapapi.map.MKMapViewListener;
//import com.baidu.mapapi.map.MapController;
//import com.baidu.mapapi.map.MapPoi;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MyLocationOverlay;
//import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
//import com.baidu.mapapi.map.OverlayItem;
//import com.baidu.mapapi.map.PoiOverlay;
//import com.baidu.mapapi.map.PopupClickListener;
//import com.baidu.mapapi.map.PopupOverlay;
//import com.baidu.mapapi.map.RouteOverlay;
//import com.baidu.mapapi.map.TransitOverlay;
//import com.baidu.mapapi.search.MKAddrInfo;
//import com.baidu.mapapi.search.MKBusLineResult;
//import com.baidu.mapapi.search.MKDrivingRouteResult;
//import com.baidu.mapapi.search.MKPlanNode;
//import com.baidu.mapapi.search.MKPoiInfo;
//import com.baidu.mapapi.search.MKPoiResult;
//import com.baidu.mapapi.search.MKRoute;
//import com.baidu.mapapi.search.MKSearch;
//import com.baidu.mapapi.search.MKSearchListener;
//import com.baidu.mapapi.search.MKShareUrlResult;
//import com.baidu.mapapi.search.MKSuggestionResult;
//import com.baidu.mapapi.search.MKTransitRouteResult;
//import com.baidu.mapapi.search.MKWalkingRouteResult;
//import com.baidu.platform.comapi.basestruct.GeoPoint;
//
//public class myMapTool extends Activity implements CloudListener {
//
//
//	// 定位三台效果显示--普通态、跟随态、罗盘态
//	private enum E_BUTTON_TYPE {
//		LOC, COMPASS, FOLLOW
//	}
//
//	private boolean SatelliteLayer = SEMapApplication.SatelliteLayer;
//
//	private ImageView myselfImage = null;
//
//	private String resultURL = null;
//
//	private E_BUTTON_TYPE currenttype = E_BUTTON_TYPE.LOC;
//	// 地图主控件
//	// private MapView mapview = null;
//	// 地图的控制
//	private MapController mapcontroller = null;
//	// 处理地图事件回调
//	private MKMapViewListener maplistener = null;
//	// 截获屏幕坐标
//	private MKMapTouchListener maptouchlistener = null;
//	// 搜索模块
//	public static MKSearch mapsearch = null;
//
//	public static boolean isshareaddress = false;
//	public static boolean isLongClick = false;
//	// public static boolean isMyLocation = false;
//
//	public static String extraShareString = "";
//
//	private String touchtype = null;
//	// 地图状态面板栏
//	// private static TextView statebar = null;
//
//	// 当前点击地点
//	public static GeoPoint currentpoint = null;
//	// 保存搜索结果地址
//	public static String currentaddress = null;
//	// 存储位置数据,用户所处位置
//	private LocationData locationdata = new LocationData();
//	// public boolean isLocationGetAddressInfo = true;
//	public static String myCity = "北京";
//	public static String myProvince = "北京";
//	public static String myStreet = "";
//	public static String myAddress = "";
//	public MyLocationListener locationlistener = new MyLocationListener();
//
//	// 定位图层（定位点）,继承MyLocationOverlay
//	locationOverlay myLocationOverlay = null;
//	// 弹出定位泡泡图层
//	// private PopupOverlay mypop = null;
//
//	// 自定义泡泡图层
//	public static PopupOverlay pop = null;
//	public static Button contentbutton = null;
//	// 定位点泡泡的TextView
//	private TextView mypopupText = null;
//	// 自定义点泡泡的TextView
//	// private TextView popupText = null;
//	// 储存View（在这里为存放泡泡里显示的内容）
//	private View viewCache = null;
//	// 继承ItemizedOverlay，是自定义点
//	// 共有五中MyOverlay，分别是商家、周围的人、广播消息、活动、队伍（定位点在locationOverlay）
//
//	// private MyOverlay myoverlay = null;
//	private arroundPersonpleOverlay arroundpersonPOI = null;
//	private storeOverlay storePOI = null;
//	private messageOverlay messagePOI = null;
//	private activityOverlay activityPOI = null;
//	private teamOverlay teamPOI = null;
//	private teammateOverlay teammatePOI = null;
//	// 这里是活动与学校的路线
//	// private RouteOverlay activityRoute = null;
//	// private RouteOverlay schoolRoute = null;
//
//	// 被选中的poi
//	// private OverlayItem currentItem = null;
//	// 可以通过这个保存自定义点
//	// private ArrayList<OverlayItem> itemlist = null;
//	// pop的消息
//
//	// private MapView.LayoutParams bussearchlayoutParam = null;
//
//	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
//	// 如果不处理touch事件，则无需继承，直接使用MapView即可
//	MyLocationMapView mapview = null;
//	// Projection projection;
//	public static float mapZoomLevel;
//	public static int mapRotation;
//	public static int mapOverLooking;
//
//	public static float getMapZoomLevel() {
//		return mapZoomLevel;
//	}
//
//	OnCheckedChangeListener radioButtonListener = null;
//	OnClickListener getscreenListener = null;
//	Button requestLocationButton = null;
//	// Button switch_three_state_dw = null;
//	// Button switch_three_state_gs = null;
//	// Button switch_three_state_lp = null;
//	// Button getscreenButton = null;
//	Button switchSatelliteLayer = null;
//
//	/**
//	 * 非POI长按时的功能键
//	 */
//
//	OnClickListener buttonclicklistener = null;
//	// boolean isRequest = false;
//	boolean isFirstLocat = true;
//	// RadioGroup group = null;
//
//	/**
//	 * 左下角的按钮设置布局
//	 */
//	// private Button buttonCamera, buttonDelete, buttonWith, buttonPlace,
//	// buttonMusic, buttonThought, buttonSleep;
//	// private Animation animationTranslate, animationRotate, animationScale;
//	// private static int width, height;
//
//	// 曾经出现过问题，这里是动态布局的相关声明
//	// private LayoutParams params = new LayoutParams(0, 0);
//	// private static Boolean isClick = false;
//
//	// 路线导航相关
//	/**
//	 * 在这里可以进行自定义导航！ 可以添加新生入学向导！
//	 */
//	// Button customRoute = null;// 自定义路线
//
//	Button showwaydetail = null; // 展示到达细节
//
//	private int wayPlanFence = 0;
//	public static MKDrivingRouteResult drivingrouteresult = null;
//	public static MKTransitRouteResult transitrouteresult = null;
//	public static MKWalkingRouteResult walkingrouteresult = null;
//
//	int nodeIndex = -2;
//	public static MKRoute route = null;
//	public static TransitOverlay transitOverlay = null;
//	public static RouteOverlay routeOverlay = null;
//	boolean useDefaultIcon = false;
//	int searchType = -1;
//	// private PopupOverlay routeNodePop = null;
//	// private TextView routePopupText = null;
//	// private View routeViewCache = null;
//
//	// Button changoverList = null;
//
//	public static RadialMenuWidget PieMenu; // wedge可重用
//	public static LinearLayout whatisthislayout;
//	LinearLayout.LayoutParams whatisthislayoutparam;
//
//	private ExpandTabView expandTabView;
//	private ArrayList<View> mViewArray = new ArrayList<View>();
//	private ViewMiddle viewMiddle;
//
//	private boolean areButtonsShowing1 = false;
//	private RelativeLayout composerButtonsWrapperLeft;
//	private ImageView composerButtonsShowHideButtonIconLeft;
//	private RelativeLayout composerButtonsShowHideButtonLeft;
//
//	SharedPreferences mySharedPreferences;
//	Editor myEditor;
//	final String MYPREFS = "MyPreferences";
//	final int mode = Activity.MODE_PRIVATE;
//
//	Button sendmessage = null;
//
//	private Handler myHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//
//			HTTPResponceInfo info;
//			switch (msg.what) {
//			case baiduPOIOverlay.baiduPOITag:
//				MKPoiInfo baiduPOI = (MKPoiInfo) msg.obj;
//				currentpoint = baiduPOI.pt;
//				addPublicPieMenu(baiduPOI.name);
//				break;
//			case protocolwithbaidustore.replybaidutableactivityPOI:
//				break;
//			default:
//				Log.e("unknown packet", "unknown packet");
//				/**
//				 * ...实现相关协议
//				 */
//			}
//
//			/**
//			 * 这里只是一个小小的演示
//			 */
//
//		}
//	};
//
//	public static Messenger remoteMessenger = null;
//	public static Messenger myMessenger = null;
//
//	/**
//	 * Service数据指针 读取Service数据
//	 */
//	NetService.MyBinder binder;
//	private ServiceConnection connection = new ServiceConnection() {
//
//		/**
//		 * 应该是在service中的onbind()方法调用之后
//		 */
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//
//			remoteMessenger = new Messenger(service);
//			myMessenger = new Messenger(myHandler);
//			SEMapApplication.currentMessenger = myMessenger;
//			Log.e("SECurrentMessenger",
//					SEMapApplication.currentMessenger.toString());
//
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//
//		}
//
//	};
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		Intent intent = new Intent();
//		intent.setAction("com.example.semap.NetService");
//		boolean isSuccess = bindService(intent, connection,
//				Service.BIND_AUTO_CREATE);
//		if (isSuccess != true) {
//			Toast.makeText(myMapTool.this, "绑定服务未成功", Toast.LENGTH_LONG).show();
//		}
//
//		// 初始化云管理器
//		CloudManager.getInstance().init(myMapTool.this);
//
//		// 设置内容
//		setContentView(R.layout.h_activity_mymap);
//		mapview = (MyLocationMapView) findViewById(R.id.bmapView);
//		// 设置标题
//
//		myselfImage = (ImageView) findViewById(R.id.h_myselfimage);
//		requestLocationButton = (Button) findViewById(R.id.h_switch_three_state);
//		showwaydetail = (Button) findViewById(R.id.h_showwaydetail);
//		switchSatelliteLayer = (Button) findViewById(R.id.h_switch_SatelliteLayer);
//		// customRoute = (Button) findViewById(R.id.h_customroute);
//		contentbutton = new Button(this);
//		contentbutton.setBackgroundResource(R.drawable.m_popup12);
//
//		groupButtonInvisible();
//
//		expandTabView = (ExpandTabView) findViewById(R.id.h_expandtab_view);
//		// expandTabView.setVisibility(View.INVISIBLE);
//
//		// testservice = (Button) findViewById(R.id.testservice);
//		// sendregister = (Button) findViewById(R.id.sendregister);
//
//		// 初始化image
//		initMyselfImage();
//		// 初始话筛选
//		initExpandTabView();
//		// 初始化搜索模块
//		initSearch();
//
//		// 初始化监听器，按钮
//		initListener();
//
//		// 地图控制器
//		initMapController();
//
//		// 制作泡泡
//		createPaopao();
//
//		// 初始化图层
//		initOverlay();
//		// 初始化起终点overlay图标
//		initSEIcon();
//
//		// 初始化地图监听器
//		initMapListener();
//
//		// 初始化mapview
//		initMapView();
//
//		// if(savedInstanceState != null)
//		// {
//		// if(savedInstanceState.containsKey("x") &&
//		// savedInstanceState.containsKey("y"))
//		// {
//		// mapview.getController().animateTo(
//		// new GeoPoint(savedInstanceState.getInt("y"),
//		// savedInstanceState.getInt("x")));
//		// }
//
//		// 首先移动地图至上次定位点
//		mySharedPreferences = getSharedPreferences(MYPREFS, mode);
//		myEditor = mySharedPreferences.edit();
//		myEditor.putInt("time", 1);
//		// commit()为同步方法;apply为异步方法
//		// myEditor.commit();
//		myEditor.apply();
//		if (mySharedPreferences != null
//				&& mySharedPreferences.contains("MyLatitudeE6")
//				&& mySharedPreferences.contains("MyLongitudeE6")) {
//			int MyLatitudeE6 = mySharedPreferences.getInt("MyLatitudeE6",
//					28185083);
//			int MyLongitudeE6 = mySharedPreferences.getInt("MyLongitudeE6",
//					112949889);
//			GeoPoint gp = new GeoPoint(MyLatitudeE6, MyLongitudeE6);
//			mapview.getController().animateTo(gp);
//			currentpoint = gp;
//		} else {
//			mapview.getController()
//					.animateTo(new GeoPoint(28185083, 112949889));
//		}
//
//		// 初始化定位信息
//		SEMapApplication.initLocationInfo(myMapTool.this, locationdata,
//				locationlistener);
//
//		/**
//		 * 左右下角按钮设置
//		 */
//		MyAnimations.initOffset(myMapTool.this);
//		initialLeftButton();
//		// initialRightButton();
//
//		whatisthislayout = new LinearLayout(this);
//		whatisthislayoutparam = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//		addContentView(whatisthislayout, whatisthislayoutparam);
//
//	}
//
//	private void initialLeftButton() {
//		findViews1();
//		setListener1();
//		// 加号的动画
//		composerButtonsShowHideButtonLeft.startAnimation(MyAnimations
//				.getRotateAnimation(0, 360, 200));
//	}
//
//	private void findViews1() {
//		composerButtonsWrapperLeft = (RelativeLayout) findViewById(R.id.h_leftbuttonbackground);
//		composerButtonsShowHideButtonLeft = (RelativeLayout) findViewById(R.id.h_composer_buttons_show_hide_button_left);
//		composerButtonsShowHideButtonIconLeft = (ImageView) findViewById(R.id.h_button_friends_delete_left);
//
//	}
//
//	private void setListener1() {
//		// 给大按钮设置点击事件
//		composerButtonsShowHideButtonLeft
//				.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						if (!areButtonsShowing1) {
//
//							System.out.println("--startAnimationsIn");
//							// 图标的动画
//							MyAnimations.startAnimationsIn(
//									composerButtonsWrapperLeft, 300, 1);
//							// 加号的动画
//							composerButtonsShowHideButtonIconLeft
//									.startAnimation(MyAnimations
//											.getRotateAnimation(0, -225, 300));
//						} else {
//							System.out.println("##startAnimationsOut");
//							// hideFourButton(composerButtonsWrapperLeft);
//							// 图标的动画
//							MyAnimations.startAnimationsOut(
//									composerButtonsWrapperLeft, 300, 1);
//							// 加号的动画
//							composerButtonsShowHideButtonIconLeft
//									.startAnimation(MyAnimations
//											.getRotateAnimation(-225, 0, 300));
//							// hideFourButton(composerButtonsWrapperLeft);
//						}
//						areButtonsShowing1 = !areButtonsShowing1;
//					}
//				});
//
//		// 给小图标设置点击事件
//		for (int i = 0; i < composerButtonsWrapperLeft.getChildCount(); i++) {
//			final ImageView smallIcon = (ImageView) composerButtonsWrapperLeft
//					.getChildAt(i);
//			final int position = i;
//			smallIcon.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					// 这里写各个item的点击事件
//					// 1.加号按钮缩小后消失 缩小的animation
//					// 2.其他按钮缩小后消失 缩小的animation
//					// 3.被点击按钮放大后消失 透明度渐变 放大渐变的animation
//					if (position == 0) {
//						/**
//						 * 在这里进行处理
//						 */
//						final ScrollView wayplanForm = (ScrollView) getLayoutInflater()
//								.inflate(R.layout.h_wayplan_dailog, null);
//						EditText currentaddress = (EditText) wayplanForm
//								.findViewById(R.id.h_startaddress);
//						final EditText currentcity = (EditText) wayplanForm
//								.findViewById(R.id.h_way_plan_city);
//						currentaddress.setText(myStreet);
//						currentcity.setText(myCity);
//						final RadioGroup wayplantype = (RadioGroup) wayplanForm
//								.findViewById(R.id.h_wayplans);
//						final EditText startAddress = (EditText) wayplanForm
//								.findViewById(R.id.h_startaddress);
//						final EditText endAddress = (EditText) wayplanForm
//								.findViewById(R.id.h_endaddress);
//
//						new AlertDialog.Builder(myMapTool.this)
//								.setIcon(R.drawable.s_ljlogo)
//								.setTitle("路径规划")
//								.setView(wayplanForm)
//								.setPositiveButton(
//										"查询",
//										new android.content.DialogInterface.OnClickListener() {
//											/**
//											 * 请求信息，并更新到后台
//											 */
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												// TODO Auto-generated method
//												// stub
//												int typesearch = 0;
//
//												SearchButtonProcess(typesearch);
//											}
//
//											private void SearchButtonProcess(
//													int typesearch) {
//												// TODO Auto-generated method
//												// stub
//												route = null;
//												routeOverlay = null;
//												transitOverlay = null;
//												// 在MKSearch里面设置pre、nextbutton的可视
//
//												MKPlanNode startNode = new MKPlanNode();
//												MKPlanNode endNode = new MKPlanNode();
//												startNode.name = startAddress
//														.getText().toString();
//
//												endNode.name = endAddress
//														.getText().toString();
//
//												/**
//												 * radiobutton中的值不知道怎么取得 监听不成功！
//												 */
//												int checkedRadioButtonId = wayplantype
//														.getCheckedRadioButtonId();
//												if (checkedRadioButtonId == R.id.h_drivesearch) {
//													mapsearch
//															.drivingSearch(
//																	currentcity
//																			.getText()
//																			.toString(),
//																	startNode,
//																	currentcity
//																			.getText()
//																			.toString(),
//																	endNode);
//												} else if (checkedRadioButtonId == R.id.h_transitsearch) {
//													mapsearch
//															.transitSearch(
//																	currentcity
//																			.getText()
//																			.toString(),
//																	startNode,
//																	endNode);
//												} else if (checkedRadioButtonId == R.id.h_walksearch) {
//													mapsearch
//															.walkingSearch(
//																	currentcity
//																			.getText()
//																			.toString(),
//																	startNode,
//																	currentcity
//																			.getText()
//																			.toString(),
//																	endNode);
//												}
//											}
//
//										})
//								.setNegativeButton(
//										"取消",
//										new android.content.DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												// TODO Auto-generated method
//												// stub
//											}
//
//										}
//
//								).create().show();
//					} else if (position == 1) {
//						/**
//						 * 
//						 */
//						Intent intent = new Intent(myMapTool.this,
//								NotesListActivity.class);
//						startActivity(intent);
//					} else if (position == 2) {
//						/**
//						 * 截取当前地图
//						 */
//						Toast.makeText(myMapTool.this, "正在保存中...",
//								Toast.LENGTH_SHORT).show();
//						mapview.getCurrentMap();
//					} else if (position == 3) {
//						/**
//						 * 更新当前地图
//						 */
//						Toast.makeText(myMapTool.this, "刷新地图界面...",
//								Toast.LENGTH_LONG).show();
//						groupButtonInvisible();
//						clearOverlay(mapview);
//						mapview.getOverlays().clear();
//						setMyLocationOverlay();
//						mapview.refresh();
//					} else if (position == 4) {
//						/**
//						 * POI查询
//						 */
//						final ScrollView poiSearchForm = (ScrollView) getLayoutInflater()
//								.inflate(R.layout.h_poisearch_dialog, null);
//
//						EditText searchcity = (EditText) poiSearchForm
//								.findViewById(R.id.h_poicityname);
//						searchcity.setText(myCity);
//						new AlertDialog.Builder(myMapTool.this)
//								.setIcon(R.drawable.s_cxlogo)
//								.setTitle("poi查询")
//								.setView(poiSearchForm)
//								.setPositiveButton(
//										"查询",
//										new android.content.DialogInterface.OnClickListener() {
//											/**
//											 * 请求信息，并更新到后台
//											 */
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												// TODO Auto-generated method
//												// stub
//												int typesearch = 0;
//
//												SearchButtonProcess(typesearch);
//											}
//
//											private void SearchButtonProcess(
//													int typesearch) {
//												// TODO Auto-generated method
//												// stub
//												route = null;
//												routeOverlay = null;
//												transitOverlay = null;
//												// 在MKSearch里面设置pre、nextbutton的可视
//												EditText cityInfo = (EditText) poiSearchForm
//														.findViewById(R.id.h_poicityname);
//												EditText poiInfo = (EditText) poiSearchForm
//														.findViewById(R.id.h_poiname);
//
//												if (cityInfo == null) {
//													mapsearch
//															.poiSearchInCity(
//																	myCity,
//																	poiInfo.getText()
//																			.toString());
//												}
//												mapsearch.poiSearchInCity(
//														cityInfo.getText()
//																.toString(),
//														poiInfo.getText()
//																.toString());
//
//											}
//
//										})
//								.setNegativeButton(
//										"取消",
//										new android.content.DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												// TODO Auto-generated method
//												// stub
//											}
//
//										}
//
//								).create().show();
//					} else if (position == 5) {
//						/**
//						 * 离线地图管理
//						 */
//						Intent intent = new Intent(myMapTool.this,
//								OfflineDemo.class);
//						startActivity(intent);
//					}
//
//					composerButtonsShowHideButtonIconLeft
//							.startAnimation(MyAnimations.getRotateAnimation(
//									-225, 0, 300));
//					areButtonsShowing1 = !areButtonsShowing1;
//					smallIcon.startAnimation(MyAnimations.getMaxAnimation(400));
//					for (int j = 0; j < composerButtonsWrapperLeft
//							.getChildCount(); j++) {
//						if (j != position) {
//							final ImageView smallIcon = (ImageView) composerButtonsWrapperLeft
//									.getChildAt(j);
//							smallIcon.startAnimation(MyAnimations
//									.getMiniAnimation(300));
//
//						}
//					}
//					hideFourButton(composerButtonsWrapperLeft);
//				}
//			});
//		}
//	}
//
//	private void hideFourButton(ViewGroup viewgroup) {
//		for (int i = 0; i < viewgroup.getChildCount(); i++) {
//			ImageButton inoutimagebutton = (ImageButton) viewgroup
//					.getChildAt(i);
//			inoutimagebutton.setVisibility(ImageView.INVISIBLE);
//
//			inoutimagebutton.setClickable(false);
//			inoutimagebutton.setFocusable(false);
//		}
//	}
//
//	private void initMyselfImage() {
//		// TODO Auto-generated method stub
//		ImageManager2.from(myMapTool.this).displayImage(myselfImage,
//				SEMapApplication.UserHeadPortraitPath, R.drawable.h_aplaca, 80,
//				80);
//		myselfImage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				groupButtonInvisible();
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				mapcontroller.animateTo(new GeoPoint(
//						(int) (locationdata.latitude * 1e6),
//						(int) (locationdata.longitude * 1e6)));
//				// isRequest = false;
//				myLocationOverlay.setLocationMode(LocationMode.NORMAL);
//				// 更新定位数据
//				myLocationOverlay.setData(locationdata);
//				// 设置定位点
//				setMyLocationOverlay();
//				mapview.refresh();
//				// TODO Auto-generated method stub
//				int xLayoutSize = whatisthislayout.getWidth();
//				int yLayoutSize = whatisthislayout.getHeight();
//				PieMenu = new RadialMenuWidget(getBaseContext());
//				PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
//
//				PieMenu.setIconSize(15, 30);
//				PieMenu.setTextSize(13);
//
//				PieMenu.setCenterCircle(new Close());
//
//				PieMenu.addMenuEntry(new PersonInfo(myMapTool.this));
//				PieMenu.addMenuEntry(new Configure(myMapTool.this));
//				// PieMenu.addMenuEntry(new SearchPOISomething(myMap.this));
//				// PieMenu.addMenuEntry(new ShareLocationInfo(myMap.this));
//
//				whatisthislayout.addView(PieMenu);
//			}
//
//		});
//	}
//
//	private void initSEIcon() {
//		// TODO Auto-generated method stub
//		changeRouteIcon();
//	}
//
//	protected void changeRouteIcon() {
//		if (routeOverlay == null && transitOverlay == null) {
//			return;
//		}
//		if (useDefaultIcon) {
//			if (routeOverlay != null) {
//				routeOverlay.setStMarker(null);
//				routeOverlay.setEnMarker(null);
//			}
//			if (transitOverlay != null) {
//				transitOverlay.setStMarker(null);
//				transitOverlay.setEnMarker(null);
//			}
//			Toast.makeText(myMapTool.this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();
//		} else {
//			if (routeOverlay != null) {
//				routeOverlay.setStMarker(getResources().getDrawable(
//						R.drawable.icon_st));
//				routeOverlay.setEnMarker(getResources().getDrawable(
//						R.drawable.icon_en));
//			}
//			if (transitOverlay != null) {
//				transitOverlay.setStMarker(getResources().getDrawable(
//						R.drawable.icon_st));
//				transitOverlay.setEnMarker(getResources().getDrawable(
//						R.drawable.icon_en));
//			}
//			Toast.makeText(myMapTool.this, "将使用自定义起终点图标", Toast.LENGTH_SHORT)
//					.show();
//		}
//		useDefaultIcon = !useDefaultIcon;
//		mapview.refresh();
//	}
//
//	private void initExpandTabView() {
//		// TODO Auto-generated method stub
//		viewMiddle = new ViewMiddle(this);
//		mViewArray.add(viewMiddle);
//		ArrayList<String> mTextArray = new ArrayList<String>();
//		mTextArray.add("筛选分类");
//		expandTabView.setValue(mTextArray, mViewArray);
//		viewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {
//
//			@Override
//			public void getValue(String showText, int tagsX, int tagsY) {
//
//				onExpandTabViewRefresh(viewMiddle, showText);
//				String[] tag = viewMiddle.getTags(tagsX, tagsY).split(" ");
//
//				mapsearch.poiSearchNearBy(tag[1], new GeoPoint(
//						(int) (locationdata.latitude * 1000000),
//						(int) (locationdata.longitude * 1000000)), 1000);
//				Toast.makeText(myMapTool.this, viewMiddle.getTags(tagsX, tagsY),
//						Toast.LENGTH_LONG).show();
//			}
//		});
//	}
//
//	private void onExpandTabViewRefresh(View view, String showText) {
//
//		expandTabView.onPressBack();
//		int position = getExpandTabPositon(view);
//		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
//			expandTabView.setTitle(showText, position);
//		}
//
//	}
//
//	private int getExpandTabPositon(View tView) {
//		for (int i = 0; i < mViewArray.size(); i++) {
//			if (mViewArray.get(i) == tView) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//	private void initSearch() {
//		// TODO Auto-generated method stub
//		mapsearch = new MKSearch();
//		mapsearch.init(SEMapApplication.getInstance().mapmanager,
//				new MKSearchListener() {
//
//					@Override
//					public void onGetPoiDetailSearchResult(int type, int error) {
//						if (error != 0) {
//							Toast.makeText(myMapTool.this, "抱歉，未找到结果",
//									Toast.LENGTH_SHORT).show();
//						} else {
//							Toast.makeText(myMapTool.this, "成功，查看详情页面",
//									Toast.LENGTH_SHORT).show();
//						}
//					}
//
//					public void onGetAddrResult(MKAddrInfo res, int error) {
//						if (error != 0) {
//							Toast.makeText(myMapTool.this, "错误号：" + error,
//									Toast.LENGTH_LONG).show();
//							// isLocationGetAddressInfo = false;
//							isshareaddress = false;
//							return;
//						}
//						currentaddress = res.strAddr;
//						SEMapApplication.CurrentAddress = currentaddress;
//
//						// 地图移动到该点
//						mapview.getController().animateTo(res.geoPt);
//						if (res.type == MKAddrInfo.MK_GEOCODE) {
//
//							// 地理编码：通过地址检索坐标点
//							// String strInfo = String.format("纬度：%f 经度：%f",
//							// res.geoPt.getLatitudeE6() / 1e6,
//							// res.geoPt.getLongitudeE6() / 1e6);
//							// Toast.makeText(myMap.this, strInfo,
//							// Toast.LENGTH_LONG).show();
//						}
//						if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
//							// 反地理编码：通过坐标点检索详细地址及周边poi
//							// String strInfo = res.strAddr;
//							// Toast.makeText(myMap.this, strInfo,
//							// Toast.LENGTH_LONG).show();
//
//						}
//						if (isLongClick == true) {
//							// PieMenu.setHeader(currentaddress + "\n" + "经度" +
//							// currentpoint.getLatitudeE6()/1E6 + "纬度" +
//							// currentpoint.getLongitudeE6()/1E6 , 20);
//							PieMenu.setHeader(currentaddress, 20);
//							isLongClick = false;
//						}
//						if (isshareaddress == true) {
//							Intent it = new Intent(Intent.ACTION_SEND);
//							it.putExtra(Intent.EXTRA_TEXT, "您的朋友与您分享一个位置: "
//									+ res.strAddr + extraShareString);
//							it.setType("text/plain");
//							startActivity(Intent.createChooser(it, "将短串分享到"));
//							isshareaddress = false;
//						}
//
//						// 执行刷新使生效
//						mapview.refresh();
//					}
//
//					public void onGetPoiResult(MKPoiResult res, int type,
//							int error) {
//						// 错误号可参考MKEvent中的定义
//						if (error != 0 || res == null) {
//							Toast.makeText(myMapTool.this, "抱歉，未找到结果",
//									Toast.LENGTH_LONG).show();
//							return;
//						}
//						// 将地图移动到第一个POI中心点
//						if (res.getCurrentNumPois() > 0) {
//							// 将poi结果显示到地图上
//							baiduPOIOverlay poiOverlay = new baiduPOIOverlay(
//									myMapTool.this, mapview, mapsearch);
//							int currentPagePoiNum = res.getCurrentNumPois();
//							poiOverlay.setData(res.getAllPoi());
//							// int pageNum = res.getNumPages();
//							// for (int i = 0; i < currentPagePoiNum; i++) {
//							// MKPoiInfo poi = res.getPoi(i);
//							// OverlayItem item = new OverlayItem(poi.pt, "第"
//							// + i + "个点", "");
//							// poiOverlay.addItem(item);
//							// }
//							clearOverlay(mapview);
//							mapview.getOverlays().clear();
//							setMyLocationOverlay();
//							mapview.getOverlays().add(poiOverlay);
//							mapview.refresh();
//							// 当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
//							for (MKPoiInfo info : res.getAllPoi()) {
//								if (info.pt != null) {
//									mapview.getController().animateTo(info.pt);
//									break;
//								}
//							}
//						} else if (res.getCityListNum() > 0) {
//							// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//							clearOverlay(mapview);
//							String strInfo = "在";
//							for (int i = 0; i < res.getCityListNum(); i++) {
//								strInfo += res.getCityListInfo(i).city;
//								strInfo += ",";
//							}
//							strInfo += "找到结果";
//							// Toast.makeText(myMap.this, strInfo,
//							// Toast.LENGTH_LONG).show();
//						}
//					}
//
//					public void onGetDrivingRouteResult(
//							MKDrivingRouteResult res, int error) {
//						// 起点或终点有歧义，需要选择具体的城市列表或地址列表
//
//						drivingrouteresult = res;
//						if (error == MKEvent.ERROR_ROUTE_ADDR) {
//							// 遍历所有地址
//							// ArrayList<MKPoiInfo> stPois =
//							// res.getAddrResult().mStartPoiList;
//							// ArrayList<MKPoiInfo> enPois =
//							// res.getAddrResult().mEndPoiList;
//							// ArrayList<MKCityListInfo> stCities =
//							// res.getAddrResult().mStartCityList;
//							// ArrayList<MKCityListInfo> enCities =
//							// res.getAddrResult().mEndCityList;
//							return;
//						}
//						// 错误号可参考MKEvent中的定义
//						if (error != 0 || res == null) {
//							Toast.makeText(myMapTool.this, "抱歉，未找到结果",
//									Toast.LENGTH_SHORT).show();
//							return;
//						}
//						Toast.makeText(myMapTool.this, "结果", Toast.LENGTH_SHORT)
//								.show();
//
//						clearOverlay(mapview);
//						searchType = 0;
//						routeOverlay = new RouteOverlay(myMapTool.this, mapview);
//						// 此处仅展示一个方案作为示例
//						routeOverlay.setData(res.getPlan(0).getRoute(0));
//						wayPlanFence = 0;
//						// 清除其他图层
//						mapview.getOverlays().clear();
//						// setMyLocationOverlay();
//						// 添加路线图层
//						mapview.getOverlays().add(routeOverlay);
//						// 执行刷新使生效
//						mapview.refresh();
//						// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
//						mapview.getController().zoomToSpan(
//								routeOverlay.getLatSpanE6(),
//								routeOverlay.getLonSpanE6());
//						// 移动地图到起点
//						mapview.getController().animateTo(res.getStart().pt);
//						// 将路线数据保存给全局变量
//						route = res.getPlan(0).getRoute(0);
//						// 重置路线节点索引，节点浏览时使用
//						nodeIndex = -1;
//
//						groupButtonVisible();
//
//					}
//
//					public void onGetTransitRouteResult(
//							MKTransitRouteResult res, int error) {
//						// 起点或终点有歧义，需要选择具体的城市列表或地址列表
//						transitrouteresult = res;
//						if (error == MKEvent.ERROR_ROUTE_ADDR) {
//							// 遍历所有地址
//							// ArrayList<MKPoiInfo> stPois =
//							// res.getAddrResult().mStartPoiList;
//							// ArrayList<MKPoiInfo> enPois =
//							// res.getAddrResult().mEndPoiList;
//							// ArrayList<MKCityListInfo> stCities =
//							// res.getAddrResult().mStartCityList;
//							// ArrayList<MKCityListInfo> enCities =
//							// res.getAddrResult().mEndCityList;
//							return;
//						}
//						if (error != 0 || res == null) {
//							Toast.makeText(myMapTool.this, "抱歉，未找到结果",
//									Toast.LENGTH_SHORT).show();
//							return;
//						}
//						Toast.makeText(myMapTool.this, "结果", Toast.LENGTH_SHORT)
//								.show();
//
//						clearOverlay(mapview);
//
//						searchType = 1;
//						transitOverlay = new TransitOverlay(myMapTool.this, mapview);
//						// 此处仅展示一个方案作为示例
//						transitOverlay.setData(res.getPlan(0));
//						wayPlanFence = 0;
//						// 清除其他图层
//						mapview.getOverlays().clear();
//						// setMyLocationOverlay();
//						// 添加路线图层
//						mapview.getOverlays().add(transitOverlay);
//						// 执行刷新使生效
//						mapview.refresh();
//						// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
//						mapview.getController().zoomToSpan(
//								transitOverlay.getLatSpanE6(),
//								transitOverlay.getLonSpanE6());
//						// 移动地图到起点
//						mapview.getController().animateTo(res.getStart().pt);
//						// 重置路线节点索引，节点浏览时使用
//						nodeIndex = 0;
//
//						groupButtonVisible();
//
//					}
//
//					public void onGetWalkingRouteResult(
//							MKWalkingRouteResult res, int error) {
//						// 起点或终点有歧义，需要选择具体的城市列表或地址列表
//						walkingrouteresult = res;
//						if (error == MKEvent.ERROR_ROUTE_ADDR) {
//
//							// 遍历所有地址
//							// ArrayList<MKPoiInfo> stPois =
//							// res.getAddrResult().mStartPoiList;
//							// ArrayList<MKPoiInfo> enPois =
//							// res.getAddrResult().mEndPoiList;
//							// ArrayList<MKCityListInfo> stCities =
//							// res.getAddrResult().mStartCityList;
//							// ArrayList<MKCityListInfo> enCities =
//							// res.getAddrResult().mEndCityList;
//							return;
//						}
//						if (error != 0 || res == null) {
//							Toast.makeText(myMapTool.this, "抱歉，未找到结果",
//									Toast.LENGTH_SHORT).show();
//							return;
//						}
//						Toast.makeText(myMapTool.this, "结果", Toast.LENGTH_SHORT)
//								.show();
//
//						clearOverlay(mapview);
//
//						searchType = 2;
//						routeOverlay = new RouteOverlay(myMapTool.this, mapview);
//						// 此处仅展示一个方案作为示例
//						routeOverlay.setData(res.getPlan(0).getRoute(0));
//						wayPlanFence = 0;
//						// 清除其他图层
//						mapview.getOverlays().clear();
//						// setMyLocationOverlay();
//						// 添加路线图层
//						mapview.getOverlays().add(routeOverlay);
//						// 执行刷新使生效
//						mapview.refresh();
//						// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
//						mapview.getController().zoomToSpan(
//								routeOverlay.getLatSpanE6(),
//								routeOverlay.getLonSpanE6());
//						// 移动地图到起点
//						mapview.getController().animateTo(res.getStart().pt);
//						// 将路线数据保存给全局变量
//						route = res.getPlan(0).getRoute(0);
//						// 重置路线节点索引，节点浏览时使用
//						nodeIndex = -1;
//						groupButtonVisible();
//
//					}
//
//					public void onGetBusDetailResult(MKBusLineResult result,
//							int iError) {
//					}
//
//					@Override
//					public void onGetSuggestionResult(MKSuggestionResult res,
//							int arg1) {
//
//					}
//
//					/**
//					 * 在这之后会死屏,在收到短信,等其他事件发生后都会死屏
//					 */
//					@Override
//					public void onGetShareUrlResult(MKShareUrlResult result,
//							int type, int error) {
//						// 分享短串结果
//						resultURL = result.url;
//
//						Intent it = new Intent(Intent.ACTION_SEND);
//						it.putExtra(Intent.EXTRA_TEXT, "您的朋友与您分享一个位置: "
//								+ currentaddress + " -- " + resultURL);
//						it.setType("text/plain");
//						startActivity(Intent.createChooser(it, "将短串分享到"));
//
//					}
//
//				});
//	}
//
//	private void groupButtonVisible() {
//		showwaydetail.setVisibility(View.VISIBLE);
//	}
//
//	private void groupButtonInvisible() {
//		showwaydetail.setVisibility(View.INVISIBLE);
//	}
//
//	/**
//	 * 设计算法使得每次地图显示的POI更加简洁*****************************************************
//	 * 在网络回调时添加这些自定义点
//	 */
//
//	private void initOverlay() {
//		// // TODO Auto-generated method stub
//		// /**
//		// * 创建自定义overlay
//		// */
//		// myoverlay = new MyOverlay(getResources().getDrawable(
//		// R.drawable.icon_marka), mapview);
//
//		PopupClickListener popsListener = new PopupClickListener() {
//			@Override
//			public void onClickedPopup(int index) {
//				// TODO Auto-generated method stub
//				// if (index == 0) {
//				// // pop.hidePop();
//				// // GeoPoint point = new GeoPoint(currentItem.getPoint()
//				// // .getLatitudeE6() + 5000, currentItem.getPoint()
//				// // .getLongitudeE6() + 5000);
//				// // currentItem.setGeoPoint(point);
//				// // myoverlay.updateItem(currentItem);
//				//
//				// mapview.refresh();
//				// } else if (index == 2) {
//				// // currentItem.setMarker(getResources().getDrawable(
//				// // R.drawable.nav_turn_via_1));
//				// // myoverlay.updateItem(currentItem);
//				// mapview.refresh();
//				// }
//
//			}
//		};
//		pop = new PopupOverlay(mapview, popsListener);
//		MyLocationMapView.pop = pop;
//	}
//
//	private void initMapView() {
//		// TODO Auto-generated method stub
//		SEMapApplication app = (SEMapApplication) this.getApplication();
//		// 访问私有成员，调用本体获取权限
//		mapview.regMapViewListener(app.mapmanager, maplistener);
//
//		mapview.setBuiltInZoomControls(SEMapApplication.BuiltInZoomControls);
//
//		mapview.setSatellite(SEMapApplication.SatelliteLayer);
//
//		mapview.setTraffic(SEMapApplication.TrafficLayer);
//
//		mapview.setDoubleClickZooming(SEMapApplication.DoubleClickEnable);
//
//		mapview.setBuiltInZoomControls(SEMapApplication.BuiltInZoomControls);
//
//		// 设置定位点
////		setMyLocationOverlay();
//
//		// 修改定位数据后刷新图层生肖
////		mapview.refresh();
//	}
//
//	private void setMyLocationOverlay() {
//		// TODO Auto-generated method stub
//		Log.e("setMyLocationOverlay", "setMyLocationOverlay");
//
//		// 设置定位数据
//		myLocationOverlay.setData(locationdata);
//		// 添加定位图层
//		mapview.getOverlays().add(myLocationOverlay);
//		myLocationOverlay.enableCompass();
//		modifyLocationOverlayIcon(getResources().getDrawable(
//				R.drawable.h_aplaca));
//	}
//
//	private void initMapListener() {
//		// 设置地图点击监听器-单击、双击、长按
//		/**
//		 * 在地图上非POI点的点击事件 单击 显示位置信息与坐标
//		 * 
//		 * 双击
//		 * 
//		 * 长按 从定位位置出发到所在点的路径搜索 组件活动 组件队伍 搜索附近的POI点（通过百度地图的检索）
//		 * 搜索附近热门活动的POI点（LBS云检索）-引导界面 搜索附近热门消息的POI点（LBS云检索）-引导界面 短串分享功能
//		 */
//		// TODO Auto-generated method stub
//		maptouchlistener = new MKMapTouchListener() {
//
//			@Override
//			public void onMapClick(GeoPoint point) {
//				// TODO Auto-generated method stub
//				touchtype = "单击";
//				currentpoint = point;
//				mapcontroller.animateTo(point);
//
//				addPublicPieMenu(null);
//
//				updateMapState();
//				clearOverlay(mapview);
//			}
//
//			@Override
//			public void onMapDoubleClick(GeoPoint point) {
//				// TODO Auto-generated method stub
//				touchtype = "双击";
//				currentpoint = point;
//				updateMapState();
//				clearOverlay(mapview);
//			}
//
//			@Override
//			public void onMapLongClick(GeoPoint point) {
//				clearOverlay(mapview);
//				groupButtonInvisible();
//				/**
//				 * bug 在每次监听到之前都要清空上一次的按钮！！！！！！！！！！ (已解决)
//				 */
//				// TODO Auto-generated method stub
//				touchtype = "长按";
//				currentpoint = point;
//
//				mapcontroller.animateTo(point);
//
//				addPublicPieMenu(null);
//
//				// Point point123 = projection.toPixels(point, null);
//				// int xScreenSize =
//				// (getResources().getDisplayMetrics().widthPixels);
//				// int yScreenSize =
//				// (getResources().getDisplayMetrics().heightPixels);
//				// int xLayoutSize = whatisthislayout.getWidth();
//				// int yLayoutSize = whatisthislayout.getHeight();
//
//				// int ScreenLatitudeSpan = mapview.getLatitudeSpan();
//				// int ScreenLongitudeSpan = mapview.getLongitudeSpan();
//				// GeoPoint MapCenterGeoPoint = mapview.getMapCenter();
//				// GeoPoint LeftScreenGeoPoint = new
//				// GeoPoint((MapCenterGeoPoint.getLatitudeE6()-ScreenLatitudeSpan/2)
//				// ,
//				// (MapCenterGeoPoint.getLongitudeE6()-ScreenLongitudeSpan/2));
//				//
//				// int eventY =
//				// ((point.getLatitudeE6()-LeftScreenGeoPoint.getLatitudeE6())*xScreenSize)/ScreenLatitudeSpan;
//				// int eventX =
//				// ((point.getLongitudeE6()-LeftScreenGeoPoint.getLongitudeE6())*yScreenSize)/ScreenLongitudeSpan;
//				// eventY = 800 - eventY;
//				// Log.e("event!!!!!!", "eventX: " + eventX + "eventY: " +
//				// eventY);
//				//
//				// int xCenter = xScreenSize/2;
//				// int xSource = eventX;
//				// int yCenter = yScreenSize/2;
//				// int ySource = eventY;
//				// if (xScreenSize != xLayoutSize) {
//				// xCenter = xLayoutSize/2;
//				// xSource = eventX-(xScreenSize-xLayoutSize);
//				// }
//				// if (yScreenSize != yLayoutSize) {
//				// yCenter = yLayoutSize/2;
//				// ySource = eventY-(yScreenSize-yLayoutSize);
//				// }
//
//				// PieMenu = new RadialMenuWidget(getBaseContext());
//
//				// PieMenu.setAnimationSpeed(0L);
//
//				// int xLayoutSize = whatisthislayout.getWidth();
//				// int yLayoutSize = whatisthislayout.getHeight();
//				// PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
//				// PieMenu.setShowSourceLocation(true);
//				// PieMenu.setCenterLocation(xCenter, yCenter);
//
//				// PieMenu.setHeader(
//				// "纬度:" + (double) currentpoint.getLatitudeE6() / 1000000
//				// + "精度:"
//				// + (double) currentpoint.getLongitudeE6()
//				// / 1000000, 20);
//				// isLongClick = true;
//				// pri.h.semap.myMap.mapsearch
//				// .reverseGeocode(pri.h.semap.myMap.currentpoint);
//
//				// PieMenu.setIconSize(15, 30);
//				// PieMenu.setTextSize(13);
//
//				// PieMenu.setCenterCircle(new Close());
//				// PieMenu.addMenuEntry(new placeRadialMenuEntryRouteSearch(
//				// myMap.this));
//				// PieMenu.addMenuEntry(new SearchPOISomething(myMap.this));
//				// PieMenu.addMenuEntry(new ShareLocationInfo(myMap.this));
//				// PieMenu.addMenuEntry(new SearchFreeInformation(myMap.this));
//				//
//				// whatisthislayout.addView(PieMenu);
//
//				updateMapState();
//			}
//		};
//		mapview.regMapTouchListner(maptouchlistener);
//
//		// 设置点击地图poi的事件监听器、截屏的事件监听器、动画完成事件监听器、地图加载完成监听器、地图移动完成事件监听器
//		maplistener = new MKMapViewListener() {
//			// 回调函数
//
//			@Override
//			public void onClickMapPoi(MapPoi mapPoiInfo) {
//				// 地图点击poi的事件回调
//				String title = "";
//				extraShareString = mapPoiInfo.strText;
//				if (mapPoiInfo != null) {
//					title = mapPoiInfo.strText;
//					// Toast.makeText(myMap.this, title, Toast.LENGTH_SHORT)
//					// .show();
//					currentpoint = mapPoiInfo.geoPt;
//					mapcontroller.animateTo(mapPoiInfo.geoPt);
//
//					addPublicPieMenu(mapPoiInfo.strText);
//					// int xLayoutSize = whatisthislayout.getWidth();
//					// int yLayoutSize = whatisthislayout.getHeight();
//					// PieMenu = new RadialMenuWidget(getBaseContext());
//					// PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
//					// //
//					// pri.h.semap.myMap.mapsearch.reverseGeocode(currentpoint);
//					//
//					// PieMenu.setHeader(mapPoiInfo.strText, 20);
//					// PieMenu.setIconSize(15, 30);
//					// PieMenu.setTextSize(13);
//					//
//					// PieMenu.setCenterCircle(new Close());
//					// PieMenu.addMenuEntry(new placeRadialMenuEntryRouteSearch(
//					// myMap.this));
//					// PieMenu.addMenuEntry(new SearchPOISomething(myMap.this));
//					// PieMenu.addMenuEntry(new ShareLocationInfo(myMap.this));
//					// PieMenu.addMenuEntry(new BuildSomething(myMap.this));
//					// PieMenu.addMenuEntry(new
//					// SearchFreeInformation(myMap.this));
//
//					// whatisthislayout.addView(PieMenu);
//				}
//			}
//
//			// 可保存当前截图
//
//			@Override
//			public void onGetCurrentMap(Bitmap b) {
//				// TODO Auto-generated method stub
//				// File file = new File("/mnt/sdcard/test.png");
//
//				File file = new File("/"
//						+ Environment.getExternalStorageDirectory().getPath()
//						+ "/" + FormatTime.getFormatTime() + ".png");
//				FileOutputStream out;
//				try {
//					out = new FileOutputStream(file);
//					if (b.compress(Bitmap.CompressFormat.PNG, 70, out)) {
//						out.flush();
//						out.close();
//					}
//					Toast.makeText(myMapTool.this,
//							"屏幕截图成功，图片存在: " + file.toString(),
//							Toast.LENGTH_SHORT).show();
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//					System.out.println("你妹");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//
//			@Override
//			public void onMapAnimationFinish() {
//				// TODO Auto-generated method stub
//				updateMapState();
//			}
//
//			@Override
//			public void onMapLoadFinish() {
//				// TODO Auto-generated method stub
//				Toast.makeText(myMapTool.this, "地图加载对象完成", Toast.LENGTH_SHORT)
//						.show();
//			}
//
//			@Override
//			public void onMapMoveFinish() {
//				// TODO Auto-generated method stub
//				updateMapState();
//			}
//
//		};
//	}
//
//	// private void initLocationInfo() {
//	// // TODO Auto-generated method stub
//	// // 定位初始化
//	// locationclient = new LocationClient(this);
//	// locationdata = new LocationData();
//	// locationclient.registerLocationListener(locationlistener);
//	// LocationClientOption option = new LocationClientOption();
//	// option.setOpenGps(true);
//	// option.setCoorType("bd09ll");// 设置坐标类型
//	// option.setScanSpan(1000);
//	// option.setPoiExtraInfo(true);
//	// option.disableCache(false);
//	// option.setAddrType("all");
//	// locationclient.setLocOption(option);
//	// locationclient.start();
//	// }
//	// private void initLocationInfo() {
//	// this.locationdata = SEMapApplication.locationdata;
//	// }
//
//	public void addPublicPieMenu(String head) {
//		// TODO Auto-generated method stub
//		int xLayoutSize = whatisthislayout.getWidth();
//		int yLayoutSize = whatisthislayout.getHeight();
//		PieMenu = new RadialMenuWidget(getBaseContext());
//		PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
//		if (head == null) {
//			isLongClick = true;
//			pri.h.semap.myMapTool.mapsearch
//					.reverseGeocode(pri.h.semap.myMapTool.currentpoint);
//		} else {
//			PieMenu.setHeader(head, 20);
//		}
//
//		PieMenu.setIconSize(15, 30);
//		PieMenu.setTextSize(13);
//
//		PieMenu.setCenterCircle(new Close());
//		PieMenu.addMenuEntry(new placeRadialMenuEntryRouteSearch(myMapTool.this));
//		PieMenu.addMenuEntry(new SearchPOISomething(myMapTool.this));
//		PieMenu.addMenuEntry(new ShareLocationInfo(myMapTool.this));
//		PieMenu.addMenuEntry(new BuildSomething(myMapTool.this));
//		// PieMenu.addMenuEntry(new SearchFreeInformation(myMap.this));
//
//		whatisthislayout.addView(PieMenu);
//	}
//
//	private void initMapController() {
//		// TODO Auto-generated method stub
//		mapcontroller = mapview.getController();
//		mapcontroller.enableClick(SEMapApplication.enableclick);
//		mapcontroller.setZoom(SEMapApplication.initmapzoom);
//		mapcontroller
//				.setZoomGesturesEnabled(SEMapApplication.ZoomGesturesEnabled);
//		mapcontroller
//				.setScrollGesturesEnabled(SEMapApplication.ScrollGesturesEnabled);
//		mapcontroller.setRotationGesturesEnabled(SEMapApplication.RotateEnable);
//		mapcontroller
//				.setOverlookingGesturesEnabled(SEMapApplication.OverlookEnable);
//		if (SEMapApplication.CompassMarginEnable) {
//			// 左上角
//			mapcontroller.setCompassMargin(100, 100);
//			// 右上角
//			// mapcontroller.setCompassMargin(mapview.getWidth()-100, 100);
//
//		}
//	}
//
//	private void initListener() {
//
//		// 设置罗盘按钮监听器
//		buttonclicklistener = new OnClickListener() {
//			public void onClick(View v) {
//
//				switch (currenttype) {
//				case LOC:
//					requestLocationClick();
//					currenttype = E_BUTTON_TYPE.COMPASS;
//					break;
//				case COMPASS:
//					myLocationOverlay.setLocationMode(LocationMode.COMPASS);
//					currenttype = E_BUTTON_TYPE.LOC;
//					break;
//				default:
//					break;
//				}
//
//			}
//		};
//		requestLocationButton.setOnClickListener(buttonclicklistener);
//
//		OnClickListener showwaydetailbuttonlistener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(myMapTool.this, showWayPlanDetail.class);
//				intent.putExtra("WayPlanType", searchType);
//				startActivity(intent);
//			}
//		};
//		showwaydetail.setOnClickListener(showwaydetailbuttonlistener);
//
//		switchSatelliteLayer.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				SatelliteLayer = !SatelliteLayer;
//				if (SatelliteLayer) {
//					// switchSatelliteLayer.setText("卫星图");
//					switchSatelliteLayer.setText("");
//				} else {
//					// switchSatelliteLayer.setText("普通图");
//					switchSatelliteLayer.setText("");
//				}
//				mapview.setSatellite(SatelliteLayer);
//			}
//
//		});
//
//		// OnClickListener customClickListener = new OnClickListener() {
//		// public void onClick(View v) {
//		// // 自设路线绘制示例
//		// intentToActivity();
//		// }
//		//
//		// private void intentToActivity() {
//		// // TODO Auto-generated method stub
//		// Intent intent = new Intent(myMap.this,
//		// CustomRouteOverlayDemo.class);
//		// startActivity(intent);
//		// }
//		// };
//		// customRoute.setOnClickListener(customClickListener);
//	}
//
//	protected void onPause() {
//		mapview.onPause();
//
//		// CloudManager.getInstance();
//		// myMap.this.finish();
//
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		SEMapApplication.currentContext = this;
//		Log.e("currentContext", SEMapApplication.currentContext.toString());
//		// SEMapApplication.currentMessenger = myMessenger;
//		// Log.e("SECurrentMessenger",
//		// SEMapApplication.currentMessenger.toString());
//		mapview.onResume();
//		super.onResume();
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (SEMapApplication.locationclient != null)
//			SEMapApplication.locationclient.stop();
//		myMapTool.this.unbindService(connection);
//		mapview.destroy();
//		super.onDestroy();
//	}
//
//	// 保存现在的实例与状态
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		mapview.onSaveInstanceState(outState);
//	}
//
//	// 读取实例的状态
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		mapview.onRestoreInstanceState(savedInstanceState);
//	}
//
//	/**
//	 * android的生命周期并没有完善！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//	 */
//
//	// 更新textview状态
//	private void updateMapState() {
//		this.mapZoomLevel = mapview.getZoomLevel();
//		this.mapRotation = mapview.getMapRotation();
//		this.mapOverLooking = mapview.getMapOverlooking();
//		// if (statebar == null) {
//		// return;
//		// }
//		// String state = "";
//		// if (currentpoint == null) {
//		// state = "点击、长按、双击地图以获取经纬度和地图状态";
//		// } else {
//		// state = String.format(touchtype + ",当前经度 ： %f 当前纬度：%f",
//		// currentpoint.getLongitudeE6() * 1E-6,
//		// currentpoint.getLatitudeE6() * 1E-6);
//		// }
//		// state += "\n";
//		// state += String
//		// .format("zoom level= %.1f    rotate angle= %d   overlaylook angle=  %d",
//		// mapZoomLevel, this.mapRotation, this.mapOverLooking);
//		// statebar.setText(state);
//	}
//
//	public class MyLocationListener implements BDLocationListener {
//
//		
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			// TODO Auto-generated method stub
//			
//			locationdata.latitude = location.getLatitude();
//			locationdata.longitude = location.getLongitude();
//			if ( (location == null) || (locationdata.latitude < 3)
//					|| (locationdata.latitude > 55)
//					|| (locationdata.longitude < 70)
//					|| (locationdata.longitude > 136)) {
//				Log.e("onReceiveLocation", "!!!get wrong locationpoint" + locationdata.latitude + " , " +locationdata.longitude);
//				return;
//			}
//			Log.e("onReceiveLocation", "get locationpoint:" + locationdata.latitude + " , " +locationdata.longitude);
//			
//
//			// 保存数据定位
//			mySharedPreferences = getSharedPreferences(MYPREFS, mode);
//			myEditor = mySharedPreferences.edit();
//			myEditor.putInt("MyLatitudeE6",
//					(int) (locationdata.latitude * 1000000));
//			myEditor.putInt("MyLongitudeE6",
//					(int) (locationdata.longitude * 1000000));
//			myEditor.apply();
//
//			// 在这里替换呵呵!
//			myCity = location.getCity();
//			myProvince = location.getProvince();
//			myStreet = location.getStreet();
//			myAddress = location.getAddrStr();
//
//			// 定位精度圈半径，可设置为0
//			locationdata.accuracy = location.getRadius();
//			// ???方向
//			locationdata.direction = location.getDerect();
////			// 更新定位数据
////			myLocationOverlay.setData(locationdata);
//
//			// 更新图层数据
////			mapview.refresh();
//			// 是手动出发请求或首次定位时，移动到定位点
//			if (isFirstLocat) {
//				// 定位图层初始化
//				myLocationOverlay = new locationOverlay(mapview);
//				mapcontroller.animateTo(new GeoPoint(
//						(int) (locationdata.latitude * 1e6),
//						(int) (locationdata.longitude * 1e6)));
//				// isRequest = false;
//				myLocationOverlay.setLocationMode(LocationMode.NORMAL);
//				// 更新定位数据
//				myLocationOverlay.setData(locationdata);
//				Log.e("isFirstLocat", "setMyLocationOverlay");
//				// 设置定位点
//				setMyLocationOverlay();
//				mapview.refresh();
//
//				SEMapApplication.CurrentAddress = location.getAddrStr();
//				isFirstLocat = false;
//			}
//			// 更新定位数据
//			myLocationOverlay.setData(locationdata);
//
//			SEMapApplication.locationdata = locationdata;
//		}
//
//		@Override
//		public void onReceivePoi(BDLocation poiLocation) {
//			// TODO Auto-generated method stub
//			if (poiLocation == null) {
//				return;
//			}
//		}
//
//	}
//
//	// 设置定位点击事件
//	public class locationOverlay extends MyLocationOverlay {
//
//		public locationOverlay(MapView mapview) {
//			super(mapview);
//			// TODO Auto-generated constructor stub
//		}
//
//		protected boolean dispatchTap() {
//			// currentpoint.setLatitudeE6((int )(locationdata.latitude * 1E6));
//			// currentpoint.setLongitudeE6((int )(locationdata.longitude *
//			// 1E6));
//			mapcontroller.animateTo(new GeoPoint(
//					(int) (locationdata.latitude * 1E6),
//					(int) (locationdata.longitude * 1E6)));
//
//			PieMenu = new RadialMenuWidget(getBaseContext());
//
//			int xLayoutSize = whatisthislayout.getWidth();
//			int yLayoutSize = whatisthislayout.getHeight();
//
//			// PieMenu.
//			PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
//
//			// isLongClick = true;
//			PieMenu.setHeader("沃:" + myAddress, 20);
//			// pri.h.semap.myMap.mapsearch.reverseGeocode(currentpoint);
//			PieMenu.setIconSize(15, 30);
//			PieMenu.setTextSize(13);
//
//			PieMenu.setCenterCircle(new Close());
//			PieMenu.addMenuEntry(new StartBoardcase(myMapTool.this));
//			PieMenu.addMenuEntry(new MyLocationNote(myMapTool.this));
//
//			whatisthislayout.addView(PieMenu);
//			return true;
//		}
//
//	}
//
//	/**
//	 * 手动触发定位请求
//	 */
//	public void requestLocationClick() {
//		// isRequest = true;
//		SEMapApplication.locationclient.requestLocation();
//	}
//
//	/**
//	 * 修改位置图标
//	 * 
//	 * @param marker
//	 */
//	public void modifyLocationOverlayIcon(Drawable marker) {
//		// 当传入marker为null时，使用默认图标绘制
//		myLocationOverlay.setMarker(marker);
//		// 修改图层，需要刷新MapView生效
//		mapview.refresh();
//	}
//
//	/**
//	 * 创建定位个人图层
//	 */
//	public void createPaopao() {
//		viewCache = getLayoutInflater().inflate(R.layout.h_custom_text_view,
//				null);
//		mypopupText = (TextView) viewCache.findViewById(R.id.h_textcache);
//		// 泡泡点击响应回调
//		// pop这个也没有监听到！！！！！！！！！！！！！！！！！！！！！！！！！！！/
//
//		// 这里应该没用
//		// PopupClickListener popListener = new PopupClickListener() {
//		// @Override
//		// public void onClickedPopup(int index) {
//		// if( index == 0 )
//		// {
//		// Toast.makeText(myMap.this, "index = 0", Toast.LENGTH_LONG).show();
//		// }else if(index == 1)
//		// {
//		// Toast.makeText(myMap.this, "index = 1", Toast.LENGTH_LONG).show();
//		// } else if(index == 2)
//		// {
//		// Toast.makeText(myMap.this, "index = 2", Toast.LENGTH_LONG).show();
//		// } else {
//		// Toast.makeText(myMap.this, "index = x", Toast.LENGTH_LONG).show();
//		// }
//		//
//		// }
//		// };
//		// mypop = new PopupOverlay(mapview, popListener);
//		// MyLocationMapView.mypop = mypop;
//		//
//		// routeNodePop = new PopupOverlay(mapview, popListener);
//
//	}
//
//	/**
//	 * MyOverlay 是点击相应POI的事件
//	 * 
//	 * @author 侯斌
//	 * 
//	 */
//
//	// public class MyOverlay extends ItemizedOverlay {
//	//
//	// public MyOverlay(Drawable defaultMaker, MapView mapView) {
//	// super(defaultMaker, mapView);
//	// }
//	//
//	// // index是存入的顺序索引，即第几个点
//	// /**
//	// * 在这里添加功能按钮来控制事件
//	// */
//	// public boolean onTap(int index) {
//	// OverlayItem item = getItem(index);
//	// currentItem = item;
//	// if (index == 0) {
//	//
//	// } else {
//	// /**
//	// * 这里行不通，略显鸡肋
//	// */
//	//
//	// }
//	// return true;
//	// }
//	//
//	// /**
//	// * 在这里清除按钮
//	// */
//	// public boolean onTap(GeoPoint pt, MapView mMapView) {
//	// if (pop != null) {
//	// pop.hidePop();
//	//
//	// }
//	// return false;
//	// }
//	//
//	// }
//
//	/**
//	 * 清除所有Overlay
//	 * 
//	 * @param view
//	 */
//	public void clearOverlay(View view) {
//		// myoverlay.removeAll();
//		if (pop != null) {
//			pop.hidePop();
//			mapview.removeView(pri.h.semap.myMapTool.contentbutton);
//		}
//		// if (mypop != null) {
//		// mypop.hidePop();
//		// }
//
//		groupButtonInvisible();
//		mapview.refresh();
//	}
//
//	/**
//	 * 重新添加Overlay
//	 * 
//	 * @param view
//	 */
//
//	public void resetOverlay(View view) {
//		clearOverlay(null);
//		// myoverlay.addItem(itemlist);
//		mapview.refresh();
//	}
//
//	/**
//	 * implements CloudListener
//	 */
//	@Override
//	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
//		// TODO Auto-generated method stub
//		if (result != null) {
//
//		}
//	}
//
//	/**
//	 * implements CloudListener 自定义检索！ 回调
//	 */
//	@Override
//	public void onGetSearchResult(CloudSearchResult result, int error) {
//		// TODO Auto-generated method stub
//
//		if (result != null && result.poiList != null
//				&& result.poiList.size() > 0) {
//
//			clearOverlay(mapview);
//			mapview.getOverlays().clear();
//			setMyLocationOverlay();
//			initCloudOverlay(result);
//			// mapview.getOverlays().add();
//			mapview.refresh();
//			// Toast.makeText(myMap.this,
//			// "云检索返回值result: " + result.poiList.get(0).extras.toString(),
//			// Toast.LENGTH_LONG).show();
//			// mapview.getController().zoomToSpan(左上角,右下角);
//			mapview.getController().animateTo(
//					new GeoPoint((int) (result.poiList.get(0).latitude * 1e6),
//							(int) (result.poiList.get(0).longitude * 1e6)));
//			SEMapApplication.getInstance().setCloudSearchResult(result);
//		} else {
//			Toast.makeText(myMapTool.this, "抱歉,未找到相关信息", Toast.LENGTH_LONG).show();
//		}
//
//	}
//
//	private void initCloudOverlay(CloudSearchResult result) {
//		// TODO Auto-generated method stub
//		/**
//		 * 创建自定义overlay 相应的回调对应相应的Overlay事件 用switch作为开关
//		 * 
//		 */
//
//		// myoverlay = new MyOverlay(getResources().getDrawable(
//		// R.drawable.icon_marka), mapview);
//		/**
//		 * 准备overlay数据,这里模拟接收到的数据 坐标是反的！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//		 */
//		int status = result.status; // 状态码
//		int infoSize = result.size; // 分页参数，当前页返回数量
//		int totalInfo = result.total; // 分页参数，所有召回数量
//
//		switch (status) {
//		case 0: // 正常
//
//			List<CloudPoiInfo> item = result.poiList;
//			switch (item.get(0).geotableId) {
//			case baiduCloudStoreTableNumber.arroundpersonPOI:
//				arroundpersonPOI = new arroundPersonpleOverlay(getResources()
//						.getDrawable(R.drawable.h_aplaca), mapview, myMapTool.this);
//				arroundpersonPOI.setListOfCloudPoiInfo(item);
//				for (int fence = 0; fence < infoSize; fence++) {
//					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
//							(int) (item.get(fence).latitude * 1E6),
//							(int) (item.get(fence).longitude * 1E6)), "第"
//							+ fence + "个点", "");
//					// String a = (String)
//					// item.get(1).extras.get("acitivityname");
//					arroundpersonPOI.addItem(overlayitem);
//				}
//
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				setMyLocationOverlay();
//				mapview.getOverlays().add(arroundpersonPOI);
//				break;
//			case baiduCloudStoreTableNumber.activityPOI:
//				activityPOI = new activityOverlay(getResources().getDrawable(
//						R.drawable.h_aplaca), mapview, myMapTool.this);
//				activityPOI.setListOfCloudPoiInfo(item);
//				for (int fence = 0; fence < infoSize; fence++) {
//					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
//							(int) (item.get(fence).latitude * 1E6),
//							(int) (item.get(fence).longitude * 1E6)), "第"
//							+ fence + "个点", "");
//					activityPOI.addItem(overlayitem);
//				}
//
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				setMyLocationOverlay();
//				mapview.getOverlays().add(activityPOI);
//				break;
//			case baiduCloudStoreTableNumber.messagePOI:
//
//				messagePOI = new messageOverlay(getResources().getDrawable(
//						R.drawable.h_aplaca), mapview, myMapTool.this);
//				messagePOI.setListOfCloudPoiInfo(item);
//				for (int fence = 0; fence < infoSize; fence++) {
//					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
//							(int) (item.get(fence).latitude * 1E6),
//							(int) (item.get(fence).longitude * 1E6)), "第"
//							+ fence + "个点", "");
//					messagePOI.addItem(overlayitem);
//				}
//
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				setMyLocationOverlay();
//				mapview.getOverlays().add(messagePOI);
//				break;
//			case baiduCloudStoreTableNumber.storePOI:
//				storePOI = new storeOverlay(getResources().getDrawable(
//						R.drawable.h_aplaca), mapview, myMapTool.this);
//				storePOI.setListOfCloudPoiInfo(item);
//				for (int fence = 0; fence < infoSize; fence++) {
//					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
//							(int) (item.get(fence).latitude * 1E6),
//							(int) (item.get(fence).longitude * 1E6)), "第"
//							+ fence + "个点", "");
//					storePOI.addItem(overlayitem);
//				}
//
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				setMyLocationOverlay();
//				mapview.getOverlays().add(storePOI);
//				break;
//			case baiduCloudStoreTableNumber.teammatePOI:
//				teammatePOI = new teammateOverlay(getResources().getDrawable(
//						R.drawable.h_aplaca), mapview, myMapTool.this);
//				teammatePOI.setListOfCloudPoiInfo(item);
//				for (int fence = 0; fence < infoSize; fence++) {
//					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
//							(int) (item.get(fence).latitude * 1E6),
//							(int) (item.get(fence).longitude * 1E6)), "第"
//							+ fence + "个点", "");
//					teammatePOI.addItem(overlayitem);
//				}
//
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				setMyLocationOverlay();
//				mapview.getOverlays().add(teammatePOI);
//				break;
//			case baiduCloudStoreTableNumber.teamPOI:
//				teamPOI = new teamOverlay(getResources().getDrawable(
//						R.drawable.h_aplaca), mapview, myMapTool.this);
//				teamPOI.setListOfCloudPoiInfo(item);
//				for (int fence = 0; fence < infoSize; fence++) {
//					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
//							(int) (item.get(fence).latitude * 1E6),
//							(int) (item.get(fence).longitude * 1E6)), "第"
//							+ fence + "个点", "");
//					teamPOI.addItem(overlayitem);
//				}
//
//				clearOverlay(mapview);
//				mapview.getOverlays().clear();
//				setMyLocationOverlay();
//				mapview.getOverlays().add(teamPOI);
//				break;
//
//			}
//		}
//
//	}
//
//	/**
//	 * 使用ItemizevOvelray展示反地理编码点位置，当该点被点击时发起短串请求.
//	 */
//	private class PoiShareOverlay extends PoiOverlay {
//
//		public PoiShareOverlay(Activity activity, MapView mapView) {
//			super(activity, mapView);
//		}
//
//		@Override
//		protected boolean onTap(int i) {
//			MKPoiInfo info = getPoi(i);
//			currentaddress = info.address;
//			SEMapApplication.CurrentAddress = currentaddress;
//			mapsearch.poiDetailShareURLSearch(info.uid);
//			return true;
//		}
//	}
//
//	public class Close implements RadialMenuEntry {
//
//		@Override
//		public String getName() {
//			// TODO Auto-generated method stub
//			return "Close";
//		}
//
//		@Override
//		public String getLabel() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public int getIcon() {
//			// TODO Auto-generated method stub
//			return android.R.drawable.ic_menu_close_clear_cancel;
//		}
//
//		@Override
//		public List<RadialMenuEntry> getChildren() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public void menuActiviated() {
//			// TODO Auto-generated method stub
//
//			((LinearLayout) PieMenu.getParent()).removeView(PieMenu);
//
//		}
//
//	}
//
//	public static void outsideCircleClick() {
//		((LinearLayout) PieMenu.getParent()).removeView(PieMenu);
//	}
//}
//
//// class CloudOverlay extends ItemizedOverlay {
//// // POI类型
//// int poitype;
//// List<CloudPoiInfo> mylbspoint;
//// Activity mycontext;
////
//// public CloudOverlay(Activity context, MapView mapView, int type) {
//// // TODO Auto-generated constructor stub
//// super(null, mapView);
//// mycontext = context;
//// }
////
//// public void setData(List<CloudPoiInfo> lbsPoint) {
//// if (lbsPoint != null) {
//// mylbspoint = lbsPoint;
//// }
//// for (CloudPoiInfo thislbspoint : mylbspoint) {
//// GeoPoint point = new GeoPoint((int) (thislbspoint.latitude * 1e6),
//// (int) (thislbspoint.longitude * 1e6));
//// OverlayItem item = new OverlayItem(point, thislbspoint.title,
//// thislbspoint.address);
//// Drawable marker = this.mycontext.getResources().getDrawable(
//// R.drawable.icon_marka);
//// item.setMarker(marker);
//// addItem(item);
//// }
//// }
////
//// protected Object clone() throws CloneNotSupportedException {
//// return super.clone();
//// }
////
//// protected boolean onTap(int index) {
////
//// CloudPoiInfo item = mylbspoint.get(index);
//// return super.onTap(index);
//// }
////
//// }