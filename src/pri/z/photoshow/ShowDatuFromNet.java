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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import pri.z.main.MainActivity;
import pri.z.show.R;
import pri.z.utils.addressInfo;
import pub.util.FormatTime;
import pub.util.ImageManager4;
import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class ShowDatuFromNet extends Activity {
	String photoUrl = "";
	private ViewPager mViewPager;
	Context mContext;
	int ShowDatuFromNetType = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		//这里的是一个文件名
		photoUrl = getIntent().getStringExtra("ShowDatuFromNet");
		ShowDatuFromNetType = getIntent().getIntExtra("ShowDatuFromNetType", 0);
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
int sss = 0;
	private Point mPoint = new Point(0, 0);
	 class SamplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			LayoutInflater inflater = ((Activity) container.getContext()).getLayoutInflater();
			View view = inflater.inflate(R.layout.z_viewpagerfromnet, null);
			final PhotoView photoView = (PhotoView)view.findViewById(R.id.z_ViewPagerFromNetPhotoView);
			
			String pathFolder = "";
			if(ShowDatuFromNetType == 0){
				return view;
			}else if(ShowDatuFromNetType == addressInfo.FileTypeActivityLogo){
				pathFolder = addressInfo.visitFolderActivityLogoDatu;
			}else if(ShowDatuFromNetType == addressInfo.FileTypetouxiang){
				pathFolder = addressInfo.visitFolderUserHeadDatu;
			}
			 
			final String photoLoadUrl = "http://"+addressInfo.localIP+":"+addressInfo.visitPort+"/"
					+pathFolder+"/"+photoUrl+".png";
			ImageManager4.from(mContext).displayImage(photoView, 
					photoLoadUrl,
							R.drawable.z_logindefault);
			ImageButton imgBtn = (ImageButton)view.findViewById(R.id.z_ViewPagerFromNetSaveBtn);
			// Now just add PhotoView to ViewPager and return it
			container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			final int myPosition = position;
			imgBtn.setVisibility(View.GONE);
			//保存图片到手机
			imgBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String url = photoLoadUrl;
					File fileImgDownLoad = new File(MainActivity.UserFolderImgDownLoad);
					if (!fileImgDownLoad.exists()) {
						fileImgDownLoad.mkdirs();
					}
					String path = fileImgDownLoad+"/pic_"+FormatTime.getFormatTime()+".png";
					
					savePhoto(url,path);
				}
			});
//			photoView.setOnLongClickListener(new OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//					saveInMyMobile(myPosition);
//					return true;
//				}
//			});
			
			return view;
		}
		
//		public void saveInMyMobile(int position ){
//			
//			final int myPosition = position;
//			final LinearLayout wayplanForm = (LinearLayout) getLayoutInflater().inflate(R.layout.z_dialog_photodownload, null);
//			Button btnSure = (Button) wayplanForm
//					.findViewById(R.id.z_dialogPhotoDownLoadOK);
//			Button btnCansel = (Button) wayplanForm
//					.findViewById(R.id.z_dialogPhotoDownLoadNO);
//			final MyDialog dialog = new MyDialog(ShowDatuFromNet.this, R.style.z_myDialog);
//			dialog.setContentView(wayplanForm);
//			dialog.show();
//			btnSure.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					String myStr = listStrs.get(myPosition);
//					String url = "http://"+addressInfo.localIP+":"+addressInfo.visitPort+"/"
//							+addressInfo.visitFolderMomentDatu+"/"+myStr+".png";
//					File fileImgDownLoad = new File(MainActivity.UserFolderImgDownLoad);
//					if (!fileImgDownLoad.exists()) {
//						fileImgDownLoad.mkdirs();
//					}
//					String path = fileImgDownLoad+"/pic_"+FormatTime.getFormatTime()+".png";
//					
//					savePhoto(url,path);
//					dialog.dismiss();
//				}
//			});
//			btnCansel.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dialog.dismiss();
//				}
//			});
//		}
		
		/**
		 * 将指定的图片路径保存到指定的文件下
		 * @param imgUrl：图片地址
		 * @param saveFilePath：图片保存路径
		 */
		public void savePhoto(final String imgUrl,final String saveFilePath){
			
			new Thread(){
	            public void run() {
	            	Looper.prepare();
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
	    				Toast.makeText(mContext, "图片保存在："+saveFilePath, Toast.LENGTH_LONG).show();
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} 
	            	Looper.loop();
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
