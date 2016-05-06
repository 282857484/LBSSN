package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.DeleteObject;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.UtilsZZK;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.getUserInfo_S;
import pub.infoclass.myserver.protocol.loginUser_C;
import pub.infoclass.myserver.protocol.loginUser_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

public class LoginOrRegister extends Activity {

	public static final int RegisterRequestCode = 100;// 注册请求码
	public static final int REGISTER_OK = 101;// 注册成功返回码
	public static final int REGISTER_NO = 102;// 注册失败返回码
	
	public static final int FindPwdRequestCode = 200;//  找回密码请求码
	public static int FindPwd_OK = 201;// 找回密码成功返回码
	public static int FindPwd_NO = 202;// 找回密码失败返回码
	
	LineEditText tv_userid;// 用户Id输入框
	LineEditText tv_pwd;// 用户密码输入框
	String myLoginPassWord;// 保存用户登录的密码，方便添加到手机数据库
	Context mContext;
	Button btnLogin;
	Button btnRegister;
	TextView tvForgetPwd;
	ProgressBar loadingProgressBar;
	boolean loginFlag = false;// 用户是否登录完成：成功或失败。。主要是让ProgressBar消失的标志

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		setContentView(R.layout.z_loginorregister);

		initView();

		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_userid = (LineEditText) findViewById(R.id.z_login_username);
		tv_pwd = (LineEditText) findViewById(R.id.z_login_userpwd);
		btnLogin = (Button) findViewById(R.id.z_loginbtn);
		btnRegister = (Button) findViewById(R.id.z_register);
		loadingProgressBar = (ProgressBar) findViewById(R.id.z_loginingProgressBar1);
		tvForgetPwd = (TextView) findViewById(R.id.z_loginForgetPwdBtn);
		tvForgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
	}

	private void initListener() {
		// TODO Auto-generated method stub
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String name = tv_userid.getText().toString().trim();
				String pwd = tv_pwd.getText().toString().trim();

				if (name.equals(""))
					return;
				if (pwd.equals(""))
					return;
				if (!UtilsZZK.checkNetworkState(LoginOrRegister.this))
					return;
				myLoginPassWord = pwd;// 保存到变量中
				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);

				sendUserLoginMsg(name, pwd);
				setEnabledFalse();
				loadingProgressBar.setVisibility(View.VISIBLE);
				// startActivity(new
				// Intent(LoginOrRegister.this,LoadingActivity.class));
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!loginFlag) {
							loadingProgressBar.setVisibility(View.GONE);
							setEnabledTrue();
							Toast.makeText(LoginOrRegister.this, "登录失败", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}, 10000);
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginOrRegister.this,
						UserRegister.class);
				startActivityForResult(intent, RegisterRequestCode);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		tvForgetPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginOrRegister.this, FindPwd.class);
				startActivityForResult(intent, FindPwdRequestCode);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
	}

	/**
	 * 设计组件不可用
	 */
	public void setEnabledFalse() {
		tv_userid.setEnabled(false);
		tv_pwd.setEnabled(false);
		btnLogin.setEnabled(false);
		btnRegister.setEnabled(false);
		tvForgetPwd.setEnabled(false);
	}

	/**
	 * 设计组件可用
	 */
	public void setEnabledTrue() {
		tv_userid.setEnabled(true);
		tv_pwd.setEnabled(true);
		btnLogin.setEnabled(true);
		btnRegister.setEnabled(true);
		tvForgetPwd.setEnabled(true);
	}


	/**
	 * 
	 * 验证过用户登录的消息
	 * 
	 * @param name
	 * @param pwd
	 */
	public void sendUserLoginMsg(String userid, String pwd) {
		Message msg = Message.obtain();
		loginUser_C li = new loginUser_C();

		li.setUserID(userid);
		li.setStandardUploadTime();
		li.setCode(pwd);

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
			case protocolfromserver.loginUser_S:
				loginUser_S ls = (loginUser_S) msg.obj;
				if (ls.getMark().equals("1")) {
					// Intent intent = new Intent();
					sendMyInfoMsg(ls.getUserID());
				} else {
					Toast.makeText(LoginOrRegister.this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT)
							.show();
					// LoadingActivity.myLoadingActivity.finish();
					setEnabledTrue();
					loadingProgressBar.setVisibility(View.GONE);
					loginFlag = true;
					tv_pwd.setText("");
				}
				break;
			case protocolfromserver.getUserInfo_S:// 获取本地用户的数据
				getUserInfo_S us = (getUserInfo_S) msg.obj;
				// Gson g = new Gson();
				if (us.getMark().equals("2")) {
					Toast.makeText(LoginOrRegister.this, "登录失败", Toast.LENGTH_SHORT).show();
					return;
				}
				String UserInfoList = us.getUserInfoList();
				if(UserInfoList == null)
					return;
				if(UserInfoList.length() < 20){
					return;
				}
				List<UserInfoSelectData> ps = GsonInstance.getG().fromJson(
						UserInfoList,
						new TypeToken<List<UserInfoSelectData>>() {
						}.getType());
				UserInfoSelectData data = ps.get(0);
				data.setCode(myLoginPassWord);
//				 DBAdapter dbAdapter = new DBAdapter(mContext);
//				 dbAdapter.open();
//				 dbAdapter.deleteAllUserInfoBases();
//				 dbAdapter.insertUserInfoBase(data);
//				 //关闭数据库连接
//				 dbAdapter.close();
				
				SQLInfo sqlInfoDelete = new SQLInfo();
				sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
				String whereClause = null; 
				String[] whereArgs = null;
				sqlInfoDelete.SQLInfo = new DeleteObject(
						ContentValuesChange.UserInfo_Table, whereClause, whereArgs);
				sqlSecurityThread.handleSQl(sqlInfoDelete);
				
				SEMapApplication.AccountNumber = "0";
				
				ContentValues newValues = ContentValuesChange
						.contentInsertUserInfoBase(data);
				SQLInfo sqlInfo = new SQLInfo();
				sqlInfo.p = SQLiteProtocol.insertCommonData;
				sqlInfo.SQLInfo = new InsertObject(
						ContentValuesChange.UserInfo_Table, null, newValues);
				sqlSecurityThread.handleSQl(sqlInfo);
				
				Toast.makeText(LoginOrRegister.this, "登录成功", Toast.LENGTH_LONG).show();

				SEMapApplication.AccountNumber = data.getUserPhone() + "";
				SEMapApplication.LoginCode = data.getCode();
				SEMapApplication.LoginName = data.getUserName();
				SEMapApplication.LoginUser = data;

				// LoadingActivity.myLoadingActivity.finish();
				setEnabledTrue();
				loadingProgressBar.setVisibility(View.GONE);
				loginFlag = true;
				// 将数据同步到侧栏布局中
				LoginOrRegister.this.finish();
				overridePendingTransition(R.anim.z_push_myleft_in,
						R.anim.z_push_myleft_out);
				break;
			}
		}
	};

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	public void sendMyInfoMsg(String userId) {

		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();

		li.setUserID(userId);
		SEMapApplication.AccountNumber = userId;
		li.SearchUserID = userId;
		li.setStandardUploadTime();
		li.setPageIndex("0");
		li.setPageSize("10");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RegisterRequestCode:// 用户注册请求码
			if (resultCode == REGISTER_OK) {
				String resultUserId = data.getExtras().getString(
						"resultRegisterUserId");
				String resultUserCode = data.getExtras().getString(
						"resultRegisterUserCode");
				if (resultUserId != null || !resultUserId.equals("")) {
					tv_userid = (LineEditText) findViewById(R.id.z_login_username);
					tv_userid.setText(resultUserId);
				}
				if (resultUserCode != null || !resultUserCode.equals("")) {
					tv_pwd = (LineEditText) findViewById(R.id.z_login_userpwd);
					tv_pwd.setText(resultUserCode);
				}
			}
			break;
		case FindPwdRequestCode:// 用户找回密码请求码
			if (resultCode == FindPwd_OK) {
				String resultFindUserId = data.getExtras().getString(
						"resultFindPwdUserId");
				String resultFindUserCode = data.getExtras().getString(
						"resultFindPwdUserCode");
				if (resultFindUserId != null || !resultFindUserId.equals("")) {
					tv_userid = (LineEditText) findViewById(R.id.z_login_username);
					tv_userid.setText(resultFindUserId);
				}
				if (resultFindUserCode != null || !resultFindUserCode.equals("")) {
					tv_pwd = (LineEditText) findViewById(R.id.z_login_userpwd);
					tv_pwd.setText(resultFindUserCode);
				}
			}
			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
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
