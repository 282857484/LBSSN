package pri.z.show;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pri.z.main.MainActivity;
import pri.z.selectphoto.Bimp;
import pri.z.selectphoto.FileSelectUtils;
import pri.z.selectphoto.TestPicActivityNow;
import pri.z.utils.GetTime;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.Validate_C;
import pub.infoclass.myserver.protocol.Validate_S;
import pub.infoclass.myserver.protocol.addUser_C;
import pub.infoclass.myserver.protocol.addUser_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.util.Client2;
import pub.util.FormatTime;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserRegister extends Activity {

	public static int REGISTER_OK = 101;// 注册成功返回码
	public static int REGISTER_NO = 102;// 注册失败返回码
	
	//验证的手机号
	public static String ValidatePhone = "0";
	public static long waittingTime = 10000;
	// 头像选择的变量
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int ALBUMREQUEST = 112;// 结果
	public static int ImageSelect_OK = 102;
	public static final String IMAGE_UNSPECIFIED = "image/*";

	boolean flagUseMyHead = false;// 用户是否使用了自己的头像
	String sexChooseResult = "1";// 保存用户性别选择的结果：1为男，2为女
	Context mContext;

	boolean flagSendReg = false;// 点击注册是否成功
	boolean flagValidatePhone = false;// 点击发送手机号到服务器是否成功
	boolean flagValidateCode = false;// 点击发送验证码到服务器是否成功
	String RegisterUserID;// 用户注册的手机号
	String RegisterUserCode;// 用户注册的密码

	ImageView imgHead;// 用户注册的头像
	RadioGroup group;
	EditText editName;
	EditText editPwd;
	Button btnRegister;
	CheckBox checkboxShowPwd;
	RadioButton radioMan;
	RadioButton radioWoman;
	ProgressBar loadingProgressBar;
	
	//邮箱验证的组件
	EditText editValidatePhone;
	Button btnGetCode;
	EditText editCode;
	Button btnSendCode;
	RelativeLayout relCode;
	LinearLayout liValidateEmail;
	LinearLayout liafterValidateEmail;
	TextView tvStep2;
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		setContentView(R.layout.z_register);

		initView();

		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		// 1：先加载邮箱验证
		editValidatePhone = (EditText) findViewById(R.id.z_registerValidatePhone);
		btnGetCode = (Button) findViewById(R.id.z_regGetVerificationCodeBtn);
		editCode = (EditText) findViewById(R.id.z_regVerificationCode);
		btnSendCode = (Button) findViewById(R.id.z_regSendVerificationCodeBtn);
		relCode = (RelativeLayout) findViewById(R.id.z_regShowVerificationCodeRel);
		liValidateEmail = (LinearLayout) findViewById(R.id.z_regValidateEmailLi);
		liafterValidateEmail = (LinearLayout) findViewById(R.id.z_regAfterValidateLi);
		tvStep2 = (TextView) findViewById(R.id.z_regStep2);
		
		imgHead = (ImageView) findViewById(R.id.z_regImgHead);
		group = (RadioGroup) this.findViewById(R.id.z_regSexGroup);
		editName = (EditText) findViewById(R.id.z_register_username);
		editPwd = (EditText) findViewById(R.id.z_register_userpwd);
		checkboxShowPwd = (CheckBox) findViewById(R.id.z_reg_showPwdCheckBox);
		btnRegister = (Button) findViewById(R.id.z_registernow);
		loadingProgressBar = (ProgressBar) findViewById(R.id.z_regingProgressBar1);
		radioMan = (RadioButton) findViewById(R.id.z_regSexManRadio);
		radioWoman = (RadioButton) findViewById(R.id.z_regSexWomanRadio);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		imgHead.setOnClickListener(new headClickListener());
		initSexChooseListener();
		initShowPassword();
		
		//获取验证码
		btnGetCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = editValidatePhone.getText().toString().trim();
				if(str == null)
					return;
				if(str.equals(""))
					return;
//				Pattern pattern = Pattern
//						.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
//				Matcher matcher = pattern.matcher(str);
				if (!isMobile(str)){
					Toast.makeText(mContext, "请填写正确的手机号", 1).show();
					return;
				}
					
				if(!UtilsZZK.checkNetworkState(mContext))
					return;
				
				ValidatePhone = str;
//				Toast.makeText(mContext, "验证码已发送", 1).show();
				
				sendValidatePhoneMsg(str);
				btnGetCode.setEnabled(false);
				
				relCode.setVisibility(View.VISIBLE);
				editCode.requestFocus();
			}
		});
		
		//发送验证码
		btnSendCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = editCode.getText().toString().trim();
				if(str == null)
					return;
				if(str.equals(""))
					return;
				if(!UtilsZZK.checkNetworkState(mContext))
					return;
