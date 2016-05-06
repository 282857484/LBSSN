package pri.z.imgselect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pri.z.imgselect.MyImageView.OnMeasureListener;
import pri.z.imgselect.NativeImageLoader.NativeImageCallBack;
import pri.z.main.MainActivity;
import pri.z.show.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

public class ShowImageActivity extends Activity {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;
	
	//用户本次选择的
	List<String> listSelectedStr = new ArrayList<String>();
	public static int ShowImageRequest = 202;
	public static int ShowImage_OK = 203;
	public static int ShowImagge_NO = 204;
	Button btnFinish;//用来显示还能加多少张图片/完成提交按钮
	//前面传递过来的
	ArrayList<String> listDeliverStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.z_show_image_activity);
		
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		//得到用户已经选择了的
		listDeliverStr = getIntent().getStringArrayListExtra("ListsPhotoImageSelect");
		
		btnFinish = (Button)findViewById(R.id.z_selectPhotoFinishBtn);
		btnFinish.setText(getStr());
		if(listDeliverStr.size() > MainActivity.selectPtotoNum){
			btnFinish.setTextColor(Color.GRAY);
		}else{
			btnFinish.setTextColor(Color.WHITE);
		}
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		btnFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				
				ArrayList<String> strs = listDeliverStr;
				intent.putStringArrayListExtra("SharePhotoListBack", strs);
				setResult(ShowImage_OK, intent);
				ShowImageActivity.this.finish();
				overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			}
		});
	}

	public  String getStr(){
		String btnStr;
		if(listDeliverStr == null)
			btnStr = "0/"+MainActivity.selectPtotoNum+"  "+"完成";
		else if(listDeliverStr.size() > MainActivity.selectPtotoNum)
			btnStr = MainActivity.selectPtotoNum+"/"+MainActivity.selectPtotoNum+"  "+"完成";
		else 
			btnStr = (listDeliverStr.size()-1)+"/"+MainActivity.selectPtotoNum+"  "+"完成";
		return btnStr;
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	class ChildAdapter extends BaseAdapter {
		private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
		/**
		 * 用来存储图片的选中情况
		 */
		private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
		private GridView mGridView;
		//该相册的图片的全部路径名
		private List<String> listFileName;
		protected LayoutInflater mInflater;
		//在该相册选中的有多少不同与listDeliverStr的数量
		public ChildAdapter(Context context, List<String> listFileName, GridView mGridView) {
			this.listFileName = listFileName;
			this.mGridView = mGridView;
			mInflater = LayoutInflater.from(context);
			//将穿过来的放到mSelectMap中
			for(int i=0;i<listFileName.size();i++){
				for(int j = 0;j < listDeliverStr.size();j++){
					if(listFileName.get(i).equals(listDeliverStr.get(j))){
						mSelectMap.put(i, true);
					}
				}
			}
		}
		
		@Override
		public int getCount() {
			return listFileName.size();
		}

		@Override
		public Object getItem(int position) {
			return listFileName.get(position);
		}


		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			String path = listFileName.get(position);
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.z_grid_child_item, null);
				viewHolder = new ViewHolder();
				viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
				viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
				
				//用来监听ImageView的宽和高
				viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
					
					@Override
					public void onMeasureSize(int width, int height) {
						mPoint.set(width, height);
					}
				});
				
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.mImageView.setImageResource(R.drawable.z_addphotoicon);
			}
			viewHolder.mImageView.setTag(path);
			viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					//如果是未选中的CheckBox,则添加动画
					if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
						addAnimation(viewHolder.mCheckBox);
					}
					mSelectMap.put(position, isChecked);
					if(isChecked){//如果选中
						for (int index = 0; index < listDeliverStr.size(); index++) {
							//注意注意：不能重复添加，，否则显示的图片数量会不对
							if (!IfExitThisPhoto(listDeliverStr,listFileName.get(position))) {
								listDeliverStr.add(listDeliverStr.size()-1, listFileName.get(position));
							}
						}
						
					}else{
						int myIndex = -1;
						for(int index = 0;index < listDeliverStr.size();index++){
							if(listFileName.get(position).equals(listDeliverStr.get(index))){
								myIndex = index;
							}
						}
						if(myIndex != -1){
							listDeliverStr.remove(myIndex);
						}
						
					}
					btnFinish.setText(getStr());
					if(listDeliverStr.size() > MainActivity.selectPtotoNum){
						btnFinish.setTextColor(Color.GRAY);
					}else{
						btnFinish.setTextColor(Color.WHITE);
					}
				}
			});
			
			viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
			
			//利用NativeImageLoader类加载本地图片
			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
				
				@Override
				public void onImageLoader(Bitmap bitmap, String path) {
					ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
					if(bitmap != null && mImageView != null){
						mImageView.setImageBitmap(bitmap);
					}
				}
			});
			
			if(bitmap != null){
				viewHolder.mImageView.setImageBitmap(bitmap);
			}else{
				viewHolder.mImageView.setImageResource(R.drawable.z_addphotoicon);
			}
			
			return convertView;
		}
		
		/**
		 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画 
		 * @param view
		 */
		private void addAnimation(View view){
			float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
			AnimatorSet set = new AnimatorSet();
			set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
					ObjectAnimator.ofFloat(view, "scaleY", vaules));
					set.setDuration(150);
			set.start();
		}
		
		
		/**
		 * 获取选中的Item的position
		 * @return
		 */
		public List<Integer> getSelectItems(){
			List<Integer> list = new ArrayList<Integer>();
			for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
				Map.Entry<Integer, Boolean> entry = it.next();
				if(entry.getValue()){
					list.add(entry.getKey());
				}
			}
			return list;
		}
		
		/**
		 * 获取选中的Item的对应的文件名称
		 * @return
		 */
		public ArrayList<String> getSelectItemsFileName(){
			ArrayList<String> listStr = new ArrayList<String>();
			//得到用户点击了哪几张
			List<Integer> list = new ArrayList<Integer>();
			for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
				Map.Entry<Integer, Boolean> entry = it.next();
				if(entry.getValue()){
					list.add(entry.getKey());
				}
			}
			
			//得到选中的几张对应的文件名
			for(int index=0;index<list.size();index++){
				listStr.add(listFileName.get(list.get(index)));
			}
			return listStr;
		}
		
	}
	public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}

	public boolean IfExitThisPhoto(ArrayList<String> strs, String str) {
		boolean flag = false;
		for (int index = 0; index < strs.size(); index++) {
			if (str.equals(strs.get(index))) {
				flag = true;
			}
		}
		return flag;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}
}
