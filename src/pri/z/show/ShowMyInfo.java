package pri.z.show;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import pri.z.main.MainActivity;
import pri.z.selectphoto.Bimp;
import pri.z.selectphoto.FileSelectUtils;
import pri.z.selectphoto.TestPicActivityNow;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.changUserInfo_C;
import pub.infoclass.myserver.protocol.changUserInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.util.Client2;
import pub.util.FormatTime;
import pub.util.ImageManager3;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.InputFilter;
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
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowMyInfo extends Activity {

	// 头像选择的变量
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int ALBUMREQUEST = 112;// 结果
	public static int ImageSelect_OK = 102;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	ImageView image_head;
	Context mContext;
	/** 用户修改具体 ***/
	public static final int UserNameFlag = 101;
	public static final int UserSchoolFlag = 102;
	public static final int UserBirthdayFlag = 103;
	public static final int UserQQFlag = 104;
	public static final int UserWeixinFlag = 105;
	public static final int UserProfessionFlag = 106;
	public static final int UserHomeFlag = 107;
	public static int UserSubmitFlag = 0;// 用来记录用户提交项
	/** 我的标签请求码和结果码 ***/
	public static final int MyBelongsRequest = 801;
	public static final int MyBelongs_OK = 802;// 成功
	public static final int MyBelongs_NO = 803;// 失败

	// 用户资料修改后重新显示在组件上的请求码和结果码
	public static final int ShowMyInfoRequest = 501;
	public static final int ShowMyInfo_OK = 502;

	UserInfoSelectData userData;// 已经登录的用户的信息数据类
	String dialogEditData;// 弹出的对话框编辑后的字符串
	String dialogBirthday;// 保存对话框后的生日
	String[] strsBelongs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		setContentView(R.layout.z_myinfo);

		QueryUserRequest.queryUser();
		initHeadView();

	}

	TextView tvUserName;
	TextView tvUserSchool;
	TextView tvUserBirthday;
	TextView tvUserQQ;
	TextView tvUserProfession;
	TextView tvUserWeixin;
	TextView tvUserHome;
	GridView tvUserTags;
	String strTags;

	private void initView() {
		if (userData == null) {
			return;
		}

		tvUserName = (TextView) findViewById(R.id.z_userName);
		if (userData.getUserName() != null) {
			if (userData.getUserName().equals("0")) {
				tvUserName.setText("赶快来个么么哒昵称");
			} else {
				tvUserName.setText(userData.getUserName());
			}
		}

		RelativeLayout relUserName = (RelativeLayout) findViewById(R.id.z_userNameRel);
		relUserName.setOnClickListener(new EditUserListener(UserNameFlag));

		tvUserSchool = (TextView) findViewById(R.id.z_userSchool);
		if (userData.getSchool() != null) {
			if (userData.getSchool().equals("0")) {
				tvUserSchool.setText("");
			} else {
				tvUserSchool.setText(userData.getSchool());
			}
		}

		RelativeLayout relUserSchool = (RelativeLayout) findViewById(R.id.z_userSchoolRel);
		relUserSchool.setOnClickListener(new EditUserListener(UserSchoolFlag));

		tvUserBirthday = (TextView) findViewById(R.id.z_userBirthday);
		if (userData.getBirthday() != null) {
			if (userData.getBirthday().equals("0")) {
				tvUserBirthday.setText("");
			} else {
				tvUserBirthday.setText(userData.getBirthday());
			}
		}

		RelativeLayout relUserBirthday = (RelativeLayout) findViewById(R.id.z_userBirthdayRel);
		relUserBirthday.setOnClickListener(birthdaySelectListener);

		tvUserQQ = (TextView) findViewById(R.id.z_userQQ);
		if (userData.getUserQQ() != null) {
			if (userData.getUserQQ().equals("0")) {
				tvUserQQ.setText("留个QQ方便联系吧");
			} else {
				tvUserQQ.setText(userData.getUserQQ());
			}
		}

		RelativeLayout relUserQQ = (RelativeLayout) findViewById(R.id.z_userQQRel);
		relUserQQ.setOnClickListener(new EditUserListener(UserQQFlag));

		tvUserProfession = (TextView) findViewById(R.id.z_userProfession);
		if (userData.getProfession() != null) {
			if (userData.getProfession().equals("0")) {
				tvUserProfession.setText("");
			} else {
				tvUserProfession.setText(userData.getProfession());
			}
		}

		RelativeLayout relUserProession = (RelativeLayout) findViewById(R.id.z_userProfessionRel);
		relUserProession.setOnClickListener(new EditUserListener(
				UserProfessionFlag));

		tvUserHome = (TextView) findViewById(R.id.z_userHome);
		if (userData.getHome() != null) {
			if (userData.getHome().equals("0")) {
				tvUserHome.setText("");
			} else {
				tvUserHome.setText(userData.getHome());
			}
		}

		RelativeLayout relUserHome = (RelativeLayout) findViewById(R.id.z_userHomeRel);
		relUserHome.setOnClickListener(new EditUserListener(UserHomeFlag));

		tvUserWeixin = (TextView) findViewById(R.id.z_userWeixin);
		if (userData.getUserWeiChat() != null) {
			if (userData.getUserWeiChat().equals("0")) {
				tvUserWeixin.setText("微信号不能少哦");
			} else {
				tvUserWeixin.setText(userData.getUserWeiChat());
			}
		}

		RelativeLayout relUserWeixin = (RelativeLayout) findViewById(R.id.z_userWeixinRel);
		relUserWeixin.setOnClickListener(new EditUserListener(UserWeixinFlag));

		tvUserTags = (GridView) findViewById(R.id.z_userTags);
		// 取消点击时的黄色背景的效果
		tvUserTags.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (userData.getUserTags() != null) {
			strTags = userData.getUserTags();
			if (strTags.equals("0")) {
				strsBelongs = new String[0];
			} else {
				strsBelongs = strTags.split("#");
				tvUserTags.setAdapter(adapter);
			}
		}

		RelativeLayout relUserTags = (RelativeLayout) findViewById(R.id.z_userTagsRel);
		relUserTags.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ShowMyInfo.this, MyBelongs.class);
				// 传递过去的是原来从服务器上面读取到的
				String UserNormalTags = getNormalTags(strTags);
				intent.putExtra("myBelongsModify", UserNormalTags);
				startActivityForResult(intent, MyBelongsRequest);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		tvUserTags.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ShowMyInfo.this, MyBelongs.class);
				// 传递过去的是原来从服务器上面读取到的
				String UserNormalTags = getNormalTags(strTags);
				intent.putExtra("myBelongsModify", UserNormalTags);
				startActivityForResult(intent, MyBelongsRequest);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

	}

	OnClickListener birthdaySelectListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showBirthdayDialog();
		}
	};

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
				tv = new TextView(ShowMyInfo.this);
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

	private void showBirthdayDialog() {
		UserSubmitFlag = UserBirthdayFlag;
		String bir = tvUserBirthday.getText().toString();

		LinearLayout wayplanForm = (LinearLayout) getLayoutInflater().inflate(
				R.layout.z_dialog_birthday, null);
		Button btnSure = (Button) wayplanForm
				.findViewById(R.id.z_dialogSelectBirthdayOK);
		Button btnCansel = (Button) wayplanForm
				.findViewById(R.id.z_dialogSelectBirthdayNO);
		final DatePicker dataPicker = (DatePicker) wayplanForm
				.findViewById(R.id.z_dialogBirthdayDatePicker);
		// 设置初始值
		if (bir.length() >= 8) {
			// int first_ = bir.indexOf("-");
			int last_ = bir.lastIndexOf("-");
			String YEAR = bir.substring(0, 4);
			String MONTH = bir.substring(5, last_);
			String DAY = bir.substring(last_ + 1);
			// dataPicker.setY(y);
			dataPicker.init(Integer.valueOf(YEAR), Integer.valueOf(MONTH) - 1,
					Integer.valueOf(DAY), new OnDateChangedListener() {
						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
						}
					});
		}

		final MyDialog dialog = new MyDialog(ShowMyInfo.this,
				R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int day = dataPicker.getDayOfMonth();
				int month = dataPicker.getMonth() + 1;
				int year = dataPicker.getYear();

				String strDay = String.valueOf(day);
				String strMonth = String.valueOf(month);

				if (day < 10) {
					strDay = "0" + day;
				}
				if (month < 10) {
					strMonth = "0" + month;
				}
				int strYear = dataPicker.getYear();
				String strBir = strYear + "" + strMonth + "" + strDay;
				String timeNow = FormatTime.getFormatTime();
				String strNow = timeNow.substring(0, 8);
				int num = strBir.compareTo(strNow);
				if (num > 0) {
					Toast.makeText(mContext, "生日填写有误", Toast.LENGTH_LONG)
							.show();
					return;
				}

				dialogBirthday = strYear + "-" + strMonth + "-" + strDay;
				sendMyInfoNotifyMsg(dialogBirthday, UserBirthdayFlag);
				dialog.dismiss();
			}
		});
		btnCansel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	/**
	 * 将用户标签 aa#bb##cc 转化为aa bb cc的格式
	 * 
	 * @param strTags
	 * @return
	 */
	private String getNormalTags(String strTags) {
		String str = strTags.replace("#", " ");
		return str;
	}

	private void initHeadView() {
		// 头像
		image_head = (ImageView) findViewById(R.id.z_userheadmodify);

		// //得到网络状态
		// boolean flag = checkNetworkState();
		// if(flag){
		// ImageManager3.from(mContext).displayNewImage(
		// image_head,
		// "http://" + addressInfo.localIP + ":" + addressInfo.visitPort
		// + "/" + addressInfo.visitFolderUserHeadXiaotu + "/"
		// + SEMapApplication.AccountNumber + ".png",
		// R.drawable.z_logindefault);
		// }else{
		ImageManager3.from(mContext).displayNewImage(
				image_head,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderUserHeadDatu + "/"
						+ SEMapApplication.AccountNumber + ".png",
				R.drawable.z_logindefault);
		// }

		image_head.setOnClickListener(new headClickListener());
	}

	/**
	 * 检测网络是否连接
	 * 
	 * @return
	 */
	private boolean checkNetworkState() {
		boolean flag = false;
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		return flag;
	}

	private class EditUserListener implements OnClickListener {
		int editUserFlag;

		public EditUserListener(int editUserFlag) {
			this.editUserFlag = editUserFlag;
		}

		public String getDialogTitle() {
			String dialogTitle = "";
			switch (editUserFlag) {
			case UserNameFlag:
				dialogTitle = "昵称";
				break;
			case UserSchoolFlag:
				dialogTitle = "学校";
				break;
			case UserBirthdayFlag:
				dialogTitle = "生日";
				break;
			case UserQQFlag:
				dialogTitle = "Q  Q";
				break;
			case UserWeixinFlag:
				dialogTitle = "微信号";
				break;
			case UserProfessionFlag:
				dialogTitle = "职业";
				break;
			case UserHomeFlag:
				dialogTitle = "家乡";
				break;

			default:
				dialogTitle = "请输入";
			}
			return dialogTitle;
		}

		public String getDialogData() {
			String dialogData = "";
			switch (editUserFlag) {
			case UserNameFlag:
				dialogData = userData.getUserName();
				if (userData.getUserName() == null
						|| userData.getUserName().equals("0")) {
					dialogData = "";
				}
				break;
			case UserSchoolFlag:
				dialogData = tvUserSchool.getText().toString();
				if (userData.getSchool() == null
						|| userData.getSchool().equals("0")) {
					dialogData = "";
				}
				break;
			case UserBirthdayFlag:
				dialogData = userData.getBirthday();
				if (userData.getBirthday() == null
						|| userData.getBirthday().equals("0")) {
					dialogData = "";
				}
				break;
			case UserQQFlag:
				dialogData = userData.getUserQQ();
				if (userData.getUserQQ() == null
						|| userData.getUserQQ().equals("0")) {
					dialogData = "";
				}
				break;
			case UserWeixinFlag:
				dialogData = userData.getUserWeiChat();
				if (userData.getUserWeiChat() == null
						|| userData.getUserWeiChat().equals("0")) {
					dialogData = "";
				}
				break;
			case UserProfessionFlag:
				dialogData = userData.getProfession();
				if (userData.getProfession() == null
						|| userData.getProfession().equals("0")) {
					dialogData = "";
				}
				break;
			case UserHomeFlag:
				dialogData = userData.getHome();
				if (userData.getHome() == null
						|| userData.getHome().equals("0")) {
					dialogData = "";
				}
				break;
			default:
				dialogData = "";
			}
			return dialogData;
		}

		public int getEditTextMaxLLength() {
			int maxLength = 20;
			switch (editUserFlag) {
			case UserNameFlag:
				maxLength = 20;
				break;
			case UserSchoolFlag:
				maxLength = 32;
				break;
			case UserBirthdayFlag:
				maxLength = 32;
				break;
			case UserQQFlag:
				maxLength = 18;
				break;
			case UserWeixinFlag:
				maxLength = 20;
				break;
			case UserProfessionFlag:
				maxLength = 32;
				break;
			case UserHomeFlag:
				maxLength = 32;
				break;

			default:
				maxLength = 20;
			}
			return maxLength;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			final LinearLayout wayplanForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.z_dialog_modifyinfo, null);
			TextView tvItem = (TextView) wayplanForm
					.findViewById(R.id.z_myinfoModifyItem);
			final EditText editData = (EditText) wayplanForm
					.findViewById(R.id.z_myinfoModifyExitText);
			Button btnSure = (Button) wayplanForm
					.findViewById(R.id.z_myinfoModifyOK);
			Button btnCansel = (Button) wayplanForm
					.findViewById(R.id.z_myinfoModifyNO);
			final MyDialog dialog = new MyDialog(ShowMyInfo.this,
					R.style.z_myDialog);
			dialog.setContentView(wayplanForm);
			dialog.show();

			tvItem.setText(getDialogTitle());
			editData.setText(getDialogData());
			int MaxLength = getEditTextMaxLLength();
			// editData.setMaxEms(1);
			editData.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					MaxLength) });
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					dialogEditData = editData.getText().toString().trim();
					if (dialogEditData.equals("")) {
						Toast.makeText(ShowMyInfo.this, "请输入",
								Toast.LENGTH_LONG).show();
						return;
					}
					switch (editUserFlag) {
					case UserNameFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserNameFlag);
						break;
					case UserSchoolFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserSchoolFlag);
						break;
					case UserBirthdayFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserBirthdayFlag);
						break;
					case UserQQFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserQQFlag);
						break;
					case UserWeixinFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserWeixinFlag);
						break;
					case UserProfessionFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserProfessionFlag);
						break;
					case UserHomeFlag:
						sendMyInfoNotifyMsg(dialogEditData, UserHomeFlag);
						break;
					}

					dialog.dismiss();
				}
			});
			btnCansel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}

			});
		}
	}

	public void sendMyInfoNotifyMsg(String strWord, int editFlag) {

		Message msg = Message.obtain();
		if (userData == null)
			return;

		changUserInfo_C li = new changUserInfo_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.UserName = userData.getUserName();
		li.Code = userData.getCode();
		li.UserPhone = userData.getUserPhone();
		li.UserJoinActivity = userData.getUserJoinActivity();
		li.UserAttentionClass = userData.getUserAttentionClass();
		li.UserQQ = userData.getUserQQ();
		li.UserWeiChat = userData.getUserWeiChat();
		li.UserTags = userData.getUserTags();
		li.UserClass = userData.getUserClass();
		li.UserDescribe = userData.getUserDescribe();
		li.UserLevel = userData.getUserLevel();
		li.UserLogo = userData.getUserLogo();

		switch (editFlag) {
		case UserNameFlag:
			li.UserName = strWord;
			UserSubmitFlag = UserNameFlag;
			break;
		case UserSchoolFlag:
			li.School = strWord;
			UserSubmitFlag = UserSchoolFlag;
			break;
		case UserBirthdayFlag:
			li.Birthday = strWord;
			UserSubmitFlag = UserBirthdayFlag;
			break;
		case UserQQFlag:
			li.UserQQ = strWord;
			UserSubmitFlag = UserQQFlag;
			break;
		case UserWeixinFlag:
			li.UserWeiChat = strWord;
			UserSubmitFlag = UserWeixinFlag;
			break;
		case UserProfessionFlag:
			li.Profession = strWord;
			UserSubmitFlag = UserProfessionFlag;
			break;
		case UserHomeFlag:
			li.Home = strWord;
			UserSubmitFlag = UserHomeFlag;
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
			// 得到手机数据库查询结果
			case SQLiteProtocol.queryUserData:
				SQLResponse response = (SQLResponse) msg.obj;
				if (response.mark == 2)
					return;
				UserInfoSelectData[] users = (UserInfoSelectData[]) response.result;
				if (users.length <= 0)
					return;
				userData = users[0];
				initView();
				break;
			case protocolfromserver.changUserInfo_S:
				changUserInfo_S ls = (changUserInfo_S) msg.obj;
				String resMark = ls.getMark();
				Intent intent = new Intent();
				if (resMark.equals("1")) {

					switch (UserSubmitFlag) {
					case UserNameFlag:
						userData.setUserName(dialogEditData);
						tvUserName.setText(dialogEditData);
						break;
					case UserSchoolFlag:
						userData.setSchool(dialogEditData);
						tvUserSchool.setText(dialogEditData);
						break;
					case UserBirthdayFlag:
						userData.setBirthday(dialogBirthday);
						tvUserBirthday.setText(dialogBirthday);
						break;
					case UserQQFlag:
						userData.setUserQQ(dialogEditData);
						tvUserQQ.setText(dialogEditData);
						break;
					case UserWeixinFlag:
						userData.setUserWeiChat(dialogEditData);
						tvUserWeixin.setText(dialogEditData);
						break;
					case UserProfessionFlag:
						userData.setProfession(dialogEditData);
						tvUserProfession.setText(dialogEditData);
						break;
					case UserHomeFlag:
						userData.setHome(dialogEditData);
						tvUserHome.setText(dialogEditData);
						break;
					}

					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter.updateOneUserInfoByID(userData.getUserPhone(),
					// userData);
					// //关闭数据库连接
					// dbAdapter.close();

					// 保存最新的用户信息到变量
					SEMapApplication.LoginUser = userData;

					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValues = ContentValuesChange
							.contentInsertUserInfoBase(userData);
					String whereClause = ContentValuesChange.Key_UserPhone
							+ "=? ";
					String[] whereArgs = new String[] { userData.getUserPhone() };
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.UserInfo_Table, newValues,
							whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);

				} else {
					Toast.makeText(ShowMyInfo.this, "修改失败", Toast.LENGTH_SHORT)
							.show();
				}
				break;

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

	/************** 更改用户的头像 **************/
	/**
	 * 对修改账号资料的监听方法/这里是头像图片的监听
	 * 
	 * @author 祝侦科 2014-3-8
	 */
	private class headClickListener implements OnClickListener {
		public void onClick(View arg0) {

			LinearLayout wayplanForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.z_dialog_selectphoto, null);
			Button btnAlbum = (Button) wayplanForm
					.findViewById(R.id.z_dialogSelectAlbum);
			Button btnCamera = (Button) wayplanForm
					.findViewById(R.id.z_dialogSelectCamera);
			Button btnCansel = (Button) wayplanForm
					.findViewById(R.id.z_dialogSelectCansel);
			final MyDialog dialog = new MyDialog(ShowMyInfo.this,
					R.style.z_myDialog);
			dialog.setContentView(wayplanForm);
			dialog.show();
			btnAlbum.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Intent intent = new Intent("android.intent.action.PICK");
//					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//					Intent intent = new Intent("android.intent.action.PICK");
//					intent.setDataAndType(
//							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//							IMAGE_UNSPECIFIED);
//					startActivityForResult(intent, PHOTOZOOM);
					
//					Intent intentFromGallery = new Intent();
//					intentFromGallery.setType("image/*"); // 设置文件类型
//					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//					startActivityForResult(intentFromGallery, PHOTOZOOM);
					
//					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);  
//                    intent.setType("image/*");  
//                    startActivityForResult(Intent.createChooser(intent, "选择图片"), PHOTOZOOM);  
//                    startActivityForResult(intent,PHOTOZOOM);
					Intent intent = new Intent(ShowMyInfo.this,
							TestPicActivityNow.class);
					if(Bimp.drr != null)
						Bimp.drr.clear();
					if(FileSelectUtils.ListsUpload != null)
						FileSelectUtils.ListsUpload.clear();
					startActivityForResult(intent, ALBUMREQUEST);
					dialog.dismiss();
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}
			});
			btnCamera.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File fileImgUpload = new File(
							MainActivity.UserFolderImgUpload);
					if (!fileImgUpload.exists()) {
						fileImgUpload.mkdirs();
					}
					intent.putExtra(
							MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(fileImgUpload + "/"
									+ SEMapApplication.AccountNumber + ".png")));
					startActivityForResult(intent, PHOTOHRAPH);
					dialog.dismiss();
				}
			});
			btnCansel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == NONE)
			return;

		// 拍照
		if (requestCode == PHOTOHRAPH) {
			// 设置文件保存路径
			File fileImgUpload = new File(MainActivity.UserFolderImgUpload);
			if (!fileImgUpload.exists()) {
				fileImgUpload.mkdirs();
			}
			File picture = new File(fileImgUpload + "/"
					+ SEMapApplication.AccountNumber + ".png");
			startPhotoZoom(Uri.fromFile(picture));
		}

		if (data == null)// 這句代碼要小心啦
			return;
		if (data.equals(""))
			return;

		/** 我的标签返回处理代码 */
		if (requestCode == MyBelongsRequest && resultCode == MyBelongs_OK) {
			strTags = data.getStringExtra("resBelongs");
			if (strTags.equals("")) {
				strsBelongs = null;
				strsBelongs = new String[0];
				tvUserTags.setAdapter(adapter);
			} else {
				strsBelongs = strTags.split("#");
				tvUserTags.setAdapter(adapter);
			}

		}

