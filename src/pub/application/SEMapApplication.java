package pub.application;

import java.util.HashMap;
import java.util.Map;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import pri.z.utils.ContactDao;
import pub.infoclass.db.UserInfoSelectData;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Messenger;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.domain.User;

/**
 * ACRA
 */
@ReportsCrashes(formKey = "", httpMethod = org.acra.sender.HttpSender.Method.PUT, reportType = org.acra.sender.HttpSender.Type.JSON, formUri = "http://121.40.123.240:5984/acra-bugreport/_design/acra-storage/_update/report", formUriBasicAuthLogin = "seteam", formUriBasicAuthPassword = "hbhb123")
public class SEMapApplication extends Application {
	private static SEMapApplication mapInstance = null;
	// 用户自定义账户
	// 个人必须信息
	public boolean isLogin = false;
	public static String AccountNumber = "0";
	
	//1014：用来记录已经登录的用户的所有信息(为了在分享动态的时候方便)：修改它的位置：1：登录成功后  2：修改个人信息后
	public static UserInfoSelectData LoginUser = null;
	
	public static String LoginCode = "0";
	public static String LoginName = "0";
	public static String Code = "0";
	private static String Phone;
	public static String UserName;
	// Object为PushMessage
	public static Map<String, Object> MapPushMessage = new HashMap<String, Object>();

	// Object为RelationActivity
	public static Map<String, Object> MapRelationActivity = new HashMap<String, Object>();
	// public static int buildTeamNumber ;
	// public static int buildActivityNumber ;
	// public static int joinTeamNumber ;
	// public static int joinActivity ;
	// public static int friendsNumber ;
	// public static int level ;
	//
	// public static List<UserTeamInfo> userTeamInfoList;
	// public static List<UserActivityInfo> userActivityInfoList;
	// public static List<UserFriendsInfo> userFriendsInfoList;

	// 个人不必须信息

	// 个人在线状态信息
	// public static int latitude , longitude;

	// 当前上下文
	public static Context currentContext = null;
	// 当前信使
	public static Messenger currentMessenger = null;
	// 当前消息处理中心
	public static Handler currentHandler = null;
	// 服务的信使
	public static Messenger serviceMessenger = null;
	// 服务的处理中心
	public static Handler serviceHandler;

	// 默认网络头像
	// public static String UserHeadPortraitPath =
	// "http://192.168.1.100/myimage/seimage/1.jpg";
	public boolean m_bKeyRight = true;
	// 设置是否可点击
	public static boolean enableclick = true;
	// 设置初始地图缩放大小
	public static float initmapzoom = 15;
	// 地图初始中心
	public static GeoPoint p = new GeoPoint((int) (39.945 * 1E6),
			(int) (116.404 * 1E6));
	// 内置缩放控件
	public static boolean BuiltInZoomControls = false;
	// 设置是否显示卫星图
	public static boolean SatelliteLayer = false;
	// 是否显示交通图
	public static boolean TrafficLayer = true;
	// 是否启用缩放手势
	public static boolean ZoomGesturesEnabled = true;
	// 是否启用平移手势
	public static boolean ScrollGesturesEnabled = true;
	// 是否启用双击放大
	public static boolean DoubleClickEnable = true;
	// 是否启用旋转手势
	public static boolean RotateEnable = true;
	// 是否启用俯视手势
	public static boolean OverlookEnable = true;
	// 是否显示内置缩放控件
	public static boolean CompassMarginEnable = true;

	public BMapManager mapmanager = null;

	public static final String strkey = "pVsYu41Wqk6KyItiYG7MnYvC";
	
	//12月13号：解决动态详情的评论数据加载不了的问题，得用这个key.还有其他的情况也得用这个key
	public static final String someStrkey = "pVsYu41Wqk6KyItiYG7MnYvC";

	public static TransferObject transferObj = new TransferObject();

	// 图片使用的客户端
	private HttpClient httpClient;

	// 未处理信息存储
	public static SharedPreferences netUndealInfo;
	public static Editor netUndealInfoEditor;
	public static final String netUndealFileName = "NETUNDEALINFO";
	public static final int netUndealMode = Activity.MODE_PRIVATE;

	//1206环信
	public static Context applicationContext;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	//1206环信
	
	public void onCreate() {
		super.onCreate();
		mapInstance = this;
		initEngineManager(this);
		httpClient = this.createHttpClient();
		ACRA.init(this);

		Intent intent = new Intent();
		intent.setAction("com.example.semap.NetService");
		startService(intent);
		// initLocationInfo();
		
		//1206环信
		applicationContext = this;

        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         * 
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         * for example:
         * 例子：
         * 
         * public class DemoHXSDKHelper extends HXSDKHelper
         * 
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
        hxSDKHelper.onInit(applicationContext);
      //1206环信
	}

	/**
	 * SEMapApplication is context!
	 * 
	 * @param seMapApplication
	 */
	private void initEngineManager(Context context) {
		if (mapmanager == null) {
			mapmanager = new BMapManager(context);
		}

		if (!mapmanager.init(strkey, new MyGeneralListener())) {
			Toast.makeText(
					SEMapApplication.getInstance().getApplicationContext(),
					"mapmanager 初始化失败", Toast.LENGTH_SHORT).show();
		}
	}

	public static SEMapApplication getInstance() {
		// TODO Auto-generated method stub
		return mapInstance;
	}

	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						SEMapApplication.getInstance().getApplicationContext(),
						"您的网络出错啦", Toast.LENGTH_SHORT).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						SEMapApplication.getInstance().getApplicationContext(),
						"输入正确的检索条件！", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(
						SEMapApplication.getInstance().getApplicationContext(),
						"请在 DemoApplication.java文件输入正确的授权Key！",
						Toast.LENGTH_SHORT).show();
				SEMapApplication.getInstance().m_bKeyRight = false;
			}
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.shutdownHttpClient();
	}

	// 创建HttpClient实例
	private HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}

	// 关闭连接管理器并释放资源
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	// 对外提供HttpClient实例
	public HttpClient getHttpClient() {
		return httpClient;
	}

	public static String getPhone() {
		setPhone();
		return Phone;
	}

	private static ContactDao cd;

	private static void setPhone() {
		cd = new ContactDao(getInstance());
		Phone = cd.getPhoneToNormal(cd.getPhoneNumber());
		AccountNumber = Phone;
	}

	public static String CurrentAddress = "";
	// 定位相关
	public static LocationClient locationclient;
	// 存储位置数据,用户所处位置
	public static LocationData locationdata = null;
	public static Context netServiceContext;

	public static void initLocationInfo(Context context,
			LocationData locationdata, BDLocationListener locationlistener) {
		// 定位初始化
		locationclient = new LocationClient(context);
		SEMapApplication.locationdata = locationdata;
		locationclient.registerLocationListener(locationlistener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");// 设置坐标类型
		option.setScanSpan(1000);
		option.setPoiExtraInfo(true);
		option.disableCache(false);
		option.setAddrType("all");
		locationclient.setLocOption(option);
		locationclient.start();
	}

	/**
	 * 可以设置Messenger 设置当前处理中心 设置之后就可以使用当前handler对消息进行处理
	 * 前提是发送信息使用的是currentMessenger 可以是在任意位置的handler 最好放在onresume中
	 * 
	 * @param cHandler
	 */
	public static void setCurrentHandler(Handler cHandler) {
		currentHandler = cHandler;
		// currentMessenger = new Messenger(currentHandler);
	}

	public static Messenger getCurrentMessenger() {
		return currentMessenger;
	}
	
	
	//1206环信
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
	    return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
	    hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(emCallBack);
	}
	
	//1206环信
}
