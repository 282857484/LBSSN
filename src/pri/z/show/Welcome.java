package pri.z.show;

import pri.z.main.MainActivity;
import pub.application.SEMapApplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Welcome extends Activity {

	Context mContext;
	EditText editAdvice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//取消标题 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		//全屏 
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
		                  WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.z_welcome);
		mContext = getBaseContext();

		
		LinearLayout liWel =(LinearLayout)findViewById(R.id.z_welcomeLi);
		AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
		animation.setDuration(1500);
		liWel.startAnimation(animation);

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initGone();
			}

		}, 2000);

	}
	public static SharedPreferences mySharedPreferences;
	public static final String MYPREFS = "WelcomePreferences";
	public static final int mode = Activity.MODE_PRIVATE;
	public static Editor myEditor;
	public static int LetsPartyVersion = 0;
	public static int version = 0;
	private void initGone() {
		// TODO Auto-generated method stub
		version = Integer.valueOf(getResources().getString(R.string.Version));
		mySharedPreferences = getSharedPreferences(MYPREFS, mode);
		myEditor = mySharedPreferences.edit();
		
		if (mySharedPreferences != null
				&& mySharedPreferences.contains("LetsPartyVersion")) {
			LetsPartyVersion = mySharedPreferences.getInt("LetsPartyVersion",
					0);
			
//			Toast.makeText(mContext, "333LetsPartyVersion == "+LetsPartyVersion, 1).show();
		} 
		
        //判断程序第几次启动
        if (LetsPartyVersion != version ) {
//        	Intent intent = new Intent(Welcome.this, IntroduceApp.class);
        	Intent intent = new Intent(Welcome.this, MainActivity.class);
			startActivity(intent);
			Welcome.this.finish();
        } else {
        	Intent intent = new Intent(Welcome.this, MainActivity.class);
			startActivity(intent);
			Welcome.this.finish();
        }
	}


	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

			}
		}
	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}
}