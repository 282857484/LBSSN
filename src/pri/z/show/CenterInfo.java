package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.z.photoshow.ShowDatuFromNet;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.SearchMomentsByUserId;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.getUserInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import pub.util.ImageManager3;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

public class CenterInfo extends Activity {
	Context mContext;
	public static final String TAG = "哈哈哈哈";
	ProgressBar progressBar;

	String momentCenterUserId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		setContentView(R.layout.z_centerinfo);

		momentCenterUserId = getIntent().getStringExtra("momentCenterUserId");

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		progressBar = (ProgressBar) findViewById(R.id.z_centerInfoProgressBar);

		
		initListener();
		initPressedFeel();
		sendGetMomentUserIdInfomationMsg();

		if (!UtilsZZK.checkNetworkState(CenterInfo.this)) {
			progressBar.setVisibility(View.GONE);
			return;
		}
	}

	/**
	 * 设置监听：让它有点击效果
	 */
	private void initPressedFeel() {
		OnClickListener pressedFeelListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		};
		RelativeLayout re1 = (RelativeLayout) findViewById(R.id.z_centerInfouserNameRel);
		RelativeLayout re2 = (RelativeLayout) findViewById(R.id.z_centerInfouserBirthdayRel);
		RelativeLayout re3 = (RelativeLayout) findViewById(R.id.z_centerInfouserHomeRel);
		RelativeLayout re4 = (RelativeLayout) findViewById(R.id.z_centerInfouserProfessionRel);
		RelativeLayout re5 = (RelativeLayout) findViewById(R.id.z_centerInfouserSchoolRel);
		RelativeLayout re6 = (RelativeLayout) findViewById(R.id.z_centerInfouserWeixinRel);
		RelativeLayout re7 = (RelativeLayout) findViewById(R.id.z_centerInfouserQQRel);
		re1.setOnClickListener(pressedFeelListener);
		re2.setOnClickListener(pressedFeelListener);
		re3.setOnClickListener(pressedFeelListener);
		re4.setOnClickListener(pressedFeelListener);
		re5.setOnClickListener(pressedFeelListener);
		re6.setOnClickListener(pressedFeelListener);
		re7.setOnClickListener(pressedFeelListener);
	}

	
	private void initListener() {
		// TODO Auto-generated method stub

		ImageView imgHead = (ImageView) findViewById(R.id.z_centerInfoUserHead);
		ImageManager3.from(mContext).displayImage(
				imgHead,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderUserHeadXiaotu + "/"
						+ momentCenterUserId + ".png",
				R.drawable.z_logindefault);
		imgHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CenterInfo.this,
						ShowDatuFromNet.class);
				intent.putExtra("ShowDatuFromNetType",
						addressInfo.FileTypetouxiang);
				intent.putExtra("CurrentItem", 0);// 起始为0
				intent.putExtra("ShowDatuFromNet", momentCenterUserId + "");
				startActivity(intent);
			}
		});
		TextView btnMoment = (TextView) findViewById(R.id.z_centerInfoMomentBtn);
		TextView btnActivity = (TextView) findViewById(R.id.z_centerInfoActivityBtn);

		btnActivity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(CenterInfo.this,
						CenterActivity.class);
				intent.putExtra("momentCenterUserId", momentCenterUserId);

				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);

			}
		});
		btnMoment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CenterInfo.this, CenterMoment.class);
				intent.putExtra("momentCenterUserId", momentCenterUserId);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
	}

	/**
	 * 得到用户的个人资料
	 */
	public void sendGetMomentUserIdInfomationMsg() {
		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();
		li.SearchUserID = momentCenterUserId;
		li.setUserID(SEMapApplication.AccountNumber);
		if (SEMapApplication.AccountNumber.equals("0")) {
			Toast.makeText(mContext, "您尚未登录，不能看ta的资料", Toast.LENGTH_SHORT).show();
			progressBar.setVisibility(View.GONE);
			return;
		}

		li.setStandardUploadTime();

		ArrayList<Object> list = new ArrayList<Object>();
		list.add(li);

		msg.replyTo = myMessenger;
		msg.what = li.getP();
		String timeStampString = li.getUploadTime();
		msg.arg1 = Integer.parseInt(timeStampString.substring(0, 7));
		msg.arg2 = Integer.parseInt(timeStampString.substring(7, 15));
		msg.obj = list;

		if (remoteMessenger == null) {
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
			case protocolfromserver.getUserInfo_S:
				getUserInfo_S gs = (getUserInfo_S) msg.obj;
				String strLists = gs.getUserInfoList();
				// Gson gson = new Gson();
				if(strLists == null)
					return;
				if(strLists.length() < 20){
					return;
				}
				List<UserInfoSelectData> users = GsonInstance.getG().fromJson(
						strLists, new TypeToken<List<UserInfoSelectData>>() {
						}.getType());
				UserInfoSelectData user = users.get(0);

				initSearchUserInfomation(user);
				break;
			}
		}

	};

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	String[] strsBelongs;

	private void initSearchUserInfomation(UserInfoSelectData user) {
		// TODO Auto-generated method stub

		// ScrollView scroll = (ScrollView)
		// findViewById(R.id.z_centerInfoScrollView);
		// scroll.setVisibility(View.VISIBLE);
		LinearLayout linear = (LinearLayout) findViewById(R.id.z_centerInfoLinearLayout);
		linear.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		TextView tvTitle = (TextView) findViewById(R.id.z_centerInfoTitleTv);
		GridView gdUserTags = (GridView) findViewById(R.id.z_centerInfouserTags);
		// 取消点击时的黄色背景的效果
		gdUserTags.setSelector(new ColorDrawable(Color.TRANSPARENT));
		String strTags;

		if (user.getUserTags() != null) {
			strTags = user.getUserTags();
			if (user.getUserTags().equals("0")) {
				strsBelongs = new String[0];
			} else {
				strsBelongs = strTags.split("#");
				gdUserTags.setAdapter(adapter);
			}
		}

		TextView tvUserName = (TextView) findViewById(R.id.z_centerInfouserName);
		if (user.getUserName() != null) {
			if (user.getUserName().equals("0")) {
				tvUserName.setText("");
			} else {
				tvUserName.setText(user.getUserName());
				tvTitle.setText(UtilsTrans.getUserName(user.getUserName())
						+ "的主页");
			}
		}

		TextView tvUserBirthday = (TextView) findViewById(R.id.z_centerInfouserBirthday);
		if (user.getBirthday() != null) {
			if (user.getBirthday().equals("0")) {
				tvUserBirthday.setText("");
			} else {
				tvUserBirthday.setText(user.getBirthday());
			}
		}

		TextView tvUserHome = (TextView) findViewById(R.id.z_centerInfouserHome);
		if (user.getHome() != null) {
			if (user.getHome().equals("0")) {
				tvUserHome.setText("");
			} else {
				tvUserHome.setText(user.getHome());
			}
		}

		TextView tvUserProfession = (TextView) findViewById(R.id.z_centerInfouserProfession);
		if (user.getProfession() != null) {
			if (user.getProfession().equals("0")) {
				tvUserProfession.setText("");
			} else {
				tvUserProfession.setText(user.getProfession());
			}
		}

		TextView tvUserSchool = (TextView) findViewById(R.id.z_centerInfouserSchool);
		if (user.getSchool() != null) {
			if (user.getSchool().equals("0")) {
				tvUserSchool.setText("");
			} else {
				tvUserSchool.setText(user.getSchool());
			}
		}

		TextView tvUserDescribe = (TextView) findViewById(R.id.z_centerInfouserDescribe);
		if (user.getUserDescribe() != null) {
			if (user.getUserDescribe().equals("0")) {
				tvUserDescribe.setText("");
			} else {
				tvUserDescribe.setText(user.getUserDescribe());
			}
		}

		TextView tvUserQQ = (TextView) findViewById(R.id.z_centerInfouserQQ);
		if (user.getUserQQ() != null) {
			if (user.getUserQQ().equals("0")) {
				tvUserQQ.setText("");
			} else {
				tvUserQQ.setText(user.getUserQQ());
			}
		}

		TextView tvUserWeixin = (TextView) findViewById(R.id.z_centerInfouserWeixin);
		if (user.getUserWeiChat() != null) {
			if (user.getUserWeiChat().equals("0")) {
				tvUserWeixin.setText("");
			} else {
				tvUserWeixin.setText(user.getUserWeiChat());
			}
		}

	}

	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return strsBelongs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return strsBelongs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(CenterInfo.this);
			} else {
				tv = (TextView) convertView;
			}
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.LTGRAY);
			tv.setText(strsBelongs[position]);
			return tv;
		}

	};

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