package pri.z.show;

import pri.z.mydb.SearchDistance;
import pri.z.sqlite.DBAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DistanceSetting extends Activity {
	protected static final String TAG = "哈哈哈哈";
	private SeekBar seekBar;
	private TextView myTextView;
	private EditText myEditText;

	public static final int MomentDis_OK = 901;
	public static final int ActivityDis_OK = 902;
	public static final int DistanceType_Moment = 1001;
	public static final int DistanceType_Activity = 1002;
	int TypeInt = 0;
	Context mContext;
	SearchDistance myDistance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		setContentView(R.layout.z_settingseekbar);
		myDistance = (SearchDistance) getIntent().getSerializableExtra("SearchDistance");
		InitView();
		InitListener();
	}
	
	public void InitView(){
		TextView tvTitle = (TextView) findViewById(R.id.z_settingDistanceTitle);
		myTextView = (TextView) findViewById(R.id.z_settingSeekBarShow);
		myEditText = (EditText) findViewById(R.id.z_settingSeekBarShowEdit);
		myEditText.addTextChangedListener(EditTextChangerListener);
		
		seekBar = (SeekBar) findViewById(R.id.z_settingSeekBar);
		
		//得到用户设定的距离如果没有的话就设定是默认的
//		DBAdapter dbAdapter = new DBAdapter(mContext);
//		dbAdapter.open();
//		SearchDistance[] searchs  = dbAdapter.queryAllSearchDistances();
////		//关闭数据库连接
////		dbAdapter.close();
//		if(searchs != null && searchs.length > 0){
//			myDistance = searchs[0];
//		}
		seekBar.setOnSeekBarChangeListener(seekListener);
		
		TypeInt = getIntent().getIntExtra("DistanceType", 0);
		if(TypeInt == DistanceType_Moment){
			//设置标题
			tvTitle.setText(getResources().getString(R.string.z_str_settingmomentdistance));
			if(myDistance != null ){
				seekBar.setProgress(Integer.valueOf(myDistance.DistanceMomentDis));
				myTextView.setText("搜索附近动态的范围圈：" + myDistance.DistanceMomentDis +"千米");
				myEditText.setText(myDistance.DistanceMomentDis+"");
				myEditText.setSelection(myEditText.getText().toString().length());
			}else{
				seekBar.setProgress(3);
				myTextView.setText("搜索附近动态的范围圈：" + 3 +"千米");
				myEditText.setText(3+"");
				myEditText.setSelection(myEditText.getText().toString().length());
			}
			
		}else if(TypeInt == DistanceType_Activity){
			tvTitle.setText(getResources().getString(R.string.z_str_settingactivitydistance));
			if(myDistance != null ){
				seekBar.setProgress(Integer.valueOf(myDistance.DistanceActivityDis));
				myTextView.setText("搜索附近活动的范围圈：" + myDistance.DistanceActivityDis +"千米");
				myEditText.setText(myDistance.DistanceActivityDis+"");
				myEditText.setSelection(myEditText.getText().toString().length());
			}else{
				seekBar.setProgress(3);
				myTextView.setText("搜索附近活动的范围圈：" + 3 +"千米");
				myEditText.setText(3+"");
				myEditText.setSelection(myEditText.getText().toString().length());
			}
		}
	}
	
	
	TextWatcher EditTextChangerListener = new TextWatcher() {

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
			String strs = myEditText.getText().toString().trim();
			if(strs.equals("")){
				strs = "0";
			}
			int str = Integer.valueOf(strs);
			if(TypeInt == DistanceType_Moment){
				myTextView.setText("搜索附近动态的范围圈：" + str +"千米");
				seekBar.setProgress(str);
			}else if(TypeInt == DistanceType_Activity){
				myTextView.setText("搜索附近活动的范围圈：" + str +"千米");
				seekBar.setProgress(str);
			}
		}
	};
	//给按钮设置监听
	public void InitListener(){
		Button  btnSure = (Button)findViewById(R.id.z_settingSeekBarOnSureBtn);
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				
				if(TypeInt == DistanceType_Moment){//动态
					Intent intent = new Intent();
					intent.putExtra("DistanceResult", seekBar.getProgress());
					setResult(MomentDis_OK, intent);
					DistanceSetting.this.finish();
					overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
				}else if(TypeInt == DistanceType_Activity){//活动
					Intent intent = new Intent();
					intent.putExtra("DistanceResult", seekBar.getProgress());
					setResult(ActivityDis_OK, intent);
					DistanceSetting.this.finish();
					overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
				}
			}
		});
	}
	
	//拖动条的滑动监听
	private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener(){
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(TypeInt == DistanceType_Moment){
				myTextView.setText("搜索附近动态的范围圈：" + progress +"千米");
			}else if(TypeInt == DistanceType_Activity){
				myTextView.setText("搜索附近活动的范围圈：" + progress +"千米");
			}
			myEditText.setText(progress+"");
			myEditText.setSelection(myEditText.getText().toString().length());

		}
	};
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}
