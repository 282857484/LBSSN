package pri.z.show;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pri.z.main.MainActivity;
import pri.z.main.XListView;
import pri.z.main.XListView.IXListViewListener;
import pri.z.photoshow.ViewPagerFromNetActivity;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.DiscussMoment;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.SearchMomentDiscussByMessageId;
import pub.httptransfer.SearchMomentsByUserId;
import pub.httptransfer.SearchOneMomentDetailById;
import pub.httptransfer.SearchPoiById;
import pub.httptransfer.creatPoi;
import pub.httptransfer.updatePoi;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.myserver.protocol.getActivityInfo_C;
import pub.infoclass.myserver.protocol.getRelaion_C;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.util.FormatTime;
import pub.util.ImageManager3;
import pub.util.ImageManager4;
import pub.util.timeCha;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CenterMoment extends Activity implements IXListViewListener {

	Context mContext;
	public static final String TAG = "哈哈哈哈";
	List<MomentBaseInfo> momentLists = new ArrayList<MomentBaseInfo>();
	List<DiscussMoment> DiscussLists = new ArrayList<DiscussMoment>();
	// 动态的详情的请求码和返回码
	public static int MomentDetailRequest = 604;
	public static int MomentDetail_OK = 704;// 有数据返回
	TextView tvMomentPraise;// 点赞的数目
	TextView tvMomentCriticizen;// 点踩的数目
	private InputMethodManager imm;
	private int scrollPos;
	private int scrollTop;
	ProgressBar progressBar;
	XListView momentListView;
	ImageView imgHasNoData;
	MomentCenterMomentAdapter momentAdapter;
	boolean momentNoFirstTime = false;
	String momentCenterUserId;
	int pageIndexMoments = 1;// 加载更多数据的page_index
	public int UpdatePosition = -1;// 记录用户赞或踩的List下标

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		setContentView(R.layout.z_centermoment);

		momentCenterUserId = getIntent().getStringExtra("momentCenterUserId");

		new Thread(new SearchMomentsByUserId(momentCenterUserId, "0")).start();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		progressBar = (ProgressBar) findViewById(R.id.z_centerMomentProgressBar);
		imgHasNoData = (ImageView) findViewById(R.id.z_centerMomentNoData);
		if (!UtilsZZK.checkNetworkState(CenterMoment.this)) {
			progressBar.setVisibility(View.GONE);
			imgHasNoData.setVisibility(View.VISIBLE);
			return;
		}
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		momentListView = (XListView) findViewById(R.id.z_centerMomentlistview);

		momentListView.setPullLoadEnable(true);
		momentListView.setXListViewListener(CenterMoment.this);
		momentListView.setFastScrollEnabled(true);
		momentListView.setOnScrollListener(scrollListener);
		momentListView.setCacheColorHint(1);

	}

	private void initMomentCenterListView() {
		momentListView.setVisibility(View.VISIBLE);
		imgHasNoData.setVisibility(View.GONE);
		if (momentLists != null && momentLists.size() > 0) {
			momentAdapter = new MomentCenterMomentAdapter(mContext,
					momentLists, null);
			momentListView.setAdapter(momentAdapter);
		}

	}

	public void sendGetMomentUserIdInfomationMsg() {
		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();
		li.SearchUserID = momentCenterUserId;
		li.setUserID(SEMapApplication.AccountNumber);

		li.setStandardUploadTime();

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

	/**
	 * 得到用户关注的活动的信息
	 */
	public void sendGetMomentUserIdRelationActivitiesMsg(String page_index) {
		Message msg = Message.obtain();
		getRelaion_C li = new getRelaion_C();
		li.setSearchUserID(momentCenterUserId);
		li.setUserID(SEMapApplication.AccountNumber);
		li.setPageIndex(page_index);

		li.setStandardUploadTime();

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

	/**
	 * 得到用户关注的活动的信息
	 */
	public void sendGetActivityInfomationMsg(String activityId) {
		Message msg = Message.obtain();
		getActivityInfo_C li = new getActivityInfo_C();
		li.setActivityID(activityId);
		li.setUserID(SEMapApplication.AccountNumber);

		li.setStandardUploadTime();

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

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;

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

					// 更新到ListView对应的数据上
					if (UpdatePosition != -1) {
						MomentBaseInfo mInfo = momentLists.get(UpdatePosition);
						mInfo.PraiseNum = praiseNum;
						momentLists.remove(UpdatePosition);
						momentLists.add(UpdatePosition, mInfo);
					}

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
					// //关闭数据库连接
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

					// 更新到ListView对应的数据上
					if (UpdatePosition != -1) {
						MomentBaseInfo mInfo = momentLists.get(UpdatePosition);
						mInfo.CritizenNum = criticizeNum;
						momentLists.remove(UpdatePosition);
						momentLists.add(UpdatePosition, mInfo);
					}

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
					// //关闭数据库连接
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

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case z_baiduprotocol.baiduOneUserAllMoments:// 得到用户的所有动态
				try {
					onLoadR();// 停止刷新
					progressBar.setVisibility(View.GONE);
					boolean HasDataAddFlag = false;// 每次是否有新的数据添加
					JSONObject json = new JSONObject(String.valueOf(msg.obj));

					String size = json.getString("size");
					if (size.equals("0")) {// 如果没有更多的动态了
						if (!momentNoFirstTime) {// 第一次加载
							// 显示无数据的图
							imgHasNoData.setVisibility(View.VISIBLE);
						}
					}
					JSONArray jsonArray = new JSONArray(json.getString("pois"));
					int jsonLength = jsonArray.length();
					for (int index = 0; index < jsonLength; index++) {
						JSONObject jsonObj = (JSONObject) jsonArray.get(index);
						MomentBaseInfo dismom = new MomentBaseInfo();
						dismom.MessageId = jsonObj.getString("id");
						dismom.UserId = jsonObj.getString("univeralindex");
						dismom.UserName = jsonObj.getString("username");
						dismom.UploadTime = jsonObj.getString("uploadingtime");
						dismom.MomentContent = jsonObj
								.getString("broadcastcontent");
						dismom.PraiseNum = jsonObj.getString("praisenumber");
						dismom.CritizenNum = jsonObj
								.getString("criticizenumber");
						dismom.ShowPhotos = jsonObj.getString("photo1");
						dismom.Location = jsonObj.getString("location");
						dismom.Sex = jsonObj.getString("sex");
						dismom.Age = jsonObj.getString("age");
						new Thread(new SearchMomentDiscussByMessageId(
								dismom.MessageId)).start();
						// 去掉动态有重复的
						int myIndex = RemoveDuplicateMoment(momentLists, dismom);
						if (myIndex == -1) {
							momentLists.add(dismom);
							HasDataAddFlag = true;
						} else {
							momentLists.remove(myIndex);
							momentLists.add(myIndex, dismom);
						}

					}
					// 将动态加载到Activity上
					if (!momentNoFirstTime) {// 第一次加载
						initMomentCenterListView();
						momentNoFirstTime = true;
					} else {// 后面加载更多数据的加载
						momentAdapter.notifyDataSetChanged();
					}
					if (!HasDataAddFlag) {

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case z_baiduprotocol.baiduMomentDiscuss:// 得到用户的评论
				try {
					JSONObject json = new JSONObject(String.valueOf(msg.obj));

					JSONArray jsonArray = new JSONArray(json.getString("pois"));
					int jsonLength = jsonArray.length();
					for (int index = 0; index < jsonLength; index++) {
						JSONObject jsonObj = (JSONObject) jsonArray.get(index);

						DiscussMoment dismom = new DiscussMoment();
						dismom.DiscussUid = jsonObj.getString("id");
						dismom.DiscussMessageId = jsonObj
								.getString("messageid");
						dismom.DiscussUserId = jsonObj.getString("userid");
						dismom.DiscussUserName = jsonObj.getString("username");
						dismom.DiscussPointId = jsonObj
								.getString("pointdiscussid");
						dismom.DiscussUploadTime = jsonObj
								.getString("uploadtime");
						dismom.DiscussContent = jsonObj
								.getString("discusscontent");

						if (!UtilsTrans.RemoveDuplicateDiscuss(DiscussLists,
								dismom)) {
							DiscussLists.add(dismom);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}
		}

	};

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	/**
	 * 如果List中已经存在了一样的MomentBaseInfo,则返回index 如果不存在则返回 -1
	 */
	public int RemoveDuplicateMoment(List<MomentBaseInfo> myList,
			MomentBaseInfo myMon) {

		int flag = -1;
		if (myList == null)
			return -1;
		else if (myList.size() <= 0)
			return -1;

		else if (myList.size() > 0) {
			for (int index = 0; index < myList.size(); index++) {
				boolean flag1 = myMon.UploadTime
						.equals(myList.get(index).UploadTime);
				boolean flag2 = myMon.MomentContent
						.equals(myList.get(index).MomentContent);

				if (flag1 && flag2) {
					flag = index;
					break;
				}
			}
		}
		return flag;
	}

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

	private class MomentCenterMomentAdapter extends BaseAdapter {
		Context mContext;
		List<MomentBaseInfo> momentLists;

		public MomentCenterMomentAdapter(Context mContext,
				List<MomentBaseInfo> momentLists,
				List<DiscussMoment> discussLists) {
			this.mContext = mContext;
			this.momentLists = momentLists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return momentLists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return momentLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			View view = convertView;
			ViewHolder holder = null;
			MomentBaseInfo moment = momentLists.get(position);
			if (view == null) {

				holder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_itemof_moment, parent, false);

				holder.imgHead = (ImageView) view.findViewById(R.id.w_friend);
				holder.tvUserName = (TextView) view
						.findViewById(R.id.w_frienddescription);
				holder.tvComment = (TextView) view
						.findViewById(R.id.w_contentOfMovement);
				holder.LikeLayout = (RelativeLayout) view
						.findViewById(R.id.w_Like);
				holder.UnLikeLayout = (RelativeLayout) view
						.findViewById(R.id.w_UnLike);
				holder.commentLayout = (RelativeLayout) view
						.findViewById(R.id.w_comment);
				holder.publishTime = (TextView) view.findViewById(R.id.w_times);

				// /////
				holder.tvPraiseNum = (TextView) view
						.findViewById(R.id.z_momentPraiseNum);
				holder.tvCritizenNum = (TextView) view
						.findViewById(R.id.z_momentTrampleNum);
				holder.tvPraiseAnim = (TextView) view
						.findViewById(R.id.z_momentPraiseAnim);
				holder.tvCritizenAnim = (TextView) view
						.findViewById(R.id.z_momentTrampleAnim);

				// //下面的三张图片
				holder.imgPhoto1 = (ImageView) view
						.findViewById(R.id.z_itemMomentPhoto1);
				holder.imgPhoto2 = (ImageView) view
						.findViewById(R.id.z_itemMomentPhoto2);
				holder.imgPhoto3 = (ImageView) view
						.findViewById(R.id.z_itemMomentPhoto3);

				// 后面加的属性
				holder.tvDistance = (TextView) view
						.findViewById(R.id.z_itemMomentDistanceTv);
				holder.tvSexAndAge = (TextView) view
						.findViewById(R.id.z_itemMomentSexAndAgeTv);
				holder.tvCommentNum = (TextView) view
						.findViewById(R.id.z_itemMomentCommentNumTv);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.tvSexAndAge.setVisibility(View.GONE);
			ImageManager3.from(mContext).displayNewImage(
					holder.imgHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ moment.getUserId() + ".png",
					R.drawable.z_logindefault);

			String username = moment.getUserName();
			holder.tvUserName.setText(UtilsTrans.getUserName(username));
			String sexStr = moment.Sex;
			if (sexStr.equals("1")) {
				holder.tvUserName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernameman));
			} else {
				holder.tvUserName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernamewoman));
			}
			// holder.publishTime.setText(GetTime.getNoYearTime((String)
			// mData.get(position).get(Key_MomentUploadTime)));
			String TimeStart = moment.getUploadTime();
			String TimeEnd = FormatTime.getFormatTime();

			holder.publishTime.setText(timeCha.MillisToWord(timeCha.TimeCha(
					TimeStart, TimeEnd)));
			holder.tvComment.setText(moment.MomentContent);
			holder.tvPraiseNum.setText(moment.PraiseNum);
			holder.tvCritizenNum.setText(moment.CritizenNum);

			String momentLocation = String.valueOf(moment.Location);
			String myLocation = "[" + MainActivity.a + "," + MainActivity.b
					+ "]";
			double finalDistance = UtilsZZK.getDistanceByLocation(myLocation,
					momentLocation);
			holder.tvDistance.setText(finalDistance + " km");

			if (sexStr.equals("1")) {
				holder.tvSexAndAge.setBackgroundResource(R.drawable.z_sex_man);
			} else {
				holder.tvSexAndAge
						.setBackgroundResource(R.drawable.z_sex_woman);
			}
			holder.tvSexAndAge.setText(moment.Age);

			// //加载图片
			String strPhoto1 = moment.ShowPhotos;
			final String strPhotoNet = strPhoto1;
			if (!strPhoto1.equals("0")) {// 如果用户有上传头像的话
				String[] strs = strPhoto1.split("#");

				if (strs.length > 0) {// 只有一张图片
					LinearLayout lili = (LinearLayout) view
							.findViewById(R.id.z_itemMomentPhotosLi);
					lili.setVisibility(View.VISIBLE);
					holder.imgPhoto1.setVisibility(View.VISIBLE);
					ImageManager4.from(mContext).displayImage(
							holder.imgPhoto1,
							"http://" + addressInfo.localIP + ":"
									+ addressInfo.visitPort + "/"
									+ addressInfo.visitFolderMomentXiaotu + "/"
									+ strs[0] + ".png",
							R.drawable.z_logindefault);
					holder.imgPhoto1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(CenterMoment.this,
									ViewPagerFromNetActivity.class);
							intent.putExtra("CurrentItem", 0);// 起始为0
							String[] strsNet = strPhotoNet.split("#");
							ArrayList<String> listsPhotoNet = new ArrayList<String>();
							for (int index = 0; index < strsNet.length; index++) {
								listsPhotoNet.add(strsNet[index]);
							}
							intent.putStringArrayListExtra("oneUserShowLists",
									listsPhotoNet);
							startActivity(intent);
						}
					});
					if (strs.length > 1) {// 有两张图片
						holder.imgPhoto2.setVisibility(View.VISIBLE);
						ImageManager4.from(mContext).displayImage(
								holder.imgPhoto2,
								"http://" + addressInfo.localIP + ":"
										+ addressInfo.visitPort + "/"
										+ addressInfo.visitFolderMomentXiaotu
										+ "/" + strs[1] + ".png",
								R.drawable.z_logindefault);
						holder.imgPhoto2
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(
												CenterMoment.this,
												ViewPagerFromNetActivity.class);
										intent.putExtra("CurrentItem", 1);// 起始为1
										String[] strsNet = strPhotoNet
												.split("#");
										ArrayList<String> listsPhotoNet = new ArrayList<String>();
										for (int index = 0; index < strsNet.length; index++) {
											listsPhotoNet.add(strsNet[index]);
										}
										intent.putStringArrayListExtra(
												"oneUserShowLists",
												listsPhotoNet);
										startActivity(intent);
									}
								});
						if (strs.length > 2) {// 有三张或者是三张以上的图片
							holder.imgPhoto3.setVisibility(View.VISIBLE);
							ImageManager4
									.from(mContext)
									.displayImage(
											holder.imgPhoto3,
											"http://"
													+ addressInfo.localIP
													+ ":"
													+ addressInfo.visitPort
													+ "/"
													+ addressInfo.visitFolderMomentXiaotu
													+ "/" + strs[2] + ".png",
											R.drawable.z_logindefault);
							holder.imgPhoto3
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											Intent intent = new Intent(
													CenterMoment.this,
													ViewPagerFromNetActivity.class);
											intent.putExtra("CurrentItem", 2);// 起始为2
											String[] strsNet = strPhotoNet
													.split("#");
											ArrayList<String> listsPhotoNet = new ArrayList<String>();
											for (int index = 0; index < strsNet.length; index++) {
												listsPhotoNet
														.add(strsNet[index]);
											}
											intent.putStringArrayListExtra(
													"oneUserShowLists",
													listsPhotoNet);
											startActivity(intent);
										}
									});
						} else {
							holder.imgPhoto3.setVisibility(View.GONE);
						}
					} else {
						holder.imgPhoto2.setVisibility(View.GONE);
						holder.imgPhoto3.setVisibility(View.GONE);
					}
				} else {
					LinearLayout lili = (LinearLayout) view
							.findViewById(R.id.z_itemMomentPhotosLi);
					lili.setVisibility(View.GONE);
					holder.imgPhoto1.setVisibility(View.GONE);
					holder.imgPhoto2.setVisibility(View.GONE);
					holder.imgPhoto3.setVisibility(View.GONE);
				}
			} else {// 用户没有上传头像
				LinearLayout lili = (LinearLayout) view
						.findViewById(R.id.z_itemMomentPhotosLi);
				lili.setVisibility(View.GONE);
				holder.imgPhoto1.setVisibility(View.GONE);
				holder.imgPhoto2.setVisibility(View.GONE);
				holder.imgPhoto3.setVisibility(View.GONE);
			}

			final RelativeLayout myLiComment = holder.commentLayout;
			final TextView myTvPraiseNum = holder.tvPraiseNum;
			final TextView mytvCritizenNum = holder.tvCritizenNum;
			final TextView myTvPraiseAnim = holder.tvPraiseAnim;
			final TextView mytvCritizenAnim = holder.tvCritizenAnim;

			// 不需要显示任何文字，避免荣不下0916
			holder.tvCommentNum.setText("0");
			if (DiscussLists != null && DiscussLists.size() > 0) {
				List<DiscussMoment> myDiscussList = UtilsTrans
						.getDiscussMomentByMessageId(moment.MessageId,
								DiscussLists);
				if (myDiscussList != null && myDiscussList.size() > 0) {
					holder.tvCommentNum.setText(myDiscussList.size() + "");
				}
			}
			final String myMessageId = moment.MessageId;
			holder.LikeLayout.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("static-access")
				public void onClick(View v) {
					final String messageId = myMessageId;

					if (getHasOrNo(messageId).equals("1")) {
						Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT)
								.show();
						return;
					} else if (getHasOrNo(messageId).equals("-1")) {
						Toast.makeText(mContext, "已踩", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					Animation animation = AnimationUtils.loadAnimation(
							mContext, R.anim.z_praise_animation);
					myTvPraiseAnim.setVisibility(View.VISIBLE);
					myTvPraiseAnim.startAnimation(animation);

					// postdelayed不能取消，，否则没有animation效果
					new Handler().postDelayed(new Runnable() {
						public void run() {
							myTvPraiseAnim.setVisibility(View.GONE);
							new Thread(new SearchPoiById(Integer
									.valueOf(messageId), 49442, 1)).start();

							UpdatePosition = position;
							tvMomentPraise = myTvPraiseNum;
							tvMomentCriticizen = mytvCritizenNum;
						}
					}, 1000);
				}
			});

			holder.UnLikeLayout.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("static-access")
				public void onClick(View v) {
					final String messageId = myMessageId;
					if (getHasOrNo(messageId).equals("1")) {
						Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT)
								.show();
						return;
					} else if (getHasOrNo(messageId).equals("-1")) {
						Toast.makeText(mContext, "已踩", Toast.LENGTH_SHORT)
								.show();
						return;
					}

					Animation animation = AnimationUtils.loadAnimation(
							mContext, R.anim.z_praise_animation);
					mytvCritizenAnim.setVisibility(View.VISIBLE);
					mytvCritizenAnim.startAnimation(animation);
					// postdelayed不能取消，，否则没有animation效果
					new Handler().postDelayed(new Runnable() {
						public void run() {
							mytvCritizenAnim.setVisibility(View.GONE);
							new Thread(new SearchPoiById(Integer
									.valueOf(messageId), 49442, -1)).start();
							UpdatePosition = position;
							tvMomentPraise = myTvPraiseNum;
							tvMomentCriticizen = mytvCritizenNum;
						}
					}, 1000);

				}
			});

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String messageId = myMessageId;
					// new Thread(new SearchMomentDiscussByMessageId(messageId))
					// .start();
					// new Thread(new SearchOneMomentDetailById(messageId))
					// .start();

					Intent intent = new Intent(mContext, MomentDetail.class);
					intent.putExtra("MomentDetailId", messageId);
					startActivityForResult(intent, MomentDetailRequest);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}
			});

			return view;
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
			MomentBaseInfo[] moments = dbAdapter
					.queryOneMomentBaseInfo(messageId);
			// 关闭数据库连接
			dbAdapter.close();
			if (moments != null && moments.length > 0) {
				HasOrNo = moments[0].HasOrNo;
			}
			return HasOrNo;
		}

	}

	public final class ViewHolder {
		public ImageView imgHead;
		public TextView tvUserName;
		public TextView tvComment;
		public RelativeLayout LikeLayout;
		public RelativeLayout UnLikeLayout;
		public RelativeLayout commentLayout;
		public TextView publishTime;
		// public TableLayout TableComment;

		public TextView tvPraiseNum;
		public TextView tvCritizenNum;
		public TextView tvPraiseAnim;
		public TextView tvCritizenAnim;

		public ImageView imgPhoto1;// 图片集//最多执法三张
		public ImageView imgPhoto2;
		public ImageView imgPhoto3;

		// 后面加的
		public TextView tvDistance;// 距离
		public TextView tvSexAndAge;// 性别和年龄
		public TextView tvCommentNum;// 评论数
	}

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

		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		new Thread(new SearchMomentsByUserId(momentCenterUserId, "0")).start();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		new Thread(new SearchMomentsByUserId(momentCenterUserId,
				pageIndexMoments + "")).start();
		pageIndexMoments++;
	}

	private void onLoadR() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		String time = df.format(c.getTime());
		momentListView.stopRefresh();
		momentListView.stopLoadMore();
		momentListView.setRefreshTime(time);

	}

	private void onLoadM() {
		momentListView.stopRefresh();
		momentListView.stopLoadMore();
	}

	private OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// // scrollPos
			// scrollPos = mListView.getFirstVisiblePosition();
			// }
			// if (mData != null) {
			// View v = mListView.getChildAt(0);
			// scrollTop = (v == null) ? 0 : v.getTop();
			// }
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MomentDetailRequest) {// 活动详情的返回
			if (resultCode == MomentDetail_OK) {
				// 得到赞踩数
				String resultNum = data.getStringExtra("MomentDetailResultNum");
				String resultMessageId = data.getStringExtra("resultMessageId");// 得到返回的MessageID
				if (resultNum.equals("0")) {

				} else {// 如果赞了或者是踩了
						// 先得到对应动态的Map
					MomentBaseInfo mom = new MomentBaseInfo();
					for (int index = 0; index < momentLists.size(); index++) {
						if (resultMessageId
								.equals(momentLists.get(index).MessageId)) {
							mom = momentLists.get(index);
						}
					}
					if (resultNum.equals("1")) {// 赞
						String praiseNum = mom.PraiseNum;
						int Num = Integer.valueOf(praiseNum);
						int lastNum = Num + 1;
						mom.PraiseNum = lastNum + "";
					} else if (resultNum.equals("-1")) {// 踩
						String CritizenNum = mom.CritizenNum;
						int lastNum = Integer.valueOf(CritizenNum) + 1;
						mom.CritizenNum = lastNum + "";
					}
				}
				momentAdapter.notifyDataSetChanged();
				// momentListView.setAdapter(momentAdapter);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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