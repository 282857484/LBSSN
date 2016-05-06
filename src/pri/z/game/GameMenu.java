package pri.z.game;

import pri.z.show.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class GameMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_gamemenu);

		initView();
	}

	private void initView() {
		// 谁是卧底
		ImageView imgSpy = (ImageView) findViewById(R.id.z_gameSpyImg);
		imgSpy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(GameMenu.this, Spy.class);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		// 真心话大冒险
		ImageView imgTruth = (ImageView) findViewById(R.id.z_gameTeelTheTruthImg);
		imgTruth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(GameMenu.this, TellTheTruth.class);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		// 筛子
		ImageView imgDice = (ImageView) findViewById(R.id.z_gameDiceImg);
		imgDice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(GameMenu.this, DiceActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		// 筛子
		ImageView imgOther = (ImageView) findViewById(R.id.z_gameOtherImg);
		imgOther.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				
			}
		});

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
