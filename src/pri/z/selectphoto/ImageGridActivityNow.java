package pri.z.selectphoto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pri.z.selectphoto.BitmapCache.ImageCallback;
import pri.z.selectphoto.ImageGridAdapter.TextCallback;
import pri.z.show.R;
import pri.z.utils.UtilsTrans;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageGridActivityNow extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static int ShowImageRequest = 202;
	public static int ShowImage_OK = 203;
	public static int ShowImagge_NO = 204;
	// ArrayList<Entity> dataList;//鐢ㄦ潵瑁呰浇鏁版嵁婧愮殑鍒楄〃
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapterNow adapter;// 鑷畾涔夌殑閫傞厤鍣�
	AlbumHelper helper;
	Button bt;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivityNow.this, "最多选择9张图片", 100).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_grid);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);

		initView();
		bt = (Button) findViewById(R.id.bt);
		bt.setVisibility(View.GONE);
		if (Bimp.drr != null)
			if (Bimp.drr.size() > 0)
				bt.setText("完成" + "(" + Bimp.drr.size() + ")");
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ArrayList<String> list = new ArrayList<String>();
				// Collection<String> c = adapter.map.values();
				// Iterator<String> it = c.iterator();
				// for (; it.hasNext();) {
				// list.add(it.next());
				// }
				//
				// if (Bimp.act_bool) {
				// Bimp.act_bool = false;
				// }
				// for (int i = 0; i < list.size(); i++) {
				// if (Bimp.drr.size() < 9) {
				// Bimp.drr.add(list.get(i));
				// }
				// }
				Intent intent = new Intent();
				setResult(ShowImage_OK, intent);
				finish();
				overridePendingTransition(R.anim.z_push_myleft_in,
						R.anim.z_push_myleft_out);
			}

		});
	}

	/**
	 * 鍒濆鍖杤iew瑙嗗浘
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapterNow(ImageGridActivityNow.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
//		adapter.setTextCallback(new TextCallback() {
//			public void onListen(int count) {
//				bt.setText("完成" + "(" + count + ")");
//			}
//		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 鏍规嵁position鍙傛暟锛屽彲浠ヨ幏寰楄窡GridView鐨勫瓙View鐩哥粦瀹氱殑瀹炰綋绫伙紝鐒跺悗鏍规嵁瀹冪殑isSelected鐘舵
				 * �锛� 鏉ュ垽鏂槸鍚︽樉绀洪�涓晥鏋溿� 鑷充簬閫変腑鏁堟灉鐨勮鍒欙紝涓嬮潰閫傞厤鍣ㄧ殑浠ｇ爜涓細鏈夎鏄�
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * 閫氱煡閫傞厤鍣紝缁戝畾鐨勬暟鎹彂鐢熶簡鏀瑰彉锛屽簲褰撳埛鏂拌鍥�
				 */
				adapter.notifyDataSetChanged();
			}

		});

	}
	
	
	class ImageGridAdapterNow extends BaseAdapter {

		private TextCallback textcallback = null;
		final String TAG = getClass().getSimpleName();
		Activity act;
		List<ImageItem> dataList;
		Map<String, String> map = new HashMap<String, String>();
		BitmapCache cache;
		private Handler mHandler;
		ImageCallback callback = new ImageCallback() {
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap,
					Object... params) {
				if (imageView != null && bitmap != null) {
					String url = (String) params[0];
					if (url != null && url.equals((String) imageView.getTag())) {
						((ImageView) imageView).setImageBitmap(bitmap);
					} else {
						Log.e(TAG, "callback, bmp not match");
					}
				} else {
					Log.e(TAG, "callback, bmp null");
				}
			}
		};

//		public static interface TextCallback {
//			public void onListen(int count);
//		}
//
//		public void setTextCallback(TextCallback listener) {
//			textcallback = listener;
//		}

		public ImageGridAdapterNow(Activity act, List<ImageItem> list, Handler mHandler) {
			this.act = act;
			dataList = list;
			cache = new BitmapCache();
			this.mHandler = mHandler;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (dataList != null) {
				count = dataList.size();
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView text;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Holder holder;

			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(act, R.layout.item_image_grid, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.image);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.isselected);
				holder.text = (TextView) convertView
						.findViewById(R.id.item_image_grid_text);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final ImageItem item = dataList.get(position);

			holder.iv.setTag(item.imagePath);
			cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
					callback);
			boolean flag = false;// 不含相同的
			for (int index = 0; index < Bimp.drr.size(); index++) {
				if (item.imagePath.equals(Bimp.drr.get(index))) {
					flag = true;
					break;
				}
			}
			if (flag) {
				item.isSelected = true;
			} else {
				item.isSelected = false;
			}

			if (item.isSelected) {
				holder.selected.setImageResource(R.drawable.icon_data_select);
				holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
			} else {
				holder.selected.setImageResource(-1);
				holder.text.setBackgroundColor(0x00000000);
			}
			
			holder.iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String path = dataList.get(position).imagePath;

					if ((Bimp.drr.size()) < 9) {
						item.isSelected = !item.isSelected;
						if (item.isSelected) {
//							holder.selected
//									.setImageResource(R.drawable.icon_data_select);
							holder.text
									.setBackgroundResource(R.drawable.bgd_relatly_line);

							map.put(path, path);
							if (Bimp.drr.size() < 9) {
								if (!UtilsTrans.IfExitStringInLists(Bimp.drr, path))
									Bimp.drr.add(path);
							}

							if (textcallback != null)
								textcallback.onListen(Bimp.drr.size());
						} else if (!item.isSelected) {
							holder.selected.setImageResource(-1);
							holder.text.setBackgroundColor(0x00000000);

							map.remove(path);

							if (Bimp.drr.size() < 9) {
								int myIndex = UtilsTrans.getStringIndexInList(Bimp.drr, path);
								if (myIndex != -1)
									Bimp.drr.remove(myIndex);
							}

							if (textcallback != null)
								textcallback.onListen(Bimp.drr.size());
						}
					} else if (Bimp.drr.size() >= 9) {
						if (item.isSelected == true) {
							item.isSelected = !item.isSelected;
							holder.selected.setImageResource(-1);
							map.remove(path);
							
							int myIndex = UtilsTrans.getStringIndexInList(Bimp.drr, path);
							if (myIndex != -1)
								Bimp.drr.remove(myIndex);

						} else {
							Message message = Message.obtain(mHandler, 0);
							message.sendToTarget();
						}
					}
					
					
					Intent intent = new Intent();
					setResult(ShowImage_OK, intent);
					finish();
					overridePendingTransition(R.anim.z_push_myleft_in,
							R.anim.z_push_myleft_out);
				}

			});

			return convertView;
		}
	}
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
