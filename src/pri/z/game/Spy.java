package pri.z.game;

import pri.z.show.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Spy extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_spysetting);
		initView();
	}

	TextView tvShowNum;
	SeekBar seekBar;
	TextView tvShowCivilianNum;
	TextView tvShowSpyNum;
	Button btnStartGame;
	private void initView() {
		// TODO Auto-generated method stub  
		tvShowNum = (TextView)findViewById(R.id.z_spyShowNumTv);
		
		//最小是3
		seekBar = (SeekBar)findViewById(R.id.z_spyNumSeekBar);
		tvShowCivilianNum = (TextView)findViewById(R.id.z_spyCivilianNumTv);
		tvShowSpyNum = (TextView)findViewById(R.id.z_spySpyNumTv);
		btnStartGame = (Button)findViewById(R.id.z_spyStartGameBtn);
		
		seekBar.setOnSeekBarChangeListener(seekBarListener);
		btnStartGame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Spy.this,SpyMainGame.class);
				intent.putExtra("TotalNum", seekBar.getProgress()+3);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,R.anim.z_push_left_out);
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
			int strInt = progress+3;
			tvShowNum.setText(""+strInt);
			tvShowCivilianNum.setText("平民"+(strInt-1)+"人");
			tvShowSpyNum.setText("卧底1人");
			
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}

}
