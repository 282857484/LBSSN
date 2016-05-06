package pri.z.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pri.z.show.MyDialog;
import pri.z.show.R;
import pri.z.utils.UtilsTrans;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpyMainGame extends Activity {

	int TotalNum = 3;
	int randomNum = 0;

	public String cicilianStr = "平民";
	public String spyStr = "卧底";
	// 记录当前按下的第几个:-1表示没有在选的项
	int currentPosition = -1;
	List<Integer> listsHasSaw = new ArrayList<Integer>();
	List<Integer> listsHasDead = new ArrayList<Integer>();
	boolean showResultflag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_spymain);

		TotalNum = getIntent().getIntExtra("TotalNum", 3);
		randomNum = new Random().nextInt(TotalNum);

		initView();
	}

	GridView gv;
	TextView tvTitle;
	private void initView() {
		// TODO Auto-generated method stub  
		tvTitle = (TextView) findViewById(R.id.z_activityLocalTitleName);
		gv = (GridView) findViewById(R.id.z_spyMainGridView);
		String[] strs = ReadSpyKu.getOneWords(getBaseContext());
		if(strs != null){
			if(strs.length >= 2){
				cicilianStr = strs[0];
				spyStr = strs[1];
			}
		}
		showResultflag = true;
		gv.setAdapter(adapter);
	}

	BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = getLayoutInflater().inflate(R.layout.z_item_spy, null);
				holder.imgSpyBg = (ImageView) view
						.findViewById(R.id.z_itemSpyImg);
				holder.tvOrder = (TextView) view
						.findViewById(R.id.z_itemSpyOrderTv);
				holder.tvWord = (TextView) view
						.findViewById(R.id.z_itemSpyWordTv);
				holder.tvResult = (TextView) view
						.findViewById(R.id.z_itemSpyResultTv);
				holder.btnOnSure = (Button) view.findViewById(R.id.z_spySureOn);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.tvOrder.setText("" + (position + 1));

			//已经看到身份和没有看到身份的标识
			if (UtilsTrans.IfExitThisNumber(listsHasSaw, position)) {
				holder.imgSpyBg
						.setBackgroundResource(R.drawable.z_itemwodihassaw);
			} else {
				holder.imgSpyBg.setBackgroundResource(R.drawable.z_itemwodibg);
			}
			
			//查看结果
			if(listsHasSaw.size() == TotalNum){
				if (UtilsTrans.IfExitThisNumber(listsHasDead, position)) {
					if (randomNum == position) {//平民胜利
						holder.tvResult.setText("卧底");
						if(showResultflag){
							showResultDialog("平民", cicilianStr, spyStr);
							showResultflag = false;
						}
					} else {//卧底胜利
						holder.tvResult.setText("冤死");
						if(listsHasDead.size()+2 >= TotalNum){
							if(showResultflag){
								showResultDialog("卧底", cicilianStr, spyStr);
								showResultflag = false;
							}
						}
					}
					holder.tvResult.setVisibility(View.VISIBLE);
				} else {
					holder.tvResult.setVisibility(View.INVISIBLE);
				}
			}else{
				holder.tvResult.setVisibility(View.INVISIBLE);
			}
			
			//正在选择中的时候
			if (currentPosition != -1) {// 如果已经选中，则不能选其他的了
				if(currentPosition == position){
					holder.imgSpyBg.setVisibility(View.INVISIBLE);
					holder.tvWord.setVisibility(View.VISIBLE);
					holder.btnOnSure.setVisibility(View.VISIBLE);
				}else{
					holder.imgSpyBg.setVisibility(View.VISIBLE);
					holder.tvWord.setVisibility(View.INVISIBLE);
					holder.btnOnSure.setVisibility(View.INVISIBLE);
				}
			}else{
				holder.imgSpyBg.setVisibility(View.VISIBLE);
				holder.tvWord.setVisibility(View.INVISIBLE);
				holder.btnOnSure.setVisibility(View.INVISIBLE);
			}
			
			holder.imgSpyBg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 如果都已经选好了
					if (listsHasSaw.size() == TotalNum) {
						showSpyOutDialog(position);
						return;
					}
					// 如果已经选择 了的话就不可以点击 了
					if (UtilsTrans.IfExitThisNumber(listsHasSaw, position)) {
						return;
					}
					if (currentPosition != -1) {// 如果已经选中，则不能选其他的了
						return;
					}
					// TODO Auto-generated method stub
					holder.imgSpyBg.setVisibility(View.INVISIBLE);
					holder.tvWord.setVisibility(View.VISIBLE);
					holder.btnOnSure.setVisibility(View.VISIBLE);
					if (randomNum == position) {
						holder.tvWord.setText(spyStr);
					} else {
						holder.tvWord.setText(cicilianStr);
					}
					currentPosition = position;
				}
			});
			holder.btnOnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					holder.imgSpyBg.setVisibility(View.VISIBLE);
					holder.tvWord.setVisibility(View.INVISIBLE);
					holder.btnOnSure.setVisibility(View.INVISIBLE);
					holder.imgSpyBg
							.setBackgroundResource(R.drawable.z_itemwodihassaw);
					if (!UtilsTrans.IfExitThisNumber(listsHasSaw, position)) {
						listsHasSaw.add(position);
					}
					currentPosition = -1;
					if (listsHasSaw.size() == TotalNum) {
						tvTitle.setText("游戏中...");
					}
				}
			});
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return TotalNum;
		}
	};

	// 弹出对话框
	public void showSpyOutDialog(final int position) {
		LinearLayout wayplanForm = (LinearLayout) getLayoutInflater().inflate(
				R.layout.z_dialog_spy_out, null);
		Button btn_OK = (Button) wayplanForm
				.findViewById(R.id.z_dialogSpyOutOK);
		Button btn_NO = (Button) wayplanForm
				.findViewById(R.id.z_dialogSpyOutNO);
		final MyDialog dialog = new MyDialog(SpyMainGame.this,
				R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();
		//
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listsHasDead.add(position);
				gv.setAdapter(adapter);
				dialog.dismiss();
			}
		});
		//
		btn_NO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 
	 * @param statue
	 */
	public void showResultDialog(String bunko, String indentityCivilian,
			String indentitySpy) {
		LinearLayout wayplanForm = (LinearLayout) getLayoutInflater().inflate(
				R.layout.z_dialog_spy_result, null);
		TextView tvShowBunko = (TextView) wayplanForm
				.findViewById(R.id.z_spyResultBunko);
		TextView tvShowIndentity = (TextView) wayplanForm
				.findViewById(R.id.z_spyResultIndentity);
		Button btn_Again = (Button) wayplanForm
				.findViewById(R.id.z_dialogSpyResultAgain);
		Button btn_Punish = (Button) wayplanForm
				.findViewById(R.id.z_dialogSpyResultPunish);
		final MyDialog dialog = new MyDialog(SpyMainGame.this,
				R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.setCancelable(false);
		tvShowBunko.setText(bunko + "胜利!");
		tvShowIndentity.setText("平民（" + indentityCivilian + "）  " + " 卧底（"
				+ indentitySpy + "）");
		dialog.show();
		//
		btn_Again.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listsHasDead.clear();
				listsHasSaw.clear();
				randomNum = new Random().nextInt(TotalNum);
				String[] strs = ReadSpyKu.getOneWords(getBaseContext());
				if(strs != null){
					if(strs.length >= 2){
						cicilianStr = strs[0];
						spyStr = strs[1];
					}
				}
				showResultflag = true;
				gv.setAdapter(adapter);
				tvTitle.setText("选择中...");
				dialog.dismiss();
			}
		});
		//
		btn_Punish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(SpyMainGame.this,TellTheTruth.class);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,R.anim.z_push_left_out);
			}
		});
	}

	class ViewHolder {
		ImageView imgSpyBg;
		TextView tvOrder;
		TextView tvWord;
		TextView tvResult;
		Button btnOnSure;
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
