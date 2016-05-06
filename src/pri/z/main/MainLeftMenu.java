package pri.z.main;

import java.io.File;
import java.util.ArrayList;

import pri.z.game.GameMenu;
import pri.z.show.CarePeople;
import pri.z.show.CommonSetting;
import pri.z.show.LoginDialog;
import pri.z.show.LoginOrRegister;
import pri.z.show.MyMomentCenter;
import pri.z.show.R;
import pri.z.show.ShowMyActivity;
import pri.z.show.ShowMyAttendActivity;
import pri.z.show.ShowMyAttentionActivity;
import pri.z.show.ShowMyInfo;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.UtilsTrans;
import pri.z.utils.VersionUpdate;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
import pub.infoclass.myserver.protocol.changUserInfo_C;
import pub.infoclass.myserver.protocol.changUserInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.util.ImageManager3;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainLeftMenu extends Fragment {
	UserInfoSelectData userInfoData;
	Context mContext;

	public ImageView imgHead;

	// 编辑的类型进行发送到服务器editFlag
	public static final int editFlagUserName = 123;
	public static final int editFlagUserDesc = 125;
	public static int editFlagSubmit = 0;// 用户当前修改的内容：签名/昵称
	TextView editUserName;
	EditText editUserDesc;
	TextView btnUserInfo;
	// 随时记录下用户编辑的数据
	String myUserName = "";
	String myUserDesc = "";
	ImageView imgSubmitDesc;
	public static Context context;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mContext = getActivity().getBaseContext();
		context = getActivity();
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

	}

	View viewabc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.z_mainleftmenu, null);
		viewabc = view;
		QueryUserRequest.queryUser();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
		remoteMessenger = SEMapApplication.serviceMessenger;

		QueryUserRequest.queryUserInMainLeft();
	}

	private void initListener(View view) {
		// 设置
		RelativeLayout relSetting = (RelativeLayout) view
				.findViewById(R.id.z_mainLeftMenuSettingRel);
		relSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent5 = new Intent(getActivity(), CommonSetting.class);
				startActivity(intent5);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		/*** 主页 *****/
		RelativeLayout btnPersonPage = (RelativeLayout) view
				.findViewById(R.id.z_leftPersonPageRel);
		btnPersonPage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userid = SEMapApplication.AccountNumber;
				if (userid.equals("0")) {
					LoginDialog.showLoginDialog(getActivity());
				} else {
					Intent intent = new Intent(getActivity(),
							MyMomentCenter.class);
					intent.putExtra("momentCenterUserId", userid);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.z_push_left_in, R.anim.z_push_left_out);
				}
			}
		});

		/*** 主办 *****/
		RelativeLayout relMyActitity = (RelativeLayout) view
				.findViewById(R.id.z_leftMyActivityRel);
		relMyActitity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), ShowMyActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		/*** 参加 *****/
		RelativeLayout relAttendAct = (RelativeLayout) view
				.findViewById(R.id.z_leftAttendActivitiesRel);
		relAttendAct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						ShowMyAttendActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		/*** 关注 *****/
		RelativeLayout relAttentionAct = (RelativeLayout) view
				.findViewById(R.id.z_leftAttentionActivitiesRel);
		relAttentionAct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(),
						ShowMyAttentionActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
