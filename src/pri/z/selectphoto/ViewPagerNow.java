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
package pri.z.selectphoto;

import java.util.ArrayList;
import java.util.List;

import pri.z.photoshow.HackyViewPager;
import pri.z.show.R;
import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

public class ViewPagerNow extends Activity {
	private ViewPager mViewPager;
	
	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	public int max;
	public static int PhotoActivityRequest = 201;
	public static int PhotoActivity_OK = 202;
	SamplePagerAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		for (int i = 0; i < Bimp.bmp.size(); i++) {
			bmp.add(Bimp.bmp.get(i));
		}
		for (int i = 0; i < Bimp.drr.size(); i++) {
			drr.add(Bimp.drr.get(i));
		}
		max = Bimp.max;
		
		mViewPager = new HackyViewPager(this);
		mViewPager.setBackgroundColor(Color.BLACK);
		setContentView(mViewPager);
		adapter = new SamplePagerAdapter();
		mViewPager.setAdapter(adapter);
		int currentItem = getIntent().getIntExtra("ID", 0);
		mViewPager.setCurrentItem(currentItem);
	}


	class SamplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return bmp.size();
		}

		@Override
		public View instantiateItem(final ViewGroup container, final int position) {

			LayoutInflater inflater = ((Activity) container.getContext())
					.getLayoutInflater();
			View view = inflater.inflate(R.layout.z_viewpagerfrommobile, null);
			final PhotoView photoView = (PhotoView) view
					.findViewById(R.id.z_ViewPagerFromMobilePhotoView);

			photoView.setImageBitmap(bmp.get(position));

			TextView tvShowCurrentAndNum = (TextView) view
					.findViewById(R.id.z_ViewPagerFromMobileShowNumTv);
			tvShowCurrentAndNum.setText((position + 1) + "/"
					+ (bmp.size()));

			final int count = position;
			ImageButton imgRemotePhoto = (ImageButton) view
					.findViewById(R.id.z_ViewPagerFromMobileRemoteIcon);
			imgRemotePhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (Bimp.bmp.size() == 1) {
						Bimp.bmp.clear();
						Bimp.drr.clear();
						Bimp.max = 0;
						FileSelectUtils.deleteDir();
						FileSelectUtils.ListsUpload.clear();
						
						Intent intent = new Intent();
						setResult(PhotoActivity_OK, intent);
						finish();
					} else {
						String newStr = drr.get(count).substring(
								drr.get(count).lastIndexOf("/") + 1,
								drr.get(count).lastIndexOf("."));
						bmp.remove(count);
						drr.remove(count);
						del.add(newStr);
						max--;
						
						mViewPager.setAdapter(new SamplePagerAdapter());
						int currentItem = position - 1;
						if(position == 0){
							currentItem = 0;
						}
						mViewPager.setCurrentItem(currentItem);
						
						if(bmp.size() == 0){
							Bimp.bmp.clear();
							Bimp.drr.clear();
							Bimp.max = 0;
							FileSelectUtils.deleteDir();
							FileSelectUtils.ListsUpload.clear();

							Intent intent = new Intent();
							setResult(PhotoActivity_OK, intent);
							finish();
						}
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

			Bimp.bmp = bmp;
			Bimp.drr = drr;
			Bimp.max = max;
			for (int i = 0; i < del.size(); i++) {
				FileSelectUtils.delFile(del.get(i) + ".JPEG");
			}

			Intent intent = new Intent();
			setResult(PhotoActivity_OK, intent);
			finish();
			
			return false;
		}
		return false;
	}

}
