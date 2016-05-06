/**
 * 
 */
package pri.z.show;

import pri.z.main.MainActivity;
import pri.z.mydb.SearchDistance;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pub.application.SEMapApplication;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author 祝侦科 2014-5-23
 */
public class CommonSetting extends Activity {

	public static final int MomentDisRequest = 801;
	public static final int ActivityDisRequest = 802;
	public static final int MomentDis_OK = 901;
	public static final int ActivityDis_OK = 902;
	public static final int DistanceType_Moment = 1001;
	public static final int DistanceType_Activity = 1002;

	SearchDistance myDistanceUpdate;
	boolean exitFlag = false;// 退出标志
	String TAG = "哈哈哈哈";
	TextView tvCommentDis;
	TextView tvActivityDis;
	Context mContext;
	public static Activity CommonSettingActivity;

	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_more_commomsetting);
		CommonSettingActivity = this;
		mContext = getBaseContext();
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		QueryUserRequest.qureyAllSearchDistances();

		InitListener();

		// 退出监听
		initInfoExit();
	}

	private void initView(SearchDistance myDistance) {
		// TODO Auto-generated method stub
		tvCommentDis = (TextView) findViewById(R.id.z_settingMomentDistanceTv);
		tvActivityDis = (TextView) findViewById(R.id.z_settingActivityDistanceTv);
		if (myDistance == null) {
			tvCommentDis.setText("5 千米");
			tvActivityDis.setText("5 千米");
		} else {
			tvCommentDis.setText(myDistance.DistanceMomentDis + " 千米");
			tvActivityDis.setText(myDistance.DistanceActivityDis + " 千米");
		}
	}

	public void InitListener() {
		RelativeLayout btnMomentDis = (RelativeLayout) findViewById(R.id.z_settingMomentDistanceRel);
		RelativeLayout btnActivityDis = (RelativeLayout) findViewById(R.id.z_settingActivityDistanceRel);
		RelativeLayout btnInviteFriends = (RelativeLayout) findViewById(R.id.z_settingInviteFriendsRel);

		btnMomentDis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CommonSetting.this,
						DistanceSetting.class);
				intent.putExtra("DistanceType", DistanceType_Moment);
				intent.putExtra("SearchDistance", myDistanceUpdate);
				startActivityForResult(intent, MomentDisRequest);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		btnActivityDis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CommonSetting.this,
						DistanceSetting.class);
				intent.putExtra("DistanceType", DistanceType_Activity);
				intent.putExtra("SearchDistance", myDistanceUpdate);
				startActivityForResult(intent, ActivityDisRequest);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		btnInviteFriends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = "";
				content = shareAppContent();
				Intent it = new Intent(Intent.ACTION_SEND);
				it.putExtra(Intent.EXTRA_TEXT, content);
				it.setType("text/plain");
				startActivity(Intent.createChooser(it, "选择邀请方式"));
			}
		});
	}

	public String shareAppContent() {
		String str = "Hello，我正在使用“来此party”手机社交软件，"+
				"随时随地发起活动，聚集兴趣相同的伙伴，"+
				"在现实生活中认识更多朋友，扩大交友圈，遇见精彩生活。"+
				"赶快下载和我一起去组织或参加自己感兴趣的活动吧。"+ 
				"android系统手机下载地址："+
				"http://121.40.123.240:5984/_utils/client/LetsParty.apk   "+
				"建议您在浏览器中打开链接";

		return str;
	}

	Button btnExit;

	private void initInfoExit() {
		// TODO Auto-generated method stub
		btnExit = (Button) findViewById(R.id.z_infoExitLogin);
		// 如果用户没有登录。那么按钮将不显示
		if (SEMapApplication.AccountNumber.equals("0")) {
			btnExit.setVisibility(View.GONE);
		}
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(CommonSetting.this,
						ExitFromSettings.class);
				startActivity(intent);
			}

		});
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
			case SQLiteProtocol.queryAllSearchDistances:
				SQLResponse responseAct = (SQLResponse) msg.obj;
				// if (responseAct.mark == 2)// Object == null
				// return;
				SearchDistance[] rels = (SearchDistance[]) responseAct.result;
				if (rels != null) {
					if (rels.length > 0)
						myDistanceUpdate = rels[0];
					else {
						myDistanceUpdate = new SearchDistance(1, "10", "10");

						ContentValues newValues = ContentValuesChange
								.contentInsertSearchDistance(myDistanceUpdate);
						SQLInfo sqlInfo = new SQLInfo();
						sqlInfo.p = SQLiteProtocol.insertCommonData;
						sqlInfo.SQLInfo = new InsertObject(
								ContentValuesChange.SearchDistance_Table, null,
								newValues);
						sqlSecurityThread.handleSQl(sqlInfo);
					}
				} else {
					myDistanceUpdate = new SearchDistance(1, "10", "10");

					ContentValues newValues = ContentValuesChange
							.contentInsertSearchDistance(myDistanceUpdate);
					SQLInfo sqlInfo = new SQLInfo();
					sqlInfo.p = SQLiteProtocol.insertCommonData;
					sqlInfo.SQLInfo = new InsertObject(
							ContentValuesChange.SearchDistance_Table, null,
							newValues);
					sqlSecurityThread.handleSQl(sqlInfo);
				}

				initView(myDistanceUpdate);
				break;
			}
		}
	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MomentDisRequest && resultCode == MomentDis_OK) {
			MainActivity.modifyMomentDistanceFlag = true;
			
			// 动态范围的返回值
			String commentDis = data.getIntExtra("DistanceResult", 0) + "";
			tvCommentDis.setText(commentDis + " 千米");

			MainActivity.SearchMomentDistance = Integer.valueOf(commentDis) * 1000;
			myDistanceUpdate.DistanceMomentDis = commentDis;
			SQLInfo sqlInfoUpdate = new SQLInfo();
			sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
			ContentValues newValues = ContentValuesChange
					.contentUpdateOneSearchDistance(myDistanceUpdate);
			String whereClause = ContentValuesChange.Key_SearchDistanceId
					+ "=? ";
			String[] whereArgs = new String[] { String
					.valueOf(myDistanceUpdate.DistanceId) };
			sqlInfoUpdate.SQLInfo = new UpdateObject(
					ContentValuesChange.SearchDistance_Table, newValues,
					whereClause, whereArgs);
			sqlSecurityThread.handleSQl(sqlInfoUpdate);
		}
		if (requestCode == ActivityDisRequest && resultCode == ActivityDis_OK) {
			MainActivity.modifyActivityDistanceFlag = true;
			// 活动范围的返回值
			String activityDis = data.getIntExtra("DistanceResult", 0) + "";
			tvActivityDis.setText(activityDis + " 千米");

			MainActivity.SearchActivityDistance = Integer.valueOf(activityDis) * 1000;
			myDistanceUpdate.DistanceActivityDis = activityDis;
			SQLInfo sqlInfoUpdate = new SQLInfo();
			sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
			ContentValues newValues = ContentValuesChange
					.contentUpdateOneSearchDistance(myDistanceUpdate);
			String whereClause = ContentValuesChange.Key_SearchDistanceId
					+ "=? ";
			String[] whereArgs = new String[] { String
					.valueOf(myDistanceUpdate.DistanceId) };
			sqlInfoUpdate.SQLInfo = new UpdateObject(
					ContentValuesChange.SearchDistance_Table, newValues,
					whereClause, whereArgs);
			sqlSecurityThread.handleSQl(sqlInfoUpdate);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

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
