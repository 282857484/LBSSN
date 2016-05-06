package pri.z.show;

import java.io.File;
import java.util.ArrayList;

import pri.z.main.MainActivity;
import pri.z.utils.VersionUpdate;
import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_C;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {

	public static final String TAG = "哈哈哈哈";
	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_more_aboutactivity);
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
		TextView tvVersion = (TextView) findViewById(R.id.z_versionTv);
		tvVersion.setText("来此party V3.1.0");
	}

	private void initListener() {
		// TODO Auto-generated method stub
		RelativeLayout relVersion = (RelativeLayout) findViewById(R.id.z_versionRel);
		relVersion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 sendVersionUpdateMsg();
//				String url = "http://192.168.1.105:80/MyApp.apk";
//				File fileAppDownload = new File(
//						MainActivity.UserFolderAppDownload);
//				if (!fileAppDownload.exists()) {
//					fileAppDownload.mkdirs();
//				}
//				String path = fileAppDownload + "";
//				new VersionUpdate().showDownLoadDialog(AboutActivity.this,url,
//						path);
			}
		});
	}

	public void sendVersionUpdateMsg() {
		Message msg = Message.obtain();
		CheckVersionAndOtherInfo_C li = new CheckVersionAndOtherInfo_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setVersion(getResources().getString(R.string.Version));

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
					new VersionUpdate().showDownLoadDialog(AboutActivity.this,
							url, path);
				} else {
					Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
				}
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