package pri.z.show;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pri.z.imgselect.MyImageView;
import pri.z.main.MainActivity;
import pri.z.photoshow.ViewPagerFromNetActivity;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.DiscussMoment;
import pri.z.utils.GetTime;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.HTTPResponceInfo;
import pub.httptransfer.SearchMomentsByUserId;
import pub.httptransfer.SearchPoiById;
import pub.httptransfer.creatPoi;
import pub.httptransfer.updatePoi;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.infoclass.myserver.protocol.userPushInfo_C;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.util.FormatTime;
import pub.util.ImageManager3;
import pub.util.ImageManager4;
import pub.util.timeCha;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MomentDetail extends Activity {

	// MomentBaseInfo mbi ;//传递过来的动态数据
	Context mContext;
	public static final String TAG = "哈哈哈哈";
	TextView tvMomentPraise;// 点赞的数目
	TextView tvMomentCriticizen;// 点踩的数目
	private InputMethodManager imm;
	private EditText editComment;
	private Button btnCommentSend;
	ProgressBar progressBar;
	PhotoGridView gridViewPhoto;
	ListView commentsListView;
	// 动态的详情的请求码和返回码
	public static int MomentDetailRequest = 304;
	public static int MomentDetail_OK = 305;// 有数据返回
	List<DiscussMoment> DiscussLists = new ArrayList<DiscussMoment>();;
	// List<DiscussMoment> DiscussLists = new ArrayList<DiscussMoment>();
	String myCommentMessageId, myCommentTime, myCommentDiscuss;// 保存我发的评论的信息

	// 用户的操作
	boolean PraiseFlag = false;// 是否赞了
	boolean CriticizenFlag = false;// 是否踩了
	boolean CommentFlag = false;// 是否评论了
	List<DiscussMoment> resultDiscussLists = new ArrayList<DiscussMoment>();
	// 该动态的具体信息
	MomentBaseInfo myMoment;
	ScrollView scrollLayout;
	ImageView hasNoNetImg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		setContentView(R.layout.z_momentdetail);

		scrollLayout = (ScrollView) findViewById(R.id.z_momentDetailScrollView);
		progressBar = (ProgressBar) findViewById(R.id.z_momentDetailProgressBar);
		hasNoNetImg = (ImageView) findViewById(R.id.z_momentDetailNoNetImg);
		
		final String messageId = getIntent().getStringExtra("MomentDetailId");
		sendMomentDetail(messageId);
		
		setNetView();
		hasNoNetImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMomentDetail(messageId);
				
				setNetView();
			}
		});
		
	}

	/**
	 * 设置有无网络的View
	 */
	public void setNetView(){
		if (!UtilsZZK.checkNetworkState(MomentDetail.this)) {
			progressBar.setVisibility(View.GONE);
			hasNoNetImg.setVisibility(View.VISIBLE);
		}else{
			progressBar.setVisibility(View.VISIBLE);
			hasNoNetImg.setVisibility(View.GONE);
		}
	}
	
	private void sendMomentDetail(String messageId) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = z_baiduprotocol.baiduMomentDetail;
		msg.obj = messageId;
		if (remoteMessenger == null) {
			remoteMessenger = SEMapApplication.serviceMessenger;
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}

	/**
	 * 根据动态详细类填充数据
	 * 
	 * @param mbi
	 */
	private void initMomentDetail(MomentBaseInfo mbi) {
		progressBar.setVisibility(View.INVISIBLE);
		scrollLayout.setVisibility(View.VISIBLE);
		if (mbi == null) {
			return;
		}
		final MomentBaseInfo myFinalMbi = mbi;
		// 最上面的头像，昵称和时间
		ImageView imgHead = (ImageView) findViewById(R.id.z_momentDetailHead);
		TextView tvUserName = (TextView) findViewById(R.id.z_commentDetailuserName);
		TextView tvTime = (TextView) findViewById(R.id.z_commentDetailTime);
		ImageManager3.from(mContext).displayImage(
				imgHead,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderUserHeadXiaotu + "/"
						+ mbi.UserId + ".png", R.drawable.z_logindefault);
		
		String sexStr = mbi.Sex;
		tvUserName.setText(UtilsTrans.getUserName(mbi.UserName));
		if (sexStr.equals("1")) {
			tvUserName.setTextColor(getResources().getColor(
					R.color.z_color_moment_usernameman));
		} else {
			tvUserName.setTextColor(getResources().getColor(
					R.color.z_color_moment_usernamewoman));
		}
		
		tvTime.setText(GetTime.getNoYearTime(mbi.UploadTime));

		// 动态内容
		TextView tvMomentContent = (TextView) findViewById(R.id.z_commentDetailContent);
		tvMomentContent.setText(mbi.MomentContent);

		// 赞
		RelativeLayout LikeLayout = (RelativeLayout) findViewById(R.id.z_commentDetailLike);
		final TextView tvPraiseNum = (TextView) findViewById(R.id.z_commentDetailPraiseNum);
		final TextView tvPraiseAnim = (TextView) findViewById(R.id.z_commentDetailPraiseAnim);
		tvPraiseNum.setText(mbi.PraiseNum);

		// 踩
		RelativeLayout UnLikeLayout = (RelativeLayout) findViewById(R.id.z_commentDetailUnLike);
		final TextView tvTrampleNum = (TextView) findViewById(R.id.z_commentDetailTrampleNum);
		final TextView tvTrampleAnim = (TextView) findViewById(R.id.z_commentDetailTrampleAnim);
		tvTrampleNum.setText(mbi.CritizenNum);

		initMomentPhoto(mbi.ShowPhotos);

		// 赞和踩的字体颜色
		String str = getHasOrNo(mbi.MessageId);
		if (str.equals("1")) {
			// tvPraiseNum.setTextColor(Color.RED);
		} else if (str.equals("-1")) {
			// tvTrampleNum.setTextColor(Color.RED);
		}

		/******* 设置监听 *******/
		imgHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CenterInfo.class);
				intent.putExtra("momentCenterUserId", myFinalMbi.UserId);
				new Thread(new SearchMomentsByUserId(myFinalMbi.UserId, "0"))
						.start();
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		LikeLayout.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("static-access")
			public void onClick(View v) {

				String messageId = myFinalMbi.MessageId;
				if (getHasOrNo(messageId).equals("1")) {
					Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
					return;
				} else if (getHasOrNo(messageId).equals("-1")) {
					Toast.makeText(mContext, "已踩", Toast.LENGTH_SHORT).show();
					return;
				}
				Animation animation = AnimationUtils.loadAnimation(mContext,
						R.anim.z_praise_animation);
				tvPraiseAnim.setVisibility(View.VISIBLE);
				tvPraiseAnim.startAnimation(animation);
				// postdelayed不能取消，，否则没有animation效果
				new Handler().postDelayed(new Runnable() {
					public void run() {
						tvPraiseAnim.setVisibility(View.GONE);
						new Thread(new SearchPoiById(Integer
								.valueOf(myFinalMbi.MessageId), 49442, 1))
								.start();

						tvMomentPraise = tvPraiseNum;
						tvMomentCriticizen = tvTrampleNum;
					}
				}, 1000);

			}
		});

		UnLikeLayout.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("static-access")
			public void onClick(View v) {

				String messageId = myFinalMbi.MessageId;
				if (getHasOrNo(messageId).equals("1")) {
					Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
					return;
				} else if (getHasOrNo(messageId).equals("-1")) {
					Toast.makeText(mContext, "已踩", Toast.LENGTH_SHORT).show();
					return;
				}
				Animation animation = AnimationUtils.loadAnimation(mContext,
						R.anim.z_praise_animation);
				tvTrampleAnim.setVisibility(View.VISIBLE);
				tvTrampleAnim.startAnimation(animation);
				// postdelayed不能取消，，否则没有animation效果
				new Handler().postDelayed(new Runnable() {
					public void run() {
						tvTrampleAnim.setVisibility(View.GONE);
						new Thread(new SearchPoiById(Integer
								.valueOf(myFinalMbi.MessageId), 49442, -1))
								.start();
						tvMomentPraise = tvPraiseNum;
						tvMomentCriticizen = tvTrampleNum;
					}
				}, 1000);

			}
		});

		Button btnComment = (Button) findViewById(R.id.z_momentCommentBtn);

		btnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SEMapApplication.AccountNumber.equals("0")) {
					LoginDialog.showLoginDialog(MomentDetail.this);
				} else {
					showPopUpWindow(v,  myFinalMbi.MessageId);
				}
			}
		});
	}

	private void initMomentPhoto(String str) {
		// TODO Auto-generated method stub

		if (str.length() < 2) {// 如果用户没有上传图片那么就不显示
			return;
		}
		gridViewPhoto = (PhotoGridView) findViewById(R.id.z_MomentDetailGridview);
		gridViewPhoto.setVisibility(View.GONE);
		String[] strs = str.split("#");
		ArrayList<String> listsP = new ArrayList<String>();
		for (int index = 0; index < strs.length; index++) {
			listsP.add(strs[index]);
		}
		if (listsP.size() > 0) {

			gridViewPhoto.setVisibility(View.VISIBLE);
			PhotoAdapter adapterPhoto = new PhotoAdapter(listsP);
			gridViewPhoto.setAdapter(adapterPhoto);
		}

	}

	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象

	private class PhotoAdapter extends BaseAdapter {
		ArrayList<String> listsPhoto;

		public PhotoAdapter(ArrayList<String> lists) {
			this.listsPhoto = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (listsPhoto.size() > 10) {// 最多只能发表9张
				return 9;
			}
			return listsPhoto.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listsPhoto.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;// 当前的View
			ViewHolderPhoto holder;
			String myStr = listsPhoto.get(position);
			final int myPosition = position;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_item_sharephoto, parent, false);
				holder = new ViewHolderPhoto();
				holder.showPhoto = (MyImageView) view
						.findViewById(R.id.z_itemSharePhotoImg);

				view.setTag(holder);
			} else {
				holder = (ViewHolderPhoto) view.getTag();
			}

			ImageManager4.from(mContext).displayImage(
					holder.showPhoto,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderMomentXiaotu + "/" + myStr
							+ ".png", R.drawable.z_logindefault);

			holder.showPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MomentDetail.this,
							ViewPagerFromNetActivity.class);
					intent.putExtra("CurrentItem", myPosition);
					intent.putStringArrayListExtra("oneUserShowLists",
							listsPhoto);
					startActivity(intent);
				}
			});
			return view;
		}

	};

	class ViewHolderPhoto {
		MyImageView showPhoto;
	}

	MomentDetailDiscussAdapter adapter;

	private void initDiscussMomentListView() {
		int count = DiscussLists.size();
		if (count <= 0) {
			Button btnNoComment = (Button) findViewById(R.id.z_momentDetailNoComment);
			btnNoComment.setVisibility(View.VISIBLE);
			return;
		}
		commentsListView = (ListView) findViewById(R.id.z_commentDetailCommentsListView);
		// List<DiscussMoment> lists = new ArrayList<DiscussMoment>();
		// for(int index = 0;index < count;index++){
		// DiscussMoment dis = myDislists.get(index);
		// lists.add(dis);
		// }

		adapter = new MomentDetailDiscussAdapter(mContext, DiscussLists);
		commentsListView.setAdapter(adapter);
		setLvHeight(commentsListView);
	}

	List<ActivityDiscussSelectData> DisLists;
	RelationSelectData relationData;// 用户对该活动的参加或者是关注的数据
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case z_baiduprotocol.baiduMomentPraise:// 赞
				try {
					// 更新赞和踩的数量
					JSONObject json = (JSONObject) msg.obj;
					int id = Integer.valueOf(json.getString("id"));
					String praiseNum = String.valueOf((Integer.valueOf(json
							.getString("praisenumber").toString()) + 1));
					String criticizeNum = String.valueOf((Integer.valueOf(json
							.getString("criticizenumber").toString())));

					updateMyBaiduData(id, praiseNum, criticizeNum);
					if (tvMomentPraise != null) {
						tvMomentPraise.setText(praiseNum);
						// tvMomentPraise.setTextColor(Color.RED);
					}
					if (tvMomentCriticizen != null) {
						tvMomentCriticizen.setText(criticizeNum);
					}
					// 赞成功后修改手机数据
					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter.updateOneMomentByMessageId(id, praiseNum,
					// criticizeNum, "1");
					// // 关闭数据库连接
					// dbAdapter.close();

					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValues = ContentValuesChange
							.contentUpdateOneMomentByMessageId(praiseNum,
									criticizeNum, "1");
					String whereClause = ContentValuesChange.Key_MomentMessageId
							+ "=? ";
					String[] whereArgs = new String[] { String.valueOf(id) };
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.Moment_Table, newValues,
							whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);

					PraiseFlag = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case z_baiduprotocol.baiduMomentCriticizen:// 踩
				try {
					// 更新赞和踩的数量
					JSONObject json = (JSONObject) msg.obj;
					int id = Integer.valueOf(json.getString("id"));
					String praiseNum = String.valueOf((Integer.valueOf(json
							.getString("praisenumber").toString())));
					String criticizeNum = String.valueOf((Integer.valueOf(json
							.getString("criticizenumber").toString()) + 1));

					updateMyBaiduData(id, praiseNum, criticizeNum);
					if (tvMomentPraise != null) {
						tvMomentPraise.setText(praiseNum);
					}
					if (tvMomentCriticizen != null) {
						tvMomentCriticizen.setText(criticizeNum);
						// tvMomentCriticizen.setTextColor(Color.RED);
					}
					// 赞成功后修改手机数据
					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter.updateOneMomentByMessageId(id, praiseNum,
					// criticizeNum, "-1");
					// // 关闭数据库连接
					// dbAdapter.close();

					SQLInfo sqlInfoUpdate = new SQLInfo();
					sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
					ContentValues newValues = ContentValuesChange
							.contentUpdateOneMomentByMessageId(praiseNum,
									criticizeNum, "-1");
					String whereClause = ContentValuesChange.Key_MomentMessageId
							+ "=? ";
					String[] whereArgs = new String[] { String.valueOf(id) };
					sqlInfoUpdate.SQLInfo = new UpdateObject(
							ContentValuesChange.Moment_Table, newValues,
							whereClause, whereArgs);
					sqlSecurityThread.handleSQl(sqlInfoUpdate);

					CriticizenFlag = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case z_baiduprotocol.baiduOneMomentDetail:// 某动态的详情
				try {
					// 更新赞和踩的数量
					JSONObject json = (JSONObject) msg.obj;
					myMoment = new MomentBaseInfo();
					
					myMoment.MessageId = json.getString("id");
					myCommentMessageId = myMoment.MessageId;
					Log.v("哈哈哈哈", "myMoment.MessageId: "+myMoment.MessageId);
					myMoment.UserId = json.getString("univeralindex");
					myMoment.UserName = json.getString("username");
					myMoment.UploadTime = json.getString("uploadingtime");
					myMoment.MomentContent = json.getString("broadcastcontent");
					myMoment.PraiseNum = json.getString("praisenumber");
					myMoment.CritizenNum = json.getString("criticizenumber");
					myMoment.ShowPhotos = json.getString("photo1");
					myMoment.Sex = json.getString("sex");
					// myMoment.MyUserId = json.getString("id");
					// myMoment.MyUserName = json.getString("id");
					// myMoment.HasOrNo = json.getString("id");

					initMomentDetail(myMoment);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case z_baiduprotocol.baiduMomentDiscuss:// 得到用户的评论
				try {
					JSONObject json = new JSONObject(String.valueOf(msg.obj));
					String DisSize = json.getString("size");
					if (DisSize.equals("0")) {
						initDiscussMomentListView();
					} else {
						JSONArray jsonArray = new JSONArray(
								json.getString("pois"));
						int jsonLength = jsonArray.length();
						for (int index = 0; index < jsonLength; index++) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							DiscussMoment dismom = new DiscussMoment();
							dismom.DiscussUid = jsonObj.getString("id");
							dismom.DiscussMessageId = jsonObj
									.getString("messageid");
							dismom.DiscussUserId = jsonObj.getString("userid");
							dismom.DiscussUserName = jsonObj
									.getString("username");
							dismom.DiscussPointId = jsonObj
									.getString("pointdiscussid");
							dismom.DiscussUploadTime = jsonObj
									.getString("uploadtime");
							dismom.DiscussContent = jsonObj
									.getString("discusscontent");
							DiscussLists.add(dismom);
						}
						// 关闭数据库连接
						initDiscussMomentListView();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case protocolwithbaidustore.replybaidutablemessagediscuss:
				HTTPResponceInfo http = (HTTPResponceInfo) msg.obj;
				if (http.getStatus() == 0) {

					// 添加到
					DiscussMoment dis = new DiscussMoment();
					dis.DiscussUid = "";
					dis.DiscussMessageId = myCommentMessageId;
					dis.DiscussUserId = SEMapApplication.AccountNumber;
					dis.DiscussUserName = SEMapApplication.LoginName;
					dis.DiscussPointId = "";
					dis.DiscussUploadTime = myCommentTime;
					dis.DiscussContent = myCommentDiscuss;
					DiscussLists.add(0, dis);

					if (adapter == null) {// 如果前面没有评论的时候
						commentsListView = (ListView) findViewById(R.id.z_commentDetailCommentsListView);
						adapter = new MomentDetailDiscussAdapter(mContext,
								DiscussLists);
						Button btnNoComment = (Button) findViewById(R.id.z_momentDetailNoComment);
						btnNoComment.setVisibility(View.GONE);
						commentsListView.setAdapter(adapter);
						setLvHeight(commentsListView);
					} else {
						adapter.notifyDataSetChanged();
						setLvHeight(commentsListView);
					}

					resultDiscussLists.add(dis);// 将评论加到返回的List
					CommentFlag = true;

					// 评论成功，将数据发到服务器推送消息
					myMoment.UploadTime = myCommentTime;
					myMoment.MyUserId = dis.DiscussUserId;
					myMoment.MyUserName = dis.DiscussUserName;
					myMoment.HasOrNo = myCommentDiscuss;

					sendCommentPushMsg(myMoment);
				}

				break;
			}
		}
	};

	public void sendCommentPushMsg(MomentBaseInfo moment) {
		Message msg = Message.obtain();
		userPushInfo_C li = new userPushInfo_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setThisUserID(moment.UserId);
		// 如果评论人与发表者是一个人，那么不发送推送消息
		if (moment.MyUserId.equals(moment.UserId)) {
			return;
		}
		Gson gson = new Gson();
		String str = gson.toJson(moment);
		li.setMbi(str);

		ArrayList<Object> list = new ArrayList<Object>();
		list.add(li);

		msg.replyTo = myMessenger;
		msg.what = li.getP();
		String timeStampString = li.getUploadTime();
		msg.arg1 = Integer.parseInt(timeStampString.substring(0, 7));
		msg.arg2 = Integer.parseInt(timeStampString.substring(7, 15));
		msg.obj = list;

		if (remoteMessenger == null) {
			remoteMessenger = SEMapApplication.serviceMessenger;
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	// 修改数据
	public void updateMyBaiduData(int id, String praiseNum, String criticizeNum) {

		updatePoi cre = new updatePoi();
		cre.setId(id);// /必须
		cre.setCoord_type(3);// 必须

		// 更新别人的动态数据时，不能修改别人发起动态的经纬度
		// cre.setLongitude(MainActivity.a);
		// cre.setLatitude(MainActivity.b);
		cre.setGeotable_id("49442");// 必须
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("praisenumber", praiseNum);
		map.put("criticizenumber", criticizeNum);
		cre.setColumnkey(map);
		MainActivity.sendToBaidu(cre, FormatTime.getFormatTime(),
				protocolwithbaidustore.baidutableupdatemessagediscuss);

	}

	private class MomentDetailDiscussAdapter extends BaseAdapter {

		List<DiscussMoment> lists;// 包括所有的List
		Context mContext;

		public MomentDetailDiscussAdapter(Context mContext,
				List<DiscussMoment> lists) {
			this.mContext = mContext;
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder;
			// 第二版本
			DiscussMoment discuss = lists.get(position);
			final DiscussMoment myFinalDiscuss = discuss;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_item_momentdetaildiscuss, parent, false);

				viewHolder.imgDiscussUserHead = (ImageView) view
						.findViewById(R.id.z_momentDetailDiscussItemHead);
				viewHolder.tvDiscussUserName = (TextView) view
						.findViewById(R.id.z_momentDetailDiscussItemUserName);
				viewHolder.tvDiscussTime = (TextView) view
						.findViewById(R.id.z_momentDetailDiscussItemTime);
				viewHolder.tvDiscussComment = (TextView) view
						.findViewById(R.id.z_momentDetailDiscussItemContent);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			/** 加载用户头像 **/
			ImageManager3.from(mContext).displayImage(
					viewHolder.imgDiscussUserHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ discuss.DiscussUserId + ".png",
					R.drawable.z_logindefault);

			viewHolder.tvDiscussUserName.setText(UtilsTrans
					.getUserName(discuss.DiscussUserName));

			String TimeStart = discuss.DiscussUploadTime;
			String TimeEnd = FormatTime.getFormatTime();

			viewHolder.tvDiscussTime.setText(timeCha.MillisToWord(timeCha
					.TimeCha(TimeStart, TimeEnd)));
			// viewHolder.tvDiscussTime.setText(GetTime
			// .getNoYearTime(discuss.DiscussUploadTime));
			viewHolder.tvDiscussComment.setText(discuss.DiscussContent);

			viewHolder.imgDiscussUserHead
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(mContext,
									CenterInfo.class);
							intent.putExtra("momentCenterUserId",
									myFinalDiscuss.DiscussUserId);
							new Thread(new SearchMomentsByUserId(
									myFinalDiscuss.DiscussUserId, "0")).start();
							startActivity(intent);
							overridePendingTransition(R.anim.z_push_left_in,
									R.anim.z_push_left_out);
						}
					});
			viewHolder.tvDiscussUserName
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(mContext,
									CenterInfo.class);
							intent.putExtra("momentCenterUserId",
									myFinalDiscuss.DiscussUserId);
							new Thread(new SearchMomentsByUserId(
									myFinalDiscuss.DiscussUserId, "0")).start();
							startActivity(intent);
							overridePendingTransition(R.anim.z_push_left_in,
									R.anim.z_push_left_out);
						}
					});

			return view;

		}

	}

	/**
	 * 得到该评论时否已经被赞、踩
	 * 
	 * @param messageId
	 * @return：1表示赞，-1表示踩，0表示尚未赞或踩
	 */
	public String getHasOrNo(String messageId) {
		DBAdapter dbAdapter = new DBAdapter(mContext);
		dbAdapter.open();
		String HasOrNo = "0";
		MomentBaseInfo[] moments = dbAdapter.queryOneMomentBaseInfo(messageId);
		// 关闭数据库连接
		dbAdapter.close();
		if (moments != null && moments.length > 0) {
			HasOrNo = moments[0].HasOrNo;
		}
		return HasOrNo;
	}

	public static class ViewHolder {
		ImageView imgDiscussUserHead;
		TextView tvDiscussUserName;
		TextView tvDiscussTime;
		TextView tvDiscussComment;
	}

	RelativeLayout relComment;

	public void showPopUpWindow(View v,  String messageId) {

		relComment = (RelativeLayout) findViewById(R.id.z_momentDetailCommentRel);
		relComment.setVisibility(View.VISIBLE);
		
		imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
		
		editComment = (EditText) findViewById(R.id.z_momentDetailCommentSendMessage);
		editComment.requestFocus();
		editComment.addTextChangedListener(EditTextChangerListener);
		
		btnCommentSend = (Button) findViewById(R.id.z_momentDetailCommentBtnSend);
		btnCommentSend.setText("取消");
		final String myMessageId = messageId;
		btnCommentSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pointId = "0";

				String discusscontent = editComment.getText().toString().trim();

				if (discusscontent.length() > 0) {
					// 先发到百度，发送成功后才发送到服务器上
					sendMomentCommentToBaiDu(myMessageId, pointId,
							discusscontent);
				} else {

				}
				editComment.setText("");
				relComment.setVisibility(View.GONE);
				imm.hideSoftInputFromWindow(btnCommentSend.getWindowToken(), 0);
			}
		});

	}

	
	TextWatcher EditTextChangerListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String str = editComment.getText().toString();
			if (str.length() <= 0) {
				btnCommentSend.setText("取消");
			} else {
				btnCommentSend.setText("评论");
			}
		}
	};

	/**
	 * 想百度数据库提交评论
	 * 
	 * @param messageId
	 *            ：动态的id（指向动态表）
	 * @param pointDiscussId
	 *            ：指向ID。最开始的一条设为0，后面跟帖的设为discussid
	 * @param discusscontent
	 *            ：评论内容
	 */
	public void sendMomentCommentToBaiDu(String messageId,
			String pointDiscussId, String discusscontent) {

		// TODO Auto-generated method stub
		if (discusscontent.length() > 0) {
			/*
			 * send to server
			 */
			creatPoi cre = new creatPoi();
			String time = FormatTime.getFormatTime();
			String account = SEMapApplication.AccountNumber;
			cre.setCoord_type(3);
			cre.setAddress(account);
			// 更新别人的动态数据时，不能修改别人发起动态的经纬度
			// cre.setLongitude(MainActivity.a);
			// cre.setLatitude(MainActivity.b);
			cre.setGeotable_id("74065");// 对动态的评论
			cre.setTitle("myDiscuss");
			Map<String, Object> map = new HashMap<String, Object>();
			// map.put("single", time+account);//唯一标识
			map.put("messageid", messageId);// 要评论的动态ID
			map.put("userid", account);// 上传时间
			map.put("pointdiscussid", pointDiscussId);
			map.put("uploadtime", time);
			map.put("username", SEMapApplication.LoginName);
			map.put("discusscontent", discusscontent);
			cre.setColumnkey(map);
			MainActivity.sendToBaidu(cre, FormatTime.getFormatTime(),
					protocolwithbaidustore.baidutablemessagediscuss);
			myCommentDiscuss = discusscontent;
			myCommentTime = time;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			// messageId
			intent.putExtra("resultMessageId", myCommentMessageId);

			// 返回是否点赞/踩/没有点
			String MomentDetailResultNum = "";
			if (PraiseFlag) {
				MomentDetailResultNum = "1";
			} else if (CriticizenFlag) {
				MomentDetailResultNum = "-1";
			} else {
				MomentDetailResultNum = "0";
			}
			intent.putExtra("MomentDetailResultNum", MomentDetailResultNum);

			// 返回评论/没有评论
			intent.putExtra("resultMomentDetailCommentLists",
					(Serializable) resultDiscussLists);
			setResult(MomentDetail_OK, intent);

			MomentDetail.this.finish();
			overridePendingTransition(R.anim.z_push_myleft_in,
					R.anim.z_push_myleft_out);
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置ListView的高度
	 * 注意注意：这里对应的Item必须是LinearLayout，对应的item布局的最外层至少有两个LinearLayout
	 * 一个是整个布局的，一个是包括所有组件的，否则设置的高度会不对 设置ListView的高度，否则只能显示1-2行）
	 * 在setAdapter之后，在设置ListView的高度（不是wrap_content）,其高度 = 每行高度 * 行数 + 行间距 * （行数
	 * - 1），用ViewGroup.LayoutParams 设置ListView 的高度
	 * 
	 * @param mListView
	 */
	private void setLvHeight(ListView mListView) {
		ListAdapter adapter = mListView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View itemView = adapter.getView(i, null, mListView);
			itemView.measure(0, 0);
			// 由于之前的显示不完全，故现在将高度乘以2
			totalHeight += itemView.getMeasuredHeight();
		}
		ViewGroup.LayoutParams layoutParams = mListView.getLayoutParams();
		// 为了在最下面还能多出来一些空间，在原来基础上加上30
		layoutParams.height = 30 + totalHeight
				+ (mListView.getDividerHeight() * (adapter.getCount() - 1));// 总行高+每行的间距
		mListView.setLayoutParams(layoutParams);
	}
}