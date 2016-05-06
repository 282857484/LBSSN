package pri.z.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pri.z.build.ShareMoment;
import pri.z.main.XListView.IXListViewListener;
import pri.z.mydb.ContactPerson;
import pri.z.mydb.MobileContacts;
import pri.z.photoshow.ViewPagerFromNetActivity;
import pri.z.show.CenterInfo;
import pri.z.show.CommonSetting;
import pri.z.show.MomentDetail;
import pri.z.show.R;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.DiscussMoment;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.VersionUpdate;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.HTTPResponceInfo;
import pub.httptransfer.SearchMomentDiscussByMessageId;
import pub.httptransfer.SearchMoments;
import pub.httptransfer.SearchPoiById;
import pub.httptransfer.updatePoi;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.util.FormatTime;
import pub.util.ImageManager3;
import pub.util.ImageManager4;
import pub.util.timeCha;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Moment extends Fragment implements IXListViewListener {

	private int scrollPos;
	private int scrollTop;// 这是
	static int PageIndex = 1;// 最开始的时候是1，加载更多是要加载第1个十条（本来是从0开始的）
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();;
	private XListView mListView;
	public static final String TAG = "哈哈哈哈";
	static Context mContext;
	// 动态的详情的请求码和返回码
	public static int MomentDetailRequest = 304;
	public static int MomentDetail_OK = 305;// 有数据返回
	private TextView tvMomentPraise;
	private TextView tvMomentCriticizen;
	View viewAbc;
	// 广播的键
	public static final String Key_MomentMessageId = "messageID";
	public static final String Key_MomentUserID = "momentUserID";
	public static final String Key_MomentUserName = "momentUserName";
	public static final String Key_MomentUploadTime = "momentUploadTime";
	public static final String Key_MomentContent = "momentContent";
	public static final String Key_MomentPraiseNum = "momentPraiseNum";
	public static final String Key_MomentCritizenNum = "momentCritizenNum";
	public static final String Key_MomentMyUserId = "momentMyUserId";
	public static final String Key_MomentMyUserName = "momentMyUserName";
	public static final String key_MomentHasOrNo = "momentHasOrNo";
	public static final String key_MomentPhoto1 = "momentPhoto1";
	public static final String key_MomentLocation = "momentLocation";
	public static final String key_MomentSex = "momentSex";
	public static final String key_MomentAge = "momentAge";

	// 广播评论的键
	public static final String Key_DisCussMessageID = "discussMessageID";
	public static final String Key_DisCussUserId = "discussUserID";
	public static final String Key_DisCussPointID = "discussPointID";
	public static final String Key_DisCussUploadTime = "discussUploadTime";
	public static final String Key_DiscussUserName = "discussUserName";
	public static final String Key_DiscussContent = "discussContent";

	ProgressBar liProgressLoading;
	List<DiscussMoment> DiscussLists = new ArrayList<DiscussMoment>();
	DiscussMoment[] discussArray = null;
	boolean CreateViewFlag = false;
	int FirstTimeLoadData = 0;
	// 自己新加的评论数据
	String myCommentMessageId, myCommentTime, myCommentDiscuss;

	public static Context context;
	public static int loadMillis = 10000;// 加载数据的时间：加载数据时运行的最大时间
	ImageView imgNoRefreshData;
	RelativeLayout relNoData;
	// 确保加载数据只能是一种方式，要么加载更多，要么刷新
	public boolean LoadDataOnlyOne = true;
	public int UpdatePosition = -1;// 记录用户赞或踩的List下标
	static List<ContactPerson> listsContact;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getBaseContext();
		context = getActivity();
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		
		QueryUserRequest.queryMoments();
//		listsContact = MobileContacts.getContacts(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.z_lay_moment, container, false);
		viewAbc = view;
		initNoDataView(view);
		// 得到刷新加载
		// if (liProgressLoading == null)
		liProgressLoading = (ProgressBar) view
				.findViewById(R.id.z_mainMomentProgressLoading);
		// if (mListView == null)
		mListView = (XListView) view.findViewById(R.id.z_list_moment);

		// if (imgNoRefreshData == null)
		imgNoRefreshData = (ImageView) view
				.findViewById(R.id.z_noMomentRefreshData);

		liProgressLoading.setVisibility(View.GONE);
		// 在fragment之间切换
		if (CreateViewFlag) {
			if (mData.size() > 0) {
				mListView.setAdapter(mAdapter);
			}
		}
		CreateViewFlag = true;
		mListView.setOnScrollListener(scrollListener);
		mListView.setCacheColorHint(1);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		mListView.setFastScrollEnabled(true);
		return view;
	}

	private void initNoDataView(View view) {
		// TODO Auto-generated method stub
		relNoData = (RelativeLayout) view.findViewById(R.id.z_momentNoDataRel);
		Button btnNoDataSetting = (Button) view
				.findViewById(R.id.z_momentNoDataSetting);
		Button btnNoDataCreate = (Button) view
				.findViewById(R.id.z_momentNoDataShareMoment);
		Button btnNoDataRefresh = (Button) view
				.findViewById(R.id.z_momentNoDataRefresh);
		btnNoDataSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), CommonSetting.class));
			}
		});
		btnNoDataCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), ShareMoment.class));
			}
		});
		btnNoDataRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!UtilsZZK.checkNetworkState(mContext)){
					return;
				}
				relNoData.setVisibility(View.GONE);
				if (liProgressLoading != null) {
					liProgressLoading.setVisibility(View.VISIBLE);
				}
				SearchMonent();
			}
		});
	}

	public void onStart() {
		super.onStart();
	}

	/**
	 * 通过时间经行排序，每次更新数据时
	 * 
	 * @param lists
	 * @return
	 */
	public List<Map<String, Object>> sortMomentsByUploadTime(
			List<Map<String, Object>> lists) {
		if (lists == null)
			return null;
		if (lists.size() <= 1)
			return lists;
		Comparator comUploadTime = new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				Map<String, Object> a1 = (Map<String, Object>) o1;
				Map<String, Object> a2 = (Map<String, Object>) o2;

				return a2.get(Key_MomentUploadTime).toString()
						.compareTo(a1.get(Key_MomentUploadTime).toString());
			}

		};

		Collections.sort(lists, comUploadTime);
		return lists;
	}

	List<ActivityDiscussSelectData> DisLists;
	RelationSelectData relationData;// 用户对该活动的参加或者是关注的数据
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case protocolfromserver.CheckVersionAndOtherInfo_S:
				CheckVersionAndOtherInfo_S checkVersion = (CheckVersionAndOtherInfo_S) msg.obj;
				String mark = checkVersion.getMark();
				if (mark.equals("1")) {
					String url = checkVersion.getURL();
					File fileAppDownload = new File(
							MainActivity.UserFolderAppDownload);
					if (!fileAppDownload.exists()) {
						fileAppDownload.mkdirs();
					}
					String path = fileAppDownload + "";
					// String url =
					// "http://121.40.123.240:5984/_utils/image/se/SamplesActivity.apk";
					// String url = "http://192.168.1.105:80/MyApp.apk";
					new VersionUpdate().showDownLoadDialog(context, url, path);
				}
				break;
			case SQLiteProtocol.queryUserData:
				SQLResponse responseUser = (SQLResponse) msg.obj;
				if (responseUser.mark == 2)
					return;
				UserInfoSelectData[] users = (UserInfoSelectData[]) responseUser.result;
				if (users.length <= 0)
					return;
				UserInfoSelectData userData = users[0];
				// 加载用户基本信息保存到静态变量中
				MainActivity.initSEMapApplication(userData);
				break;
			case SQLiteProtocol.queryMomentData:
				SQLResponse responseMoment = (SQLResponse) msg.obj;
				if (responseMoment.mark == 2)
					return;
				MomentBaseInfo[] moments = (MomentBaseInfo[]) responseMoment.result;
				if (moments.length <= 0)
					return;
				getDataFromMobile(moments);
				break;
			case z_baiduprotocol.baiduSomeMomentsOnCreate:

				String resultOncreate = String.valueOf(msg.obj);
				if (!resultOncreate.equals("")) {
					LoadDataOnlyOne = true;
					if (mData.size() > 0)
						mData.removeAll(mData);
					try {
						JSONObject json = new JSONObject(
								String.valueOf(resultOncreate));

						JSONArray jsonArray = new JSONArray(
								json.getString("contents"));
						int jsonLength = jsonArray.length();

						// 不能删除数据库，否则就会刻意无数次点赞
						// if (jsonLength > 8) {
						// // 如果有超过八条的数据量，就把本地的数据删除
						// dbAdapter.deleteAllMomentBaseInfos();
						// }
						boolean InsertFlag = false;// 插入标志
						for (int index = 0; index < jsonLength; index++) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							Map<String, Object> map = new HashMap<String, Object>();
							map.put(Key_MomentMessageId,
									jsonObj.getString("uid"));
							map.put(Key_MomentUserID,
									jsonObj.getString("univeralindex"));
							map.put(Key_MomentUserName,
									jsonObj.getString("username"));
							map.put(Key_MomentUploadTime,
									jsonObj.getString("uploadingtime"));
							map.put(Key_MomentContent,
									jsonObj.getString("broadcastcontent"));
							map.put(Key_MomentPraiseNum,
									jsonObj.getString("praisenumber"));
							map.put(Key_MomentCritizenNum,
									jsonObj.getString("criticizenumber"));
							map.put(key_MomentPhoto1,
									jsonObj.getString("photo1"));
							map.put(key_MomentLocation,
									jsonObj.getString("location"));
							map.put(key_MomentSex, jsonObj.getString("sex"));
							map.put(key_MomentAge, jsonObj.getString("age"));

							// 将信息全部封装到类中
							MomentBaseInfo moment = new MomentBaseInfo();
							moment.MessageId = map.get(Key_MomentMessageId)
									.toString();// 存在百度上的动态id //唯一
							moment.UserId = map.get(Key_MomentUserID)
									.toString();// 发表动态的用户Id
							moment.UserName = map.get(Key_MomentUserName)
									.toString();// 发表动态的用户昵称
							moment.UploadTime = map.get(Key_MomentUploadTime)
									.toString();// 发表时间
							moment.MomentContent = map.get(Key_MomentContent)
									.toString();// 动态内容
							moment.PraiseNum = map.get(Key_MomentPraiseNum)
									.toString();// 赞的数量
							moment.CritizenNum = map.get(Key_MomentCritizenNum)
									.toString();// 踩的数量
							// moment.MyUserId = new
							// LoginInfo(mContext).getUserInfoPhone();//赞的人的id
							// moment.MyUserName = new
							// LoginInfo(mContext).getUserInfoName();//赞的人的昵称
							moment.MyUserId = "0";// 赞的人的id
							moment.MyUserName = "0";// 赞的人的昵称
							moment.HasOrNo = "0";
							moment.ShowPhotos = map.get(key_MomentPhoto1)
									.toString();
							moment.Location = map.get(key_MomentLocation)
									.toString();
							moment.Sex = map.get(key_MomentSex).toString();
							moment.Age = map.get(key_MomentAge).toString();

							String messageid = map.get(Key_MomentMessageId)
									.toString();
							new Thread(new SearchMomentDiscussByMessageId(
									messageid)).start();
							int myIndex = RemoveDuplicate(mData, map);
							if (myIndex == -1) {// 如果不存在
								InsertFlag = true;
								mData.add(0, map);
								// long count = dbAdapter
								// .insertMomentBaseInfo(moment);

								boolean flag = false;
								for (int q = 0; q < MainActivity.MapMomentMessageID
										.size(); q++) {
									if ((String
											.valueOf(MainActivity.MapMomentMessageID
													.get("MapMomentMessageID"
															+ q)))
											.equals(moment.MessageId)) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									ContentValues newValues = ContentValuesChange
											.contentInsertMomentBaseInfo(moment);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.Moment_Table,
											null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
								}

							} else {// 否则更新
								Map<String, Object> myMapUpdate = mData
										.get(myIndex);
								myMapUpdate.put(Key_MomentMessageId,
										jsonObj.getString("uid"));
								myMapUpdate.put(Key_MomentUserID,
										jsonObj.getString("univeralindex"));
								myMapUpdate.put(Key_MomentUserName,
										jsonObj.getString("username"));
								myMapUpdate.put(Key_MomentUploadTime,
										jsonObj.getString("uploadingtime"));
								myMapUpdate.put(Key_MomentContent,
										jsonObj.getString("broadcastcontent"));
								myMapUpdate.put(Key_MomentPraiseNum,
										jsonObj.getString("praisenumber"));
								myMapUpdate.put(Key_MomentCritizenNum,
										jsonObj.getString("criticizenumber"));
								myMapUpdate.put(key_MomentPhoto1,
										jsonObj.getString("photo1"));
								myMapUpdate.put(key_MomentLocation,
										jsonObj.getString("location"));
								myMapUpdate.put(key_MomentSex,
										jsonObj.getString("sex"));
								myMapUpdate.put(key_MomentAge,
										jsonObj.getString("age"));

								mData.remove(myIndex);
								mData.add(myMapUpdate);

								int MapSize = MainActivity.MapMomentMessageID
										.size();
								MainActivity.MapActivityID.put(
										"MapMomentMessageID" + MapSize,
										moment.MessageId);

								SQLInfo sqlInfoUpdate = new SQLInfo();
								sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
								ContentValues newValues = ContentValuesChange
										.contentUpdateOneMomentByMessageId(moment);
								String whereClause = ContentValuesChange.Key_MomentMessageId
										+ "=? ";
								String[] whereArgs = new String[] { String
										.valueOf(moment.MessageId) };
								sqlInfoUpdate.SQLInfo = new UpdateObject(
										ContentValuesChange.Moment_Table,
										newValues, whereClause, whereArgs);
								sqlSecurityThread.handleSQl(sqlInfoUpdate);

							}

						}
						mData = sortMomentsByUploadTime(mData);
						if (!InsertFlag) {
							// Toast.makeText(mContext, "没有数据了", 1).show();
							// imgNoRefreshData.setVisibility(View.VISIBLE);
							// new Handler().postDelayed(new Runnable() {
							// @Override
							// public void run() {
							// imgNoRefreshData.setVisibility(View.GONE);
							// }
							// }, 3000);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (mData.size() > 0) {
						if(relNoData != null)
							relNoData.setVisibility(View.GONE);
						if (mListView == null)
							mListView = (XListView) viewAbc
									.findViewById(R.id.z_list_moment);
						if (mListView != null)
							mListView.setAdapter(mAdapter);
					} else {
//						relNoData.setVisibility(View.VISIBLE);
					}

					liProgressLoading.setVisibility(View.GONE);
				}
				break;

			case z_baiduprotocol.baiduSomeMomentsOnRefresh:
				String resultOnRefresh = String.valueOf(msg.obj);
				if (!resultOnRefresh.equals("")) {
					LoadDataOnlyOne = true;
					onLoadR();
					try {
						JSONObject json = new JSONObject(
								String.valueOf(resultOnRefresh));

						JSONArray jsonArray = new JSONArray(
								json.getString("contents"));
						int jsonLength = jsonArray.length();
						// 不能删除数据库，否则就会刻意无数次点赞
						// if (jsonLength > 8) {
						// // 如果有超过八条的数据量，就把本地的数据删除
						// dbAdapter.deleteAllMomentBaseInfos();
						// }
						boolean InsertFlag = false;// 插入标志
						for (int index = jsonLength - 1; index >= 0; index--) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							Map<String, Object> map = new HashMap<String, Object>();
							map.put(Key_MomentMessageId,
									jsonObj.getString("uid"));
							map.put(Key_MomentUserID,
									jsonObj.getString("univeralindex"));
							map.put(Key_MomentUserName,
									jsonObj.getString("username"));
							map.put(Key_MomentUploadTime,
									jsonObj.getString("uploadingtime"));
							map.put(Key_MomentContent,
									jsonObj.getString("broadcastcontent"));
							map.put(Key_MomentPraiseNum,
									jsonObj.getString("praisenumber"));
							map.put(Key_MomentCritizenNum,
									jsonObj.getString("criticizenumber"));
							map.put(key_MomentPhoto1,
									jsonObj.getString("photo1"));
							map.put(key_MomentLocation,
									jsonObj.getString("location"));
							map.put(key_MomentSex, jsonObj.getString("sex"));
							map.put(key_MomentAge, jsonObj.getString("age"));

							// 将信息全部封装到类中
							MomentBaseInfo moment = new MomentBaseInfo();
							moment.MessageId = map.get(Key_MomentMessageId)
									.toString();// 存在百度上的动态id //唯一
							moment.UserId = map.get(Key_MomentUserID)
									.toString();// 发表动态的用户Id
							moment.UserName = map.get(Key_MomentUserName)
									.toString();// 发表动态的用户昵称
							moment.UploadTime = map.get(Key_MomentUploadTime)
									.toString();// 发表时间
							moment.MomentContent = map.get(Key_MomentContent)
									.toString();// 动态内容
							moment.PraiseNum = map.get(Key_MomentPraiseNum)
									.toString();// 赞的数量
							moment.CritizenNum = map.get(Key_MomentCritizenNum)
									.toString();// 踩的数量
							// moment.MyUserId = new
							// LoginInfo(mContext).getUserInfoPhone();//赞的人的id
							// moment.MyUserName = new
							// LoginInfo(mContext).getUserInfoName();//赞的人的昵称
							moment.MyUserId = "0";// 赞的人的id
							moment.MyUserName = "0";// 赞的人的昵称
							moment.HasOrNo = "0";
							moment.ShowPhotos = map.get(key_MomentPhoto1)
									.toString();
							moment.Location = map.get(key_MomentLocation)
									.toString();
							moment.Sex = map.get(key_MomentSex).toString();
							moment.Age = map.get(key_MomentAge).toString();

							String messageid = map.get(Key_MomentMessageId)
									.toString();
							new Thread(new SearchMomentDiscussByMessageId(
									messageid)).start();
							int myIndex = RemoveDuplicate(mData, map);
							if (myIndex == -1) {// 如果不存在
								InsertFlag = true;
								mData.add(0, map);
								// long count = dbAdapter
								// .insertMomentBaseInfo(moment);

								boolean flag = false;
								for (int q = 0; q < MainActivity.MapMomentMessageID
										.size(); q++) {
									if ((String
											.valueOf(MainActivity.MapMomentMessageID
													.get("MapMomentMessageID"
															+ q)))
											.equals(moment.MessageId)) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									ContentValues newValues = ContentValuesChange
											.contentInsertMomentBaseInfo(moment);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.Moment_Table,
											null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
								}
							} else {// 否则更新
								Map<String, Object> myMapUpdate = mData
										.get(myIndex);
								myMapUpdate.put(Key_MomentMessageId,
										jsonObj.getString("uid"));
								myMapUpdate.put(Key_MomentUserID,
										jsonObj.getString("univeralindex"));
								myMapUpdate.put(Key_MomentUserName,
										jsonObj.getString("username"));
								myMapUpdate.put(Key_MomentUploadTime,
										jsonObj.getString("uploadingtime"));
								myMapUpdate.put(Key_MomentContent,
										jsonObj.getString("broadcastcontent"));
								myMapUpdate.put(Key_MomentPraiseNum,
										jsonObj.getString("praisenumber"));
								myMapUpdate.put(Key_MomentCritizenNum,
										jsonObj.getString("criticizenumber"));
								myMapUpdate.put(key_MomentPhoto1,
										jsonObj.getString("photo1"));
								myMapUpdate.put(key_MomentLocation,
										jsonObj.getString("location"));
								myMapUpdate.put(key_MomentSex,
										jsonObj.getString("sex"));
								myMapUpdate.put(key_MomentAge,
										jsonObj.getString("age"));

								mData.remove(myIndex);
								mData.add(myMapUpdate);

								int MapSize = MainActivity.MapMomentMessageID
										.size();
								MainActivity.MapActivityID.put(
										"MapMomentMessageID" + MapSize,
										moment.MessageId);

								SQLInfo sqlInfoUpdate = new SQLInfo();
								sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
								ContentValues newValues = ContentValuesChange
										.contentUpdateOneMomentByMessageId(moment);
								String whereClause = ContentValuesChange.Key_MomentMessageId
										+ "=? ";
								String[] whereArgs = new String[] { String
										.valueOf(moment.MessageId) };
								sqlInfoUpdate.SQLInfo = new UpdateObject(
										ContentValuesChange.Moment_Table,
										newValues, whereClause, whereArgs);
								sqlSecurityThread.handleSQl(sqlInfoUpdate);
							}

						}
						mData = sortMomentsByUploadTime(mData);
						if (!InsertFlag) {
							// Toast.makeText(mContext, "没有数据了", 1).show();
							// imgNoRefreshData.setVisibility(View.VISIBLE);
							// new Handler().postDelayed(new Runnable() {
							// @Override
							// public void run() {
							// imgNoRefreshData.setVisibility(View.GONE);
							// }
							// }, 3000);
						}
						if(mData.size() <= 0){
							if(relNoData != null)
								relNoData.setVisibility(View.GONE);
						}
						mAdapter.notifyDataSetChanged();
						liProgressLoading.setVisibility(View.GONE);

					} catch (JSONException e) {
						e.printStackTrace();
					}
					mAdapter.notifyDataSetChanged();
					liProgressLoading.setVisibility(View.GONE);
				}

				break;

			case z_baiduprotocol.baiduSomeMomentsOnLoadmore:
				String resultOnLoadmore = String.valueOf(msg.obj);
				if (!resultOnLoadmore.equals("")) {
					LoadDataOnlyOne = true;
					onLoadR();
					try {
						JSONObject json = new JSONObject(
								String.valueOf(resultOnLoadmore));

						JSONArray jsonArray = new JSONArray(
								json.getString("contents"));
						int jsonLength = jsonArray.length();
						// 不能删除数据库，否则就会刻意无数次点赞
						// if (jsonLength > 8) {
						// // 如果有超过八条的数据量，就把本地的数据删除
						// dbAdapter.deleteAllMomentBaseInfos();
						// }
						boolean InsertFlag = false;// 插入标志
						for (int index = 0; index < jsonLength; index++) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							Map<String, Object> map = new HashMap<String, Object>();
							map.put(Key_MomentMessageId,
									jsonObj.getString("uid"));
							map.put(Key_MomentUserID,
									jsonObj.getString("univeralindex"));
							map.put(Key_MomentUserName,
									jsonObj.getString("username"));
							map.put(Key_MomentUploadTime,
									jsonObj.getString("uploadingtime"));
							map.put(Key_MomentContent,
									jsonObj.getString("broadcastcontent"));
							map.put(Key_MomentPraiseNum,
									jsonObj.getString("praisenumber"));
							map.put(Key_MomentCritizenNum,
									jsonObj.getString("criticizenumber"));
							map.put(key_MomentPhoto1,
									jsonObj.getString("photo1"));
							map.put(key_MomentLocation,
									jsonObj.getString("location"));
							map.put(key_MomentSex, jsonObj.getString("sex"));
							map.put(key_MomentAge, jsonObj.getString("age"));

							// 将信息全部封装到类中
							MomentBaseInfo moment = new MomentBaseInfo();
							moment.MessageId = map.get(Key_MomentMessageId)
									.toString();// 存在百度上的动态id //唯一
							moment.UserId = map.get(Key_MomentUserID)
									.toString();// 发表动态的用户Id
							moment.UserName = map.get(Key_MomentUserName)
									.toString();// 发表动态的用户昵称
							moment.UploadTime = map.get(Key_MomentUploadTime)
									.toString();// 发表时间
							moment.MomentContent = map.get(Key_MomentContent)
									.toString();// 动态内容
							moment.PraiseNum = map.get(Key_MomentPraiseNum)
									.toString();// 赞的数量
							moment.CritizenNum = map.get(Key_MomentCritizenNum)
									.toString();// 踩的数量
							// moment.MyUserId = new
							// LoginInfo(mContext).getUserInfoPhone();//赞的人的id
							// moment.MyUserName = new
							// LoginInfo(mContext).getUserInfoName();//赞的人的昵称
							moment.MyUserId = "0";// 赞的人的id
							moment.MyUserName = "0";// 赞的人的昵称
							moment.HasOrNo = "0";
							moment.ShowPhotos = map.get(key_MomentPhoto1)
									.toString();
							moment.Location = map.get(key_MomentLocation)
									.toString();
							moment.Sex = map.get(key_MomentSex).toString();
							moment.Age = map.get(key_MomentAge).toString();

							String messageid = map.get(Key_MomentMessageId)
									.toString();
							new Thread(new SearchMomentDiscussByMessageId(
									messageid)).start();
							int myIndex = RemoveDuplicate(mData, map);
							if (myIndex == -1) {// 如果不存在
								InsertFlag = true;
								mData.add(map);
								// long count = dbAdapter
								// .insertMomentBaseInfo(moment);

								boolean flag = false;
								for (int q = 0; q < MainActivity.MapMomentMessageID
										.size(); q++) {
									if ((String
											.valueOf(MainActivity.MapMomentMessageID
													.get("MapMomentMessageID"
															+ q)))
											.equals(moment.MessageId)) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									ContentValues newValues = ContentValuesChange
											.contentInsertMomentBaseInfo(moment);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.Moment_Table,
											null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
								}
							} else {// 否则更新
								Map<String, Object> myMapUpdate = mData
										.get(myIndex);
								myMapUpdate.put(Key_MomentMessageId,
										jsonObj.getString("uid"));
								myMapUpdate.put(Key_MomentUserID,
										jsonObj.getString("univeralindex"));
								myMapUpdate.put(Key_MomentUserName,
										jsonObj.getString("username"));
								myMapUpdate.put(Key_MomentUploadTime,
										jsonObj.getString("uploadingtime"));
								myMapUpdate.put(Key_MomentContent,
										jsonObj.getString("broadcastcontent"));
								myMapUpdate.put(Key_MomentPraiseNum,
										jsonObj.getString("praisenumber"));
								myMapUpdate.put(Key_MomentCritizenNum,
										jsonObj.getString("criticizenumber"));
								myMapUpdate.put(key_MomentPhoto1,
										jsonObj.getString("photo1"));
								myMapUpdate.put(key_MomentLocation,
										jsonObj.getString("location"));
								myMapUpdate.put(key_MomentSex,
										jsonObj.getString("sex"));
								myMapUpdate.put(key_MomentAge,
										jsonObj.getString("age"));

								mData.remove(myIndex);
								mData.add(myMapUpdate);

								int MapSize = MainActivity.MapMomentMessageID
										.size();
								MainActivity.MapActivityID.put(
										"MapMomentMessageID" + MapSize,
										moment.MessageId);

								SQLInfo sqlInfoUpdate = new SQLInfo();
								sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
								ContentValues newValues = ContentValuesChange
										.contentUpdateOneMomentByMessageId(moment);
								String whereClause = ContentValuesChange.Key_MomentMessageId
										+ "=? ";
								String[] whereArgs = new String[] { String
										.valueOf(moment.MessageId) };
								sqlInfoUpdate.SQLInfo = new UpdateObject(
										ContentValuesChange.Moment_Table,
										newValues, whereClause, whereArgs);
								sqlSecurityThread.handleSQl(sqlInfoUpdate);
							}

						}

						mData = sortMomentsByUploadTime(mData);
						if (!InsertFlag) {
							// Toast.makeText(mContext, "没有数据了", 1).show();
						}
						if(mData.size() <= 0){
							if(relNoData != null)
								relNoData.setVisibility(View.GONE);
						}
						mAdapter.notifyDataSetChanged();
						PageIndex++;
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				// mListView.setSelectionFromTop(scrollPos, scrollTop);
				break;

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
						Map<String, Object> myMap = mData.get(UpdatePosition);
						myMap.put(Key_MomentPraiseNum, praiseNum);
						mData.remove(UpdatePosition);
						mData.add(UpdatePosition, myMap);
					}

					if (tvMomentPraise != null) {
						tvMomentPraise.setText(praiseNum);
						// tvMomentPraise.setTextColor(Color.RED);
					}
					if (tvMomentCriticizen != null) {
						tvMomentCriticizen.setText(criticizeNum);
					}

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
						Map<String, Object> myMap = mData.get(UpdatePosition);
						myMap.put(Key_MomentCritizenNum, criticizeNum);
						mData.remove(UpdatePosition);
						mData.add(UpdatePosition, myMap);
					}

					if (tvMomentPraise != null) {
						tvMomentPraise.setText(praiseNum);
					}
					if (tvMomentCriticizen != null) {
						tvMomentCriticizen.setText(criticizeNum);
						// tvMomentCriticizen.setTextColor(Color.RED);
					}
					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter.updateOneMomentByMessageId(id, praiseNum,
					// criticizeNum, "-1");
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
			case z_baiduprotocol.baiduMomentDiscuss:// 得到用户的评论
				try {
					JSONObject json = new JSONObject(String.valueOf(msg.obj));

					JSONArray jsonArray = new JSONArray(json.getString("pois"));
					int jsonLength = jsonArray.length();
					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
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

					// mAdapter.notifyDataSetChanged();
					// mListView.setAdapter(mAdapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case protocolwithbaidustore.replybaidutablemessagePOI:
				HTTPResponceInfo http = (HTTPResponceInfo) msg.obj;
				if (http.getStatus() == 0) {// 发表后加载数据
					new Thread(new SearchMoments("0", mContext,
							Fragment_Moment.myMessenger,
							z_baiduprotocol.baiduSomeMomentsOnRefresh)).start();
				}

				break;
			}
		}
	};

	public Messenger remoteMessenger = null;
	public static Messenger myMessenger = null;

	public static void SearchMonent() {
		new Thread(new SearchMoments(0 + "", mContext, myMessenger,
				z_baiduprotocol.baiduSomeMomentsOnCreate)).start();
	}

	// 修改数据
	public void updateMyBaiduData(int id, String praiseNum, String criticizeNum) {

		updatePoi cre = new updatePoi();
		cre.setId(id);// /必须
		cre.setCoord_type(3);// 必须
		// 更新别人的动态数据时，不能修改别人发起动态的经纬度
		// cre.setLatitude(MainActivity.a);
		// cre.setLongitude(MainActivity.b);
		cre.setGeotable_id("49442");// 必须
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("praisenumber", praiseNum);
		map.put("criticizenumber", criticizeNum);
		cre.setColumnkey(map);
		MainActivity.sendToBaidu(cre, FormatTime.getFormatTime(),
				protocolwithbaidustore.baidutableupdatemessagediscuss);

	}

	@Override
	public void onDestroy() {
		// getActivity().unbindService(connection);

		super.onDestroy();
	}

	private void getDataFromMobile(MomentBaseInfo[] moments) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//
		// DBAdapter dbAdapter = new DBAdapter(getActivity());
		// dbAdapter.open();
		// MomentBaseInfo[] moments = dbAdapter.queryAllMomentBaseInfoTop10();
		//
		// //关闭数据库连接
		// dbAdapter.close();
		//
		// if (moments == null || moments.length == 0) {
		// return list;
		// }
		// for (int i = 0; i < moments.length; i++) {
		// Map<String, Object> map = new HashMap<String, Object>();
		//
		// map.put(Key_MomentMessageId, moments[i].MessageId);
		// map.put(Key_MomentUserID, moments[i].UserId);
		// map.put(Key_MomentUserName, moments[i].UserName);
		// map.put(Key_MomentUploadTime, moments[i].UploadTime);
		// map.put(Key_MomentContent, moments[i].MomentContent);
		// map.put(Key_MomentPraiseNum, moments[i].PraiseNum);
		// map.put(Key_MomentCritizenNum, moments[i].CritizenNum);
		// map.put(Key_MomentMyUserId, moments[i].MyUserId);
		// map.put(Key_MomentMyUserName, moments[i].MyUserName);
		// map.put(key_MomentHasOrNo, moments[i].HasOrNo);
		//
		// map.put(key_MomentPhoto1, moments[i].ShowPhotos);
		// map.put(key_MomentLocation, moments[i].Location);
		// map.put(key_MomentSex, moments[i].Sex);
		// map.put(key_MomentAge, moments[i].Age);
		// list.add(map);
		// }

		if (moments == null)
			return;
		if (moments.length == 0)
			return;

		for (int index = 0; index < moments.length; index++) {
			MainActivity.MapMomentMessageID.put("MapMomentMessageID" + index,
					moments[index].MessageId);
		}

		// 只取十条数据
		if (moments.length >= 10) {
			for (int i = moments.length - 1; i >= moments.length - 10; i--) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put(Key_MomentMessageId, moments[i].MessageId);
				map.put(Key_MomentUserID, moments[i].UserId);
				map.put(Key_MomentUserName, moments[i].UserName);
				map.put(Key_MomentUploadTime, moments[i].UploadTime);
				map.put(Key_MomentContent, moments[i].MomentContent);
				map.put(Key_MomentPraiseNum, moments[i].PraiseNum);
				map.put(Key_MomentCritizenNum, moments[i].CritizenNum);
				map.put(Key_MomentMyUserId, moments[i].MyUserId);
				map.put(Key_MomentMyUserName, moments[i].MyUserName);
				map.put(key_MomentHasOrNo, moments[i].HasOrNo);

				map.put(key_MomentPhoto1, moments[i].ShowPhotos);
				map.put(key_MomentLocation, moments[i].Location);
				map.put(key_MomentSex, moments[i].Sex);
				map.put(key_MomentAge, moments[i].Age);
				list.add(map);
			}
		} else {
			for (int i = 0; i < moments.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put(Key_MomentMessageId, moments[i].MessageId);
				map.put(Key_MomentUserID, moments[i].UserId);
				map.put(Key_MomentUserName, moments[i].UserName);
				map.put(Key_MomentUploadTime, moments[i].UploadTime);
				map.put(Key_MomentContent, moments[i].MomentContent);
				map.put(Key_MomentPraiseNum, moments[i].PraiseNum);
				map.put(Key_MomentCritizenNum, moments[i].CritizenNum);
				map.put(Key_MomentMyUserId, moments[i].MyUserId);
				map.put(Key_MomentMyUserName, moments[i].MyUserName);
				map.put(key_MomentHasOrNo, moments[i].HasOrNo);

				map.put(key_MomentPhoto1, moments[i].ShowPhotos);
				map.put(key_MomentLocation, moments[i].Location);
				map.put(key_MomentSex, moments[i].Sex);
				map.put(key_MomentAge, moments[i].Age);
				list.add(map);
			}
		}

		// 排序
		mData = sortMomentsByUploadTime(list);

		if (mData.size() > 0) {
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
		remoteMessenger = SEMapApplication.serviceMessenger;

		//改变距离后产生的效果//如果用了的话，每次ListView的位置就会置顶
		
		//解决方法：设置一个变量：静态flag=false,每次改了之后变为true，setAdapter后变味false
		if(MainActivity.modifyMomentDistanceFlag){

			if (mData.size() > 0) {
				relNoData.setVisibility(View.GONE);
				removeOutOfDistanceMoment(mData);
				mListView.setAdapter(mAdapter);
				
			} 
			MainActivity.modifyMomentDistanceFlag = false;
		}

	}
	/**
	 * 消除超出范围的数据
	 * 
	 * @param lists
	 */
	private void removeOutOfDistanceMoment(List<Map<String, Object>> lists) {

		if (lists == null) {
			return;
		}
		if (lists.size() > 0) {
			// List从后往前删
			for (int index = lists.size() - 1; index >= 0; index--) {

				String momentLocation = lists.get(index).get(key_MomentLocation)
						.toString();;
				String myLocation = "[" + MainActivity.a + "," + MainActivity.b
						+ "]";
				double finalDistance = UtilsZZK.getDistanceByLocation(
						myLocation, momentLocation);

				if (finalDistance > (MainActivity.SearchMomentDistance / 1000)) {
					lists.remove(index);
				}

			}
		}
	}
	
	/**
	 * 如果List中已经存在了一样的ActivitySelectData,则返回index 如果不存在则返回 -1
	 */
	public int RemoveDuplicate(List<Map<String, Object>> myList,
			Map<String, Object> myMon) {
		int flag = -1;
		if (myList == null)
			return -1;
		else if (myList.size() <= 0)
			return -1;
		else if (myList.size() > 0) {
			for (int index = 0; index < myList.size(); index++) {
				boolean flag1 = myMon.get(Key_MomentContent).equals(
						myList.get(index).get(Key_MomentContent));
				boolean flag2 = myMon.get(Key_MomentUploadTime).equals(
						myList.get(index).get(Key_MomentUploadTime));
				if (flag1 && flag2) {
					flag = index;
					break;
				}
			}
		}

		return flag;
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

	private void onLoadR() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		Calendar c = Calendar.getInstance();
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		String time = df.format(c.getTime());
		mListView.setRefreshTime(time);
	}

	private void onLoadM() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onRefresh() {
		if (LoadDataOnlyOne) {
			new Thread(new SearchMoments("0", mContext, myMessenger,
					z_baiduprotocol.baiduSomeMomentsOnRefresh)).start();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					LoadDataOnlyOne = true;
					onLoadM();// 如果网络没有进来。HeadView 的刷新时间不许重新设置
				}
			}, loadMillis);
			LoadDataOnlyOne = false;
		} else {
			mListView.stopRefresh();
			return;
		}

	}

	@Override
	public void onLoadMore() {
		if (LoadDataOnlyOne) {
			Log.v("哈哈哈哈", "PageIndex=== "+PageIndex);
			new Thread(new SearchMoments(PageIndex + "", mContext, myMessenger,
					z_baiduprotocol.baiduSomeMomentsOnLoadmore)).start();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					LoadDataOnlyOne = true;
					onLoadM();
					// Toast.makeText(mContext, "网络连接异常，请稍后再试", 1).show();
				}
			}, loadMillis);
			LoadDataOnlyOne = false;
		} else {
			mListView.stopLoadMore();
			return;
		}

	}

	// 先将数组转化为List,然后将List按条件筛选，最后将筛选结果转化为数组
	public DiscussMoment[] getDiscussMomentByMessageIdInMobile(
			String messageId, DiscussMoment[] lists) {
		List<DiscussMoment> mylists = new ArrayList<DiscussMoment>();// 全部转化后的数据
		for (int index = 0; index < lists.length; index++) {
			mylists.add(lists[index]);
		}
		List<DiscussMoment> myGetList = new ArrayList<DiscussMoment>();// 存放筛选后的数据
		for (int index = 0; index < mylists.size(); index++) {
			if (messageId.equals(mylists.get(index).DiscussMessageId)) {
				myGetList.add(mylists.get(index));
			}
		}

		int length = myGetList.size();
		if (length == 0) {
			return null;
		}
		DiscussMoment[] myGetArray = new DiscussMoment[length];

		for (int x = 0; x < length; x++) {
			myGetArray[x] = myGetList.get(x);
		}
		return myGetArray;
	}


	
	private BaseAdapter mAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			View view = convertView;
			ViewHolder holder = null;
			if (view == null) {

				holder = new ViewHolder();
				view = LayoutInflater.from(getActivity()).inflate(
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
			ImageManager3.from(mContext).displayImage(
					holder.imgHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ mData.get(position).get(Key_MomentUserID)
							+ ".png", R.drawable.z_logindefault);

			// holder.publishTime.setText(GetTime.getNoYearTime((String)
			// mData.get(position).get(Key_MomentUploadTime)));
			String TimeStart = (String) mData.get(position).get(
					Key_MomentUploadTime);
			String TimeEnd = FormatTime.getFormatTime();

			holder.publishTime.setText(timeCha.MillisToWord(timeCha.TimeCha(
					TimeStart, TimeEnd)));
			holder.tvComment.setText((String) mData.get(position).get(
					Key_MomentContent));
			holder.tvPraiseNum.setText(String.valueOf(mData.get(position).get(
					Key_MomentPraiseNum)));
			holder.tvCritizenNum.setText(String.valueOf(mData.get(position)
					.get(Key_MomentCritizenNum)));

			String momentLocation = String.valueOf(mData.get(position).get(
					key_MomentLocation));
			String myLocation = "[" + MainActivity.a + "," + MainActivity.b
					+ "]";
			double finalDistance = UtilsZZK.getDistanceByLocation(myLocation,
					momentLocation);
			holder.tvDistance.setText(finalDistance + " km");

			String username = (String) mData.get(position).get(
					Key_MomentUserName);
			
//			String myName = "";
//			String userid = String.valueOf(mData.get(position).get(Key_MomentUserID));
//			
//			for(int i=0;i<listsContact.size();i++){
//				String phone = listsContact.get(i).getContactPhone().trim().replaceAll(" ", "");
//				if(userid.equals(phone)){
//					myName = listsContact.get(i).getContactName();
//				}
//			}
//			if(!myName.equals("")){
//				holder.tvUserName.setText(UtilsTrans.getUserName(myName));
//			}else{
				holder.tvUserName.setText(UtilsTrans.getUserName(username));
//			}
			

			String sexStr = (String) mData.get(position).get(key_MomentSex);
			if (sexStr.equals("1")) {
				holder.tvSexAndAge.setBackgroundResource(R.drawable.z_sex_man);
				holder.tvUserName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernameman));
			} else {
				holder.tvSexAndAge
						.setBackgroundResource(R.drawable.z_sex_woman);
				holder.tvUserName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernamewoman));
			}
			holder.tvSexAndAge.setText((String) mData.get(position).get(
					key_MomentAge));

			// //加载图片
			String strPhoto1 = (String) mData.get(position).get(
					key_MomentPhoto1);
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
							Intent intent = new Intent(getActivity(),
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
												getActivity(),
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
													getActivity(),
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

			holder.tvCommentNum.setText("0");
			if (DiscussLists != null && DiscussLists.size() > 0) {
				List<DiscussMoment> myDiscussList = UtilsTrans
						.getDiscussMomentByMessageId(
								mData.get(position).get(Key_MomentMessageId)
										.toString(), DiscussLists);
				if (myDiscussList != null && myDiscussList.size() > 0) {

					holder.tvCommentNum.setText(myDiscussList.size() + "");
				}
			} else {// 否则，查看数据库中是否还有评论的内容
				if (discussArray != null && discussArray.length > 0) {
					DiscussMoment[] myDiscussArray = getDiscussMomentByMessageIdInMobile(
							mData.get(position).get(Key_MomentMessageId)
									.toString(), discussArray);
					if (myDiscussArray != null && myDiscussArray.length > 0) {
						holder.tvCommentNum.setText(myDiscussArray.length + "");
					}

				}
			}

			holder.LikeLayout.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("static-access")
				public void onClick(View v) {
					final String messageId = mData.get(position)
							.get(Key_MomentMessageId).toString();

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
					final String messageId = mData.get(position)
							.get(Key_MomentMessageId).toString();
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

			holder.imgHead.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, CenterInfo.class);
					intent.putExtra("momentCenterUserId", mData.get(position)
							.get(Key_MomentUserID).toString());
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.z_push_left_in, R.anim.z_push_left_out);
				}
			});

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(mContext, MomentDetail.class);
					String messageId = mData.get(position)
							.get(Key_MomentMessageId).toString();
					intent.putExtra("MomentDetailId", messageId);
					startActivityForResult(intent, MomentDetailRequest);

					getActivity().overridePendingTransition(
							R.anim.z_push_left_in, R.anim.z_push_left_out);
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
					Map<String, Object> map = new HashMap<String, Object>();
					if (mData.size() <= 0)
						return;
					for (int index = 0; index < mData.size(); index++) {
						if (resultMessageId.equals(String.valueOf(mData.get(
								index).get(Key_MomentMessageId)))) {
							map = mData.get(index);
						}
					}
					if (resultNum.equals("1")) {// 赞
						String praiseNum = String.valueOf(map
								.get(Key_MomentPraiseNum));
						int Num = Integer.valueOf(praiseNum);
						int lastNum = Num + 1;
						map.put(Key_MomentPraiseNum, String.valueOf(lastNum));
					} else if (resultNum.equals("-1")) {// 踩
						String CritizenNum = String.valueOf(map
								.get(Key_MomentCritizenNum));
						int lastNum = Integer.valueOf(CritizenNum) + 1;
						map.put(Key_MomentCritizenNum, String.valueOf(lastNum));
					}
				}
				// 得到评论
				List<DiscussMoment> resultLists = (ArrayList<DiscussMoment>) data
						.getSerializableExtra("resultMomentDetailCommentLists");
				if (resultLists.size() > 0) {
					for (int index = 0; index < resultLists.size(); index++) {
						DiscussLists.add(0, resultLists.get(index));
					}
				}

				mAdapter.notifyDataSetChanged();
				mListView.setAdapter(mAdapter);
				mListView.setSelectionFromTop(scrollPos, scrollTop);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// The view is not scrolling.
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//				// scrollPos
				scrollPos = mListView.getFirstVisiblePosition();
//				if (imgAddMoment != null) {
//					imgAddMoment.setVisibility(View.VISIBLE);
//				}
			} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//				if (imgAddMoment != null) {
//					imgAddMoment.setVisibility(View.GONE);
//				}
			}
			if (mData != null) {
				View v = mListView.getChildAt(0);
				scrollTop = (v == null) ? 0 : v.getTop();
			}
		}
	};

}
