package pri.z.show;

import java.util.ArrayList;

import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class LoadingActivity extends Activity {

	public static Context mContext;
	static Activity myLoadingActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_loading);
		myLoadingActivity = this;

		mContext = getBaseContext();
		// Intent intent = new Intent();
		// intent.setAction("com.example.semap.NetService");
		// boolean isSuccess = bindService(intent, connection,
		// Service.BIND_AUTO_CREATE);

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		// 这里Handler的postDelayed方法，等待10000毫秒在执行run方法。
		// 在Activity中我们经常需要使用Handler方法更新UI或者执行一些耗时事件，
		// 并且Handler中post方法既可以执行耗时事件也可以做一些UI更新的事情，比较好用，推荐使用
		// new Handler().postDelayed(new Runnable(){
		// public void run(){
		// //等待10000毫秒后销毁此页面，并提示登陆成功
		// LoadingActivity.this.finish();
		// // Toast.makeText(getApplicationContext(), "登录成功",
		// Toast.LENGTH_SHORT).show();
		// }
		// }, 10000);
	}

	// public static Messenger remoteMessenger = null;
	// public static Messenger myMessenger = null;
	//
	// NetService.MyBinder binder;
	// private ServiceConnection connection = new ServiceConnection(){
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// // TODO Auto-generated method stub
	// remoteMessenger = new Messenger(service);
	// myMessenger = new Messenger(myHandler);
	// SEMapApplication.currentMessenger = myMessenger;
	// }
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// };
	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;

	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			// case protocolfromserver.loginUser_S:
			// loginUser_S ls = (loginUser_S)msg.obj;
			// if(ls.getMark().equals("1")){
			// sendMyInfoMsg(ls.getUserID());
			// }else{
			// Intent intent = new
			// Intent(LoadingActivity.this,LoginOrRegister.class);
			// startActivity(intent);
			// Toast.makeText(LoadingActivity.this, "您的账号或者密码输入错误，请重新输入",
			// 1).show();
			// LoadingActivity.this.finish();
			// }
			// break;
			// case protocolfromserver.getUserInfo_S://获取本地用户的数据
			// getUserInfo_S us = (getUserInfo_S)msg.obj;
			// Gson g = new Gson();
			//
			// String UserInfoList = us.getUserInfoList();
			// List<UserInfoSelectData> ps = g.fromJson(UserInfoList, new
			// TypeToken<List<UserInfoSelectData>>(){}.getType());
			// UserInfoSelectData data = ps.get(0);
			//
			// DBAdapter dbAdapter = new DBAdapter(mContext);
			// dbAdapter.open();
			// dbAdapter.deleteAllUserInfoBases();
			// dbAdapter.insertUserInfoBase(data);
			// Intent intent = new
			// Intent(LoadingActivity.this,MyInfoCenter.class);
			// startActivity(intent);
			// Toast.makeText(LoadingActivity.this, "登录成功", 1).show();
			// LoadingActivity.this.finish();
			// break;
			}
		}
	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	public void sendMyInfoMsg(String userId) {

		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();

		li.setUserID(userId);
		li.setStandardUploadTime();
		li.setPageIndex("1");
		li.setPageSize("5");
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
}