//		if (requestCode == ALBUMREQUEST) {
//			String strFile = data.getStringExtra("SelectImagePath");
//			File picture = new File(strFile);
//			startPhotoZoom(Uri.fromFile(picture));
//		}
		if (requestCode == ALBUMREQUEST && resultCode == ImageSelect_OK) {
			if(Bimp.drr.size() <= 0)
				return;
			String strFile = Bimp.drr.get(0);
			File picture = new File(strFile);
			startPhotoZoom(Uri.fromFile(picture));
		}
		// 读取相册缩放图片
		if (requestCode == PHOTOZOOM) {
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == PHOTORESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 75, stream);// (0
																		// -100)压缩文件

				// 设置成圆形
				Bitmap bmBitmap = UtilsZZK.toRoundBitmap(photo);
				image_head.setImageBitmap(bmBitmap);

				// image_head.setImageBitmap(photo);//保存bitmap到SD卡上
				saveMyBitmap(photo);// 映射到信息修改中的头像上

				// 修改头像后，将头像上传到服务器
				File fileImgUpload = new File(MainActivity.UserFolderImgUpload);
				if (!fileImgUpload.exists()) {
					fileImgUpload.mkdirs();
				}
				String filePath = fileImgUpload + "/"
						+ SEMapApplication.AccountNumber + ".png";
				File file = new File(filePath);
				if (!file.exists()) {
					Toast.makeText(mContext, "头像文件不存在", Toast.LENGTH_SHORT).show();
					return;
				}
				String fileName = SEMapApplication.AccountNumber;
				if (fileName.equals("0") || fileName == null)
					return;
				new Thread(new Client2(filePath, fileName,
						addressInfo.FileTypetouxiang)).start();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 将Bitmap转化为图片保存到SD卡上
	 * 
	 * @author 祝侦科 2014-3-7
	 * @param mBitmap
	 */
	public void saveMyBitmap(Bitmap mBitmap) {
		File fileImgUpload = new File(MainActivity.UserFolderImgUpload);
		if (!fileImgUpload.exists()) {
			fileImgUpload.mkdirs();
		}
		File f = new File(fileImgUpload + "/" + SEMapApplication.AccountNumber
				+ ".png");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(fOut != null){
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void startPhotoZoom(Uri uri) {
		// Intent intent = new Intent("com.android.camera.action.CROP");
		// intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		// intent.putExtra("crop", "true");
		// // aspectX aspectY 是宽高的比例
		// intent.putExtra("aspectX", 1);
		// intent.putExtra("aspectY", 1);
		// // outputX outputY 是裁剪图片宽高
		// intent.putExtra("outputX", 128);
		// intent.putExtra("outputY", 128);
		// intent.putExtra("return-data", true);
		// startActivityForResult(intent, PHOTORESOULT);

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 240);
		intent.putExtra("outputY", 240);
		intent.putExtra("return-data", true);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTORESOULT);
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
