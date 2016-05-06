/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package pri.z.photoshow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import pri.z.show.R;
import pub.util.ImageManager4;
import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

public class ViewPagerPhotoWall extends Activity {
	ArrayList<String> listStrs;
	private ViewPager mViewPager;
	Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		//得到上一步穿过来的List<String> :
		//照片墙，包括全部http://192.168.,,,,,,,152133.png之类的
		listStrs = getIntent().getStringArrayListExtra("oneUserShowLists");
		
		mViewPager = new HackyViewPager(this);
		mViewPager.setBackgroundColor(Color.BLACK);
		setContentView(mViewPager);

		mViewPager.setAdapter(new SamplePagerAdapter());
		int currentItem = getIntent().getIntExtra("CurrentItem", 0);
		mViewPager.setCurrentItem(currentItem);
		
		//保存网络上的图片到本地时，加上这句话就不会出错,用了线程之后就不用这句话了
//		 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
	}

	private Point mPoint = new Point(0, 0);
	 class SamplePagerAdapter extends PagerAdapter {


		@Override
		public int getCount() {
			return listStrs.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			final PhotoView photoView = new PhotoView(container.getContext());
			//用来监听ImageView的宽和高
//			photoView.setImageResource(sDrawables[position]);
			String myStr = listStrs.get(position);
//			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(myStr, mPoint, new NativeImageCallBack() {
//				@Override
//				public void onImageLoader(Bitmap bitmap, String path) {
//						photoView.setImageBitmap(bitmap);
//				}
//			});
//			
//			photoView.setImageBitmap(bitmap);		
			ImageManager4.from(mContext).displayImage(photoView, 
					listStrs.get(position), R.drawable.z_logindefault);

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			final int myPosition = position;
			photoView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					saveInMyMobile(myPosition);
					
					return true;
				}
			});
			
			return photoView;
		}
		
		public void saveInMyMobile(int position ){
			Builder builder = new Builder(ViewPagerPhotoWall.this);
			
			builder.setTitle("登录提示");
			final TextView editData = new TextView(ViewPagerPhotoWall.this);
			editData.setText("保存到手机");
			editData.setTextColor(Color.WHITE);
			builder.setView(editData);
			final int myPosition = position;
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
//					String url = "http://pic.yesky.com/imagelist/09/01/11277904_7147.jpg";
//					String path = Environment.getExternalStorageDirectory().toString()+"/head0819.png";
					String myStr = listStrs.get(myPosition);
					String path = Environment.getExternalStorageDirectory().toString()+"/"+myStr+".png";
					savePhoto(myStr,path);
				}
			});
			
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			builder.create().show();
		}
		
		/**
		 * 将指定的图片路径保存到指定的文件下
		 * @param imgUrl：图片地址
		 * @param saveFilePath：图片保存路径
		 */
		public void savePhoto(final String imgUrl,final String saveFilePath){
			
			new Thread(){
	            public void run() {
	            	try {
	    				FileOutputStream fos = new   FileOutputStream(saveFilePath);
	    				InputStream is = new URL(imgUrl).openStream();
	    				int   data = is.read(); 
	    				while(data!=-1){ 
	    				        fos.write(data); 
	    				        data=is.read(); 
	    				} 
	    				is.close();
	    				fos.close();			
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} 	
	            };
	        }.start();
			
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

}
