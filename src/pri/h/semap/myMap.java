package pri.h.semap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pri.h.semap.chooseview.ExpandTabView;
import pri.h.semap.chooseview.ViewMiddle;
import pri.h.uitool.BuildSomething;
import pri.h.uitool.RadialMenuWidget;
import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
import pri.h.uitool.SearchPOISomething;
import pri.h.uitool.ShareLocationInfo;
import pri.h.uitool.busThat;
import pri.h.uitool.driveThat;
import pri.h.uitool.placeRadialMenuEntryRouteSearch;
import pri.h.uitool.walkThat;
import pri.z.build.BuildActivity;
import pri.z.build.ShareMoment;
import pri.z.main.MainActivity;
import pri.z.show.ClearEditText;
import pri.z.show.MyDialog;
import pri.z.show.R;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.HTTPResponceInfo;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.util.FormatTime;
import pub.util.ImageManager2;
import pub.util.ImageManagerMap;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
//import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class myMap extends Activity implements CloudListener {

	final static String TAG = "MainActivity";

	static Context context = null;

	// 定位三台效果显示--普通态、跟随态、罗盘态
	private enum E_BUTTON_TYPE {
		LOC, COMPASS, FOLLOW
	}

	private boolean SatelliteLayer = SEMapApplication.SatelliteLayer;

	private String resultURL = null;

	private E_BUTTON_TYPE currenttype = E_BUTTON_TYPE.COMPASS;
	// 地图主控件
	// private MapView mapview = null;
	// 地图的控制
	private MapController mapcontroller = null;
	// 处理地图事件回调
	private MKMapViewListener maplistener = null;
	// 截获屏幕坐标
	private MKMapTouchListener maptouchlistener = null;
	// 搜索模块
	public static MKSearch mapsearch = null;

	public static boolean isshareaddress = false;
	public static boolean isLongClick = false;
	// public static boolean isMyLocation = false;

	public static String extraShareString = "";

	private String touchtype = null;
	// 地图状态面板栏
	// private static TextView statebar = null;

	// 当前点击地点
	public static GeoPoint currentpoint = null;
	// 保存搜索结果地址
	public static String currentaddress = null;
	// 存储位置数据,用户所处位置
	private static LocationData locationdata = new LocationData();
	// public boolean isLocationGetAddressInfo = true;
	public static String myCity = "北京";
	public static String myProvince = "北京";
	public static String myStreet = "";
	public static String myAddress = "";
	public MyLocationListener locationlistener = new MyLocationListener();

	// 定位图层（定位点）,继承MyLocationOverlay
	locationOverlay myLocationOverlay = null;
	// 弹出定位泡泡图层
	// private PopupOverlay mypop = null;

	// 自定义泡泡图层
	public static PopupOverlay pop = null;
	public static Button contentbutton = null;
	// 定位点泡泡的TextView
	private TextView mypopupText = null;
	// 自定义点泡泡的TextView
	// private TextView popupText = null;
	// 储存View（在这里为存放泡泡里显示的内容）
	private View viewCache = null;
	// 继承ItemizedOverlay，是自定义点
	// 共有五中MyOverlay，分别是商家、周围的人、广播消息、活动、队伍（定位点在locationOverlay）

	// private MyOverlay myoverlay = null;
	private arroundPersonpleOverlay arroundpersonPOI = null;
	private storeOverlay storePOI = null;
	private messageOverlay messagePOI = null;
	private activityOverlay activityPOI = null;
	private teamOverlay teamPOI = null;
	private teammateOverlay teammatePOI = null;
	// 这里是活动与学校的路线
	// private RouteOverlay activityRoute = null;
	// private RouteOverlay schoolRoute = null;

	// 被选中的poi
	// private OverlayItem currentItem = null;
	// 可以通过这个保存自定义点
	// private ArrayList<OverlayItem> itemlist = null;
	// pop的消息

	// private MapView.LayoutParams bussearchlayoutParam = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyLocationMapView mapview = null;
	// Projection projection;
	public static float mapZoomLevel;
	public static int mapRotation;
	public static int mapOverLooking;

	public static float getMapZoomLevel() {
		return mapZoomLevel;
	}

	OnCheckedChangeListener radioButtonListener = null;
	OnClickListener getscreenListener = null;
	ImageButton requestLocationButton = null;
	ImageButton mapRefreshBtn = null;
	ImageButton mapDirection = null;
	// Button switch_three_state_dw = null;
	// Button switch_three_state_gs = null;
	// Button switch_three_state_lp = null;
	// Button getscreenButton = null;
	ImageButton switchSatelliteLayer = null;

	ImageView h_map_searchBtn = null;
	OnClickListener h_map_searchBtnlistener = null;
	ClearEditText h_map_search = null;

	/**
	 * 非POI长按时的功能键
	 */

	OnClickListener buttonclicklistener = null;
	// boolean isRequest = false;
	boolean isFirstLocat = true;
	// RadioGroup group = null;

	/**
	 * 左下角的按钮设置布局
	 */
	// private Button buttonCamera, buttonDelete, buttonWith, buttonPlace,
	// buttonMusic, buttonThought, buttonSleep;
	// private Animation animationTranslate, animationRotate, animationScale;
	// private static int width, height;

	// 曾经出现过问题，这里是动态布局的相关声明
	// private LayoutParams params = new LayoutParams(0, 0);
	// private static Boolean isClick = false;

	// 路线导航相关
	/**
	 * 在这里可以进行自定义导航！ 可以添加新生入学向导！
	 */
	// Button customRoute = null;// 自定义路线

	Button showwaydetail = null; // 展示到达细节

	private int wayPlanFence = 0;
	public static MKDrivingRouteResult drivingrouteresult = null;
	public static MKTransitRouteResult transitrouteresult = null;
	public static MKWalkingRouteResult walkingrouteresult = null;

	int nodeIndex = -2;
	public static MKRoute route = null;
	public static TransitOverlay transitOverlay = null;
	public static RouteOverlay routeOverlay = null;
	boolean useDefaultIcon = false;
	int searchType = -1;
	// private PopupOverlay routeNodePop = null;
	// private TextView routePopupText = null;
	// private View routeViewCache = null;

	// Button changoverList = null;

	public static RadialMenuWidget PieMenu; // wedge可重用
	public static LinearLayout whatisthislayout;
	LinearLayout.LayoutParams whatisthislayoutparam;

	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewMiddle viewMiddle;

	// private boolean areButtonsShowing1 = false;
	// private RelativeLayout composerButtonsWrapperLeft;
	// private ImageView composerButtonsShowHideButtonIconLeft;
	// private RelativeLayout composerButtonsShowHideButtonLeft;

	SharedPreferences mySharedPreferences;
	Editor myEditor;
	final String MYPREFS = "MyPreferences";
	final int mode = Activity.MODE_PRIVATE;

	Button sendmessage = null;

	private static Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			HTTPResponceInfo info;
			switch (msg.what) {
			case baiduPOIOverlay.baiduPOITag:
				MKPoiInfo poiInfo = (MKPoiInfo) msg.obj;
				currentpoint = poiInfo.pt;
				SEMapApplication.CurrentAddress = poiInfo.address;

				if (mark == 4) {
					addChoiceActivityBuildPlace("选择: " + poiInfo.address
							+ " 作为活动场地?");
				} else {
					addPublicPieMenu(poiInfo.name);
				}

				break;
			case protocolwithbaidustore.replybaidutableactivityPOI:
				break;
			default:
				Log.e("unknown packet", "unknown packet");
				/**
				 * ...实现相关协议
				 */
			}

			/**
			 * 这里只是一个小小的演示
			 */

		}
	};

	public Messenger remoteMessenger = null;
	public Messenger myMessenger = null;

	/**
	 * Service数据指针 读取Service数据
	 */
	// NetService.MyBinder binder;
	// private ServiceConnection connection = new ServiceConnection() {
	//
	// /**
	// * 应该是在service中的onbind()方法调用之后
	// */
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	//
	// remoteMessenger = new Messenger(service);
	// myMessenger = new Messenger(myHandler);
	//
	// // Log.e("SECurrentMessenger",
	// // SEMapApplication.currentMessenger.toString());
	//
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// };

	private static int mark = 0;

	public void onCreate(Bundle InstanceState) {
		super.onCreate(InstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		context = myMap.this;

		Bundle savedInstanceState = getIntent().getExtras();
		if (savedInstanceState.containsKey("mark")) {
			mark = savedInstanceState.getInt("mark");

			if (mark == 4) {
				Toast.makeText(context, "点击可选择您的活动举办地", Toast.LENGTH_LONG)
						.show();
			}
		}
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		// 初始化云管理器
		CloudManager.getInstance().init(myMap.this);

		// 设置内容
		setContentView(R.layout.h_activity_mymap);
		mapview = (MyLocationMapView) findViewById(R.id.bmapView);
		// 设置标题

		requestLocationButton = (ImageButton) findViewById(R.id.h_switch_three_state);
		mapRefreshBtn = (ImageButton) findViewById(R.id.h_map_refresh);
		mapDirection = (ImageButton) findViewById(R.id.h_direction);

		ImageButton cutphotoImageView = (ImageButton) findViewById(R.id.h_cutphotoImageView);
		showwaydetail = (Button) findViewById(R.id.h_showwaydetail);
		switchSatelliteLayer = (ImageButton) findViewById(R.id.h_switch_SatelliteLayer);
		h_map_searchBtn = (ImageView) findViewById(R.id.h_map_searchBtn);
		h_map_search = (ClearEditText) findViewById(R.id.h_map_search);

		// customRoute = (Button) findViewById(R.id.h_customroute);
		contentbutton = new Button(this);
		contentbutton.setBackgroundResource(R.drawable.m_popup12);

		cutphotoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(myMap.this, "正在保存中...", Toast.LENGTH_SHORT)
						.show();
				mapview.getCurrentMap();
			}
		});
		groupButtonInvisible();

		expandTabView = (ExpandTabView) findViewById(R.id.h_expandtab_view);
		// expandTabView.setVisibility(View.INVISIBLE);

		// testservice = (Button) findViewById(R.id.testservice);
		// sendregister = (Button) findViewById(R.id.sendregister);

		// 初始化image
		// initMyselfImage();
		// 初始话筛选
		initExpandTabView();
		// 初始化搜索模块
		initSearch();

		// 初始化监听器，按钮
		initListener();

		// 地图控制器
		initMapController();

		// 制作泡泡
		// createPaopao();

		// 初始化图层
		initOverlay();
		// 初始化起终点overlay图标
		initSEIcon();

		// 初始化地图监听器
		initMapListener();

		// 初始化mapview
		initMapView();

		// 初始化底部按钮监听
		initBuildListener();

		// if(savedInstanceState != null)
		// {
		// if(savedInstanceState.containsKey("x") &&
		// savedInstanceState.containsKey("y"))
		// {
		// mapview.getController().animateTo(
		// new GeoPoint(savedInstanceState.getInt("y"),
		// savedInstanceState.getInt("x")));
		// }

		if (mark == 0) {
			// 首先移动地图至上次定位点
			mySharedPreferences = getSharedPreferences(MYPREFS, mode);
			myEditor = mySharedPreferences.edit();
			myEditor.putInt("time", 1);
			// commit()为同步方法;apply为异步方法
			// myEditor.commit();
			myEditor.apply();
			if (mySharedPreferences != null
					&& mySharedPreferences.contains("MyLatitudeE6")
					&& mySharedPreferences.contains("MyLongitudeE6")) {
				int MyLatitudeE6 = mySharedPreferences.getInt("MyLatitudeE6",
						28185083);
				int MyLongitudeE6 = mySharedPreferences.getInt("MyLongitudeE6",
						112949889);
				GeoPoint gp = new GeoPoint(MyLatitudeE6, MyLongitudeE6);
				mapview.getController().animateTo(gp);
				currentpoint = gp;

				locationdata.latitude = ((double) MyLatitudeE6) / 1E6;
				locationdata.longitude = ((double) MyLongitudeE6) / 1E6;
			} else {
				mapview.getController().animateTo(
						new GeoPoint(28185083, 112949889));
				locationdata.latitude = ((double) 28185083) / 1E6;
				locationdata.longitude = ((double) 112949889) / 1E6;
			}
		} else {
			if (mark == 1) {
				CloudPoiInfo cpi = SEMapApplication.getInstance().transferObj
						.getShowCloudPoiInfo();
			}
		}

		// 初始化定位信息
		SEMapApplication.initLocationInfo(myMap.this, locationdata,
				locationlistener);

		/**
		 * 左右下角按钮设置
		 */
		// MyAnimations.initOffset(myMap.this);
		// initialLeftButton();
		// initialRightButton();

		/**
		 * replace leftbutton
		 */
		initialBottomButton();

		whatisthislayout = new LinearLayout(this);
		whatisthislayoutparam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		addContentView(whatisthislayout, whatisthislayoutparam);

		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(h_map_searchBtn.getWindowToken(), 0);

		initResources();// 初始化view
		initServices();// 初始化传感器和位置服务
	}

	private final float MAX_ROATE_DEGREE = 1.0f;// 最多旋转一周，即360°
	private SensorManager mSensorManager;// 传感器管理对象
	private Sensor mOrientationSensor;// 传感器对象
	private float mDirection;// 当前浮点方向
	private float mTargetDirection;// 目标浮点方向
	private AccelerateInterpolator mInterpolator;// 动画从开始到结束，变化率是一个加速的过程,就是一个动画速率
	protected final Handler mHandler = new Handler();
	private boolean mStopDrawing;// 是否停止指南针旋转的标志位
	private boolean mChinease;// 系统当前是否使用中文
	private long firstExitTime = 0L;// 用来保存第一次按返回键的时间

	// 重力传感器
	private Vibrator mVibrator;
	private final int ROCKPOWER = 15;// 这是传感器系数

	// View mCompassView;
	// LinearLayout mDirectionLayout;// 显示方向（东南西北）的view
	// LinearLayout mAngleLayout;// 显示方向度数的view

	// 这个是更新指南针旋转的线程，handler的灵活使用，每20毫秒检测方向变化值，对应更新指南针旋转
	protected Runnable mCompassViewUpdater = new Runnable() {
		@Override
		public void run() {
			if (!mStopDrawing) {
				if (mDirection != mTargetDirection) {

					// calculate the short routine
					float to = mTargetDirection;
					if (to - mDirection > 180) {
						to -= 360;
					} else if (to - mDirection < -180) {
						to += 360;
					}

					// limit the max speed to MAX_ROTATE_DEGREE
					float distance = to - mDirection;
					if (Math.abs(distance) > MAX_ROATE_DEGREE) {
						distance = distance > 0 ? MAX_ROATE_DEGREE
								: (-1.0f * MAX_ROATE_DEGREE);
					}

					// need to slow down if the distance is short
					mDirection = normalizeDegree(mDirection
							+ ((to - mDirection) * mInterpolator
									.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f
											: 0.3f)));// 用了一个加速动画去旋转图片，很细致
					// mPointer.updateDirection(mDirection);// 更新指南针旋转
				}

				updateDirection();// 更新方向值

				mHandler.postDelayed(mCompassViewUpdater, 500);// 20毫米后重新执行自己，比定时器好
			}
		}
	};

	// 初始化view
	private void initResources() {
		mDirection = 0.0f;// 初始化起始方向
		mTargetDirection = 0.0f;// 初始化目标方向
		mInterpolator = new AccelerateInterpolator();// 实例化加速动画对象
		mStopDrawing = true;
		mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");// 判断系统当前使用的语言是否为中文

		// mCompassView = findViewById(R.id.view_compass);//
		// 实际上是一个LinearLayout，装指南针ImageView和位置TextView
		// mDirectionLayout = (LinearLayout)
		// findViewById(R.id.layout_direction);// 顶部显示方向名称（东南西北）的LinearLayout
		// mAngleLayout = (LinearLayout) findViewById(R.id.layout_angle);//
		// 顶部显示方向具体度数的LinearLayout

	}

	// 初始化传感器和位置服务
	private void initServices() {
		// sensor manager
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// 震动服务, 震动需要在androidmainfest里面注册哦亲
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
	}

	private boolean isDirection = false;

	// 更新顶部方向显示的方法
	private void updateDirection() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 先移除layout中所有的view
		// mDirectionLayout.removeAllViews();
		// mAngleLayout.removeAllViews();

		// 下面是根据mTargetDirection，作方向名称图片的处理
		ImageView east = null;
		ImageView west = null;
		ImageView south = null;
		ImageView north = null;
		float direction = normalizeDegree(mTargetDirection * -1.0f);
		if (direction > 22.5f && direction < 157.5f) {
			// east
			east = new ImageView(this);
			east.setImageResource(mChinease ? R.drawable.e_cn : R.drawable.e);
			east.setLayoutParams(lp);
		} else if (direction > 202.5f && direction < 337.5f) {
			// west
			west = new ImageView(this);
			west.setImageResource(mChinease ? R.drawable.w_cn : R.drawable.w);
			west.setLayoutParams(lp);
		}

		if (direction > 112.5f && direction < 247.5f) {
			// south
			south = new ImageView(this);
			south.setImageResource(mChinease ? R.drawable.s_cn : R.drawable.s);
			south.setLayoutParams(lp);
		} else if (direction < 67.5 || direction > 292.5f) {
			// north
			north = new ImageView(this);
			north.setImageResource(mChinease ? R.drawable.n_cn : R.drawable.n);
			north.setLayoutParams(lp);
		}
		// 下面是根据系统使用语言，更换对应的语言图片资源
		if (mChinease) {
			// east/west should be before north/south
			if (east != null) {
				// mDirectionLayout.addView(east);
			}
			if (west != null) {
				// mDirectionLayout.addView(west);
			}
			if (south != null) {
				// mDirectionLayout.addView(south);
			}
			if (north != null) {
				// mDirectionLayout.addView(north);
			}
		} else {
			// north/south should be before east/west
			if (south != null) {
				// mDirectionLayout.addView(south);
			}
			if (north != null) {
				// mDirectionLayout.addView(north);
			}
			if (east != null) {
				// mDirectionLayout.addView(east);
			}
			if (west != null) {
				// mDirectionLayout.addView(west);
			}
		}
		// 下面是根据方向度数显示度数图片数字
		int direction2 = (int) direction;

		if (isDirection) {
			mapcontroller.setRotation(direction2);
		}

		boolean show = false;
		if (direction2 >= 100) {
			// mAngleLayout.addView(getNumberImage(direction2 / 100));
			direction2 %= 100;
			show = true;
		}
		if (direction2 >= 10 || show) {
			// mAngleLayout.addView(getNumberImage(direction2 / 10));
			direction2 %= 10;
		}

		// mAngleLayout.addView(getNumberImage(direction2));
		// 下面是增加一个°的图片
		// ImageView degreeImageView = new ImageView(this);
		// degreeImageView.setImageResource(R.drawable.degree);
		// degreeImageView.setLayoutParams(lp);
		// mAngleLayout.addView(degreeImageView);
	}

	// 方向传感器变化监听
	private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			float direction = event.values[0] * -1.0f;
			mTargetDirection = normalizeDegree(direction);// 赋值给全局变量，让指南针旋转
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private static boolean isMessageSearch = true;
	private static int IndexNow = 0;
	private static long lastsearchtime = 1;
	// 重力传感器
	private SensorEventListener GSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			long nowTime = Calendar.getInstance().getTimeInMillis();
			// long nowTime = Long.valueOf(FormatTime.getFormatTime());
			if ((nowTime - lastsearchtime) < 1000) {
				return;
			}
			lastsearchtime = nowTime;

			int sensorType = event.sensor.getType();
			// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
			float[] values = event.values;
			if (sensorType == Sensor.TYPE_ACCELEROMETER) {
				// 在 这个if里面写监听，写要摇一摇干么子，知道么？猪头~~~
				if ((Math.abs(values[0]) > ROCKPOWER
						|| Math.abs(values[1]) > ROCKPOWER || Math
						.abs(values[2]) > ROCKPOWER)) {
					// System.out.println("YYYYYYYYYYYY   Math.abs(values[0]=" +
					// Math.abs(values[0]) + "     Math.abs(values[1]=" +
					// Math.abs(values[1]) + "       Math.abs(values[2]" +
					// Math.abs(values[2]));
					mVibrator.vibrate(500);// 设置震动。
					if (mapview != null)
						if (isMessageSearch) {
							NearbySearchInfo searchinfo = new NearbySearchInfo();
							searchinfo.ak = SEMapApplication.strkey;
							searchinfo.geoTableId = 49436;
							// searchinfo.filter = "time:20130801,20140801";
							// 不知道经纬度是否是反的,这里的经纬度应该是currentpoint
							// searchinfo.location = (String) (((double)
							// pri.h.semap.myMap.currentpoint
							// .getLongitudeE6()) / 1000000 + "," +
							// ((double) pri.h.semap.myMap.currentpoint
							// .getLatitudeE6()) / 1000000);
							searchinfo.location = (String) ((locationdata.longitude)
									+ "," + (locationdata.latitude));
							Toast.makeText(myMap.this, searchinfo.location,
									Toast.LENGTH_SHORT).show();
							searchinfo.radius = 3000;
							searchinfo.sortby = "uploadingtime:-1";
							searchinfo.tags = "";
							searchinfo.q = "";
							searchinfo.pageIndex = IndexNow;
							IndexNow++;
							searchinfo.pageSize = 10;

							CloudManager.getInstance().nearbySearch(searchinfo);
						}

				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

	};

	// 调整方向传感器获取的值
	private float normalizeDegree(float degree) {
		return (degree + 720) % 360;
	}

	private void initialBottomButton() {
		// TODO Auto-generated method stub

	}

	public static int BuildTypeRequest = 903;
	public static int BuildType_Activity = 904;
	public static int BuildType_Moment = 905;

	private void initBuildListener() {
		// TODO Auto-generated method stub
		RelativeLayout relSearch = (RelativeLayout) findViewById(R.id.z_mymapWaySearchRel);
		ImageButton imgSearch = (ImageButton) findViewById(R.id.z_mymapWaySearchImg);
		relSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSearchDialog();
			}
		});
		imgSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSearchDialog();
			}
		});

		RelativeLayout relBuild = (RelativeLayout) findViewById(R.id.z_mymapWayBuildRel);
		ImageButton imgBuild = (ImageButton) findViewById(R.id.z_mymapWayBuildImg);
		relBuild.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectBuildType();
			}
		});
		imgBuild.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectBuildType();
			}
		});

		RelativeLayout relOffLine = (RelativeLayout) findViewById(R.id.z_mymapWayOffLineRel);
		ImageButton imgOffLine = (ImageButton) findViewById(R.id.z_mymapWayOffLineImg);
		relOffLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(myMap.this, OfflineDemo.class);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		imgOffLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(myMap.this, OfflineDemo.class);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
	}

	/**
	 * 显示弹出选择框
	 */
	public void showSelectBuildType() {

		final LinearLayout wayplanForm = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.z_dialog_selecttype, null);
		Button btnBuildActivity = (Button) wayplanForm
				.findViewById(R.id.z_selectBuildActivity);
		Button btnBuildMoment = (Button) wayplanForm
				.findViewById(R.id.z_selectShareMoment);
		final MyDialog dialog = new MyDialog(myMap.this, R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();

		btnBuildActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(getBaseContext(), "正在创建活动。。。", 1).show();
				Intent intent = new Intent(myMap.this, BuildActivity.class);
				startActivity(intent);

				dialog.dismiss();
				// finish();
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		// 创建动态
		btnBuildMoment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(myMap.this, ShareMoment.class);
				startActivity(intent);
				dialog.dismiss();
				// finish();
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

	}

	/**
	 * 显示路径规划
	 */
	public void showSearchDialog() {
		/**
		 * 在这里进行处理
		 */
		final ScrollView wayplanForm = (ScrollView) getLayoutInflater()
				.inflate(R.layout.z_dialog_wayplannow, null);
		EditText currentaddress = (EditText) wayplanForm
				.findViewById(R.id.h_startaddress);
		currentaddress.setText(myStreet);
		final RadioGroup wayplantype = (RadioGroup) wayplanForm
				.findViewById(R.id.h_wayplans);
		final EditText startAddress = (EditText) wayplanForm
				.findViewById(R.id.h_startaddress);
		final EditText endAddress = (EditText) wayplanForm
				.findViewById(R.id.h_endaddress);

		Button btn = (Button) wayplanForm.findViewById(R.id.z_dialogSearchWay);
		Button btnDismiss = (Button) wayplanForm
				.findViewById(R.id.z_dialogSearchDismiss);
		final MyDialog dialog = new MyDialog(myMap.this, R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();
		endAddress.requestFocus();

		InputMethodManager inputManager = (InputMethodManager) endAddress
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(endAddress, 0);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// int typesearch = 0;
				SearchButtonProcess(v);
				dialog.dismiss();
			}

			private void SearchButtonProcess(View v) {
				// TODO Auto-generated method
				// stub
				route = null;
				routeOverlay = null;
				transitOverlay = null;
				// 在MKSearch里面设置pre、nextbutton的可视

				MKPlanNode startNode = new MKPlanNode();
				MKPlanNode endNode = new MKPlanNode();
				startNode.name = startAddress.getText().toString();

				endNode.name = endAddress.getText().toString();

				/**
				 * radiobutton中的值不知道怎么取得 监听不成功！
				 */
				int checkedRadioButtonId = wayplantype
						.getCheckedRadioButtonId();
				if (checkedRadioButtonId == R.id.h_drivesearch) {
					mapsearch.drivingSearch(myCity, startNode, myCity, endNode);
				} else if (checkedRadioButtonId == R.id.h_transitsearch) {
					mapsearch.transitSearch(myCity, startNode, endNode);
				} else if (checkedRadioButtonId == R.id.h_walksearch) {
					mapsearch.walkingSearch(myCity, startNode, myCity, endNode);
				}
			}

		});

		btnDismiss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	// private void setListener1() {
	// // 给大按钮设置点击事件
	// composerButtonsShowHideButtonLeft
	// .setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if (!areButtonsShowing1) {
	//
	// System.out.println("--startAnimationsIn");
	// // 图标的动画
	// MyAnimations.startAnimationsIn(
	// composerButtonsWrapperLeft, 300, 1);
	// // 加号的动画
	// composerButtonsShowHideButtonIconLeft
	// .startAnimation(MyAnimations
	// .getRotateAnimation(0, -225, 300));
	// } else {
	// System.out.println("##startAnimationsOut");
	// // hideFourButton(composerButtonsWrapperLeft);
	// // 图标的动画
	// MyAnimations.startAnimationsOut(
	// composerButtonsWrapperLeft, 300, 1);
	// // 加号的动画
	// composerButtonsShowHideButtonIconLeft
	// .startAnimation(MyAnimations
	// .getRotateAnimation(-225, 0, 300));
	// // hideFourButton(composerButtonsWrapperLeft);
	// }
	// areButtonsShowing1 = !areButtonsShowing1;
	// }
	// });
	//
	// // 给小图标设置点击事件
	// for (int i = 0; i < composerButtonsWrapperLeft.getChildCount(); i++) {
	// final ImageView smallIcon = (ImageView) composerButtonsWrapperLeft
	// .getChildAt(i);
	// final int position = i;
	// smallIcon.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// // 这里写各个item的点击事件
	// // 1.加号按钮缩小后消失 缩小的animation
	// // 2.其他按钮缩小后消失 缩小的animation
	// // 3.被点击按钮放大后消失 透明度渐变 放大渐变的animation
	// if (position == 0) {
	// showSearchDialog();
	//
	// } else if (position == 1) {
	// /**
	// *
	// */
	// Intent intent = new Intent(myMap.this,
	// NotesListActivity.class);
	// startActivity(intent);
	// } else if (position == 2) {
	// /**
	// * 截取当前地图
	// */
	// Toast.makeText(myMap.this, "正在保存中...",
	// Toast.LENGTH_SHORT).show();
	// mapview.getCurrentMap();
	// } else if (position == 3) {
	// /**
	// * 更新当前地图
	// */
	// Toast.makeText(myMap.this, "刷新地图界面...",
	// Toast.LENGTH_LONG).show();
	// groupButtonInvisible();
	// clearOverlay(mapview);
	// mapview.getOverlays().clear();
	// setMyLocationOverlay();
	// mapview.refresh();
	// } else if (position == 4) {
	// /**
	// * POI查询
	// */
	// final ScrollView poiSearchForm = (ScrollView) getLayoutInflater()
	// .inflate(R.layout.h_poisearch_dialog, null);
	//
	// EditText searchcity = (EditText) poiSearchForm
	// .findViewById(R.id.h_poicityname);
	// searchcity.setText(myCity);
	// new AlertDialog.Builder(myMap.this)
	// .setIcon(R.drawable.s_cxlogo)
	// .setTitle("poi查询")
	// .setView(poiSearchForm)
	// .setPositiveButton(
	// "查询",
	// new android.content.DialogInterface.OnClickListener() {
	// /**
	// * 请求信息，并更新到后台
	// */
	// @Override
	// public void onClick(
	// DialogInterface dialog,
	// int which) {
	// // TODO Auto-generated method
	// // stub
	// int typesearch = 0;
	//
	// SearchButtonProcess(typesearch);
	// }
	//
	// private void SearchButtonProcess(
	// int typesearch) {
	// // TODO Auto-generated method
	// // stub
	// route = null;
	// routeOverlay = null;
	// transitOverlay = null;
	// // 在MKSearch里面设置pre、nextbutton的可视
	// EditText cityInfo = (EditText) poiSearchForm
	// .findViewById(R.id.h_poicityname);
	// EditText poiInfo = (EditText) poiSearchForm
	// .findViewById(R.id.h_poiname);
	//
	// if (cityInfo == null) {
	// mapsearch
	// .poiSearchInCity(
	// myCity,
	// poiInfo.getText()
	// .toString());
	// }
	// mapsearch.poiSearchInCity(
	// cityInfo.getText()
	// .toString(),
	// poiInfo.getText()
	// .toString());
	//
	// }
	//
	// })
	// .setNegativeButton(
	// "取消",
	// new android.content.DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(
	// DialogInterface dialog,
	// int which) {
	// // TODO Auto-generated method
	// // stub
	// }
	//
	// }
	//
	// ).create().show();
	// } else if (position == 5) {
	// /**
	// * 离线地图管理
	// */
	// Intent intent = new Intent(myMap.this,
	// OfflineDemo.class);
	// startActivity(intent);
	// }
	//
	// composerButtonsShowHideButtonIconLeft
	// .startAnimation(MyAnimations.getRotateAnimation(
	// -225, 0, 300));
	// areButtonsShowing1 = !areButtonsShowing1;
	// smallIcon.startAnimation(MyAnimations.getMaxAnimation(400));
	// for (int j = 0; j < composerButtonsWrapperLeft
	// .getChildCount(); j++) {
	// if (j != position) {
	// final ImageView smallIcon = (ImageView) composerButtonsWrapperLeft
	// .getChildAt(j);
	// smallIcon.startAnimation(MyAnimations
	// .getMiniAnimation(300));
	//
	// }
	// }
	// hideFourButton(composerButtonsWrapperLeft);
	// }
	// });
	// }
	// }
	//
	// private void hideFourButton(ViewGroup viewgroup) {
	// for (int i = 0; i < viewgroup.getChildCount(); i++) {
	// ImageButton inoutimagebutton = (ImageButton) viewgroup
	// .getChildAt(i);
	// inoutimagebutton.setVisibility(ImageView.INVISIBLE);
	//
	// inoutimagebutton.setClickable(false);
	// inoutimagebutton.setFocusable(false);
	// }
	// }

	// private void initMyselfImage() {
	// // TODO Auto-generated method stub
	// ImageManager2.from(myMap.this).displayImage(myselfImage,
	// SEMapApplication.UserHeadPortraitPath, R.drawable.h_aplaca, 80,
	// 80);
	// myselfImage.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// groupButtonInvisible();
	// clearOverlay(mapview);
	// mapview.getOverlays().clear();
	// mapcontroller.animateTo(new GeoPoint(
	// (int) (locationdata.latitude * 1e6),
	// (int) (locationdata.longitude * 1e6)));
	// // isRequest = false;
	// myLocationOverlay.setLocationMode(LocationMode.NORMAL);
	// // 更新定位数据
	// myLocationOverlay.setData(locationdata);
	// // 设置定位点
	// setMyLocationOverlay();
	// mapview.refresh();
	// // TODO Auto-generated method stub
	// int xLayoutSize = whatisthislayout.getWidth();
	// int yLayoutSize = whatisthislayout.getHeight();
	// PieMenu = new RadialMenuWidget(getBaseContext());
	// PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
	//
	// PieMenu.setIconSize(15, 30);
	// PieMenu.setTextSize(13);
	//
	// PieMenu.setCenterCircle(new Close());
	//
	// PieMenu.addMenuEntry(new PersonInfo(myMap.this));
	// PieMenu.addMenuEntry(new Configure(myMap.this));
	// // PieMenu.addMenuEntry(new SearchPOISomething(myMap.this));
	// // PieMenu.addMenuEntry(new ShareLocationInfo(myMap.this));
	//
	// whatisthislayout.addView(PieMenu);
	// }
	//
	// });
	// }

	private void initSEIcon() {
		// TODO Auto-generated method stub
		changeRouteIcon();
	}

	protected void changeRouteIcon() {
		if (routeOverlay == null && transitOverlay == null) {
			return;
		}
		if (useDefaultIcon) {
			if (routeOverlay != null) {
				routeOverlay.setStMarker(null);
				routeOverlay.setEnMarker(null);
			}
			if (transitOverlay != null) {
				transitOverlay.setStMarker(null);
				transitOverlay.setEnMarker(null);
			}
			// Toast.makeText(myMap.this, "将使用系统起终点图标",
			// Toast.LENGTH_SHORT).show();
		} else {
			if (routeOverlay != null) {
				routeOverlay.setStMarker(getResources().getDrawable(
						R.drawable.icon_st));
				routeOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.icon_en));
			}
			if (transitOverlay != null) {
				transitOverlay.setStMarker(getResources().getDrawable(
						R.drawable.icon_st));
				transitOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.icon_en));
			}
			// Toast.makeText(myMap.this, "将使用自定义起终点图标", Toast.LENGTH_SHORT)
			// .show();
		}
		useDefaultIcon = !useDefaultIcon;
		mapview.refresh();
	}

	private void initExpandTabView() {
		// TODO Auto-generated method stub
		viewMiddle = new ViewMiddle(this);
		mViewArray.add(viewMiddle);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("");
		expandTabView.setValue(mTextArray, mViewArray);
		viewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {

			@Override
			public void getValue(String showText, int tagsX, int tagsY) {

				onExpandTabViewRefresh(viewMiddle, showText);
				String[] tag = viewMiddle.getTags(tagsX, tagsY).split(" ");

				// mapsearch.poiSearchNearBy(tag[1], new GeoPoint(
				// (int) (locationdata.latitude * 1000000),
				// (int) (locationdata.longitude * 1000000)), 1000);
				NearbySearchInfo searchinfo = new NearbySearchInfo();
				searchinfo.ak = SEMapApplication.strkey;
				searchinfo.geoTableId = 49436;
				// 不知道经纬度是否是反的,这里的经纬度应该是currentpoint
				searchinfo.location = (String) ((locationdata.longitude + "," + locationdata.latitude));
				searchinfo.radius = 3000;
				searchinfo.sortby = "uploadingtime:-1";
				searchinfo.tags = tag[1];
				searchinfo.q = "";
				searchinfo.pageIndex = 0;
				searchinfo.pageSize = 10;
				// searchinfo.sn = "";

				CloudManager.getInstance().nearbySearch(searchinfo);
				Toast.makeText(myMap.this, viewMiddle.getTags(tagsX, tagsY),
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private void onExpandTabViewRefresh(View view, String showText) {

		expandTabView.onPressBack();
		int position = getExpandTabPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}

	}

	private int getExpandTabPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}

	private void initSearch() {
		// TODO Auto-generated method stub
		mapsearch = new MKSearch();
		mapsearch.init(SEMapApplication.getInstance().mapmanager,
				new MKSearchListener() {

					@Override
					public void onGetPoiDetailSearchResult(int type, int error) {
						if (error != 0) {
							Toast.makeText(myMap.this, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(myMap.this, "成功，查看详情页面",
									Toast.LENGTH_SHORT).show();
						}
					}

					public void onGetAddrResult(MKAddrInfo res, int error) {
						if (error != 0) {
							Toast.makeText(myMap.this, "错误号：" + error,
									Toast.LENGTH_LONG).show();
							// isLocationGetAddressInfo = false;
							isshareaddress = false;
							return;
						}
						currentaddress = res.strAddr;
						SEMapApplication.CurrentAddress = currentaddress;

						// 地图移动到该点
						mapview.getController().animateTo(res.geoPt);
						if (res.type == MKAddrInfo.MK_GEOCODE) {

							// 地理编码：通过地址检索坐标点
							// String strInfo = String.format("纬度：%f 经度：%f",
							// res.geoPt.getLatitudeE6() / 1e6,
							// res.geoPt.getLongitudeE6() / 1e6);
							// Toast.makeText(myMap.this, strInfo,
							// Toast.LENGTH_LONG).show();
						}
						if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
							// 反地理编码：通过坐标点检索详细地址及周边poi
							// String strInfo = res.strAddr;
							// Toast.makeText(myMap.this, strInfo,
							// Toast.LENGTH_LONG).show();

						}
						if (isLongClick == true) {
							// PieMenu.setHeader(currentaddress + "\n" + "经度" +
							// currentpoint.getLatitudeE6()/1E6 + "纬度" +
							// currentpoint.getLongitudeE6()/1E6 , 20);
							if (mark == 4) {
								PieMenu.setHeader("是否确认:" + currentaddress
										+ " 作为活动举办地", 18);
							} else {
								PieMenu.setHeader(currentaddress, 20);
							}

							isLongClick = false;
						}
						if (isshareaddress == true) {
							Intent it = new Intent(Intent.ACTION_SEND);
							it.putExtra(Intent.EXTRA_TEXT, "您的朋友与您分享一个位置: "
									+ res.strAddr + extraShareString);
							it.setType("text/plain");
							startActivity(Intent.createChooser(it, "将您的位置分享到"));
							isshareaddress = false;
						}

						// 执行刷新使生效
						mapview.refresh();
					}

					public void onGetPoiResult(MKPoiResult res, int type,
							int error) {
						// 错误号可参考MKEvent中的定义
						if (error != 0 || res == null) {
							Toast.makeText(myMap.this, "抱歉，未找到结果",
									Toast.LENGTH_LONG).show();
							return;
						}
						// 将地图移动到第一个POI中心点
						if (res.getCurrentNumPois() > 0) {

							// 将poi结果显示到地图上
							storeOverlay poiOverlay = new storeOverlay(
									getResources().getDrawable(
											R.drawable.h_aplaca), mapview,
									myMap.this);
							int currentPagePoiNum = res.getCurrentNumPois();
							poiOverlay.setItemList(res.getAllPoi());

							int pageNum = res.getNumPages();
							for (int i = 0; i < currentPagePoiNum; i++) {
								MKPoiInfo poi = res.getPoi(i);
								OverlayItem item = new OverlayItem(poi.pt, "第"
										+ i + "个点", "");
								switch (i) {
								case 0:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_marka));
									break;
								case 1:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markb));
									break;
								case 2:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markc));
									break;
								case 3:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markd));
									break;
								case 4:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_marke));
									break;
								case 5:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markf));
									break;
								case 6:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markg));
									break;
								case 7:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markh));
									break;
								case 8:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_marki));
									break;
								case 9:
									item.setMarker(getResources().getDrawable(
											R.drawable.icon_markj));
									break;
								default:
									item.setMarker(getResources().getDrawable(
											R.drawable.h_aplaca));

								}
								poiOverlay.addItem(item);
							}
							clearOverlay(mapview);
							mapview.getOverlays().clear();
							setMyLocationOverlay();
							mapview.getOverlays().add(poiOverlay);
							mapview.refresh();
							// 当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
							for (MKPoiInfo info : res.getAllPoi()) {
								if (info.pt != null) {
									mapview.getController().animateTo(info.pt);
									break;
								}
							}
						} else if (res.getCityListNum() > 0) {
							// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
							clearOverlay(mapview);
							String strInfo = "在";
							for (int i = 0; i < res.getCityListNum(); i++) {
								strInfo += res.getCityListInfo(i).city;
								strInfo += ",";
							}
							strInfo += "找到结果";
							// Toast.makeText(myMap.this, strInfo,
							// Toast.LENGTH_LONG).show();
						}
					}

					public void onGetDrivingRouteResult(
							MKDrivingRouteResult res, int error) {
						// 起点或终点有歧义，需要选择具体的城市列表或地址列表

						drivingrouteresult = res;
						if (error == MKEvent.ERROR_ROUTE_ADDR) {
							Toast.makeText(myMap.this, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
							// 遍历所有地址
							// ArrayList<MKPoiInfo> stPois =
							// res.getAddrResult().mStartPoiList;
							// ArrayList<MKPoiInfo> enPois =
							// res.getAddrResult().mEndPoiList;
							// ArrayList<MKCityListInfo> stCities =
							// res.getAddrResult().mStartCityList;
							// ArrayList<MKCityListInfo> enCities =
							// res.getAddrResult().mEndCityList;
							return;
						}
						// 错误号可参考MKEvent中的定义
						if (error != 0 || res == null) {
							Toast.makeText(myMap.this, "抱歉，未找到结果-",
									Toast.LENGTH_SHORT).show();
							return;
						}
						// Toast.makeText(myMap.this, "结果", Toast.LENGTH_SHORT)
						// .show();

						clearOverlay(mapview);
						searchType = 0;
						routeOverlay = new RouteOverlay(myMap.this, mapview);
						// 此处仅展示一个方案作为示例
						// if (res.getPlan(0).getRoute(0) == null)
						// return;
						if (res.getPlan(0).getRoute(0) == null) {
							Toast.makeText(myMap.this, "抱歉，未找到结果-",
									Toast.LENGTH_SHORT).show();
							return;
						}
						routeOverlay.setData(res.getPlan(0).getRoute(0));
						wayPlanFence = 0;
						// 清除其他图层
						mapview.getOverlays().clear();
						// setMyLocationOverlay();
						// 添加路线图层
						mapview.getOverlays().add(routeOverlay);
						// 执行刷新使生效
						mapview.refresh();
						// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
						mapview.getController().zoomToSpan(
								routeOverlay.getLatSpanE6(),
								routeOverlay.getLonSpanE6());
						// 移动地图到起点
						mapview.getController().animateTo(res.getStart().pt);
						// 将路线数据保存给全局变量
						route = res.getPlan(0).getRoute(0);
						// 重置路线节点索引，节点浏览时使用
						nodeIndex = -1;

						groupButtonVisible();

					}

					public void onGetTransitRouteResult(
							MKTransitRouteResult res, int error) {
						// 起点或终点有歧义，需要选择具体的城市列表或地址列表
						// Toast.makeText(myMap.this, "return 公交",
						// Toast.LENGTH_LONG).show();
						transitrouteresult = res;
						if (error == MKEvent.ERROR_ROUTE_ADDR) {
							Toast.makeText(myMap.this, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
							// 遍历所有地址
							// ArrayList<MKPoiInfo> stPois =
							// res.getAddrResult().mStartPoiList;
							// ArrayList<MKPoiInfo> enPois =
							// res.getAddrResult().mEndPoiList;
							// ArrayList<MKCityListInfo> stCities =
							// res.getAddrResult().mStartCityList;
							// ArrayList<MKCityListInfo> enCities =
							// res.getAddrResult().mEndCityList;
							return;
						}
						if (error != 0 || res == null) {
							Toast.makeText(myMap.this,
									"抱歉，未找到结果-错误号 ： " + error,
									Toast.LENGTH_SHORT).show();
							return;
						}

						clearOverlay(mapview);

						searchType = 1;
						transitOverlay = new TransitOverlay(myMap.this, mapview);
						// 此处仅展示一个方案作为示例
						if (res.getPlan(0) == null) {
							Toast.makeText(myMap.this, "抱歉，未找到结果--",
									Toast.LENGTH_SHORT).show();
							return;
						}
						transitOverlay.setData(res.getPlan(0));
						wayPlanFence = 0;
						// 清除其他图层
						mapview.getOverlays().clear();
						// setMyLocationOverlay();
						// 添加路线图层
						mapview.getOverlays().add(transitOverlay);
						// 执行刷新使生效
						mapview.refresh();
						// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
						mapview.getController().zoomToSpan(
								transitOverlay.getLatSpanE6(),
								transitOverlay.getLonSpanE6());
						// 移动地图到起点
						mapview.getController().animateTo(res.getStart().pt);
						// 将路线数据保存给全局变量
						route = res.getPlan(0).getRoute(0);
						// 重置路线节点索引，节点浏览时使用
						nodeIndex = 0;

						groupButtonVisible();

					}

					public void onGetWalkingRouteResult(
							MKWalkingRouteResult res, int error) {
						// 起点或终点有歧义，需要选择具体的城市列表或地址列表
						walkingrouteresult = res;
						if (error == MKEvent.ERROR_ROUTE_ADDR) {
							Toast.makeText(myMap.this, "抱歉，未找到结果",
									Toast.LENGTH_SHORT).show();
							// 遍历所有地址
							// ArrayList<MKPoiInfo> stPois =
							// res.getAddrResult().mStartPoiList;
							// ArrayList<MKPoiInfo> enPois =
							// res.getAddrResult().mEndPoiList;
							// ArrayList<MKCityListInfo> stCities =
							// res.getAddrResult().mStartCityList;
							// ArrayList<MKCityListInfo> enCities =
							// res.getAddrResult().mEndCityList;
							return;
						}
						if (error != 0 || res == null) {
							Toast.makeText(myMap.this, "抱歉，未找到结果-",
									Toast.LENGTH_SHORT).show();
							return;
						}

						clearOverlay(mapview);

						searchType = 2;
						routeOverlay = new RouteOverlay(myMap.this, mapview);
						// 此处仅展示一个方案作为示例
						// if (res.getPlan(0).getRoute(0) == null)
						// return;
						if (res.getPlan(0).getRoute(0) == null) {
							Toast.makeText(myMap.this, "抱歉，未找到结果--",
									Toast.LENGTH_SHORT).show();
							return;
						}
						routeOverlay.setData(res.getPlan(0).getRoute(0));
						wayPlanFence = 0;
						// 清除其他图层
						mapview.getOverlays().clear();
						// setMyLocationOverlay();
						// 添加路线图层
						mapview.getOverlays().add(routeOverlay);
						// 执行刷新使生效
						mapview.refresh();
						// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
						mapview.getController().zoomToSpan(
								routeOverlay.getLatSpanE6(),
								routeOverlay.getLonSpanE6());
						// 移动地图到起点
						mapview.getController().animateTo(res.getStart().pt);
						// 将路线数据保存给全局变量
						route = res.getPlan(0).getRoute(0);
						// 重置路线节点索引，节点浏览时使用
						nodeIndex = -1;
						groupButtonVisible();

					}

					public void onGetBusDetailResult(MKBusLineResult result,
							int iError) {
					}

					@Override
					public void onGetSuggestionResult(MKSuggestionResult res,
							int arg1) {

					}

					/**
					 * 在这之后会死屏,在收到短信,等其他事件发生后都会死屏
					 */
					@Override
					public void onGetShareUrlResult(MKShareUrlResult result,
							int type, int error) {
						// 分享短串结果
						resultURL = result.url;

						Intent it = new Intent(Intent.ACTION_SEND);
						it.putExtra(Intent.EXTRA_TEXT, "您的朋友与您分享一个位置: "
								+ currentaddress + " -- " + resultURL);
						it.setType("text/plain");
						startActivity(Intent.createChooser(it, "将您的位置分享到"));

					}

				});
	}

	private void groupButtonVisible() {
		showwaydetail.setVisibility(View.VISIBLE);
	}

	private void groupButtonInvisible() {
		showwaydetail.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设计算法使得每次地图显示的POI更加简洁*****************************************************
	 * 在网络回调时添加这些自定义点
	 */

	private void initOverlay() {
		// // TODO Auto-generated method stub
		// /**
		// * 创建自定义overlay
		// */
		// myoverlay = new MyOverlay(getResources().getDrawable(
		// R.drawable.icon_marka), mapview);

		PopupClickListener popsListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				// TODO Auto-generated method stub
				// if (index == 0) {
				// // pop.hidePop();
				// // GeoPoint point = new GeoPoint(currentItem.getPoint()
				// // .getLatitudeE6() + 5000, currentItem.getPoint()
				// // .getLongitudeE6() + 5000);
				// // currentItem.setGeoPoint(point);
				// // myoverlay.updateItem(currentItem);
				//
				// mapview.refresh();
				// } else if (index == 2) {
				// // currentItem.setMarker(getResources().getDrawable(
				// // R.drawable.nav_turn_via_1));
				// // myoverlay.updateItem(currentItem);
				// mapview.refresh();
				// }

			}
		};
		pop = new PopupOverlay(mapview, popsListener);
		MyLocationMapView.pop = pop;
	}

	private void initMapView() {
		// TODO Auto-generated method stub
		SEMapApplication app = (SEMapApplication) this.getApplication();
		// 访问私有成员，调用本体获取权限
		mapview.regMapViewListener(app.mapmanager, maplistener);

		mapview.setBuiltInZoomControls(SEMapApplication.BuiltInZoomControls);

		mapview.setSatellite(SEMapApplication.SatelliteLayer);

		mapview.setTraffic(SEMapApplication.TrafficLayer);

		mapview.setDoubleClickZooming(SEMapApplication.DoubleClickEnable);

		mapview.setBuiltInZoomControls(SEMapApplication.BuiltInZoomControls);

		// 设置定位点
		// setMyLocationOverlay();

		// 修改定位数据后刷新图层生肖
		// mapview.refresh();
	}

	private void setMyLocationOverlay() {
		// TODO Auto-generated method stub
		Log.e("setMyLocationOverlay", "setMyLocationOverlay");

		if (myLocationOverlay == null) {
			myLocationOverlay = new locationOverlay(mapview);
			myLocationOverlay.setLocationMode(LocationMode.NORMAL);
			// 设置定位点

		}

		if (mapview.getOverlays().contains(myLocationOverlay)) {
			mapview.getOverlays().remove(myLocationOverlay);
		}

		// 设置定位数据
		myLocationOverlay.setData(locationdata);
		// 添加定位图层
		if (mapview.getOverlays().contains(myLocationOverlay)) {
			mapview.getOverlays().remove(myLocationOverlay);
			mapview.getOverlays().add(myLocationOverlay);
		} else {
			mapview.getOverlays().add(myLocationOverlay);
		}
		myLocationOverlay.enableCompass();
		modifyLocationOverlayIcon(getResources().getDrawable(
				R.drawable.h_aplaca));
		mapview.refresh();
	}

	private void initMapListener() {
		// 设置地图点击监听器-单击、双击、长按
		/**
		 * 在地图上非POI点的点击事件 单击 显示位置信息与坐标
		 * 
		 * 双击
		 * 
		 * 长按 从定位位置出发到所在点的路径搜索 组件活动 组件队伍 搜索附近的POI点（通过百度地图的检索）
		 * 搜索附近热门活动的POI点（LBS云检索）-引导界面 搜索附近热门消息的POI点（LBS云检索）-引导界面 短串分享功能
		 */
		// TODO Auto-generated method stub
		maptouchlistener = new MKMapTouchListener() {

			@Override
			public void onMapClick(GeoPoint point) {
				// TODO Auto-generated method stub
				touchtype = "单击";
				currentpoint = point;

				// InputMethodManager imm = (InputMethodManager)
				// getSystemService(Context.INPUT_METHOD_SERVICE);
				// if (imm.isActive()) {
				// // 隐藏软键盘
				// imm.hideSoftInputFromWindow(
				// h_map_searchBtn.getWindowToken(), 0);
				// return;
				// }

				mapcontroller.animateTo(point);

				if (pri.h.semap.myMap.pop != null) {
					pri.h.semap.myMap.pop.hidePop();
					mapview.removeView(pri.h.semap.myMap.contentbutton);

				}

				if (mark == 4) {
					addChoiceActivityBuildPlace(null);
				} else {
					addPublicPieMenu(null);
				}

				updateMapState();
				clearOverlay(mapview);
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				// TODO Auto-generated method stub
				touchtype = "双击";
				currentpoint = point;
				updateMapState();
				clearOverlay(mapview);
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				clearOverlay(mapview);
				groupButtonInvisible();
				/**
				 * bug 在每次监听到之前都要清空上一次的按钮！！！！！！！！！！ (已解决)
				 */
				// TODO Auto-generated method stub
				touchtype = "长按";
				currentpoint = point;

				mapcontroller.animateTo(point);

				if (mark == 4) {
					addChoiceActivityBuildPlace(null);
				} else {
					addPublicPieMenu(null);
				}

				// Point point123 = projection.toPixels(point, null);
				// int xScreenSize =
				// (getResources().getDisplayMetrics().widthPixels);
				// int yScreenSize =
				// (getResources().getDisplayMetrics().heightPixels);
				// int xLayoutSize = whatisthislayout.getWidth();
				// int yLayoutSize = whatisthislayout.getHeight();

				// int ScreenLatitudeSpan = mapview.getLatitudeSpan();
				// int ScreenLongitudeSpan = mapview.getLongitudeSpan();
				// GeoPoint MapCenterGeoPoint = mapview.getMapCenter();
				// GeoPoint LeftScreenGeoPoint = new
				// GeoPoint((MapCenterGeoPoint.getLatitudeE6()-ScreenLatitudeSpan/2)
				// ,
				// (MapCenterGeoPoint.getLongitudeE6()-ScreenLongitudeSpan/2));
				//
				// int eventY =
				// ((point.getLatitudeE6()-LeftScreenGeoPoint.getLatitudeE6())*xScreenSize)/ScreenLatitudeSpan;
				// int eventX =
				// ((point.getLongitudeE6()-LeftScreenGeoPoint.getLongitudeE6())*yScreenSize)/ScreenLongitudeSpan;
				// eventY = 800 - eventY;
				// Log.e("event!!!!!!", "eventX: " + eventX + "eventY: " +
				// eventY);
				//
				// int xCenter = xScreenSize/2;
				// int xSource = eventX;
				// int yCenter = yScreenSize/2;
				// int ySource = eventY;
				// if (xScreenSize != xLayoutSize) {
				// xCenter = xLayoutSize/2;
				// xSource = eventX-(xScreenSize-xLayoutSize);
				// }
				// if (yScreenSize != yLayoutSize) {
				// yCenter = yLayoutSize/2;
				// ySource = eventY-(yScreenSize-yLayoutSize);
				// }

				// PieMenu = new RadialMenuWidget(getBaseContext());

				// PieMenu.setAnimationSpeed(0L);

				// int xLayoutSize = whatisthislayout.getWidth();
				// int yLayoutSize = whatisthislayout.getHeight();
				// PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
				// PieMenu.setShowSourceLocation(true);
				// PieMenu.setCenterLocation(xCenter, yCenter);

				// PieMenu.setHeader(
				// "纬度:" + (double) currentpoint.getLatitudeE6() / 1000000
				// + "精度:"
				// + (double) currentpoint.getLongitudeE6()
				// / 1000000, 20);
				// isLongClick = true;
				// pri.h.semap.myMap.mapsearch
				// .reverseGeocode(pri.h.semap.myMap.currentpoint);

				// PieMenu.setIconSize(15, 30);
				// PieMenu.setTextSize(13);

				// PieMenu.setCenterCircle(new Close());
				// PieMenu.addMenuEntry(new placeRadialMenuEntryRouteSearch(
				// myMap.this));
				// PieMenu.addMenuEntry(new SearchPOISomething(myMap.this));
				// PieMenu.addMenuEntry(new ShareLocationInfo(myMap.this));
				// PieMenu.addMenuEntry(new SearchFreeInformation(myMap.this));
				//
				// whatisthislayout.addView(PieMenu);

				updateMapState();
			}
		};
		mapview.regMapTouchListner(maptouchlistener);

		// 设置点击地图poi的事件监听器、截屏的事件监听器、动画完成事件监听器、地图加载完成监听器、地图移动完成事件监听器
		maplistener = new MKMapViewListener() {
			// 回调函数

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// 地图点击poi的事件回调
				String title = "";
				extraShareString = mapPoiInfo.strText;

				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					// Toast.makeText(myMap.this, title, Toast.LENGTH_SHORT)
					// .show();
					currentpoint = mapPoiInfo.geoPt;
					mapsearch.reverseGeocode(currentpoint);
					mapcontroller.animateTo(mapPoiInfo.geoPt);

					if (mark == 4) {
						addChoiceActivityBuildPlace("是否确认:"
								+ mapPoiInfo.strText + " 作为活动场地?");
					} else {
						addPublicPieMenu(mapPoiInfo.strText);
					}
				}
			}

			// 可保存当前截图

			@Override
			public void onGetCurrentMap(Bitmap b) {
				// TODO Auto-generated method stub
				File fileMapScreenShot = new File(
						MainActivity.UserFolderImgMapScreenShot);
				if (!fileMapScreenShot.exists()) {
					fileMapScreenShot.mkdirs();
				}

				File file = new File(fileMapScreenShot + "/"
						+ FormatTime.getFormatTime() + ".png");
				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					if (b.compress(Bitmap.CompressFormat.PNG, 70, out)) {
						out.flush();
						out.close();
					}
					Toast.makeText(myMap.this, "截图保存在: " + file.toString(),
							Toast.LENGTH_LONG).show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				updateMapState();
			}

			@Override
			public void onMapLoadFinish() {
				// TODO Auto-generated method stub
				// Toast.makeText(myMap.this, "地图加载对象完成", Toast.LENGTH_SHORT)
				// .show();
				if (mark == 2) {
					CloudPoiInfo cpi = SEMapApplication.getInstance().transferObj
							.getRoutePlanPoiInfo();
					currentpoint = new GeoPoint((int) (cpi.latitude * 1E6),
							(int) (cpi.longitude * 1E6));
					mapview.getController().animateTo(currentpoint);
					// addPublicPieMenu(null);
					addPublicPieMenuForRoutePlan(null);
					mapview.refresh();
				} else if (mark == 3) {
					List<CloudPoiInfo> cpiList = SEMapApplication.getInstance().transferObj
							.getCloudPoiInfoList();
					currentpoint = new GeoPoint(
							(int) (cpiList.get(0).latitude * 1E6),
							(int) (cpiList.get(0).longitude * 1E6));
					mapview.getController().animateTo(currentpoint);
					switch (cpiList.get(0).geotableId) {
					case baiduCloudStoreTableNumber.activityPOI:
						activityPOI = new activityOverlay(getResources()
								.getDrawable(R.drawable.h_real_aplaca),
								mapview, myMap.this);
						activityPOI.setListOfCloudPoiInfo(cpiList);
						for (int fence = 0; fence < cpiList.size(); fence++) {
							OverlayItem overlayitem = new OverlayItem(
									new GeoPoint(
											(int) (cpiList.get(fence).latitude * 1E6),
											(int) (cpiList.get(fence).longitude * 1E6)),
									"第" + fence + "个点", "");
							activityPOI.addItem(overlayitem);
						}

						clearOverlay(mapview);
						mapview.getOverlays().clear();
						setMyLocationOverlay();
						mapview.getOverlays().add(activityPOI);
						break;
					case baiduCloudStoreTableNumber.messagePOI:

						messagePOI = new messageOverlay(getResources()
								.getDrawable(R.drawable.h_real_aplaca),
								mapview, myMap.this, cpiList.size());
						messagePOI.setListOfCloudPoiInfo(cpiList);
						for (int fence = 0; fence < cpiList.size(); fence++) {
							OverlayItem overlayitem = new OverlayItem(
									new GeoPoint(
											(int) (cpiList.get(fence).latitude * 1E6),
											(int) (cpiList.get(fence).longitude * 1E6)),
									"第" + fence + "个点", "");
							messagePOI.addItem(overlayitem);
						}

						clearOverlay(mapview);
						mapview.getOverlays().clear();
						setMyLocationOverlay();
						mapview.getOverlays().add(messagePOI);
						break;
					}
					mapview.refresh();
				} else if (mark == 4) {
					// 在单击事件中进行设置
				}

			}

			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
				updateMapState();
			}

		};
	}

	// private void initLocationInfo() {
	// // TODO Auto-generated method stub
	// // 定位初始化
	// locationclient = new LocationClient(this);
	// locationdata = new LocationData();
	// locationclient.registerLocationListener(locationlistener);
	// LocationClientOption option = new LocationClientOption();
	// option.setOpenGps(true);
	// option.setCoorType("bd09ll");// 设置坐标类型
	// option.setScanSpan(1000);
	// option.setPoiExtraInfo(true);
	// option.disableCache(false);
	// option.setAddrType("all");
	// locationclient.setLocOption(option);
	// locationclient.start();
	// }
	// private void initLocationInfo() {
	// this.locationdata = SEMapApplication.locationdata;
	// }

	protected void addPublicPieMenuForRoutePlan(String head) {
		// TODO Auto-generated method stub
		int xLayoutSize = whatisthislayout.getWidth();
		int yLayoutSize = whatisthislayout.getHeight();
		PieMenu = new RadialMenuWidget(context);
		PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
		if (head == null) {
			isLongClick = true;
			mapsearch.reverseGeocode(currentpoint);
		} else {
			PieMenu.setHeader(head, 20);
		}

		PieMenu.setIconSize(15, 30);
		PieMenu.setTextSize(13);

		PieMenu.setCenterCircle(new Close());
		PieMenu.addMenuEntry(new walkThat(context));
		PieMenu.addMenuEntry(new driveThat(context));
		PieMenu.addMenuEntry(new busThat(context));
		// PieMenu.addMenuEntry(new SearchFreeInformation(myMap.this));

		whatisthislayout.addView(PieMenu);

	}

	private static void addChoiceActivityBuildPlace(String head) {
		// TODO Auto-generated method stub
		int xLayoutSize = whatisthislayout.getWidth();
		int yLayoutSize = whatisthislayout.getHeight();
		PieMenu = new RadialMenuWidget(context);
		PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
		if (head == null) {
			isLongClick = true;
			pri.h.semap.myMap.mapsearch.reverseGeocode(currentpoint);
		} else {
			PieMenu.setHeader(head, 18);
		}

		PieMenu.setIconSize(15, 30);
		PieMenu.setTextSize(13);

		PieMenu.setCenterCircle(new Close());
		PieMenu.addMenuEntry(new choiceRight(context));
		PieMenu.addMenuEntry(new choiceCancel(context));
		// PieMenu.addMenuEntry(new SearchFreeInformation(myMap.this));

		whatisthislayout.addView(PieMenu);
	}

	public static void addPublicPieMenu(String head) {
		// TODO Auto-generated method stub
		int xLayoutSize = whatisthislayout.getWidth();
		int yLayoutSize = whatisthislayout.getHeight();
		PieMenu = new RadialMenuWidget(context);
		PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
		if (head == null) {
			isLongClick = true;
			mapsearch.reverseGeocode(currentpoint);
		} else {
			PieMenu.setHeader(head, 20);
		}

		PieMenu.setIconSize(15, 30);
		PieMenu.setTextSize(13);

		PieMenu.setCenterCircle(new Close());
		PieMenu.addMenuEntry(new placeRadialMenuEntryRouteSearch(context));
		PieMenu.addMenuEntry(new SearchPOISomething(context));
		PieMenu.addMenuEntry(new ShareLocationInfo(context));
		PieMenu.addMenuEntry(new BuildSomething(context));
		// PieMenu.addMenuEntry(new SearchFreeInformation(myMap.this));

		whatisthislayout.addView(PieMenu);

	}

	private void initMapController() {
		// TODO Auto-generated method stub
		mapcontroller = mapview.getController();
		mapcontroller.enableClick(SEMapApplication.enableclick);
		mapcontroller.setZoom(SEMapApplication.initmapzoom);
		mapcontroller
				.setZoomGesturesEnabled(SEMapApplication.ZoomGesturesEnabled);
		mapcontroller
				.setScrollGesturesEnabled(SEMapApplication.ScrollGesturesEnabled);
		mapcontroller.setRotationGesturesEnabled(SEMapApplication.RotateEnable);
		mapcontroller
				.setOverlookingGesturesEnabled(SEMapApplication.OverlookEnable);
		if (SEMapApplication.CompassMarginEnable) {
			// 左上角
			mapcontroller.setCompassMargin(100, 100);
			// 右上角
			// mapcontroller.setCompassMargin(mapview.getWidth()-100, 100);

		}
	}

	private void initListener() {
		// 搜索框查询按钮
		h_map_searchBtnlistener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!h_map_search.getText().toString().trim().equals("")) {
					// 隐藏软键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							h_map_searchBtn.getWindowToken(), 0);

					mapsearch.poiSearchInCity(myCity, h_map_search.getText()
							.toString());
				} else {
					Toast.makeText(myMap.this, "请输入", Toast.LENGTH_LONG).show();
				}

			}

		};
		h_map_searchBtn.setOnClickListener(h_map_searchBtnlistener);

		// 设置罗盘按钮监听器
		buttonclicklistener = new OnClickListener() {
			public void onClick(View v) {

				switch (currenttype) {
				case LOC:
					requestLocationClick();
					myLocationOverlay.setLocationMode(LocationMode.NORMAL);
					currenttype = E_BUTTON_TYPE.COMPASS;
					mapview.refresh();
					break;
				case COMPASS:
					myLocationOverlay.setLocationMode(LocationMode.COMPASS);
					currenttype = E_BUTTON_TYPE.LOC;
					mapview.refresh();
					break;
				default:
					break;
				}

			}
		};
		requestLocationButton.setOnClickListener(buttonclicklistener);

		OnClickListener showwaydetailbuttonlistener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(myMap.this, showWayPlanDetail.class);
				intent.putExtra("WayPlanType", searchType);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		};
		showwaydetail.setOnClickListener(showwaydetailbuttonlistener);

		// Rotate动画 - 画面旋转
		// final Animation rotateAnimation = null;
		OnClickListener mapRefreshlistener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				groupButtonInvisible();
				clearOverlay(mapview);
				mapview.getOverlays().clear();
				setMyLocationOverlay();
				mapview.getController().animateTo(
						new GeoPoint((int) (locationdata.latitude * 1e6),
								(int) (locationdata.longitude * 1e6)));
				mapview.refresh();

				// 创建一个AnimationSet对象（AnimationSet是存放多个Animations的集合）
				AnimationSet animationSet = new AnimationSet(true);
				// 创建一个RotateAnimation对象（以某个点为圆心旋转360度）
				// RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				// Animation.RELATIVE_TO_PARENT, 0.5f,
				// Animation.RELATIVE_TO_PARENT, 0.25f);
				// RotateAnimation rotateAnimation = new RotateAnimation(0,
				// 360);
				RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				// 设置动画执行的时间（单位：毫秒）
				rotateAnimation.setDuration(600);
				// 将RotateAnimation对象添加到AnimationSet当中
				animationSet.addAnimation(rotateAnimation);
				// 使用ImageView的startAnimation方法开始执行动画
				mapRefreshBtn.startAnimation(animationSet);
			}
		};
		mapRefreshBtn.setOnClickListener(mapRefreshlistener);

		OnClickListener directionChangeListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isDirection = !isDirection;
			}

		};
		mapDirection.setOnClickListener(directionChangeListener);
		switchSatelliteLayer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SatelliteLayer = !SatelliteLayer;
				if (SatelliteLayer) {
					// switchSatelliteLayer.setText("卫星图");
					// switchSatelliteLayer.setText("");
				} else {
					// switchSatelliteLayer.setText("普通图");
					// switchSatelliteLayer.setText("");
				}
				mapview.setSatellite(SatelliteLayer);
			}

		});

		// OnClickListener customClickListener = new OnClickListener() {
		// public void onClick(View v) {
		// // 自设路线绘制示例
		// intentToActivity();
		// }
		//
		// private void intentToActivity() {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(myMap.this,
		// CustomRouteOverlayDemo.class);
		// startActivity(intent);
		// }
		// };
		// customRoute.setOnClickListener(customClickListener);
	}

	protected void onPause() {
		mapview.onPause();
		// unbindService(connection);

		super.onPause();
		mStopDrawing = true;
		if (mOrientationSensor != null) {
			mSensorManager.unregisterListener(mOrientationSensorEventListener);
		}

		if (GSensorEventListener != null) {
			mSensorManager.unregisterListener(GSensorEventListener);
		}
	}

	@Override
	protected void onResume() {
		SEMapApplication.currentContext = this;
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
		// Intent intent = new Intent();
		// intent.setAction("com.example.semap.NetService");
		// boolean isSuccess = bindService(intent, connection,
		// Service.BIND_AUTO_CREATE);
		// if (isSuccess != true) {
		// Toast.makeText(myMap.this, "绑定服务未成功", Toast.LENGTH_LONG).show();
		// }
		Log.e("currentContext", SEMapApplication.currentContext.toString());
		mapview.onResume();
		super.onResume();
		if ((mapZoomLevel >= 18)) {
			mapview.getController().setZoom(mapZoomLevel);
			// locationdata
			if (!((locationdata == null) || (locationdata.latitude < 3)
					|| (locationdata.latitude > 55)
					|| (locationdata.longitude < 70) || (locationdata.longitude > 136))) {
				GeoPoint gp = new GeoPoint((int) (locationdata.latitude * 1E6),
						(int) (locationdata.longitude * 1E6));
				mapview.getController().animateTo(gp);
			}
		}

		// 传感器控制
		if (mOrientationSensor != null) {
			// 陀螺仪传感器
			mSensorManager.registerListener(mOrientationSensorEventListener,
					mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);

			// 加速度传感器
			mSensorManager.registerListener(GSensorEventListener,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					// 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
					// 根据不同应用，需要的反应速率不同，具体根据实际情况设定
					SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Toast.makeText(this, R.string.cannot_get_sensor, Toast.LENGTH_SHORT)
					.show();
		}
		mStopDrawing = false;
		mHandler.postDelayed(mCompassViewUpdater, 20);// 20毫秒执行一次更新指南针图片旋转

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (SEMapApplication.locationclient != null)
			SEMapApplication.locationclient.stop();
		// myMap.this.unbindService(connection);
		mapview.destroy();
		super.onDestroy();
	}

	// 保存现在的实例与状态
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	// 读取实例的状态
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mapview.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * android的生命周期并没有完善！
	 */

	// 更新textview状态
	private void updateMapState() {
		this.mapZoomLevel = mapview.getZoomLevel();
		this.mapRotation = mapview.getMapRotation();
		this.mapOverLooking = mapview.getMapOverlooking();
		// if (statebar == null) {
		// return;
		// }
		// String state = "";
		// if (currentpoint == null) {
		// state = "点击、长按、双击地图以获取经纬度和地图状态";
		// } else {
		// state = String.format(touchtype + ",当前经度 ： %f 当前纬度：%f",
		// currentpoint.getLongitudeE6() * 1E-6,
		// currentpoint.getLatitudeE6() * 1E-6);
		// }
		// state += "\n";
		// state += String
		// .format("zoom level= %.1f    rotate angle= %d   overlaylook angle=  %d",
		// mapZoomLevel, this.mapRotation, this.mapOverLooking);
		// statebar.setText(state);
	}

	private static int LocationCount = 0;
	private boolean LongDestence = false;
	private GeoPoint LastLocPoint = null;
	private boolean IsFirstLoc = true;

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			LocationCount++;

			locationdata.latitude = location.getLatitude();
			locationdata.longitude = location.getLongitude();

			if ((location == null) || (locationdata.latitude < 3)
					|| (locationdata.latitude > 55)
					|| (locationdata.longitude < 70)
					|| (locationdata.longitude > 136)) {
				Log.e("onReceiveLocation", "get wrong locationpoint"
						+ locationdata.latitude + " , "
						+ locationdata.longitude);
				mySharedPreferences = getSharedPreferences(MYPREFS, mode);
				// myEditor = mySharedPreferences.edit();
				// commit()为同步方法;apply为异步方法
				// myEditor.commit();
				// myEditor.apply();
				if (mySharedPreferences != null
						&& mySharedPreferences.contains("MyLatitudeE6")
						&& mySharedPreferences.contains("MyLongitudeE6")) {
					int MyLatitudeE6 = mySharedPreferences.getInt(
							"MyLatitudeE6", 28185083);
					int MyLongitudeE6 = mySharedPreferences.getInt(
							"MyLongitudeE6", 112949889);
					GeoPoint gp = new GeoPoint(MyLatitudeE6, MyLongitudeE6);

					if (IsFirstLoc)
						mapview.getController().animateTo(gp);

					locationdata.latitude = ((double) MyLatitudeE6) / 1E6;
					locationdata.longitude = ((double) MyLongitudeE6) / 1E6;
					if (IsFirstLoc || currentpoint == null) {
						currentpoint = gp;
						IsFirstLoc = false;
					}

					setMyLocationOverlay();
				} else {
					mapview.getController().animateTo(
							new GeoPoint(28185083, 112949889));
					locationdata.latitude = ((double) 28185083) / 1E6;
					locationdata.longitude = ((double) 112949889) / 1E6;
					GeoPoint gp = new GeoPoint(28185083, 112949889);

					if (IsFirstLoc || currentpoint == null) {
						currentpoint = gp;
						IsFirstLoc = false;
					}

					setMyLocationOverlay();
				}
				return;
			}

			if ((location.getCity() != null)) {
				myCity = location.getCity();
			}
			if ((location.getProvince() != null)) {
				myProvince = location.getProvince();
			}
			if ((location.getStreet() != null)) {
				myStreet = location.getStreet();
			}
			if ((location.getAddrStr() != null)) {
				myAddress = location.getAddrStr();
			}
			// 在这里替换呵呵!
			if (myAddress == null) {
				myAddress = myProvince + myCity + myStreet;
			}
			if (myAddress == null) {
				myAddress = "";
			}

			// 定位精度圈半径，可设置为0
			locationdata.accuracy = location.getRadius();
			// ???方向
			locationdata.direction = location.getDerect();

			// if(true) {
			// mapcontroller.setRotation((int)locationdata.direction);
			// }
			// // 更新定位数据
			// myLocationOverlay.setData(locationdata);

			// 更新图层数据
			// mapview.refresh();
			// 是手动出发请求或首次定位时，移动到定位点

			GeoPoint mypt = new GeoPoint((int) (locationdata.latitude * 1e6),
					(int) (locationdata.longitude * 1e6));
			if (isFirstLocat) {
				// 定位图层初始化
				myLocationOverlay = new locationOverlay(mapview);
				mapcontroller.animateTo(mypt);
				// isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.NORMAL);
				// 更新定位数据
				// myLocationOverlay.setData(locationdata);
				// mapview.getOverlays().add(myLocationOverlay);
				Log.e("isFirstLocat", "setMyLocationOverlay");
				// 设置定位点
				setMyLocationOverlay();
				mapview.refresh();

				SEMapApplication.CurrentAddress = location.getAddrStr();
				isFirstLocat = false;
			}

			if (LastLocPoint != null) {
				double distance = DistanceUtil.getDistance(mypt, LastLocPoint);
				if (distance > 25) {
					LongDestence = true;
				}
			}

			// 更新定位数据
			if ((LocationCount > 20) || LongDestence) {

				// 保存数据定位
				mySharedPreferences = getSharedPreferences(MYPREFS, mode);
				myEditor = mySharedPreferences.edit();
				myEditor.putInt("MyLatitudeE6",
						(int) (locationdata.latitude * 1000000));
				myEditor.putInt("MyLongitudeE6",
						(int) (locationdata.longitude * 1000000));
				myEditor.apply();

				setMyLocationOverlay();
				LocationCount = 0;
				LongDestence = false;
			}
			SEMapApplication.locationdata = locationdata;
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			// TODO Auto-generated method stub
			if (poiLocation == null) {
				return;
			}
		}

	}

	// 设置定位点击事件
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapview) {
			super(mapview);
			// TODO Auto-generated constructor stub
		}

		protected boolean dispatchTap() {
			// currentpoint.setLatitudeE6((int )(locationdata.latitude * 1E6));
			// currentpoint.setLongitudeE6((int )(locationdata.longitude *
			// 1E6));
			mapcontroller.animateTo(new GeoPoint(
					(int) (locationdata.latitude * 1E6),
					(int) (locationdata.longitude * 1E6)));

			PieMenu = new RadialMenuWidget(getBaseContext());

			int xLayoutSize = whatisthislayout.getWidth();
			int yLayoutSize = whatisthislayout.getHeight();

			// PieMenu.
			PieMenu.setSourceLocation(xLayoutSize, yLayoutSize);

			// isLongClick = true;
			PieMenu.setHeader("我-" + myAddress, 20);
			// pri.h.semap.myMap.mapsearch.reverseGeocode(currentpoint);
			PieMenu.setIconSize(15, 30);
			PieMenu.setTextSize(13);

			PieMenu.setCenterCircle(new Close());
			PieMenu.addMenuEntry(new SearchPOISomething(context));
			PieMenu.addMenuEntry(new ShareLocationInfo(context));
			PieMenu.addMenuEntry(new BuildSomething(context));
			// PieMenu.addMenuEntry(new StartBoardcase(myMap.this));
			// PieMenu.addMenuEntry(new MyLocationNote(myMap.this));

			whatisthislayout.addView(PieMenu);
			return true;
		}

	}

	/**
	 * 手动触发定位请求
	 */
	public void requestLocationClick() {
		// isRequest = true;
		SEMapApplication.locationclient.requestLocation();
	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		myLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效
		mapview.refresh();
	}

	/**
	 * 创建定位个人图层
	 */
	// public void createPaopao() {
	// viewCache = getLayoutInflater().inflate(R.layout.h_custom_text_view,
	// null);
	// mypopupText = (TextView) viewCache.findViewById(R.id.h_textcache);
	// 泡泡点击响应回调
	// pop这个也没有监听到！！！！！！！！！！！！！！！！！！！！！！！！！！！/

	// 这里应该没用
	// PopupClickListener popListener = new PopupClickListener() {
	// @Override
	// public void onClickedPopup(int index) {
	// if( index == 0 )
	// {
	// Toast.makeText(myMap.this, "index = 0", Toast.LENGTH_LONG).show();
	// }else if(index == 1)
	// {
	// Toast.makeText(myMap.this, "index = 1", Toast.LENGTH_LONG).show();
	// } else if(index == 2)
	// {
	// Toast.makeText(myMap.this, "index = 2", Toast.LENGTH_LONG).show();
	// } else {
	// Toast.makeText(myMap.this, "index = x", Toast.LENGTH_LONG).show();
	// }
	//
	// }
	// };
	// mypop = new PopupOverlay(mapview, popListener);
	// MyLocationMapView.mypop = mypop;
	//
	// routeNodePop = new PopupOverlay(mapview, popListener);

	// }

	/**
	 * MyOverlay 是点击相应POI的事件
	 * 
	 * @author 侯斌
	 * 
	 */

	// public class MyOverlay extends ItemizedOverlay {
	//
	// public MyOverlay(Drawable defaultMaker, MapView mapView) {
	// super(defaultMaker, mapView);
	// }
	//
	// // index是存入的顺序索引，即第几个点
	// /**
	// * 在这里添加功能按钮来控制事件
	// */
	// public boolean onTap(int index) {
	// OverlayItem item = getItem(index);
	// currentItem = item;
	// if (index == 0) {
	//
	// } else {
	// /**
	// * 这里行不通，略显鸡肋
	// */
	//
	// }
	// return true;
	// }
	//
	// /**
	// * 在这里清除按钮
	// */
	// public boolean onTap(GeoPoint pt, MapView mMapView) {
	// if (pop != null) {
	// pop.hidePop();
	//
	// }
	// return false;
	// }
	//
	// }

	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		// myoverlay.removeAll();
		if (pop != null) {
			pop.hidePop();
			mapview.removeView(pri.h.semap.myMap.contentbutton);
		}
		// if (mypop != null) {
		// mypop.hidePop();
		// }

		groupButtonInvisible();
		mapview.refresh();
	}

	/**
	 * 重新添加Overlay
	 * 
	 * @param view
	 */

	public void resetOverlay(View view) {
		clearOverlay(null);
		// myoverlay.addItem(itemlist);
		mapview.refresh();
	}

	/**
	 * implements CloudListener
	 */
	@Override
	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
		// TODO Auto-generated method stub
		if (result != null) {

		}
	}

	/**
	 * implements CloudListener 自定义检索！ 回调
	 */
	@Override
	public void onGetSearchResult(CloudSearchResult result, int error) {
		// TODO Auto-generated method stub

		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {

			clearOverlay(mapview);
			mapview.getOverlays().clear();
			setMyLocationOverlay();
			initCloudOverlay(result);
			// mapview.getOverlays().add();
			mapview.refresh();
			// Toast.makeText(myMap.this,
			// "云检索返回值result: " + result.poiList.get(0).extras.toString(),
			// Toast.LENGTH_LONG).show();
			// mapview.getController().zoomToSpan(左上角,右下角);
			mapview.getController().animateTo(
					new GeoPoint((int) (result.poiList.get(0).latitude * 1e6),
							(int) (result.poiList.get(0).longitude * 1e6)));
			SEMapApplication.getInstance().transferObj
					.setCloudPoiInfoList(result.poiList);
		} else {
			if (IndexNow != 0) {

				IndexNow = 0;
			}
			Toast.makeText(myMap.this, "抱歉,未找到相关信息", Toast.LENGTH_LONG).show();
		}

	}

	private void initCloudOverlay(CloudSearchResult result) {
		// TODO Auto-generated method stub
		/**
		 * 创建自定义overlay 相应的回调对应相应的Overlay事件 用switch作为开关
		 * 
		 */

		// myoverlay = new MyOverlay(getResources().getDrawable(
		// R.drawable.icon_marka), mapview);
		/**
		 * 准备overlay数据,这里模拟接收到的数据 坐标是反的！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		 */
		int status = result.status; // 状态码
		int infoSize = result.size; // 分页参数，当前页返回数量
		int totalInfo = result.total; // 分页参数，所有召回数量

		// result.poiList.get(0).extras.get("mobilephone");
		switch (status) {
		case 0: // 正常

			List<CloudPoiInfo> item = result.poiList;

			switch (item.get(0).geotableId) {
			case baiduCloudStoreTableNumber.arroundpersonPOI:
				clearOverlay(mapview);
				setMyLocationOverlay();

				// List<arroundPersonpleOverlay> aplist = new
				// ArrayList<arroundPersonpleOverlay>(10);
				for (int fence = 0; fence < infoSize; fence++) {
					String ID = (String) item.get(fence).extras
							.get("univeralindex");
					int resId = R.drawable.h_real_aplaca;
					String url = "";
					url = "http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/" + ID
							+ ".png";
//					Drawable drawable0 = ImageManagerMap.from(getBaseContext())
//							.displayImage(url, resId);
					// drawable0.setBounds(new Rect(50,50, resId, resId));
					arroundpersonPOI = new arroundPersonpleOverlay(getResources().getDrawable(resId),
							mapview, myMap.this);
					List<CloudPoiInfo> oneitem = new ArrayList<CloudPoiInfo>();
					oneitem.add(item.get(fence));
					arroundpersonPOI.setListOfCloudPoiInfo(oneitem);
					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
							(int) (item.get(fence).latitude * 1E6),
							(int) (item.get(fence).longitude * 1E6)), "第"
							+ fence + "个点", "");
					activityPOI.addItem(overlayitem);
					mapview.getOverlays().add(activityPOI);
				}

				break;

			// for (int fence = 0; fence < infoSize; fence++) {
			// // arroundpersonPOI = new arroundPersonpleOverlay(getResources()
			// // .getDrawable(R.drawable.h_real_aplaca), mapview,
			// // myMap.this);
			// arroundpersonPOI.setListOfCloudPoiInfo(oneItemList);
			// OverlayItem overlayitem = new OverlayItem(new GeoPoint(
			// (int) (item.get(fence).latitude * 1E6),
			// (int) (item.get(fence).longitude * 1E6)), "第"
			// + fence + "个点", "");
			// // String a = (String)
			// // item.get(1).extras.get("acitivityname");
			// arroundpersonPOI.addItem(overlayitem);
			// }
			//
			// clearOverlay(mapview);
			// mapview.getOverlays().clear();
			// setMyLocationOverlay();
			// mapview.getOverlays().add(arroundpersonPOI);
			// break;
			case baiduCloudStoreTableNumber.activityPOI:

				// int resId0 = R.drawable.h_real_aplaca;
				// String url0 = "";
				// url0 = "http://" + addressInfo.localIP + ":"
				// + addressInfo.visitPort + "/"
				// + addressInfo.visitFolderUserHeadXiaotu + "/"
				// + "15273131134.png";
				// Drawable drawable0 =
				// ImageManagerMap.from(getBaseContext()).displayImage(url0,
				// resId0);

				// activityPOI = new activityOverlay(drawable0, mapview,
				// myMap.this);
				activityPOI = new activityOverlay(getResources().getDrawable(
						R.drawable.h_real_aplaca), mapview, myMap.this);

				activityPOI.setListOfCloudPoiInfo(item);
				for (int fence = 0; fence < infoSize; fence++) {
					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
							(int) (item.get(fence).latitude * 1E6),
							(int) (item.get(fence).longitude * 1E6)), "第"
							+ fence + "个点", "");
					activityPOI.addItem(overlayitem);
				}

				clearOverlay(mapview);
				mapview.getOverlays().clear();
				setMyLocationOverlay();
				mapview.getOverlays().add(activityPOI);
				break;
			case baiduCloudStoreTableNumber.messagePOI:
				// clearOverlay(mapview);
				// setMyLocationOverlay();
				// mapview.getOverlays().clear();
				//
				// // List<messageOverlay> p = new
				// ArrayList<messageOverlay>(10);
				// for (int fence = 0; fence < infoSize; fence++) {
				// String ID = String.valueOf(item.get(fence).extras
				// .get("univeralindex"));
				//
				// messagePOI = new messageOverlay(drawable0, mapview,
				// myMap.this);
				// List<CloudPoiInfo> oneitem = new ArrayList<CloudPoiInfo>();
				// oneitem.add(item.get(fence));
				// messagePOI.setListOfCloudPoiInfo(oneitem);
				// OverlayItem overlayitem = new OverlayItem(new GeoPoint(
				// (int) (item.get(fence).latitude * 1E6),
				// (int) (item.get(fence).longitude * 1E6)), "第"
				// + fence + "个点", "");
				// messagePOI.addItem(overlayitem);
				// mapview.getOverlays().add(messagePOI);
				// }
				//
				// break;

				// int resId2 = R.drawable.h_real_aplaca;
				// String url2 = "";
				// url2 = "http://" + addressInfo.localIP + ":"
				// + addressInfo.visitPort + "/"
				// + addressInfo.visitFolderUserHeadXiaotu + "/"
				// + "15273131134.png";
				// Drawable drawable2 =
				// ImageManagerMap.from(getBaseContext()).displayImage(url2,
				// resId2);
				//
				// messagePOI = new messageOverlay(drawable2, mapview,
				// myMap.this);
				messagePOI = new messageOverlay(getResources().getDrawable(
						R.drawable.h_real_aplaca), mapview, myMap.this , infoSize);
				messagePOI.setListOfCloudPoiInfo(item);
				for (int fence = 0; fence < infoSize; fence++) {
					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
							(int) (item.get(fence).latitude * 1E6),
							(int) (item.get(fence).longitude * 1E6)), "第"
							+ fence + "个点", "");
					messagePOI.addItem(overlayitem);
				}

				clearOverlay(mapview);
				mapview.getOverlays().clear();
				setMyLocationOverlay();
				mapview.getOverlays().add(messagePOI);
				break;

			// case baiduCloudStoreTableNumber.storePOI:
			// storePOI = new storeOverlay(getResources().getDrawable(
			// R.drawable.h_aplaca), mapview, myMap.this);
			// storePOI.setListOfCloudPoiInfo(item);
			// for (int fence = 0; fence < infoSize; fence++) {
			// OverlayItem overlayitem = new OverlayItem(new GeoPoint(
			// (int) (item.get(fence).latitude * 1E6),
			// (int) (item.get(fence).longitude * 1E6)), "第"
			// + fence + "个点", "");
			// storePOI.addItem(overlayitem);
			// }
			//
			// clearOverlay(mapview);
			// mapview.getOverlays().clear();
			// setMyLocationOverlay();
			// mapview.getOverlays().add(storePOI);
			// break;
			case baiduCloudStoreTableNumber.teammatePOI:
				teammatePOI = new teammateOverlay(getResources().getDrawable(
						R.drawable.h_real_aplaca), mapview, myMap.this);
				teammatePOI.setListOfCloudPoiInfo(item);
				for (int fence = 0; fence < infoSize; fence++) {
					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
							(int) (item.get(fence).latitude * 1E6),
							(int) (item.get(fence).longitude * 1E6)), "第"
							+ fence + "个点", "");
					teammatePOI.addItem(overlayitem);
				}

				clearOverlay(mapview);
				mapview.getOverlays().clear();
				setMyLocationOverlay();
				mapview.getOverlays().add(teammatePOI);
				break;
			case baiduCloudStoreTableNumber.teamPOI:
				teamPOI = new teamOverlay(getResources().getDrawable(
						R.drawable.h_real_aplaca), mapview, myMap.this);
				teamPOI.setListOfCloudPoiInfo(item);
				for (int fence = 0; fence < infoSize; fence++) {
					OverlayItem overlayitem = new OverlayItem(new GeoPoint(
							(int) (item.get(fence).latitude * 1E6),
							(int) (item.get(fence).longitude * 1E6)), "第"
							+ fence + "个点", "");
					teamPOI.addItem(overlayitem);
				}

				clearOverlay(mapview);
				mapview.getOverlays().clear();
				setMyLocationOverlay();
				mapview.getOverlays().add(teamPOI);
				break;

			}
		}

	}

	/**
	 * 使用ItemizevOvelray展示反地理编码点位置，当该点被点击时发起短串请求.
	 */
	private class PoiShareOverlay extends PoiOverlay {

		public PoiShareOverlay(Activity activity, MapView mapView) {
			super(activity, mapView);
		}

		@Override
		protected boolean onTap(int i) {
			MKPoiInfo info = getPoi(i);
			currentaddress = info.address;
			SEMapApplication.CurrentAddress = currentaddress;
			mapsearch.poiDetailShareURLSearch(info.uid);
			return true;
		}
	}

	public static class Close implements RadialMenuEntry {

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "Close";
		}

		@Override
		public String getLabel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getIcon() {
			// TODO Auto-generated method stub
			return android.R.drawable.ic_menu_close_clear_cancel;
		}

		@Override
		public List<RadialMenuEntry> getChildren() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void menuActiviated() {
			// TODO Auto-generated method stub
			if (PieMenu != null)
				if (PieMenu.getParent() != null)
					((LinearLayout) PieMenu.getParent()).removeView(PieMenu);

		}

	}

	public static void outsideCircleClick() {
		if (PieMenu != null)
			if (PieMenu.getParent() != null)
				((LinearLayout) PieMenu.getParent()).removeView(PieMenu);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in,
					R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}

// class CloudOverlay extends ItemizedOverlay {
// // POI类型
// int poitype;
// List<CloudPoiInfo> mylbspoint;
// Activity mycontext;
//
// public CloudOverlay(Activity context, MapView mapView, int type) {
// // TODO Auto-generated constructor stub
// super(null, mapView);
// mycontext = context;
// }
//
// public void setData(List<CloudPoiInfo> lbsPoint) {
// if (lbsPoint != null) {
// mylbspoint = lbsPoint;
// }
// for (CloudPoiInfo thislbspoint : mylbspoint) {
// GeoPoint point = new GeoPoint((int) (thislbspoint.latitude * 1e6),
// (int) (thislbspoint.longitude * 1e6));
// OverlayItem item = new OverlayItem(point, thislbspoint.title,
// thislbspoint.address);
// Drawable marker = this.mycontext.getResources().getDrawable(
// R.drawable.icon_marka);
// item.setMarker(marker);
// addItem(item);
// }
// }
//
// protected Object clone() throws CloneNotSupportedException {
// return super.clone();
// }
//
// protected boolean onTap(int index) {
//
// CloudPoiInfo item = mylbspoint.get(index);
// return super.onTap(index);
// }
//
// }