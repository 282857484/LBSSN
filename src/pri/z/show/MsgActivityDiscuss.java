package pri.z.show;

import java.util.ArrayList;

import pri.z.utils.GetTime;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.myserver.protocol.addActivityDiscuss_C;
import pub.infoclass.myserver.protocol.addActivityDiscuss_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.util.ImageManager2;
import pub.util.ImageManager3;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MsgActivityDiscuss extends Activity {

	Context mContext;
	EditText editAdvice;
	ActivityDiscussSelectData actDiscuss;// 传递过来的评论类信息

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_inmsg_activitydiscuss);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		
		initView();
		initListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}

	private void initView() {
		// TODO Auto-generated method stub
		actDiscuss = (ActivityDiscussSelectData) getIntent().getExtras()
				.getSerializable("MsgActivityDiscussDetail");

		TextView tvUserName = (TextView) findViewById(R.id.z_inMsgActityDiscussUserName);
		TextView tvTime = (TextView) findViewById(R.id.z_inMsgActityDiscussTime);
		TextView tvDiscussMoment = (TextView) findViewById(R.id.z_inMsgActityDiscussContent);
		TextView tvActName = (TextView) findViewById(R.id.z_inMsgActityDiscussActName);
		RelativeLayout relItem = (RelativeLayout) findViewById(R.id.z_inMsgActDisItemRel);
		
		ImageView imgUserHead = (ImageView)findViewById(R.id.z_inMsgActDisUserHead);
		ImageView imgLogo = (ImageView)findViewById(R.id.z_inMsgActDisLogo);
		
		ImageManager3.from(mContext).displayImage(
				imgUserHead,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderUserHeadXiaotu + "/"
						+ actDiscuss.getUserID() + ".png",
				R.drawable.z_logindefault);
		ImageManager2.from(mContext).displayImage(
				imgLogo,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
				+ "/" + addressInfo.visitFolderActivityLogoXiaotu + "/"
				+ actDiscuss.getActivityID() + ".png",
				R.drawable.z_logindefault);
		
		tvUserName.setText(actDiscuss.getUserName());
		tvTime.setText(GetTime.getNoYearTime(actDiscuss.getUploadTime()));
		tvDiscussMoment.setText(actDiscuss.getDiscussContent());
		tvActName.setText(actDiscuss.getActivityName());
		
		final String activityId = actDiscuss.getActivityID()+"";
		relItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MsgActivityDiscuss.this,
						ActivityDetail.class);
				intent.putExtra("ActivityDetailActivityID", activityId);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
	}

	/**
	 * @author 祝侦科 2014-5-27
	 */
	private void initListener() {
		// TODO Auto-generated method stub
		Button btn_send = (Button) findViewById(R.id.z_inMsgActityDiscussReplySendBtn);
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				editAdvice = (EditText) findViewById(R.id.z_inMsgActityDiscussReplyEdit);
				String advice = editAdvice.getText().toString().trim();
				if (advice.equals("")) {
					Toast.makeText(mContext, "请输入", Toast.LENGTH_LONG).show();
				} else {
					sendUserRegisterMsg(advice);
				}
			}
		});
	}

	public void sendUserRegisterMsg(String comment) {
		Message msg = Message.obtain();
		addActivityDiscuss_C li = new addActivityDiscuss_C();
		li.setActivityID(actDiscuss.getActivityID() + "");
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.ActivityName = actDiscuss.getActivityName();
		li.UserName = SEMapApplication.LoginName;
		li.ThisUserID = actDiscuss.getUserID() + "";
		li.DiscussContent = comment;
		li.PointDiscussID = actDiscuss.getActivityID() + "";
		li.Photo = "0";

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

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case protocolfromserver.addActivityDiscuss_S:
				addActivityDiscuss_S addusers = (addActivityDiscuss_S) msg.obj;
				String mark = addusers.getMark();
				if (mark.equals("1")) {
					Toast.makeText(mContext, "回复成功", Toast.LENGTH_SHORT).show();
					MsgActivityDiscuss.this.finish();
					overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
				} else {
					Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
				}
				break;

			}
		}
	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}