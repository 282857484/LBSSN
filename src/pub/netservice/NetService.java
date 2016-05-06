package pub.netservice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pri.z.build.ShareMomentPhotoThread;
import pri.z.sqlite.CheckPushMessageNow;
import pri.z.sqlite.MySQLSecurityThread;
import pub.application.SEMapApplication;
import pub.httptransfer.SearchMomentDiscussByMessageId;
import pub.httptransfer.SearchOneMomentDetailById;
import pub.infoclass.myserver.protocol.loginUser_C;
import pub.infoclass.myserver.protocol.loginUser_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.infoclass.pushservice.REQ_Heart_Beat;
import pub.infoclass.pushservice.h_protocol_pusher;
import pub.util.FormatTime;
import pub.util.KeywordFliter;
import pub.util.MsgReplace;
import pub.util.notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.baidu.mapapi.BMapManager;

public class NetService extends Service {

	// static DatagramSocket socket = null;

	private String data = new String();
	public static ConcurrentHashMap<String, statusRow> statusMap = null;
	private Timer checkTimer;
	static ExecutorService SQLPool;
	static ExecutorService CheckPool;
	private boolean LoginFlag = false;

	// private static Context mContext = new NetService();
	/**
	 * <String, String> <PushID, PushMessage>
	 */
	public static ConcurrentHashMap<String, String> receivePusher;

	// static boolean firstStartThread = true;
	public static ExecutorService ReceivePool;

	/**
	 * 这里是准备数据的地方 也可以引用Application的数据
	 */

	public class MyBinder extends Binder {
		/**
		 * 这里可以写一些可以调用Service的一些方法 可以读取Service的一些数据
		 */
		public String getData() {
			return data;
		}
	}

