/**
 * 
 */
package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.z.build.BuildActivity;
import pri.z.mydb.RelationActivity;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DeleteObject;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.GetTime;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.myserver.protocol.getActivityInfo_C;
import pub.infoclass.myserver.protocol.getActivityInfo_S;
import pub.infoclass.myserver.protocol.getRelaion_C;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import pub.util.FormatTime;
import pub.util.ImageManager2;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

public class ShowMyActivity extends Activity {
	private ListView listView;

	List<RelationActivity> lists = new ArrayList<RelationActivity>();
	Context mContext;
	Button btnSync;
	RelativeLayout imgHasNoDataRel;
	RelativeLayout relLoading;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		setContentView(R.layout.z_showmyacvitity);

		QueryUserRequest.queryRelationActivitysDataInMy();

		imgHasNoDataRel = (RelativeLayout) findViewById(R.id.z_myActHasNoDataRel);
		relLoading = (RelativeLayout) findViewById(R.id.z_myActivityLoadingRel);
		btnSync = (Button) findViewById(R.id.z_showMyActivityTitleSync);
		Button btnBuildActivity = (Button) findViewById(R.id.z_myActivityBuildActivityBtn);
		btnSync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userid = SEMapApplication.AccountNumber;
				if (userid.equals("0")) {
					LoginDialog.showLoginDialog(ShowMyActivity.this);
					return;
				}
				if (!UtilsZZK.checkNetworkState(ShowMyActivity.this)) {
					return;
				}
				sendUserHostActivities(userid);
				imgHasNoDataRel.setVisibility(View.GONE);
				relLoading.setVisibility(View.VISIBLE);
				btnSync.setEnabled(false);
				btnSync.setText("正在同步");
			}
		});
		btnBuildActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShowMyActivity.this,BuildActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * 请求活动详情
	 * 
	 * @param ActivityId
	 */
	public void sendUserHostActivities(String searchId) {

		Message msg = Message.obtain();
		getActivityInfo_C li = new getActivityInfo_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.HelpPhone = searchId;
		// 查询用户所有的主办的活动
		li.setPageSize("10000");
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

	/**
	 * 展示我主办的活动
	 */
	private void showMyActivity(RelationActivity[] acts) {
		listView = (ListView) findViewById(R.id.z_myActivityListView);
		// DBAdapter dbAdapter = new DBAdapter(mContext);
		// dbAdapter.open();
		// RelationActivity[] acts =
		// dbAdapter.queryAllMyHostRelationActivitys();
		// //关闭数据库连接
		// dbAdapter.close();
		if (acts == null) {
			imgHasNoDataRel.setVisibility(View.VISIBLE);
			return;
		}

		if (acts.length <= 0) {
			imgHasNoDataRel.setVisibility(View.VISIBLE);
			return;
		}
		imgHasNoDataRel.setVisibility(View.GONE);
		if (lists != null) {
			if (lists.size() > 0)
				lists.removeAll(lists);
		}

		for (int index = 0; index < acts.length; index++) {
			lists.add(acts[index]);
		}
		if (lists.size() <= 0) {
			imgHasNoDataRel.setVisibility(View.VISIBLE);
			return;
		}
		listView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}

	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View vi = convertView;
			ViewHolder holder = null;
			final int myPosition = position;
			RelationActivity rela = lists.get(position);
			// 样式1
			if (vi == null) {

				holder = new ViewHolder();

				LayoutInflater inflater = getLayoutInflater();
				vi = inflater.inflate(R.layout.z_item_myactivity, null);
				holder.actLogo = (ImageView) vi
						.findViewById(R.id.z_itemMyActLogo);
				holder.actName = (TextView) vi
						.findViewById(R.id.z_itemMyActivityName);
				holder.actStartTime = (TextView) vi
						.findViewById(R.id.z_itemMyActivityStartTime);
				vi.setTag(holder);

			} else {
				holder = (ViewHolder) vi.getTag();
			}
			ImageManager2.from(mContext).displayImage(
					holder.actLogo,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderActivityLogoDatu + "/"
							+ rela.RelationActivityID + ".png",
					R.drawable.z_logindefault);

			holder.actName.setText(rela.RelationActivityName);
			holder.actStartTime.setText(GetTime
					.getNoYearChineseTime(rela.RelationActivityStartTime) + " 开始");
			final String activityId = rela.RelationActivityID;
			vi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ShowMyActivity.this,
							ActivityLocal.class);
					intent.putExtra("ActivityLocalActivityID", activityId);
					// 传递类型：-1主办，1参加，4关注
					intent.putExtra("ActivityLocalType", -1);
					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}

			});
			return vi;
		}

	};

	public final class ViewHolder {
		public ImageView actLogo;
		public TextView actName;
		public TextView actStartTime;

	}

	/**
	 * 用户参加和关注活动的信息
	 * 
	 * @param li
	 */
	public void sendActivityAttendOrAttentionMsg(getRelaion_C li) {
		Message msg = Message.obtain();
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

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SQLiteProtocol.queryRelationActivitysDataInMy:
				SQLResponse responseAct = (SQLResponse) msg.obj;
				// if (responseAct.mark == 2)
				// return;
				RelationActivity[] rels = (RelationActivity[]) responseAct.result;
				// if (rels.length <= 0)
				// return;
				showMyActivity(rels);
				break;

			case protocolfromserver.getActivityInfo_S:
				getActivityInfo_S gas = (getActivityInfo_S) msg.obj;
				// Log.v("哈哈哈哈", "gas.toString is : " + gas.toString());
				String mark = gas.getMark();
				relLoading.setVisibility(View.GONE);
				btnSync.setText("云同步");
				// 只能同步一次
				btnSync.setEnabled(true);
				String strsAct = gas.getActivityInfoList();
				if (strsAct == null){
					imgHasNoDataRel.setVisibility(View.VISIBLE);
					return;
				}
				if (strsAct.length() < 20) {
					imgHasNoDataRel.setVisibility(View.VISIBLE);
					return;
				}
				// Log.v("哈哈哈哈", "strsAct is : " + strsAct);
				List<ActivitySelectData> listsAct = GsonInstance.getG()
						.fromJson(strsAct,
								new TypeToken<List<ActivitySelectData>>() {
								}.getType());
				// Log.v("哈哈哈哈", "listsAct.size() is : " + listsAct.size());
				if (listsAct != null) {
					if (listsAct.size() > 0) {
						// DBAdapter dbAdapter = new DBAdapter(mContext);
						// dbAdapter.open();
						// dbAdapter.deleteUserHostRelationActivitys();
						// //关闭数据库连接
						// dbAdapter.close();

						SQLInfo sqlInfoDelete = new SQLInfo();
						sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
						String whereClause = ContentValuesChange.Key_RelationActivityStatus
								+ "=? ";
						String[] whereArgs = new String[] { "-1" };
						sqlInfoDelete.SQLInfo = new DeleteObject(
								ContentValuesChange.RelationActivity_Table,
								whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoDelete);

						for (int index = 0; index < listsAct.size(); index++) {
							ActivitySelectData act = listsAct.get(index);
							String noeTime = FormatTime.getFormatTime();
							String StartNotifyFlag = "2";
							String EndNotifyFlag = "2";
							if (act.getActivityStartTime().compareTo(noeTime) > 0) {
								StartNotifyFlag = "1";
							}
							if (act.getActivityEndTime().compareTo(noeTime) > 0) {
								EndNotifyFlag = "1";
							}
							RelationActivity re = new RelationActivity(1,
									act.getBuildActivityUserID() + "",
									act.getActivityID() + "",
									act.getActivityName(),
									act.getBuildActivityUserID() + "",
									act.getActivityStartTime(),
									act.getActivityEndTime(), StartNotifyFlag,
									EndNotifyFlag, "-1");

							// dbAdapter.insertRelationActivity(re);
							ContentValues newValues = ContentValuesChange
									.contentInsertRelationActivity(re);
							SQLInfo sqlInfo = new SQLInfo();
							sqlInfo.p = SQLiteProtocol.insertCommonData;
							sqlInfo.SQLInfo = new InsertObject(
									ContentValuesChange.RelationActivity_Table,
									null, newValues);
							sqlSecurityThread.handleSQl(sqlInfo);
						}

						QueryUserRequest.queryRelationActivitysDataInMy();
					}
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
			overridePendingTransition(R.anim.z_push_myleft_in,
					R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}
