package pri.z.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pri.h.semap.myMap;
import pri.z.build.BuildActivity;
import pri.z.build.ShareMoment;
import pri.z.mydb.PushMessage;
import pri.z.mydb.RelationActivity;
import pri.z.mydb.SearchDistance;
import pri.z.selectphoto.FileSelectUtils;
import pri.z.show.AboutActivity;
import pri.z.show.FeedBack;
import pri.z.show.R;
import pri.z.show.Welcome;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.utils.VersionUpdate;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_C;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.map.LocationData;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity implements CloudListener {

	/**
	 * a:经度 b:纬度
	 */
	public static double a = 0, b = 0;
	public static CloudSearchResult poidata = null;
	public static LocationData locationdata = new LocationData();

	public LocationListener locationlistener = new LocationListener();
	// 右方弹出菜单的请求码和结果码
	public static final int TopRightDialogRequest = 400;
	public static final int TopRightDialog_Null = 499;
	public static final int TopRightDialog_OK_01 = 401;
	public static final int TopRightDialog_OK_02 = 402;
	public static final int TopRightDialog_OK_03 = 403;
	public static int selectPtotoNum = 9;// 保存本地选择图片的最大数量，默认是9
	public static int RadiusInMap = 5000;//地图数据搜索范围（米）
	private static final String TAG = "哈哈哈哈";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private ImageView ivBottomLine;
	private TextView tvTabActivity, tvTabMoments, tvTabMore;

	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;

	private SlidingMenu menu;
	Context mContext;
	public static Activity MainActivtyExitUse;

	public static Messenger remoteMessenger = null;
	public static Messenger myMessenger = null;
	SharedPreferences mySharedPreferences;
	Editor myEditor;
	final String MYPREFS = "MyPreferences";
	final int mode = Activity.MODE_PRIVATE;

	public static String UserFolderImgMapScreenShot = "0";
	public static String UserFolderImgDownLoad = "0";
	public static String UserFolderImgUpload = "0";
	public static String UserFolderCache = "0";
	public static String UserFolderAppDownload = "0";
	public static String UserMomentUpload = "0";
	public static Context context;
	static Handler handlerRedPoint;
	//默认是10千米
	public static int SearchActivityDistance = 10000;
	public static int SearchMomentDistance = 10000;
	private static Resources resources;
	public static boolean UpdateVersionFlag = false;
	//Object为本地数据库的ActivityID
	public static Map<String,Object> MapActivityID = new HashMap<String, Object>();
	//Object为本地数据库的MomentMessageID
	public static Map<String,Object> MapMomentMessageID = new HashMap<String, Object>();
	
	public static boolean modifyMomentDistanceFlag = false;
	public static boolean modifyActivityDistanceFlag = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MainActivtyExitUse = this;
		handlerRedPoint = new Handler();
		context = this;
		resources = getResources();
		mContext = getBaseContext();
		setContentView(R.layout.mainactivity);
		ImageButton btnShowMore = (ImageButton) findViewById(R.id.z_mainTitleShowMore);
		btnShowMore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						MainTopRight.class);
				startActivityForResult(intent, TopRightDialogRequest);
			}
		});

		SEMapApplication.initLocationInfo(MainActivity.this, locationdata,
				locationlistener);
		CloudManager.getInstance().init(MainActivity.this);

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		
		QueryUserRequest.queryRelationActivitysDataInHis();
		QueryUserRequest.queryUser();
		QueryUserRequest.qureyAllSearchDistancesInMainActivity();
		QueryUserRequest.qureyAllPushMessages();
		
		resources = getResources();
		InitWidth();
		InitTextView();
		InitViewPager();
		MyAnimations.initOffset(MainActivity.this);

		initSEMapListener();
		// configure the SlidingMenu
		menu = new SlidingMenu(this);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MainLeftMenu()).commit();

		ImageButton btnShowLeft = (ImageButton) findViewById(R.id.z_mainTitleLeft);
		btnShowLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				menu.showMenu();
			}
		});

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
			a = MyLongitudeE6 / (1E6);
			b = MyLatitudeE6 / (1E6);
		} else {
			a = 112949889 / (1E6);
			b = 28185083 / (1E6);
		}


		// 创建用户文件夹
		createUserFolder();
		
		recudeUseTime();
		
		//删除上次用户发表的图片
		FileSelectUtils.deleteDir();
	}

	
	private void initSEMapListener() {  
		// TODO Auto-generated method stub
		ImageButton imgSEMap = (ImageButton)findViewById(R.id.z_mainSEMapIcon);
		imgSEMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(new Intent(getApplicationContext(),
						myMap.class));
				Bundle bundle = new Bundle();
				bundle.putInt("mark", 0);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
	}


	private void recudeUseTime() {
		// TODO Auto-generated method stub
		// 保存Count++
		if (Welcome.mySharedPreferences != null) {
			// 保存数据定位
			Editor myEditor = Welcome.mySharedPreferences.edit();
			myEditor.putInt("LetsPartyVersion",Integer.valueOf(getResources().getString(R.string.Version)));
			Welcome.mySharedPreferences.getInt(
					"LetsPartyVersion", 0);
			// commit()为同步方法;apply为异步方法
			myEditor.commit();
			myEditor.apply();
		}
	}


	/**
	 * 加载用户基本信息保存到静态变量中
	 */
	public static void initSEMapApplication(UserInfoSelectData data) {
		// TODO Auto-generated method stub
		if (data != null){
			SEMapApplication.AccountNumber = data.getUserPhone() + "";
			SEMapApplication.LoginCode = data.getCode();
			SEMapApplication.LoginName = data.getUserName();
		}
		
		//进入应用的时候版本更新消息发送一次
		if(!UpdateVersionFlag){
			sendVersionUpdateMsg();
			UpdateVersionFlag = true;
		}
		
	}
	
	public static void sendVersionUpdateMsg() {
		Message msg = Message.obtain();
		CheckVersionAndOtherInfo_C li = new CheckVersionAndOtherInfo_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setVersion(resources.getString(R.string.Version));
		

		ArrayList<Object> list = new ArrayList<Object>();
		list.add(li);

		msg.replyTo = myMessenger;
		msg.what = li.getP();
		String timeStampString = li.getUploadTime();
		msg.arg1 = Integer.parseInt(timeStampString.substring(0, 7));
		msg.arg2 = Integer.parseInt(timeStampString.substring(7, 15));
		msg.obj = list;

		if(remoteMessenger == null) {
			remoteMessenger = SEMapApplication.serviceMessenger;
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建用户文件夹
	 */
	private void createUserFolder() {
		String status = Environment.getExternalStorageState();
		// 有SD卡
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			UserFolderImgMapScreenShot = Environment
					.getExternalStorageDirectory().toString()
					+ "/Party/image/MapScreenShot/";
			File fileMapScreenShot = new File(UserFolderImgMapScreenShot);
			if (!fileMapScreenShot.exists()) {
				fileMapScreenShot.mkdirs();
			}
			UserFolderImgDownLoad = Environment.getExternalStorageDirectory()
					.toString() + "/Party/image/Download/";
			File fileImgDownLoad = new File(UserFolderImgDownLoad);
			if (!fileImgDownLoad.exists()) {
				fileImgDownLoad.mkdirs();
			}
			UserFolderImgUpload = Environment.getExternalStorageDirectory()
					.toString() + "/Party/image/Upload/";
			File fileImgUpload = new File(UserFolderImgUpload);
			if (!fileImgUpload.exists()) {
				fileImgUpload.mkdirs();
			}
			UserFolderCache = Environment.getExternalStorageDirectory()
					.toString() + "/Party/cache/";
			File fileCache = new File(UserFolderCache);
			if (!fileCache.exists()) {
				fileCache.mkdirs();
			}
			UserFolderAppDownload = Environment.getExternalStorageDirectory()
					.toString() + "/Party/app/Download";
			File fileAppDownload = new File(UserFolderAppDownload);
			if (!fileAppDownload.exists()) {
				fileAppDownload.mkdirs();
			}
			UserMomentUpload = Environment.getExternalStorageDirectory()
					.toString() + "/Party/image/Formats/";
			File fileUserMomentUpload = new File(UserMomentUpload);
			if (!fileUserMomentUpload.exists()) {
				fileUserMomentUpload.mkdirs();
			}
		} else {
//			UserFolderImgMapScreenShot = Environment
//					.getRootDirectory().toString()
//					+ "/SE/image/MapScreenShot/";
//			File fileMapScreenShot = new File(UserFolderImgMapScreenShot);
//			if (!fileMapScreenShot.exists()) {
//				fileMapScreenShot.mkdirs();
//			}
//			UserFolderImgDownLoad = Environment.getRootDirectory()
//					.toString() + "/SE/image/Download/";
//			File fileImgDownLoad = new File(UserFolderImgDownLoad);
//			if (!fileImgDownLoad.exists()) {
//				fileImgDownLoad.mkdirs();
//			}
//			UserFolderImgUpload = Environment.getRootDirectory()
//					.toString() + "/SE/image/Upload/";
//			File fileImgUpload = new File(UserFolderImgUpload);
//			if (!fileImgUpload.exists()) {
//				fileImgUpload.mkdirs();
//			}
//			UserFolderCache = Environment.getRootDirectory()
//					.toString() + "/SE/cache/";
//			File fileCache = new File(UserFolderCache);
//			if (!fileCache.exists()) {
//				fileCache.mkdirs();
//			}
//			UserFolderAppDownload = Environment.getRootDirectory()
//					.toString() + "/SE/app/Download";
//			File fileAppDownload = new File(UserFolderAppDownload);
//			if (!fileAppDownload.exists()) {
//				fileAppDownload.mkdirs();
//			}
		}
	}

	static Runnable runRedPoint = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			initRedPointNum(numberRedPoint);
		}

		public void initRedPointNum(int number) {
			ImageView imgRedPoint = (ImageView) ((Activity) context)
					.findViewById(R.id.z_moreTagRedPoint);
			if (imgRedPoint != null) {
				if (number <= 0) {
					imgRedPoint.setVisibility(View.GONE);
				} else {
					imgRedPoint.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	/**
	 * 在调用activity中的onResume后调用
	 */
	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
		remoteMessenger = SEMapApplication.serviceMessenger;
	}

	/**
	 * 检测活动 活动开始时间在一个小时之内:true
	 * 
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public static boolean TimeCheck(String starttime, String endtime) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		long start = Long.valueOf(starttime);
		long end = Long.valueOf(endtime);
		long timeChaaa = end - start;
		int startyear = Integer.valueOf(starttime.substring(0, 4));
		int startmonth = Integer.valueOf(starttime.substring(4, 6));
		int startday = Integer.valueOf(starttime.substring(6, 8));
		int starthour = Integer.valueOf(starttime.substring(8, 10));
		int startminute = Integer.valueOf(starttime.substring(10, 12));
		int startms = Integer.valueOf(starttime.substring(12, 15));
		System.out.println(startyear + ":" + startmonth + ":" + startday + ":"
				+ starthour + ":" + startminute + ":" + startms);
		int endyear = Integer.valueOf(endtime.substring(0, 4));
		int endmonth = Integer.valueOf(endtime.substring(4, 6));
		int endday = Integer.valueOf(endtime.substring(6, 8));
		int endhour = Integer.valueOf(endtime.substring(8, 10));
		int endminute = Integer.valueOf(endtime.substring(10, 12));
		int endms = Integer.valueOf(endtime.substring(12, 15));
		System.out.println(endyear + ":" + endmonth + ":" + endday + ":"
				+ endhour + ":" + endminute + ":" + endms);
		if (timeChaaa > 0) {
			// timeChaaa = timeChaaa * (-1);
			if ((endyear - startyear) != 0) {
			} else if ((endmonth - startmonth) != 0) {
			} else if ((endday - startday) != 0) {
			} else if ((endhour - starthour) != 0) {
			} else {
				return true;
			}
			return false;
		} else {
			if ((endyear - startyear) != 0) {
			} else if ((endmonth - startmonth) != 0) {
			} else if ((endday - startday) != 0) {
			} else if ((endhour - starthour) != 0) {
			} else {
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		Intent intent = new Intent(getApplicationContext(),
				MainTopRight.class);
		startActivityForResult(intent, TopRightDialogRequest);
		return false;
	}

	static int numberRedPoint;
	private static Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SQLiteProtocol.queryUserData:
				SQLResponse response = (SQLResponse) msg.obj;
				if (response.mark == 2)
					return;
				UserInfoSelectData[] users = (UserInfoSelectData[]) response.result;
				if (users.length <= 0)
					return;
				UserInfoSelectData userData = users[0];
				// 加载用户基本信息保存到静态变量中
				initSEMapApplication(userData);
				break;
			case SQLiteProtocol.queryAllSearchDistancesInMainActivity:
				SQLResponse responseAct = (SQLResponse) msg.obj;
				if (responseAct.mark == 2)// Object == null
					return;
				SearchDistance[] rels = (SearchDistance[]) responseAct.result;

				if (rels.length > 0){
					SearchActivityDistance = Integer.valueOf(rels[0].DistanceActivityDis)*1000;
					SearchMomentDistance = Integer.valueOf(rels[0].DistanceMomentDis)*1000;
				}
				break;
			case SQLiteProtocol.qureyAllPushMessages:
				SQLResponse responsePush = (SQLResponse) msg.obj;
				if (responsePush.mark == 2)
					return;
				PushMessage[] pushs = (PushMessage[]) responsePush.result;
				if(pushs == null)
					return;
				if(pushs.length <= 0)
					return;
				for(int index = 0;index < pushs.length;index++){
					SEMapApplication.MapPushMessage.put("MapPushMessage"+index, pushs[index]);
				}
				break;
			case SQLiteProtocol.queryRelationActivitysDataInHis:
				SQLResponse responseRelation = (SQLResponse) msg.obj;
				if (responseRelation.mark == 2)
					return;
				RelationActivity[] relations = (RelationActivity[]) responseRelation.result;
				if(relations == null)
					return;
				if(relations.length <= 0)
					return;
				for(int index = 0;index < relations.length;index++){
					SEMapApplication.MapRelationActivity.put("MapRelationActivity"+index, relations[index]);
				}
				break;
			case z_baiduprotocol.noticeRedPointPro:
				// 处理好点
				numberRedPoint = Integer.valueOf(String.valueOf(msg.obj));

				handlerRedPoint.post(runRedPoint);

				Message msgMore = Message.obtain();
				msgMore.what = z_baiduprotocol.noticeRedPointPro;
				msgMore.obj = numberRedPoint;
				if (Fragment_More.myMessenger != null)
					try {
						Fragment_More.myMessenger.send(msgMore);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case protocolfromserver.CheckVersionAndOtherInfo_S:
				CheckVersionAndOtherInfo_S checkVersion = (CheckVersionAndOtherInfo_S) msg.obj;
				String mark = checkVersion.getMark();
				if (mark.equals("1")) {
					String url = checkVersion.getURL();
					File fileAppDownload = new File(
							MainActivity.UserFolderAppDownload);
					if (!fileAppDownload.exists()) {
						fileAppDownload.mkdirs();
					}
					String path = fileAppDownload + "";
					// String url =
					// "http://121.40.123.240:5984/_utils/image/se/SamplesActivity.apk";
					// String url = "http://192.168.1.105:80/MyApp.apk";
					new VersionUpdate().showDownLoadDialog(context,
							url, path);
				} else {
//					Toast.makeText(mContext, "已是最新版本", 1).show();
				}
				break;
			case protocolwithbaidustore.baidutableactivityPOI:
				break;
			case protocolwithbaidustore.baidutableactivityRoute:
				break;
			case protocolwithbaidustore.baidutablearroundpersonPOI:
				break;
			case protocolwithbaidustore.replybaidutabledeletemessagePOI:
				break;
			case protocolwithbaidustore.baidutableschoolRoute:
				break;
			case protocolwithbaidustore.baidutablestorePOI:
				break;
			case protocolwithbaidustore.baidutableteammatePOI:
				break;
			case protocolwithbaidustore.baidutableteamPOI:
				break;
			case protocolwithbaidustore.replybaidutablemessagePOI:
				break;

			}

		}
	};

	public static void sendToBaiduComment(Object pu, String timeStamp) {
		Message msg = Message.obtain();

		msg.replyTo = myMessenger;

		msg.what = protocolwithbaidustore.baidutableudpatemessagePOI;
		msg.arg1 = Integer.parseInt(timeStamp.substring(0, 7));
		msg.arg2 = Integer.parseInt(timeStamp.substring(7, 15));
		msg.obj = pu;

		try {
			SEMapApplication.serviceMessenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void sendToBaidu(Object pu, String timeStamp, int protocol) {
		Message msg = Message.obtain();

		// List<Object> lo = new ArrayList<Object>();
		// lo.add(pu);

		msg.replyTo = myMessenger;

		// msg.what = protocolwithbaidustore.baidutablemessagePOI;
		msg.what = protocol;

		msg.arg1 = Integer.parseInt(timeStamp.substring(0, 7));
		msg.arg2 = Integer.parseInt(timeStamp.substring(7, 15));
		msg.obj = pu;

		try {
			SEMapApplication.serviceMessenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return false;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(0, 0, 0, "ContextMenu 1");
		menu.add(0, 1, 0, "ContextMenu 2");
		menu.add(0, 2, 0, "ContextMenu 3");
	}

	public boolean onContextItemSelected(MenuItem mi) {
		switch (mi.getItemId()) {
		case 0:
		case 1:
		case 2:
		case 3:
			break;
		}
		return true;

	}

	private void InitTextView() {
		tvTabActivity = (TextView) findViewById(R.id.z_tv_tab_activity);
		tvTabMoments = (TextView) findViewById(R.id.z_tv_tab_moment);
		tvTabMore = (TextView) findViewById(R.id.w_tv_tab_more);
		tvTabActivity.setTextColor(resources
				.getColor(R.color.z_mainmenu_selected));
		tvTabMoments.setTextColor(resources.getColor(R.color.z_mainmenu_normal));
		tvTabMore.setTextColor(resources.getColor(R.color.z_mainmenu_normal));

		tvTabActivity.setOnClickListener(new MyOnClickListener(0));
		tvTabMoments.setOnClickListener(new MyOnClickListener(1));
		tvTabMore.setOnClickListener(new MyOnClickListener(2));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.w_vPager);
		fragmentsList = new ArrayList<Fragment>();

		fragmentsList.add(new Fragment_Activity());
		fragmentsList.add(new Fragment_HotActivity());
		fragmentsList.add(new Fragment_CircleActivity());
		fragmentsList.add(new Fragment_Moment());
		fragmentsList.add(new Fragment_More());

		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.w_iv_bottom_line);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 得到屏幕的宽度

		offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);

		position_one = (int) (screenW / 3.0);
		position_two = position_one * 2;

		ViewGroup.LayoutParams params = ivBottomLine.getLayoutParams();
		params.width = (int) (screenW / 3.0);
		ivBottomLine.setLayoutParams(params);
		ivBottomLine.setBackgroundColor(resources
				.getColor(R.color.z_mainmenu_selected));
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					tvTabMoments.setTextColor(resources
							.getColor(R.color.z_mainmenu_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two, 0, 0, 0);
					tvTabMore.setTextColor(resources
							.getColor(R.color.z_mainmenu_normal));
				}
				tvTabActivity.setTextColor(resources
						.getColor(R.color.z_mainmenu_selected));
				Fragment_Activity.refreshAdapter();
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_one, 0, 0);
					tvTabActivity.setTextColor(resources
							.getColor(R.color.z_mainmenu_normal));
					// Fragment_Activity.refreshAdapter();
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_one, 0, 0);
					tvTabMore.setTextColor(resources
							.getColor(R.color.z_mainmenu_normal));
				}
				tvTabMoments.setTextColor(resources
						.getColor(R.color.z_mainmenu_selected));
				Fragment_Activity.refreshAdapter();
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(0, position_two, 0, 0);
					tvTabActivity.setTextColor(resources
							.getColor(R.color.z_mainmenu_normal));
					// Fragment_Activity.refreshAdapter();
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_two, 0, 0);
					tvTabMoments.setTextColor(resources
							.getColor(R.color.z_mainmenu_normal));
					// Fragment_Activity.refreshAdapter();
				}
				tvTabMore.setTextColor(resources
						.getColor(R.color.z_mainmenu_selected));
				Fragment_Activity.refreshAdapter();
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TopRightDialogRequest) {
			switch (resultCode) {
			case TopRightDialog_Null:

				break;
			case TopRightDialog_OK_01:
				Intent intent = new Intent(new Intent(getApplicationContext(),
						BuildActivity.class));
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
				break;
			case TopRightDialog_OK_02:
				startActivity(new Intent(getApplicationContext(),
						ShareMoment.class));
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		CloudManager.getInstance().destory();
		super.onDestroy();
	}

	@Override
	public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	// ~
	@Override
	public void onGetSearchResult(CloudSearchResult result, int arg1) {
		// TODO Auto-generated method stub
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			poidata = result;
			SEMapApplication.getInstance().transferObj
					.setCloudPoiInfoList(result.poiList);
		} else {
			poidata = result;
			// Toast.makeText(this, "没有内容了...", 1).show();
		}
	}

	boolean sendMoreTime = false;// 记录只发送一次的标志

	public class LocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub

			if (location == null) {
				return;
			}
			locationdata.latitude = location.getLatitude();
			locationdata.longitude = location.getLongitude();
			if ((location == null) || (locationdata.latitude < 3)
					|| (locationdata.latitude > 55)
					|| (locationdata.longitude < 70)
					|| (locationdata.longitude > 136)) {
				Log.e("onReceiveLocation", "!!!get wrong locationpoint"
						+ locationdata.latitude + " , "
						+ locationdata.longitude);
				return;
			}

			// 保存数据定位
			mySharedPreferences = getSharedPreferences(MYPREFS, mode);
			myEditor = mySharedPreferences.edit();
			myEditor.putInt("MyLatitudeE6",
					(int) (locationdata.latitude * 1000000));
			myEditor.putInt("MyLongitudeE6",
					(int) (locationdata.longitude * 1000000));
			myEditor.apply();

			a = location.getLongitude();
			b = location.getLatitude();
			// 确认已经得到经纬度
			if (a != 0.0 && b != 0.0) {
				// 在得到经纬度后进行搜索
				if (!sendMoreTime) {
					Fragment_Activity.SearchActivity();
					Fragment_Moment.SearchMonent();
					sendMoreTime = true;
				}
			}

			// 保存数据定位
			// mySharedPreferences = getSharedPreferences(MYPREFS, mode);
			// myEditor = mySharedPreferences.edit();
			// myEditor.putInt("MyLatitudeE6",
			// (int) (locationdata.latitude * 1000000));
			// myEditor.putInt("MyLongitudeE6",
			// (int) (locationdata.longitude * 1000000));
			// myEditor.apply();

			// // 在这里替换呵呵!
			// myCity = location.getCity();
			// myProvince = location.getProvince();
			// myStreet = location.getStreet();

			// 定位精度圈半径，可设置为0
			locationdata.accuracy = location.getRadius();
			// ???方向
			locationdata.direction = location.getDerect();
			// 更新定位数据

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

	/** 连续点击两次退出程序 ****/
	boolean isExit;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出",
					Toast.LENGTH_SHORT).show();
			exitHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}
	}

	Handler exitHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isExit = false;
		}

	};

}