	int startModel = 1;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SEMapApplication.serviceHandler = myHandler;
		SEMapApplication.serviceMessenger = myMessenger;
		super.onStartCommand(intent, flags, startId);
		startForeground(0, notification.mBuilder.build());
		return startModel;
	}

	private static Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			// creatpoi
			case protocolwithbaidustore.baidutableschoolRoute:
			case protocolwithbaidustore.baidutablestorePOI:
			case protocolwithbaidustore.baidutablemessagePOI:
			case protocolwithbaidustore.baidutablearroundpersonPOI:
			case protocolwithbaidustore.baidutableteammatePOI:
			case protocolwithbaidustore.baidutableteamPOI:
			case protocolwithbaidustore.baidutableactivityRoute:
			case protocolwithbaidustore.baidutableactivityPOI:

				// udpatepoi
			case protocolwithbaidustore.baidutableudpateschoolRoute:
			case protocolwithbaidustore.baidutableudpatestorePOI:
			case protocolwithbaidustore.baidutableudpatemessagePOI:
			case protocolwithbaidustore.baidutableudpatearroundpersonPOI:
			case protocolwithbaidustore.baidutableudpateteammatePOI:
			case protocolwithbaidustore.baidutableudpateteamPOI:
			case protocolwithbaidustore.baidutableudpateactivityRoute:
			case protocolwithbaidustore.baidutableudpateactivityPOI:

				// deletepoi
			case protocolwithbaidustore.baidutabledeleteschoolRoute:
			case protocolwithbaidustore.baidutabledeletestorePOI:
			case protocolwithbaidustore.baidutabledeletemessagePOI:
			case protocolwithbaidustore.baidutabledeletearroundpersonPOI:
			case protocolwithbaidustore.baidutabledeleteteammatePOI:
			case protocolwithbaidustore.baidutabledeleteteamPOI:
			case protocolwithbaidustore.baidutabledeleteactivityRoute:
			case protocolwithbaidustore.baidutabledeleteactivityPOI:

				// creatpoi
			case protocolwithbaidustore.replybaidutableschoolRoute:
			case protocolwithbaidustore.replybaidutablestorePOI:
			case protocolwithbaidustore.replybaidutablemessagePOI:
			case protocolwithbaidustore.replybaidutablearroundpersonPOI:
			case protocolwithbaidustore.replybaidutableteammatePOI:
			case protocolwithbaidustore.replybaidutableteamPOI:
			case protocolwithbaidustore.replybaidutableactivityRoute:
			case protocolwithbaidustore.replybaidutableactivityPOI:

				// udpatepoi
			case protocolwithbaidustore.replybaidutableudpateschoolRoute:
			case protocolwithbaidustore.replybaidutableudpatestorePOI:
			case protocolwithbaidustore.replybaidutableudpatemessagePOI:
			case protocolwithbaidustore.replybaidutableudpatearroundpersonPOI:
			case protocolwithbaidustore.replybaidutableudpateteammatePOI:
			case protocolwithbaidustore.replybaidutableudpateteamPOI:
			case protocolwithbaidustore.replybaidutableudpateactivityRoute:
			case protocolwithbaidustore.replybaidutableudpateactivityPOI:

				// deletepoi
			case protocolwithbaidustore.replybaidutabledeleteschoolRoute:
			case protocolwithbaidustore.replybaidutabledeletestorePOI:
			case protocolwithbaidustore.replybaidutabledeletemessagePOI:
			case protocolwithbaidustore.replybaidutabledeletearroundpersonPOI:
			case protocolwithbaidustore.replybaidutabledeleteteammatePOI:
			case protocolwithbaidustore.replybaidutabledeleteteamPOI:
			case protocolwithbaidustore.replybaidutabledeleteactivityRoute:
			case protocolwithbaidustore.replybaidutabledeleteactivityPOI:

				//
			case protocolwithbaidustore.baidutablemessagediscuss:
			case protocolwithbaidustore.baidutableupdatemessagediscuss:
			case protocolwithbaidustore.baidutabledeletemessagediscuss:
			case protocolwithbaidustore.replybaidutablemessagediscuss:
			case protocolwithbaidustore.replybaidutableupdatemessagediscuss:
			case protocolwithbaidustore.replybaidutabledeletemessagediscuss:
				ReceivePool.execute(new handleBaiduCloudDataThread(msg.what,
						msg.obj, msg.replyTo, msg.arg1, msg.arg2));
				break;
			// 动态上传图片后台处理
			case z_baiduprotocol.momentCenterUploadPhoto:
				ReceivePool.execute(new ShareMomentPhotoThread(msg.obj,
						msg.replyTo));
				break;
			case z_baiduprotocol.baiduMomentDetail:
				String messageId = (String) msg.obj;
				ReceivePool.execute(new SearchMomentDiscussByMessageId(
						messageId));
				ReceivePool.execute(new SearchOneMomentDetailById(messageId));
				break;
			default:
				statusRow register = new statusRow(msg.obj, msg.replyTo,
						msg.what);

				StringBuilder sbOfTimeRegister = new StringBuilder();
				sbOfTimeRegister.append(msg.arg1);
				String ss1 = String.valueOf(msg.arg2);
				if (ss1.length() < 8) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < (8 - ss1.length()); i++) {
						sb.append("0");
					}
					sbOfTimeRegister.append(sb.toString() + ss1);
				} else {
					sbOfTimeRegister.append(msg.arg2);
				}
				statusMap.put(sbOfTimeRegister.toString(), register);
				Log.e("TIMESTAMP", "SEND time : " + sbOfTimeRegister);
				ReceivePool.execute(new handleNetSendReceiveThread_TCP(
						register, sbOfTimeRegister.toString()));
				
			}

			// if (msg.what > 10000) {
			// creatPoi creatbroadcast = new creatPoi();
			// Map<String, Object> customvalue = new HashMap<String, Object>();
			// String AccountNumber = SEMapApplication.AccountNumber;
			// customvalue.put("univeralindex", AccountNumber);
			// StringBuilder sbOfTimeRegister = new StringBuilder();
			// sbOfTimeRegister.append(msg.arg1);
			// String ss1 = String.valueOf(msg.arg2);
			// if (ss1.length() < 8) {
			// StringBuilder sb = new StringBuilder();
			// for (int i = 0; i < (8 - ss1.length()); i++) {
			// sb.append("0");
			// }
			// sbOfTimeRegister.append(sb.toString() + ss1);
			// } else {
			// sbOfTimeRegister.append(msg.arg2);
			// }
			// customvalue.put("uploadingtime", sbOfTimeRegister);
			// creatbroadcast.setColumnkey(customvalue);
			// creatbroadcast.setCoord_type(3);
			// creatbroadcast
			// .setGeotable_id(String
			// .valueOf(protocolwithbaidustore.baidutablearroundpersonPOI));
			// creatbroadcast
			// .setLatitude((double) (SEMapApplication.locationdata.latitude));
			// creatbroadcast
			// .setLongitude((double)
			// (SEMapApplication.locationdata.longitude));
			//
			// ReceivePool.execute(new
			// handleBaiduCloudDataThread(protocolwithbaidustore.baidutablearroundpersonPOI,
			// creatbroadcast, msg.replyTo, msg.arg1, msg.arg2));
			// }
		}
	};

	public static Messenger myMessenger = new Messenger(myHandler);

	private static Handler serviceHandler = new Handler() {
		public void handleMessage(Message msg) {

			// Log.v("哈哈哈哈", "收到登录反馈现象---" + msg.obj.toString());
			super.handleMessage(msg);
			switch (msg.what) {
			case protocolfromserver.loginUser_S:
				loginUser_S ls = (loginUser_S) msg.obj;
				if (ls.getMark().equals("1")) {
					SEMapApplication.AccountNumber = ls.getUserID();
				} else {

				}
				break;
			}
		}
	};
	private Messenger serviceMessenger = new Messenger(serviceHandler);

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return myMessenger.getBinder();
	}

	public boolean onUnbind(Intent intent) {
		/**
		 * 这里写一些解开绑定需要调用的方法与数据
		 */
		Log.e("NetService + Handler", "called : NetService onUnbind");
		return true;
	}

	public void onCreate() {
		super.onCreate();
		SEMapApplication app = (SEMapApplication) this.getApplication();
		if (app.mapmanager == null) {
			app.mapmanager = new BMapManager(this);
			app.mapmanager.init(SEMapApplication.strkey,
					new SEMapApplication.MyGeneralListener());
		}

		SEMapApplication.netServiceContext = NetService.this;

		notification.initThisNoticification(NetService.this);
		/**
		 * 这里为Service初始化的时候运行的任务
		 */

		receivePusher = new ConcurrentHashMap<String, String>();
//
		int poolSize = Runtime.getRuntime().availableProcessors()
				* NetConfig.POOL_SCALE;
		ReceivePool = Executors.newFixedThreadPool(poolSize);

		// statusTable = new Vector<statusRow>(poolSize);
		statusMap = new ConcurrentHashMap<String, statusRow>();
		/**
		 * TimerTask task, long delay, long period 这里的period应该与
		 * socket.setSoTimeout(1000)和handleNetSendThread中重发时间保持一致
		 */
		checkTimer = new Timer();
		checkTimer.schedule(new CheckTimerTask(), 0,
				NetConfig.CheckTimerRunFreuency);

		// 单实例数据库操作
		SQLPool = Executors.newSingleThreadExecutor();
		SQLPool.execute(new MySQLSecurityThread(getApplicationContext()));
		
		CheckPool = Executors.newSingleThreadExecutor();
		// 检查推送消息
		CheckPool.execute(new CheckPushMessageNow(getApplicationContext()));

	}

	List<Object> pList;
	loginUser_C lo = null;

	private void sendLogin() {
		// TODO Auto-generated method stub

		if (!LoginFlag) {// 刚进入应用时，只发送一次

			pList = new LinkedList<Object>();
			lo = new loginUser_C();
			lo.setUserID(SEMapApplication.AccountNumber);
			lo.setStandardUploadTime();
			lo.setCode(SEMapApplication.LoginCode);

			pList.add(lo);
			LoginFlag = true;

			statusRow register = new statusRow(pList, null, lo.getP());
			ReceivePool.execute(new handleNetSendReceiveThread_TCP(register, lo
					.getUploadTime()));
			
			// statusMap.put(lo.getUploadTime(), register);
		}

	}

	public static ExecutorService getThreadPool() {
		return ReceivePool;
	}

	private int count = 0; // timetask

	public int getCount() {
		return count;
	}

	public void Countplus() {
		this.count++;
	}

	public void returnCount() {
		this.count = 0;
	}

	class CheckTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// send login info
			// 进入应用第一次加载
			if (!LoginFlag) {
				KeywordFliter.getMingGanStrs(NetService.this);
			}
			sendLogin();
			
			// 独立于重发系统的REQ
			String REQTime = FormatTime.getFormatTime();
			Countplus();
			Log.e("NetService", "Countplus" + getCount());
			if ((getCount() >= NetConfig.HeartBreakFrequency)
					&& (!(SEMapApplication.AccountNumber.equals("0")))) {
				List<Object> packetList = new LinkedList<Object>();
				
//				// 检查推送消息
//				ReceivePool.execute(new CheckPushMessageNow(getApplicationContext()));

				// 0912注释
				// SEMapApplication.AccountNumber = new LoginInfo(
				// getApplicationContext()).getUserInfoPhone();
				// if (new LoginInfo(getApplicationContext()).getUserInfoPhone()
				// .equals("")) {
				// SEMapApplication.AccountNumber = "0";
				// }
				packetList.add(new REQ_Heart_Beat(
						SEMapApplication.AccountNumber, REQTime,
						SEMapApplication.Code));

				// ReceivePool.execute(new handleNetSendThread(socket,
				// new statusRow(new MsgReplace(packetList,
				// SEMapApplication.currentMessenger,
				// h_protocol_pusher.REQ_Heart_Beat)), REQTime));
				ReceivePool.execute(new handleNetSendReceiveThread_TCP(
						new statusRow(new MsgReplace(packetList,
								SEMapApplication.currentMessenger,
								h_protocol_pusher.REQ_Heart_Beat)), REQTime));
				returnCount();
			}

			Iterator<Entry<String, statusRow>> iter = statusMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, statusRow> entry = (Map.Entry<String, statusRow>) iter
						.next();
				String formatTime = entry.getKey();
				Log.e("NetService-formatTime : ", formatTime);
				statusRow statusrow = entry.getValue();
				Log.e("NetService-statusrow : ", statusrow.getMsgReplace()
						.toString());

				if (statusrow.isSuccessed == true) // 成功则删除
				{
					statusMap.remove(formatTime);
					Log.e("NetService", "index : formatTime obj is Deleted!");
				} else // 失败则重传
				{
					if (statusrow.sendTime < NetConfig.ReSendTime) {
						statusrow.sendTime ++;
//						ReceivePool.execute(new handleNetSendReceiveThread_TCP(
//								statusrow, formatTime));
//						Log.e("NetService", "数据重传");
					} else {
						statusMap.remove(formatTime);
						Log.e("NetService",
								"index : formatTime obj is Deleted!");
						Log.e("NetService", "请检查网络是否正常连接");
					}
				}
			}
		}

	}

	public void onDestory() {
		super.onDestroy();
		/**
		 * 服务摧毁时保存数据
		 */
	}

	// public static DatagramSocket getDatagramSocket() {
	// // TODO Auto-generated method stub
	// return socket;
	// }

}
