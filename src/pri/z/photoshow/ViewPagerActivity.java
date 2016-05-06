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

import java.util.ArrayList;

import pri.z.selectphoto.Bimp;
import pri.z.show.R;
import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class ViewPagerActivity extends Activity {
	ArrayList<String> listStrs;
	private ViewPager mViewPager;
	private boolean hasRemotePhoto = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 得到上一步穿过来的List<String> :这里是多了一个String的
		listStrs = getIntent().getStringArrayListExtra("mySelectedLists");

		mViewPager = new HackyViewPager(this);
		mViewPager.setBackgroundColor(Color.BLACK);
		setContentView(mViewPager);

		mViewPager.setAdapter(new SamplePagerAdapter());
		int currentItem = getIntent().getIntExtra("CurrentItem", 0);
		mViewPager.setCurrentItem(currentItem);
	}

	private Point mPoint = new Point(0, 0);

	class SamplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return listStrs.size() - 1;
		}

		@Override
		public View instantiateItem(final ViewGroup container, final int position) {

			LayoutInflater inflater = ((Activity) container.getContext())
					.getLayoutInflater();
			View view = inflater.inflate(R.layout.z_viewpagerfrommobile, null);
			final PhotoView photoView = (PhotoView) view
					.findViewById(R.id.z_ViewPagerFromMobilePhotoView);

			// 用来监听ImageView的宽和高
			// photoView.setImageResource(sDrawables[position]);
//			String myStr = listStrs.get(position);
//			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
//					myStr, mPoint, new NativeImageCallBack() {
//						@Override
//						public void onImageLoader(Bitmap bitmap, String path) {
//							photoView.setImageBitmap(bitmap);
//						}
//					});

			photoView.setImageBitmap(Bimp.bmp.get(position));

			TextView tvShowCurrentAndNum = (TextView) view
					.findViewById(R.id.z_ViewPagerFromMobileShowNumTv);
			tvShowCurrentAndNum.setText((position + 1) + "/"
					+ (listStrs.size() - 1));

			ImageButton imgRemotePhoto = (ImageButton) view
					.findViewById(R.id.z_ViewPagerFromMobileRemoteIcon);
			imgRemotePhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					listStrs.remove(position);
					hasRemotePhoto = true;
					mViewPager.setAdapter(new SamplePagerAdapter());
					int currentItem = position - 1;
					if(position == 0){
						currentItem = 0;
					}
					mViewPager.setCurrentItem(currentItem);
					
					if(listStrs.size() - 1 == 0){
						
						Intent intent = new Intent();
						intent.putStringArrayListExtra("ViewPagerLists", listStrs);
						setResult(ViewPagerRemote_OK, intent);
						
						ViewPagerActivity.this.finish();
					}
				}
			});
			// Now just add PhotoView to ViewPager and return it
			container.addView(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return view;
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

	public static int ViewPagerRemoteRequest = 104;
	public static int ViewPagerRemote_OK = 105;
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			
			if(hasRemotePhoto){
				Intent intent = new Intent();
				intent.putStringArrayListExtra("ViewPagerLists", listStrs);
				setResult(ViewPagerRemote_OK, intent);
			}
			finish();
			return false;
		}
		return false;
	}

}
