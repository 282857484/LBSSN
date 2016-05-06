package pri.z.game;

import java.util.Random;

import pri.z.show.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TellTheTruth extends Activity {
	/** Called when the activity is first created. */
	Circleview view;
	int place = 0;
	TextView tvShow;
	ImageView imgStart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_tellthetruth);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.z_tellthetruthFrame);
		int screnWidth = getWindowManager().getDefaultDisplay().getWidth();
		/**
		 * 添加随机数，制定奖项数量为上限，一般抽奖中什么都是服务器返回的，可以在请求服务器接口成功在制定转盘转到那个奖项
		 */
		final Random random = new Random();
		final Circleview claert = new Circleview(this, screnWidth);
		layout.addView(claert, new LayoutParams(LayoutParams.MATCH_PARENT,
				DensityUtil.dip2px(this, 300)));

		tvShow = (TextView) findViewById(R.id.z_tellTheTruthTv);
		imgStart = (ImageView) findViewById(R.id.z_tellTheTruthImgBegin);
		imgStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imgStart.setVisibility(View.GONE);
				place = random.nextInt(6);
				place++;// 确保是在1-6之间
				// Toast.makeText(getBaseContext(), "当前的位置" + place, 1).show();
				claert.setStopPlace(place);
				claert.setStopRoter(false);
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						imgStart.setVisibility(View.VISIBLE);
						if(place == 1){
							tvShow.setText("恭喜您逃过一劫！");
						}else{
							String str = "";
							String strType = "";
							if (place % 2 == 0) {
								strType = "真心话";
								str =ReadTruthKu.getOneTruth(getBaseContext());
							} else {
								strType = "大冒险";
								str =ReadAdventurehKu.getOneAdventure(getBaseContext());
							}
							if (str != null) {
								tvShow.setText(strType + ": " + str + "");
							} else {
								tvShow.setText("请重新转动");
							}
						}
//						Toast.makeText(getBaseContext(),"Circleview.totalTurnTime=="
//										+ Circleview.totalTurnTime, 1).show();
					}
					
				}, Circleview.totalTurnTime);
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