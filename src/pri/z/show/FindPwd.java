package pri.z.show;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pri.z.utils.UtilsZZK;
import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.Validate_C;
import pub.infoclass.myserver.protocol.Validate_S;
import pub.infoclass.myserver.protocol.changeCode_C;
import pub.infoclass.myserver.protocol.changeCode_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindPwd extends Activity {

	public static int FindPwd_OK = 201;// 找回密码成功返回码
	public static int FindPwd_NO = 202;// 找回密码失败返回码

	// 验证的手机号
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

	Context mContext;

	boolean flagSendReg = false;// 点击注册是否成功
	boolean flagValidatePhone = false;// 点击发送手机号到服务器是否成功
	boolean flagValidateCode = false;// 点击发送验证码到服务器是否成功
	String UserCode;// 用户注册的密码

	EditText editPwd1;
	EditText editPwd2;
	Button btnFindPwd;
	CheckBox checkboxShowPwd;
	ProgressBar loadingProgressBar;

	// 邮箱验证的组件
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

		setContentView(R.layout.z_forgetpwd);

		initView();

		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		// 1：先加载邮箱验证
		editValidatePhone = (EditText) findViewById(R.id.z_findPwdValidatePhone);
		btnGetCode = (Button) findViewById(R.id.z_findPwdGetVerificationCodeBtn);
		editCode = (EditText) findViewById(R.id.z_findPwdVerificationCode);
		btnSendCode = (Button) findViewById(R.id.z_findPwdSendVerificationCodeBtn);
		relCode = (RelativeLayout) findViewById(R.id.z_findPwdShowVerificationCodeRel);
		liValidateEmail = (LinearLayout) findViewById(R.id.z_findPwdValidateEmailLi);
		liafterValidateEmail = (LinearLayout) findViewById(R.id.z_findPwdAfterValidateLi);
		tvStep2 = (TextView) findViewById(R.id.z_findpwdStep2);

		editPwd1 = (EditText) findViewById(R.id.z_findPwdEditPwd1);
		editPwd2 = (EditText) findViewById(R.id.z_findPwdEditPwd2);
		checkboxShowPwd = (CheckBox) findViewById(R.id.z_findPwdShowPwdCheckBox);
		btnFindPwd = (Button) findViewById(R.id.z_findPwdBtn);
		loadingProgressBar = (ProgressBar) findViewById(R.id.z_findPwdingProgressBar1);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		initShowPassword();

		// 获取验证码
		btnGetCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = editValidatePhone.getText().toString().trim();
				if (str == null)
					return;
				if (str.equals(""))
					return;
				if (!isMobile(str)) {
					Toast.makeText(mContext, "请填写正确的手机号", 1).show();
					return;
				}

				if (!UtilsZZK.checkNetworkState(mContext))
					return;

				sendValidatePhoneMsg(str);
				ValidatePhone = str;

				btnGetCode.setEnabled(false);
				relCode.setVisibility(View.VISIBLE);
				editCode.requestFocus();
			}
		});

		// 发送验证码
		btnSendCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = editCode.getText().toString().trim();
				if (str == null)
					return;
				if (str.equals(""))
					return;
				if (!UtilsZZK.checkNetworkState(mContext))
					return;
				sendUserVerificationCodeMsg(str);
			}
		});

		btnFindPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!UtilsZZK.checkNetworkState(FindPwd.this))
					return;

				sendChangeCodeMsg();
				// 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(btnFindPwd.getWindowToken(), 0);
				
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
							editPwd1.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
							editPwd2.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
						} else {
							// 设置密码为隐藏的
							editPwd1.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
							editPwd2.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
						}
					}

				});

	}

	/**
	 * 请求验证码
	 * 
	 * @param email
	 */
	public void sendValidatePhoneMsg(String phone) {
		Message msg = Message.obtain();

		Validate_C li = new Validate_C();

		li.setStandardUploadTime();
		li.setUserID(phone);
		li.setSendMark(2);
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
	 * 
	 * @param code
	 */
	public void sendUserVerificationCodeMsg(String code) {
		Message msg = Message.obtain();
		Validate_C li = new Validate_C();

		li.setStandardUploadTime();
		li.setSendCode(code);
		li.setSendMark(4);
		li.setUserID(ValidatePhone);
		li.setSendPhone(ValidatePhone);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!flagValidateCode) {
					Toast.makeText(mContext, "发送验证码失败", Toast.LENGTH_SHORT)
							.show();
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
	public void sendChangeCodeMsg() {
		Message msg = Message.obtain();
		changeCode_C li = new changeCode_C();

		String pwd1 = editPwd1.getText().toString().trim();
		if (pwd1.equals("")) {
			return;
		}
		if (pwd1.length() < 6) {
			Toast.makeText(mContext, "密码长度不少于六位", Toast.LENGTH_SHORT).show();
			return;
		}

		// 密码
		String pwd2 = editPwd2.getText().toString().trim();
		if (pwd2.equals("")) {
			return;
		}

		if (!pwd1.equals(pwd2)) {
			Toast.makeText(mContext, "密码不一致", Toast.LENGTH_SHORT).show();
			editPwd1.setText("");
			editPwd2.setText("");
			return;
		}

		// 保存用户注册的手机号和密码
		UserCode = pwd1;

		li.setStandardUploadTime();
		li.setNewCode(pwd1);
		li.setUserID(ValidatePhone);

		loadingProgressBar.setVisibility(View.VISIBLE);
		btnFindPwd.setEnabled(false);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!flagSendReg) {
					Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
					loadingProgressBar.setVisibility(View.GONE);
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
				if (sendMark == 2) {
					flagValidatePhone = true;
					btnGetCode.setEnabled(true);
				} else if (sendMark == 4) {
					flagValidateCode = true;
					btnSendCode.setEnabled(true);
				}
				if (mark.equals("1")) {
					if (sendMark == 2) {
						btnGetCode.setEnabled(false);
						editValidatePhone.setEnabled(false);
					} else if (sendMark == 4) {// 如果验证码正确
						liValidateEmail.setVisibility(View.GONE);
						liafterValidateEmail.setVisibility(View.VISIBLE);
						tvStep2.setTextColor(getResources().getColor(
								R.color.z_color_reg_notice));
						editPwd1.requestFocus();
					}
				} else if (mark.equals("2")) {
					if (sendMark == 2) {
						Toast.makeText(FindPwd.this, "获取验证码失败",
								Toast.LENGTH_SHORT).show();
						btnGetCode.setEnabled(true);
					}
					if (sendMark == 4) {// 如果验证码正确
						Toast.makeText(FindPwd.this, "验证码错误",
								Toast.LENGTH_SHORT).show();
						editCode.setText("");
						btnGetCode.setEnabled(true);
					}

				} else if (mark.equals("3")) {
					if (sendMark == 2) {
						Toast.makeText(FindPwd.this, "该用户不存在",
								Toast.LENGTH_SHORT).show();
						btnGetCode.setEnabled(true);
						editValidatePhone.setText("");

						relCode.setVisibility(View.GONE);
						editValidatePhone.requestFocus();
					}
					if (sendMark == 4) {// 如果验证码正确
						Toast.makeText(FindPwd.this, "验证码错误",
								Toast.LENGTH_SHORT).show();
						editCode.setText("");
						btnGetCode.setEnabled(true);
					}

				}
				break;
			case protocolfromserver.changeCode_S:
				flagSendReg = true;
				changeCode_S addusers = (changeCode_S) msg.obj;
				Intent intent = new Intent();
				if (addusers.getMark().equals("1")) {
					Toast.makeText(FindPwd.this, "修改成功", Toast.LENGTH_SHORT)
							.show();
					intent.putExtra("resultFindPwdUserId", ValidatePhone);
					intent.putExtra("resultFindPwdUserCode", UserCode);
					setResult(FindPwd_OK, intent);
					FindPwd.this.finish();
					overridePendingTransition(R.anim.z_push_myleft_in,
							R.anim.z_push_myleft_out);
				} else if (addusers.getMark().equals("2")) {
					Toast.makeText(FindPwd.this, "修改失败", Toast.LENGTH_SHORT)
							.show();
				}
				loadingProgressBar.setVisibility(View.GONE);
				btnFindPwd.setEnabled(true);
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