//		/*** 关注人 *****/
//		RelativeLayout relCarePeople = (RelativeLayout) view
//				.findViewById(R.id.z_leftMyCarePeopleRel);
//		relCarePeople.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				Intent intent = new Intent(getActivity(), CarePeople.class);
//				startActivity(intent);
//				getActivity().overridePendingTransition(R.anim.z_push_left_in,
//						R.anim.z_push_left_out);
//			}
//		});
	}

	private void initView(final View view) {
		initListener(view);
		// TODO Auto-generated method stub
		imgHead = (ImageView) view.findViewById(R.id.z_mainLeftMenuUserHead);
		String userid = SEMapApplication.AccountNumber;
		if (userid.equals("0")) {// 如果没有登录的话就显示默认的头像
			// Bitmap bmBitmap = UtilsZZK.toRoundBitmap(BitmapFactory
			// .decodeResource(getResources(), R.drawable.z_logindefault));
//			imgHead.setBackgroundResource(R.drawable.z_leftmainheadbg);
		} else if (userInfoData != null) {
			// 如果性别为空
			if (userInfoData.getUserSex() == null) {
				ImageManager3.from(mContext).displayNewImage(
						imgHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadDatu + "/"
								+ userid + ".png", R.drawable.z_leftmainheadbg);
			}
			// 如果为男
			else if (userInfoData.getUserSex().equals("1")) {

				// 为了使用户在注册没有选择头像时先显示默认头像
//				imgHead.setBackgroundResource(R.drawable.z_leftmainheadbg);

				ImageManager3.from(mContext).displayNewImage(
						imgHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadDatu + "/"
								+ userid + ".png", R.drawable.z_leftmainheadbg);
			}
			// 如果为女
			else if (userInfoData.getUserSex().equals("2")) {

				// 为了使用户在注册没有选择头像时先显示默认头像
//				imgHead.setBackgroundResource(R.drawable.z_leftmainheadbg);

				ImageManager3.from(mContext).displayNewImage(
						imgHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadDatu + "/"
								+ userid + ".png", R.drawable.z_leftmainheadbg);
			}
			// 其他情况
			else {
				ImageManager3.from(mContext).displayNewImage(
						imgHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadDatu + "/"
								+ userid + ".png", R.drawable.z_leftmainheadbg);
			}
		}

		// 昵称
		editUserName = (TextView) view
				.findViewById(R.id.z_mainLeftMenuUserName);
		if (userInfoData != null) {
			editUserName.setText(UtilsTrans.getUserName(userInfoData
					.getUserName()));
			editUserName.setClickable(false);
		} else {
			editUserName.setText("您尚未登录");
		}

		// 个性签名
		editUserDesc = (EditText) view
				.findViewById(R.id.z_mainLeftMenuUserDesc);
		editUserDesc.setVisibility(View.VISIBLE);
		imgSubmitDesc = (ImageView) view
				.findViewById(R.id.z_mainLeftMenuUserDescSubmit);
		if (userInfoData != null) {
			if (userInfoData.getUserDescribe() != null) {
				editUserDesc.setText(UtilsTrans.getUserDescCribe(userInfoData
						.getUserDescribe()));
			} else {
				editUserDesc.setVisibility(View.GONE);
			}
		} else {
			editUserDesc.setVisibility(View.GONE);
		}
		editUserDesc.addTextChangedListener(UserDescChangerListener);
		editUserDesc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgSubmitDesc.setVisibility(View.VISIBLE);
			}
		});
		imgSubmitDesc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(imgSubmitDesc.getWindowToken(), 0);

				String strUserDesc = editUserDesc.getText().toString().trim();
				sendMyInfoNotifyMsg(strUserDesc, editFlagUserDesc);
				editFlagSubmit = editFlagUserDesc;
				myUserDesc = strUserDesc;
				imgSubmitDesc.setVisibility(View.GONE);
			}
		});

		// 个人资料
		btnUserInfo = (TextView) view
				.findViewById(R.id.z_mainLeftMenuUserInfoBtn);
		RelativeLayout relUserInfo = (RelativeLayout) view
				.findViewById(R.id.z_mainLeftMenuUserInfoRel);
		if (userInfoData == null) {
			// btnUserInfo.setVisibility(View.GONE);
			btnUserInfo.setText("登录");
			btnUserInfo.setTextColor(Color.RED);
			relUserInfo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// LoginDialog.showLoginDialog(getActivity());
					Intent intentLogin = new Intent(getActivity(),
							LoginOrRegister.class);
					startActivity(intentLogin);
					getActivity().overridePendingTransition(
							R.anim.z_push_left_in, R.anim.z_push_left_out);
				}
			});
		} else {
			btnUserInfo.setText("我的资料");
			btnUserInfo.setTextColor(Color.BLACK);
			relUserInfo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent4 = new Intent(getActivity(), ShowMyInfo.class);
					startActivity(intent4);
					getActivity().overridePendingTransition(
							R.anim.z_push_left_in, R.anim.z_push_left_out);
				}
			});
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	// 用户签名改变的监听
	TextWatcher UserDescChangerListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (userInfoData != null) {
				if (userInfoData.getUserDescribe() != null) {
					if (!userInfoData.getUserDescribe().equals(
							editUserDesc.getText().toString().trim())) {
						imgSubmitDesc.setVisibility(View.VISIBLE);
					}
				}
			}

		}
	};

	public void sendMyInfoNotifyMsg(String strWord, int editFlag) {

		Message msg = Message.obtain();
		if (userInfoData == null)
			return;
		changUserInfo_C li = new changUserInfo_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.UserName = userInfoData.getUserName();
		li.UserDescribe = userInfoData.getUserDescribe();
		li.Code = userInfoData.getCode();

		switch (editFlag) {
		case editFlagUserName:
			li.UserName = strWord;
			break;
		case editFlagUserDesc:
			li.UserDescribe = strWord;
			break;
		}
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
			case protocolfromserver.CheckVersionAndOtherInfo_S:
				CheckVersionAndOtherInfo_S checkVersion = (CheckVersionAndOtherInfo_S) msg.obj;
				String markCh = checkVersion.getMark();
				if (markCh.equals("1")) {
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
					new VersionUpdate().showDownLoadDialog(context, url, path);
				}
				break;
			case SQLiteProtocol.queryUserData:
				SQLResponse responseF = (SQLResponse) msg.obj;
				// if(response.mark == 2)
				// return;
				UserInfoSelectData[] usersF = (UserInfoSelectData[]) responseF.result;
				if (usersF != null)
					if (usersF.length > 0) {
						userInfoData = usersF[0];
						MainActivity.initSEMapApplication(userInfoData);
					}

				initView(viewabc);
				break;
			case SQLiteProtocol.queryUserInMainLeft:
				SQLResponse responseS = (SQLResponse) msg.obj;
				// if(response.mark == 2)
				// return;
				UserInfoSelectData[] usersS = (UserInfoSelectData[]) responseS.result;
				if (usersS != null)
					if (usersS.length > 0) {
						userInfoData = usersS[0];
						MainActivity.initSEMapApplication(userInfoData);
					}

				initView(viewabc);
				break;
			case protocolfromserver.changUserInfo_S:
				changUserInfo_S ls = (changUserInfo_S) msg.obj;
				String mark = ls.getMark();
				if (mark.equals("1")) {
					switch (editFlagSubmit) {
					case editFlagUserName:
						userInfoData.setUserName(myUserName);
						break;
					case editFlagUserDesc:
						editUserDesc.setVisibility(View.VISIBLE);
						userInfoData.setUserDescribe(myUserDesc);
						break;
					}
					Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();

					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter.updateOneUserInfoByID(userInfoData.getUserPhone(),
					// userInfoData);
					// //关闭数据库连接
					// dbAdapter.close();

					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValues = ContentValuesChange
							.contentInsertUserInfoBase(userInfoData);
					String whereClause = ContentValuesChange.Key_UserPhone
							+ "=? ";
					String[] whereArgs = new String[] { userInfoData
							.getUserPhone() };
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.UserInfo_Table, newValues,
							whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);
				} else {
					Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
				}

				break;
			}
		}
	};

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public static Messenger myMessenger = null;

}
