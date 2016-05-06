package pri.z.show;

import java.util.ArrayList;

import pri.z.utils.UtilsZZK;
import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.addUserFeedback_C;
import pub.infoclass.myserver.protocol.addUserFeedback_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

public class FeedBack extends Activity {

	Context mContext;
	EditText editAdvice;
	Button btn_send;
	boolean SendFlag = false;//是否发送成功的标志
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_more_feedback);
		mContext = getBaseContext();
		
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		initListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;

	}

	/**
	 * @author 祝侦科 2014-5-27
	 */
	private void initListener() {
		// TODO Auto-generated method stub
		 btn_send = (Button) findViewById(R.id.z_editfeedback_send);
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(btn_send.getWindowToken(), 0);

				editAdvice = (EditText) findViewById(R.id.z_editfeedback);
				String advice = editAdvice.getText().toString().trim();
				if (advice.equals("")) {
					Toast.makeText(mContext, "请输入", 1).show();
				} else {
					if(!UtilsZZK.checkNetworkState(FeedBack.this)){
						return;
					}else{
						sendUserRegisterMsg(advice);
						btn_send.setEnabled(false);
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(!SendFlag){
									btn_send.setEnabled(true);
								}
							}
						}, 5000);
					}
				}
			}
		});
	}

    
	public void sendUserRegisterMsg(String advice) {
		Message msg = Message.obtain();
		addUserFeedback_C li = new addUserFeedback_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setFeedback(advice);
		li.setConnect(SEMapApplication.AccountNumber);

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

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case protocolfromserver.addUserFeedback_S:
				addUserFeedback_S addusers = (addUserFeedback_S) msg.obj;
				String mark = addusers.getMark();
				if (mark.equals("1")) {
					Toast.makeText(mContext, "感谢您的建议", Toast.LENGTH_SHORT).show();
					btn_send.setEnabled(true);
					editAdvice.setText("");
					FeedBack.this.finish();
				} else {
					Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
					btn_send.setEnabled(true);
					FeedBack.this.finish();
				}
				overridePendingTransition(R.anim.z_push_myleft_in,
						R.anim.z_push_myleft_out);
				break;

			}
		}
	};
	public Messenger remoteMessenger = null;
	public Messenger myMessenger = null;

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