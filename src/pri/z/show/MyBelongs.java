package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.UtilsZZK;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.changUserInfo_C;
import pub.infoclass.myserver.protocol.changUserInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MyBelongs extends Activity {
	GridView gvSelected;
	GridView gvSelect;
	Button btnComplete;
	UserInfoSelectData user;// 提交前从手机数据库督导的数据
	/** 我的标签结果码 ***/
	public static final int MyBelongs_OK = 802;// 成功
	public static final int MyBelongs_NO = 803;// 失败
	Context mContext;
	String strBelongs = "";// 我的标签：#号隔开
	List<String> listSelected = new ArrayList<String>();
	public static final String TAG = "哈哈哈哈";
	boolean sendSuccessFlag = false;
	boolean hasBack = false;//是否点击了返回
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_mybelongs);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		
		QueryUserRequest.queryUser();
		
		// 这里的是标签上的text
		strBelongs = getIntent().getStringExtra("myBelongsModify");
		if (strBelongs != null) {
			if (!strBelongs.equals("0")) {
				String[] strs = strBelongs.split(" ");
				if (strs != null) {
					if (strs.length > 0) {
						for (int index = 0; index < strs.length; index++) {
							listSelected.add(strs[index]);
						}
					}
				}
			}
		}
		// 选择好的标签布局
		gvSelected = (GridView) findViewById(R.id.z_belongsgridselected);
		gvSelected.setAdapter(adapterSeleted);
		gvSelected.setOnItemClickListener(gridSelectedListener);

		// 固有的标签布局
		gvSelect = (GridView) findViewById(R.id.z_belongsgrid);
		gvSelect.setAdapter(adapter);
		gvSelect.setOnItemClickListener(gridListener);

		btnComplete = (Button) findViewById(R.id.z_belongsComplete);
		btnComplete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listSelected.size() <= 0) {
					Toast.makeText(mContext, "请选择", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!UtilsZZK.checkNetworkState(MyBelongs.this))
					return;
				// 先将strBelongs清空
				strBelongs = "";
				for (int index = 0; index < listSelected.size(); index++) {
					if (index == 0) {
						strBelongs += listSelected.get(index);
					} else {
						strBelongs += "#" + listSelected.get(index);
					}
				}
				// 发送修改信息
				sendMyInfoNotifyMsg(strBelongs);
				setEnabledFalse();
				
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(!sendSuccessFlag){
							if(!hasBack){
								Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
								finish();
							}
						}
					}
					
				}, 10000);
			}
		});
	}
	
	/**
	 * 设置组件不可用
	 */
	public void setEnabledFalse(){
		gvSelected.setEnabled(false);
		gvSelect.setEnabled(false);
		btnComplete.setEnabled(false);
	}
	
	/**
	 * 设置组件可用
	 */
	public void setEnabledTrue(){
		gvSelected.setEnabled(true);
		gvSelect.setEnabled(true);
		btnComplete.setEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}


	public void sendMyInfoNotifyMsg(String strBelongs) {

		Message msg = Message.obtain();

		if (user == null)
			return;

		changUserInfo_C li = new changUserInfo_C();
		li.setUserID(user.getUserPhone());
		SEMapApplication.AccountNumber = li.getUserID();// /////
		li.setStandardUploadTime();
		li.Code = user.getCode();
		li.UserTags = strBelongs;// 要修改的标签

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

	private OnItemClickListener gridListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String str = strs[position];
			boolean flag = false;
			for (int index = 0; index < listSelected.size(); index++) {
				if (listSelected.get(index).equals(str)) {
					listSelected.remove(index);
					flag = true;
					break;
				}
			}
			if (!flag) {
				listSelected.add(strs[position]);
			}
			adapterSeleted.notifyDataSetChanged();
		}
	};

	private OnItemClickListener gridSelectedListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			listSelected.remove(position);
			adapterSeleted.notifyDataSetChanged();

		}
	};

	BaseAdapter adapterSeleted = new BaseAdapter() {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listSelected.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listSelected.get(position);
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
				tv = new TextView(MyBelongs.this);
			} else {
				tv = (TextView) convertView;
			}
			tv.setGravity(Gravity.CENTER);
			tv.setBackgroundColor(Color.LTGRAY);
			tv.setTextColor(Color.WHITE);
			tv.setText(listSelected.get(position));
			return tv;
		}

	};

	String[] strs = new String[] { "文艺青年", "有为青年", "白领", "学生", "IT民工", "自由职业",
			"上班族", "潜力股", "创业者", "技术宅", "小清新", "月光族", "乐活族", "愤青", "小正太",
			"小萝莉", "K歌", "果粉", "购物狂", "美食", "电影", "摄影", "旅游", "手机控", "读书",
			"动漫", "游戏", "爱狗", "爱猫", "运动", "电视剧", "桌游" };
	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return strs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return strs[position];
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
				tv = new TextView(MyBelongs.this);
			} else {
				tv = (TextView) convertView;
			}
			tv.setGravity(Gravity.CENTER);
//			tv.setBackgroundColor(Color.LTGRAY);
//			tv.setTextColor(getResources().getColor(R.color.z_color_un_mybelongsgridviewbg));
			tv.setText(strs[position]);
			return tv;
		}

	};


	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SQLiteProtocol.queryUserData:
				SQLResponse response = (SQLResponse)msg.obj;
				if(response.mark == 2)
					return;
				UserInfoSelectData[] users = (UserInfoSelectData[]) response.result;
				if(users.length <= 0)
					return;
				user = users[0];
				break;
			case protocolfromserver.changUserInfo_S:
				changUserInfo_S ls = (changUserInfo_S) msg.obj;
				String resMark = ls.getMark();
				sendSuccessFlag = true;
				Intent intent = new Intent();
				if (resMark.equals("1")) {
					user.setUserTags(strBelongs);
					
//					DBAdapter dbAdapter = new DBAdapter(mContext);
//					dbAdapter.open();
//					dbAdapter.updateOneUserInfoByID(user.getUserPhone(), user);
//					//关闭数据库连接
//					dbAdapter.close();
					
					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValues = ContentValuesChange
							.contentInsertUserInfoBase(user);
					String whereClause = ContentValuesChange.Key_UserPhone +"=? "; 
					String[] whereArgs = new String[]{user.getUserPhone()};
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.UserInfo_Table,newValues, whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);
					
					intent.putExtra("resBelongs", strBelongs);
					setResult(MyBelongs_OK, intent);
				} else {
					setResult(MyBelongs_NO, intent);
					Toast.makeText(MyBelongs.this, "提交失败", Toast.LENGTH_SHORT).show();
				}
				MyBelongs.this.finish();
				overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
				break;

			}
		}
	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			hasBack = true;
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}