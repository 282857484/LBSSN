package pri.z.game;

import java.util.ArrayList;
import java.util.List;

import pri.z.show.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DiceActivity extends Activity implements SensorEventListener {

	// 定义sensor管理器
	private SensorManager mSensorManager;
	private Vibrator mvibrator;
	boolean m_isnewgame = false;
	boolean kIsShake = false;
	RelativeLayout mrl;
	boolean OpenVoiceFlag = true;
	ImageView muparr, mdownarr;
	ImageView mimg;
	List<ImageView> m_imgview;
	long durationMillis = 3000;
	int m_imgNum = 3;
	private SoundPool sp;
	boolean m_isopen = false;
	PointF m_ptbase;
	private GestureDetector mGestureDetector;
	TextView tvShowDiceNum;
	long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_dicemain);

		SeekBar seekBar = (SeekBar) findViewById(R.id.z_diceNumSeekBar);
		seekBar.setOnSeekBarChangeListener(seekBarListener);
		tvShowDiceNum = (TextView) findViewById(R.id.z_diceNumShow);
		

		mimg = (ImageView) findViewById(R.id.imageView1);
		// doAninateleft_right(mimg);
		muparr = (ImageView) findViewById(R.id.uparr);
		mdownarr = (ImageView) findViewById(R.id.downarr);

		// 震动
		mvibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

		mGestureDetector = new GestureDetector(this, onGestureListener);
		m_isnewgame = true;
		Message msg = new Message();
		msg.what = 30;
		handler.sendMessage(msg);

		mvoce = Getvoice();
		sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		m_liu = sp.load(this, R.raw.roll, 1);
		// int[] location = new int[2];
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		// mimg.getLocationInWindow(location)

		m_ptbase = new PointF(dm.widthPixels - 200, dm.heightPixels / 2 - 100);

		mrl = (RelativeLayout) findViewById(R.id.rlayout);
		m_imgview = new ArrayList<ImageView>();
		// 获取传感器管理服务
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		final TextView tvChangeFlag = (TextView) findViewById(R.id.z_DiceGameSettingTv);
		tvChangeFlag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(OpenVoiceFlag){
					OpenVoiceFlag = false;
					tvChangeFlag.setText("开启声震");
				}else{
					OpenVoiceFlag = true;
					tvChangeFlag.setText("关闭声震");
				}
				if(kIsShake){
					if(OpenVoiceFlag){//暂时不处理开启的情况
						mvoce = Getvoice();
						sp.play(m_liu, 0.0f, mvoce, 1, 1, 1);
						// 振动
						mvibrator.vibrate(pattern, 2);
					}else{
						sp.autoPause();
						mvibrator.cancel();
					}
				}
				
			}
		});
	}

	OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			m_imgNum = progress + 1;
			tvShowDiceNum.setText("" + m_imgNum);

		}
	};
	GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@SuppressLint("NewApi")
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion > 10) {
				mimg.setPivotX(m_ptbase.x);
				mimg.setPivotY(m_ptbase.y);
			}

			if (e1.getY() > e2.getY() + 50) {

				dRolate(85.3f);
				m_isopen = !m_isopen;

			} else if (e2.getY() > e1.getY() + 50) {
				dRolate(0f);
				m_isopen = !m_isopen;

			}

			else if (e1.getX() > e2.getX() + 50) {

				dRolate(0f);
				m_isopen = !m_isopen;

			} else if (e2.getX() > e1.getX() + 50) {
				dRolate(85.3f);
				m_isopen = !m_isopen;
			}
			return true;

		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return mGestureDetector.onTouchEvent(event);

	}

	boolean m_bok = true;
	int m_liu = 0;
	float mvoce;
	int m_stop = 0;

	float Getvoice() {
		AudioManager am = (AudioManager) this
				.getSystemService(this.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

		return volumnRatio;
	}

	void Clearimg() {

		int ilen = mrl.getChildCount();
		for (int i = ilen - 1; i >= 0; i--) {
			if (mrl.getChildAt(i) instanceof ImageView) {
				if (mrl.getChildAt(i).getId() == R.id.imageView1) {
					continue;
				} else {
					mrl.getChildAt(i).setVisibility(View.INVISIBLE);
				}

			}

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 加速度传感器
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				// 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
				// 根据不同应用，需要的反应速率不同，具体根据实际情况设定
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);
		mvibrator.cancel();
		kIsShake = false;
		super.onStop();
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(this);
		mvibrator.cancel();
		kIsShake = false;
		super.onPause();
	}

	boolean m_in = false;

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int sensorType = event.sensor.getType();

		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;

		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
			m_stop = 0;
			/*
			 * 因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机的时候，瞬时加速度才会突然增大或减少。
			 * 所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置就OK了~~~
			 */
			int imax = 16;
			if ((Math.abs(values[0]) > imax || Math.abs(values[1]) > imax || Math
					.abs(values[2]) > imax)) {
				m_isnewgame = true;
				kIsShake = true;
				if(OpenVoiceFlag){
					mvoce = Getvoice();
					sp.play(m_liu, 0.0f, mvoce, 1, 1, 1);
					// 振动
					mvibrator.vibrate(pattern, 2);
				}else{
					sp.autoPause();
					mvibrator.cancel();
				}
				

				// Message msg = new Message();
				// msg.what = 1;
				// handler.sendMessage(USG);
				if (!m_in) {
					m_in = true;
					doAninateleft_right(muparr, false);
					doAninateleft_right(mdownarr, false);
					loadImage();
				}

			} else {
				m_stop++;
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	/**
	 * 动作执行
	 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int iv = msg.what;
			if (iv >= 29) {
				dRolate(0f);
				sp.autoPause();
				kIsShake = false;
				mvibrator.cancel();
				m_in = false;
				return;
			}
			int ivdd = iv % 3;
			switch (ivdd) {
			case 0:
				dRolate(1f);
				break;
			case 1:
				dRolate(-10f);
				break;
			case 2:
				dRolate(10f);
				break;
			default:
				break;
			}

		}

	};

	private void doAninateleft_right(View view, boolean show) {

		if (show) {
			TranslateAnimation alphaAnimation2 = new TranslateAnimation(60f,
					10f, 0f, 60f);
			alphaAnimation2.setDuration(1000);
			alphaAnimation2.setRepeatCount(Animation.INFINITE);
			alphaAnimation2.setRepeatMode(Animation.REVERSE);
			view.setAnimation(alphaAnimation2);
			alphaAnimation2.start();
			view.setVisibility(View.VISIBLE);

		} else {
			TranslateAnimation mHiddenAction = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, -1.0f);
			mHiddenAction.setDuration(1);
			view.setAnimation(mHiddenAction);
			mHiddenAction.start();
			view.setVisibility(View.GONE);

		}

	}

	// 获得不重复的随机数
	// max 最大值
	// num 个数
	List<Integer> GetRathbef(int max, int num) {
		List<Integer> robjsIntegers = new ArrayList<Integer>(num);
		double dv = 0;
		int ivtem = 1000;
		for (int i = 0; i < num; i++) {
			while (ivtem > max || robjsIntegers.contains(ivtem)) {
				dv = Math.random() * 10 + 1;
				ivtem = (int) dv;

			}
			robjsIntegers.add(ivtem);
		}
		return robjsIntegers;
	}

	void Rateimage() {
		Animation operatingAnim = AnimationUtils.loadAnimation(this,
				R.anim.z_dice_rolate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		mimg.startAnimation(operatingAnim);
	}

	int GetImgRes(int iv) {
		switch (iv) {
		case 1:
			return R.id.img1;
		case 2:
			return R.id.img2;
		case 3:
			return R.id.img3;
		case 4:
			return R.id.img4;
		case 5:
			return R.id.img5;
		case 6:
			return R.id.img6;
		case 7:
			return R.id.img7;
		case 8:
			return R.id.img8;
		case 9:
			return R.id.img9;

		default:
			break;
		}
		return R.id.img9;
	}

	List<Integer> mivgetIntegers, miv;

	void CreateImg() {

		// 得到帅子的值
		miv = new ArrayList<Integer>();
		for (int i = 0; i < m_imgNum; i++) {
			miv.add(GetRath());
		}

		// 得到所在的位置
		mivgetIntegers = GetRathbef(9, m_imgNum);

		showon();

	}

	void showon() {
		if (miv == null) {

			return;
		}
		int ilen = miv.size();
		for (int i = 0; i < ilen; i++) {
			ImageView ivImageView = (ImageView) findViewById(GetImgRes(mivgetIntegers
					.get(i)));
			int ivtem = Getshuanziimg(miv.get(i));
			ivImageView.setImageResource(ivtem);
			ivImageView.setVisibility(View.VISIBLE);
		}
	}

	@SuppressLint("NewApi")
	void dRolate(float dangle) {
		if (dangle < 20) {
			Clearimg();

		} else {
			if (m_isnewgame) {
				CreateImg();
				m_isnewgame = false;
			} else {
				showon();
			}

		}
		if (dangle < 0.1 && dangle > -0.1) {
			doAninateleft_right(muparr, true);
			doAninateleft_right(mdownarr, false);

		} else if (dangle > 50) {
			doAninateleft_right(muparr, false);
			doAninateleft_right(mdownarr, true);

		}
		// Rotata(mimg,dangle);
		// ------------------------------崩溃
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion > 10) {
			mimg.setRotation(dangle);
		} else {
			if (dangle > 40) {
				mimg.setVisibility(View.INVISIBLE);
			} else {
				mimg.setVisibility(View.VISIBLE);
			}

		}

	}

	public void Rotata(View vi, float dangle) {
		AnimationSet animationSet = new AnimationSet(true);
		// 后面的四个参数定义的是旋转的圆心位置
		RotateAnimation rotateAnimation = new RotateAnimation(0, dangle,
				Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT,
				0f);
		rotateAnimation.setDuration(durationMillis);
		animationSet.addAnimation(rotateAnimation);
		vi.startAnimation(animationSet);

	}

	int Getshuanziimg(int inum) {
		int izs = 0;
		int ib = GetRath();
		switch (inum) {
		case 1:
			if (ib < 3) {
				izs = R.drawable.z_dice_a1;
			} else if (ib < 5) {
				izs = R.drawable.z_dice_a2;
			} else {
				izs = R.drawable.z_dice_a3;
			}
			break;
		case 2:
			if (ib < 3) {
				izs = R.drawable.z_dice_b1;
			} else if (ib < 5) {
				izs = R.drawable.z_dice_b2;
			} else {
				izs = R.drawable.z_dice_b3;
			}
			break;
		case 3:
			if (ib < 3) {
				izs = R.drawable.z_dice_c1;
			} else if (ib < 5) {
				izs = R.drawable.z_dice_c2;
			} else {
				izs = R.drawable.z_dice_c3;
			}
			break;
		case 4:
			if (ib < 3) {
				izs = R.drawable.z_dice_d1;
			} else if (ib < 5) {
				izs = R.drawable.z_dice_d2;
			} else {
				izs = R.drawable.z_dice_d3;
			}
			break;
		case 5:
			if (ib < 3) {
				izs = R.drawable.z_dice_e1;
			} else if (ib < 5) {
				izs = R.drawable.z_dice_e2;
			} else {
				izs = R.drawable.z_dice_e3;
			}
			break;
		case 6:
			if (ib < 3) {
				izs = R.drawable.z_dice_f1;
			} else if (ib < 5) {
				izs = R.drawable.z_dice_f2;
			} else {
				izs = R.drawable.z_dice_f3;
			}
			break;
		default:
			break;
		}

		return izs;
	}

	//
	// 得到1~6随机数
	int GetRath() {
		// 1~10随机数
		double dv = Math.random() * 10 + 1;
		int iv = (int) dv;
		if (iv > 6) {
			iv = iv - 5;
		}
		return iv;
	}

	private void loadImage() {

		Thread mthreadm = new Thread() {
			public void run() {
				for (int i = 0; i < 30; i++) {
					SystemClock.sleep(100);
					Message msg = new Message();
					msg.what = i;
					handler.sendMessage(msg);

				}
			}
		};

		mthreadm.start();
	}

	@Override
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
