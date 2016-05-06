package pri.z.show;


import pri.z.main.MainActivity;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DeleteObject;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ExitFromSettings extends Activity {
	
	Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.z_exit_dialog_from_settings);
		mContext = getBaseContext();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		Button btnExit = (Button)findViewById(R.id.z_exitLoginPositive);
		Button btnCancel = (Button)findViewById(R.id.z_exitLoginNegative);
		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				DBAdapter dbAdapter = new DBAdapter(mContext);
//				dbAdapter.open();
//				dbAdapter.deleteAllUserInfoBases();
//				//关闭数据库连接
//				dbAdapter.close();
				
				SQLInfo sqlInfoDelete = new SQLInfo();
				sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
				String whereClause = null; 
				String[] whereArgs = null;
				sqlInfoDelete.SQLInfo = new DeleteObject(
						ContentValuesChange.UserInfo_Table, whereClause, whereArgs);
				sqlSecurityThread.handleSQl(sqlInfoDelete);
				
//				SEMapApplication.AccountNumber = "0";
				//队列处理数据删除操作需要时间，故这里有延时操作
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try{
							ExitFromSettings.this.finish();
							CommonSetting.CommonSettingActivity.finish();
							MainActivity.MainActivtyExitUse.finish();
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							System.exit(0);
						}
					}
					
				}, 200);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ExitFromSettings.this.finish();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	
}
