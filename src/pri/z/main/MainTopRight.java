package pri.z.main;


import pri.z.show.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class MainTopRight extends Activity {
	//private MyDialog dialog;
	
	//右方弹出菜单的请求码和结果码
	public static final int TopRightDialogRequest = 400;
	public static final int TopRightDialog_Null = 499;
	public static final int TopRightDialog_OK_01 = 401;
	public static final int TopRightDialog_OK_02 = 402;
	public static final int TopRightDialog_OK_03 = 403;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_main_top_right);
		
		initMenuListener();
		
	}

	private void initMenuListener() {
		// TODO Auto-generated method stub
		RelativeLayout firstMenu=(RelativeLayout)findViewById(R.id.z_topRightDialogFirst);
		firstMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(TopRightDialog_OK_01, intent);
				MainTopRight.this.finish();
			}
		});
		
		RelativeLayout secondMenu=(RelativeLayout)findViewById(R.id.z_topRightDialogSecond);
		secondMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(TopRightDialog_OK_02, intent);
				MainTopRight.this.finish();
			}
		});
		
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
		Intent intent = new Intent();
		setResult(TopRightDialog_Null, intent);
		MainTopRight.this.finish();
		return false;//如果返回true就是显示menu，这里不要显示这些数据，返回FALSE就可以反复实现显示Dialog的效果
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		
		return false;
	}
}
