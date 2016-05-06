package pri.z.main;

import java.io.File;
import java.util.ArrayList;

import pri.z.game.GameMenu;
import pri.z.mydb.SearchDistance;
import pri.z.show.AboutActivity;
import pri.z.show.FeedBack;
import pri.z.show.MyMessage;
import pri.z.show.R;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.utils.VersionUpdate;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.util.notification;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.LoginActivity;
import com.easemob.chatuidemo.activity.MainActivityHX;
import com.easemob.chatuidemo.activity.SplashActivity;

public class Fragment_More extends Fragment{
    Context mContext;
    DBAdapter dbAdapter;
    SearchDistance myDistance;
    static Handler handlerRedPoint;
    public static Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getBaseContext();
        context = getActivity();
        handlerRedPoint = new Handler();
        remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle){
		View view = inflater.inflate(R.layout.z_moremenu, container,false);
		initMenu(view);
		return view;
	}
    

	private void initMenu(View view) {
		/***会话*****/
		RelativeLayout relHX = (RelativeLayout)view.findViewById(R.id.z_moreHXMessageRel);
		relHX.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					//进入主页面
					startActivity(new Intent(getActivity(), MainActivityHX.class));
				}else {
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}
				
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		
		/***通知*****/
		RelativeLayout rel = (RelativeLayout)view.findViewById(R.id.z_moreMessageRel);
		rel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//点击后让所有的通知栏消息消失
				notification.mNotificationManager.cancelAll();
				
				Intent intent = new Intent(getActivity(),MyMessage.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		
		/***小游戏*****/
		RelativeLayout relGame = (RelativeLayout)view.findViewById(R.id.z_more_smallGameRel);
		relGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(),GameMenu.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		
		
		/***关于*****/
		RelativeLayout relAboutApp = (RelativeLayout)view.findViewById(R.id.z_moreAboutAppRel);
		relAboutApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(),AboutActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		
		/***意见反馈*****/
		RelativeLayout relFeedBack = (RelativeLayout)view.findViewById(R.id.z_moreFeedBackRel);
		relFeedBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(),FeedBack.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		
		
		
	}
	
	public static Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public static Messenger myMessenger = null;
	/**
	 * 获取本人的信息
	 */
	public void sendGetMomentUserIdInfomationMsg() {
		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();
		li.setUserID(SEMapApplication.AccountNumber);

		li.setStandardUploadTime();

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
	
	static int numberRedPoint;
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
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
				} 
				break;
			case SQLiteProtocol.queryUserData:
				SQLResponse response = (SQLResponse) msg.obj;
				if (response.mark == 2)
					return;
				UserInfoSelectData[] users = (UserInfoSelectData[]) response.result;
				if (users.length <= 0)
					return;
				UserInfoSelectData userData = users[0];
				// 加载用户基本信息保存到静态变量中
				MainActivity.initSEMapApplication(userData);
				break;
			case z_baiduprotocol.noticeRedPointPro:
				//处理好点
				numberRedPoint = Integer.valueOf(String.valueOf(msg.obj));
				handlerRedPoint.post(runRedPoint);
				break;
			}
		}
	};
	
	
	static Runnable runRedPoint = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			initRedPointNum(numberRedPoint);
		}
		public void initRedPointNum(int number){
			ImageView  imgRedPoint = (ImageView)((Activity) context).findViewById(R.id.z_moreMessageRedPoint);
			if(imgRedPoint != null){
				if(number <= 0){
					imgRedPoint.setVisibility(View.GONE);
				}
				else{
					imgRedPoint.setVisibility(View.VISIBLE);
				}
			}
		}
	};
	
}