//				Toast.makeText(mContext, "正在验证...", 1).show();
				sendUserVerificationCodeMsg(str);
				btnSendCode.setEnabled(false);
				// 隐藏软键盘
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(btnSendCode.getWindowToken(), 0);
				
			}
		});
		
		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (!UtilsZZK.checkNetworkState(UserRegister.this))
					return;
				
				sendUserRegisterMsg();
				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(btnRegister.getWindowToken(), 0);

			}
		});

	}

	private void initShowPassword() {
		// TODO Auto-generated method stub
		checkboxShowPwd
				.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							// 设置EditText的密码为可见的
							editPwd.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
						} else {
							// 设置密码为隐藏的
							editPwd.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
						}
					}

				});

	}

	private void initSexChooseListener() {
		// TODO Auto-generated method stub
		// 绑定一个匿名监听器
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				// 获取变更后的选中项的ID
				int sexMan = R.id.z_regSexManRadio;
				int sexWoMan = R.id.z_regSexWomanRadio;
				int radioButtonId = arg0.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例

				if (radioButtonId == sexMan) {
					sexChooseResult = "1";
				} else {
					sexChooseResult = "2";
				}
			}
		});

	}

	

	/**
	 * 请求验证码
	 * @param email
	 */
	public void sendValidatePhoneMsg(String phone) {
		Message msg = Message.obtain();
		Validate_C li = new Validate_C();
		
		li.setStandardUploadTime();
		li.setUserID(phone);
		li.setSendMark(1);
		li.setSendPhone(phone);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!flagValidatePhone) {
					Toast.makeText(mContext, "获取验证码失败", Toast.LENGTH_SHORT)
							.show();
					btnGetCode.setEnabled(true);
				}
			}

		}, waittingTime);

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
	 * 验证Code
	 * @param code
	 */
	public void sendUserVerificationCodeMsg(String code) {
		Message msg = Message.obtain();
		Validate_C li = new Validate_C();
		
		li.setStandardUploadTime();
		li.setSendCode(code);
		li.setSendMark(3);
		li.setUserID(ValidatePhone);
		li.setSendPhone(ValidatePhone);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!flagValidateCode) {
					Toast.makeText(mContext, "发送验证码失败", Toast.LENGTH_SHORT)
					.show();
					btnSendCode.setEnabled(true);
				}
			}
			
		}, waittingTime);
		
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
	 * 
	 * 验证过用户注册的消息
	 * 
	 * @param name
	 * @param pwd
	 */
	public void sendUserRegisterMsg() {
		Message msg = Message.obtain();
		addUser_C li = new addUser_C();
		// 昵称
		String name = editName.getText().toString();
		if (!flagUseMyHead) {
			Toast.makeText(mContext, "请选择头像", Toast.LENGTH_SHORT).show();
			return;
		}

		if (name.equals("")) {
			Toast.makeText(mContext, "请输入昵称", Toast.LENGTH_SHORT).show();
			return;
		}

		// 密码
		String userPwd = editPwd.getText().toString().trim();
		if (userPwd.equals("")) {
			Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (userPwd.length() < 6) {
			Toast.makeText(mContext, "密码长度不少于六位", Toast.LENGTH_SHORT).show();
			return;
		}

		loadingProgressBar.setVisibility(View.VISIBLE);
		String  phone = editValidatePhone.getText().toString().trim();
		li.UserPhone = phone;
		// SEMapApplication.AccountNumber = phone;
		li.setUserID(phone);
		li.setStandardUploadTime();
		li.Code = userPwd;

		btnRegister.setEnabled(false);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!flagSendReg) {
					Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
					loadingProgressBar.setVisibility(View.GONE);
					btnRegister.setEnabled(true);
				}
			}

		}, waittingTime);

		li.UserName = name;

		li.UserSex = sexChooseResult;

		// 设置另外一些用户的基本属性
		li.UserDescribe = "这个人什么也没留下";
		li.Birthday = GetTime.getBirthday(FormatTime.getFormatTime());

		// 保存用户注册的手机号和密码
		RegisterUserID = phone;
		RegisterUserCode = userPwd;

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
	 * 快速准确的判断其是否为合法的手机号
	 * 
	 * @param mobiles
	 * @return
	 */
	public boolean isMobile(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case protocolfromserver.Validate_S:
				
				Validate_S vs = (Validate_S) msg.obj;
				String mark = vs.getMark();
				int sendMark = vs.getSendMark();
				if(sendMark == 1){
					flagValidatePhone = true;
					btnGetCode.setEnabled(true);
				}else if(sendMark == 3){
					flagValidateCode = true;
					btnSendCode.setEnabled(true);
				}
				if (mark.equals("1")) {
					if(sendMark == 1){
						btnGetCode.setEnabled(false);
						editValidatePhone.setEnabled(false);
					}else if(sendMark == 3){//如果验证码正确
						liValidateEmail.setVisibility(View.GONE);
						liafterValidateEmail.setVisibility(View.VISIBLE);
						tvStep2.setTextColor(getResources().getColor(R.color.z_color_reg_notice));
						editName.requestFocus();
					}
				} else if (mark.equals("2")) {
					if(sendMark == 1){
						Toast.makeText(UserRegister.this, "获取验证码失败",
								Toast.LENGTH_SHORT).show();
						btnGetCode.setEnabled(true);
					}
					if(sendMark == 3){//如果验证码正确
						Toast.makeText(UserRegister.this, "验证码错误",
								Toast.LENGTH_SHORT).show();
						editCode.setText("");
					}
					
				}else if (mark.equals("3")) {//注册是用户手机号已经注册
					if(sendMark == 1){
						Toast.makeText(UserRegister.this, "该用户已注册",
								Toast.LENGTH_SHORT).show();
						btnGetCode.setEnabled(true);
						editValidatePhone.setText("");
						
						relCode.setVisibility(View.GONE);
						editValidatePhone.requestFocus();
					}
					if(sendMark == 3){//如果验证码正确
						Toast.makeText(UserRegister.this, "验证码错误",
								Toast.LENGTH_SHORT).show();
						editCode.setText("");
					}
					
				}
				break;
				
			case protocolfromserver.addUser_S:
				flagSendReg = true;
				btnRegister.setEnabled(true);
				addUser_S addusers = (addUser_S) msg.obj;
				Intent intent = new Intent();
				if (addusers.getMark().equals("1")) {
					// 发送用户的头像
					if (flagUseMyHead) {// 如果选择了
						File fileImgUpload = new File(
								MainActivity.UserFolderImgUpload);
						if (!fileImgUpload.exists()) {
							fileImgUpload.mkdirs();
						}
						String filePath = fileImgUpload + "/" + headSaveName
								+ ".png";
						if (!(new File(filePath).exists())) {
							Toast.makeText(mContext, "头像文件不存在",
									Toast.LENGTH_SHORT).show();
							return;
						}
						// 修改头像后，将头像上传到服务器
						new Thread(new Client2(filePath, RegisterUserID,
								addressInfo.FileTypetouxiang)).start();
					}
					Toast.makeText(UserRegister.this, "注册成功",
							Toast.LENGTH_SHORT).show();
					intent.putExtra("resultRegisterUserId", RegisterUserID);
					intent.putExtra("resultRegisterUserCode", RegisterUserCode);
					setResult(REGISTER_OK, intent);
					UserRegister.this.finish();
					overridePendingTransition(R.anim.z_push_myleft_in,
							R.anim.z_push_myleft_out);
				} else if (addusers.getMark().equals("2")) {
					Toast.makeText(UserRegister.this, "该手机号已被注册",
							Toast.LENGTH_SHORT).show();
				}
				loadingProgressBar.setVisibility(View.GONE);
				btnRegister.setEnabled(true);
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
	String headSaveName = "";

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
			final MyDialog dialog = new MyDialog(UserRegister.this,
					R.style.z_myDialog);
			dialog.setContentView(wayplanForm);
			dialog.show();
			btnAlbum.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Intent intent = new Intent("android.intent.action.PICK");
					// intent.setDataAndType(
					// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					// IMAGE_UNSPECIFIED);
					// startActivityForResult(intent, PHOTOZOOM);

					// Intent intent = new
					// Intent(UserRegister.this,AlbumSelectActivity.class);
					// startActivityForResult(intent, ALBUMREQUEST);
					// dialog.dismiss();

					Intent intent = new Intent(UserRegister.this,
							TestPicActivityNow.class);
					
					if (Bimp.drr != null)
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
					headSaveName = "user_" + FormatTime.getFormatTime();
					File fileImgUpload = new File(
							MainActivity.UserFolderImgUpload);
					if (!fileImgUpload.exists()) {
						fileImgUpload.mkdirs();
					}
					String filePath = fileImgUpload + "/" + headSaveName
							+ ".png";
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(filePath)));
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
			String filePath = fileImgUpload + "/" + headSaveName + ".png";
			File picture = new File(filePath);
			startPhotoZoom(Uri.fromFile(picture));
		}

		if (data == null)// 這句代碼要小心啦
			return;

		if (data.equals(""))
			return;

		// if(requestCode == ALBUMREQUEST) {
		// String strFile = data.getStringExtra("SelectImagePath");
		// File picture = new File(strFile);
		// startPhotoZoom(Uri.fromFile(picture));
		// }

		if (requestCode == ALBUMREQUEST && resultCode == ImageSelect_OK) {
			if (Bimp.drr.size() <= 0)
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

				imgHead.setImageBitmap(photo);// 保存bitmap到SD卡上
				saveMyBitmap(photo);// 映射到信息修改中的头像上

				Bitmap bmBitmap = UtilsZZK.toRoundBitmap(photo);
				imgHead.setImageBitmap(bmBitmap);
				flagUseMyHead = true;
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
		String filePath = fileImgUpload + "/" + headSaveName + ".png";
		File f = new File(filePath);
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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

	public void startPhotoZoom(Uri uri) {

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
