package pri.z.imgselect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pri.z.show.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
/**
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 *
 */
public class ImageSelectActivity extends Activity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private GroupAdapter adapter;
	private GridView mGroupGridView;
	public static int ImageSelectRequest = 101;
	public static int ImageSelect_OK = 102;
	public static int ImageSelect_NO = 103;
	
	public static int ShowImageRequest = 202;
	public static int ShowImage_OK = 203;
	public static int ShowImagge_NO = 204;
	
	ArrayList<String> ListsPhoto = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.z_imageselectalbum);
		ListsPhoto = getIntent().getStringArrayListExtra("ImageSelectListsPhoto");
		mGroupGridView = (GridView) findViewById(R.id.main_grid);
		
		getImages();
		
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> childList = mGruopMap.get(list.get(position).getFolderName());
				
				List<String> myLists = getStringList(childList);
				
				Intent mIntent = new Intent(ImageSelectActivity.this, ShowImageActivity.class);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>)myLists);
				mIntent.putStringArrayListExtra("ListsPhotoImageSelect", ListsPhoto);
				startActivityForResult(mIntent, ShowImageRequest);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});
		
	}
	
	/**
	 * 将原来的顺序调换一下，方便
	 * @return
	 */
	private List<String> getStringList(List<String> list){
		List<String> myLists = new ArrayList<String>();
		
		for(int index = list.size() - 1 ;index >= 0;index--){
			myLists.add(list.get(index));
		}
		return myLists;
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				//关闭进度条
				mProgressDialog.dismiss();
				
				adapter = new GroupAdapter(ImageSelectActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}
		
	};

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageSelectActivity.this.getContentResolver();

				//只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				
				while (mCursor.moveToNext()) {
					//获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();

					
					//根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				
				mCursor.close();
				
				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);
				
			}
		}).start();
		
	}
	
	
	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
	 * 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
		if(mGruopMap.size() == 0){
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(value.size()-1));//获取该组的第后一张图片
			list.add(mImageBean);
		}
		
		return list;
		
	}


	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		//图片选择好了之后
		if(requestCode == ShowImageRequest && resultCode == ShowImage_OK){
			//选择成功
			ArrayList<String> strs = data.getStringArrayListExtra("SharePhotoListBack");  
			
			Intent intent = new Intent();
			intent.putStringArrayListExtra("SharePhotoList", strs);
			setResult(ImageSelect_OK, intent);
			ImageSelectActivity.this.finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
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
