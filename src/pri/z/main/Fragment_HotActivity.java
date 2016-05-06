package pri.z.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pri.z.build.BuildActivity;
import pri.z.main.XListView.IXListViewListener;
import pri.z.mydb.MobileContacts;
import pri.z.show.ActivityDetail;
import pri.z.show.CommonSetting;
import pri.z.show.R;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DeleteObject;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.VersionUpdate;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.SearchActivities;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.util.FormatTime;
import pub.util.ImageManager2;
import pub.util.timeCha;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_HotActivity extends Fragment implements IXListViewListener {

	private static List<ActivitySelectData> lists = new ArrayList<ActivitySelectData>();
	private static XListView mListView;
	static Context mContext;
	public static final String TAG = "哈哈哈哈";
	ProgressBar liProgressLoading;
	ImageView imgNoRefreshData;
	static int PageIndex = 1;// 最开始的时候是1，加载更多是要加载第1个十条（本来是从0开始的）
	boolean CreateViewFlag = false;
	public static int loadMillis = 10000;// 加载数据的时间：加载数据时运行的最大时间

	public static final int BuildActivityRequest = 998;
	public static final int BuilderActivity_OK = 999;

	// 确保加载数据只能是一种方式，要么加载更多，要么刷新
	public boolean LoadDataOnlyOne = true;

	public static Context context;
	View viewAbc = null;
	RelativeLayout relNoData;
	int abs = 123;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity().getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(resultHandler);
		context = getActivity();

		// 检查网络状况，无网时提示用户
		UtilsZZK.checkNetworkState(mContext);

		deleteAllTimeOutActivities();
		QueryUserRequest.queryActivity();
		
		MobileContacts.getContacts(mContext);
	}

	/**
	 * 删除所有过时的活动
	 */
	private void deleteAllTimeOutActivities() {
		// TODO Auto-generated method stub
		SQLInfo sqlInfoDelete = new SQLInfo();
		sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
		String whereClause = ContentValuesChange.Key_ActivityStartTime + "<=? ";
		// String[] whereArgs = new String[] { FormatTime.getFormatTime() };
		String[] whereArgs = new String[] { UtilsTrans.getFormatTimeIn1Hour() };
		sqlInfoDelete.SQLInfo = new DeleteObject(
				ContentValuesChange.Activity_Table, whereClause, whereArgs);
		sqlSecurityThread.handleSQl(sqlInfoDelete);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.z_lay_activity, container, false);
		viewAbc = view;
		initNoDataView(view);
		// if(mListView == null)
		// 注意注意：每次 oncreate必须重新加载mListView等等需要用到的组件，否则数据加载不上
		// 不能够先判定是否为空，为空才加载组件，否则会出错
		mListView = (XListView) view.findViewById(R.id.z_list_activity);
		// 得到刷新加载
		liProgressLoading = (ProgressBar) view
				.findViewById(R.id.z_mainProgressLoading);
		imgNoRefreshData = (ImageView) view
				.findViewById(R.id.z_noActivityRefreshData);
		// 去除ListView的拖动背景色
		mListView.setCacheColorHint(0);
		mListView.setOnScrollListener(scrollListener);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		mListView.setFastScrollEnabled(true);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		if (CreateViewFlag) {// 从其他fragment跳转过来
			if (lists.size() > 0) {
				mListView.setAdapter(mAdapter);
			} else {

			}
		} else {// 第一次加载数据
				// 先加载本地的数据
				// lists = getDataFromMobile();
			// if (lists.size() > 0) {
			// lists = sortActivityByStartTime(lists);
			// mListView.setAdapter(mAdapter);
			// }
		}
		liProgressLoading.setVisibility(View.GONE);
		CreateViewFlag = true;

		return view;

	}

	private void initNoDataView(View view) {
		// TODO Auto-generated method stub
		relNoData = (RelativeLayout) view
				.findViewById(R.id.z_activityNoDataRel);
		Button btnNoDataSetting = (Button) view
				.findViewById(R.id.z_activityNoDataSetting);
		Button btnNoDataCreate = (Button) view
				.findViewById(R.id.z_activityNoDataCreateActivity);
		Button btnNoDataRefresh = (Button) view
				.findViewById(R.id.z_activityNoDataRefresh);
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
				startActivity(new Intent(getActivity(), BuildActivity.class));
			}
		});
		btnNoDataRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!UtilsZZK.checkNetworkState(mContext)) {
					return;
				}
				relNoData.setVisibility(View.GONE);
				if (liProgressLoading != null) {
					liProgressLoading.setVisibility(View.VISIBLE);
				}
				SearchActivity();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	// 保存现在的实例与状态
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public Handler resultHandler = new Handler() {
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
			case SQLiteProtocol.queryActivityData:
				SQLResponse responseAct = (SQLResponse) msg.obj;
				if (responseAct.mark == 2) {
					// if(!UtilsZZK.checkNetworkState(mContext)){
					// relNoData.setVisibility(View.VISIBLE);
					// }
					return;
				}
				ActivitySelectData[] acts = (ActivitySelectData[]) responseAct.result;
				if (acts.length <= 0) {
					// if(!UtilsZZK.checkNetworkState(mContext)){
					// relNoData.setVisibility(View.VISIBLE);
					// }
					return;
				}

				getDataFromMobile(acts);
				break;
			case z_baiduprotocol.baiduSomeActivitiesOnCreate:
				// TODO Auto-generated method stub
				String resultOnCreate = String.valueOf(msg.obj);
				if (!resultOnCreate.equals("")) {
					LoadDataOnlyOne = true;
					try {
						JSONObject json = new JSONObject(resultOnCreate);
						boolean InsertFlag = false;// 记录是否插入数据的标志
						JSONArray jsonArray = new JSONArray(
								json.getString("contents"));
						int jsonLength = jsonArray.length();
						if (lists != null) {
							if (lists.size() > 0) {
								lists.removeAll(lists);
							}
						}

						if (jsonLength > 8) {
							// 如果有超过八条的数据量，就把本地的数据删除
							// dbAdapter.deleteAllActivitys();
						}
						for (int index = 0; index < jsonLength; index++) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							ActivitySelectData dismom = new ActivitySelectData();
							dismom.setActivityID(jsonObj
									.getLong("univeralindex"));
							dismom.setBuildActivityUserID(jsonObj
									.getLong("launchman"));
							dismom.setActivityManagerID(jsonObj
									.getLong("managerid"));
							dismom.setUploadTime(jsonObj
									.getString("uploadingtime"));
							// dismom.setIsDirectJoinIn
							// (jsonObj.getString("geotable_id"));
							dismom.setActivityFlag(jsonObj
									.getString("activityflag"));
							dismom.setActivityMemberNumber(jsonObj
									.getString("activityalwaysjoinperson"));
							dismom.setActivityMaxMemberNumber(jsonObj
									.getString("maxmembernumber"));
							dismom.setActivityDescribe(jsonObj
									.getString("describe"));
							dismom.setActivityName(jsonObj
									.getString("activity"));
							dismom.setActivityStartTime(jsonObj
									.getString("starttime"));
							dismom.setActivityEndTime(jsonObj
									.getString("endtime"));
							dismom.setActivityHoldPlace(jsonObj
									.getString("activityholdplace"));
							dismom.setHelpPhone(jsonObj
									.getString("mobilephone"));
							dismom.setActivityBelongClass(jsonObj
									.getString("tags"));
							dismom.setActivityTags(jsonObj.getString("tags"));
							dismom.setActivityAddress(jsonObj
									.getString("address"));
							dismom.setActivityLogo(jsonObj
									.getString("activitylogo"));
							dismom.setActivityHoldPlace(jsonObj
									.getString("location"));

							int myIndex = RemoveDuplicate(lists, dismom);
							if (myIndex == -1) {
								if (lists == null) {
									lists = new ArrayList<ActivitySelectData>();
								}
								lists.add(dismom);

								boolean flag = false;
								for (int q = 0; q < MainActivity.MapActivityID
										.size(); q++) {
									if ((String
											.valueOf(MainActivity.MapActivityID
													.get("MapActivityID" + q)))
											.equals(dismom.getActivityID() + "")) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									ContentValues newValues = ContentValuesChange
											.contentInsertActivityBase(dismom);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.Activity_Table,
											null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
								}

								InsertFlag = true;
							} else {
								if (lists != null) {
									if (lists.size() >= myIndex) {
										lists.remove(myIndex);
										lists.add(dismom);

										int MapSize = MainActivity.MapActivityID
												.size();
										MainActivity.MapActivityID.put(
												"MapActivityID" + MapSize,
												dismom.getActivityID());

										SQLInfo sqlInfoUpdate = new SQLInfo();
										sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
										ContentValues newValues = ContentValuesChange
												.contentUpdateOneActivityByID(dismom);
										String whereClause = ContentValuesChange.Key_ActivityID
												+ "=? ";
										String[] whereArgs = new String[] { dismom
												.getActivityID() + "" };
										sqlInfoUpdate.SQLInfo = new UpdateObject(
												ContentValuesChange.Activity_Table,
												newValues, whereClause,
												whereArgs);
										sqlSecurityThread
												.handleSQl(sqlInfoUpdate);
									}
								}
							}

						}
						removeOutOfTimeActivity(lists);
						lists = sortActivityByStartTime(lists);

						if (!InsertFlag) {
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
					if (lists.size() > 0) {
						if (relNoData != null)
							relNoData.setVisibility(View.GONE);
						if (mListView == null)
							if (viewAbc != null)
								mListView = (XListView) viewAbc
										.findViewById(R.id.z_list_activity);
						if (mListView != null)
							mListView.setAdapter(mAdapter);
					} else {
						if (relNoData == null) {// 先判断是否为空
							if (viewAbc != null) {
								relNoData = (RelativeLayout) viewAbc
										.findViewById(R.id.z_activityNoDataRel);
							}
						}
						// if (relNoData != null)
						// relNoData.setVisibility(View.VISIBLE);
					}

					if (liProgressLoading == null) {// 先判断是否为空
						if (viewAbc != null) {
							liProgressLoading = (ProgressBar) viewAbc
									.findViewById(R.id.z_mainProgressLoading);
						}
					}
					if (liProgressLoading != null)
						liProgressLoading.setVisibility(View.GONE);

				}
				break;

			case z_baiduprotocol.baiduSomeActivitiesOnRefresh:
				String resultOnRefresh = String.valueOf(msg.obj);
				if (!resultOnRefresh.equals("")) {
					LoadDataOnlyOne = true;
					onLoadR();
					try {
						JSONObject json = new JSONObject(resultOnRefresh);

						JSONArray jsonArray = new JSONArray(
								json.getString("contents"));
						int jsonLength = jsonArray.length();

						if (jsonLength > 8) {
							// 如果有超过八条的数据量，就把本地的数据删除
							// dbAdapter.deleteAllActivitys();
						}
						boolean InsertFlag = false;// 记录是否插入数据的标志
						for (int index = 0; index < jsonLength; index++) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							ActivitySelectData dismom = new ActivitySelectData();

							dismom.setActivityID(jsonObj
									.getLong("univeralindex"));
							dismom.setBuildActivityUserID(jsonObj
									.getLong("launchman"));
							dismom.setActivityManagerID(jsonObj
									.getLong("managerid"));
							dismom.setUploadTime(jsonObj
									.getString("uploadingtime"));
							// dismom.setIsDirectJoinIn
							// (jsonObj.getString("geotable_id"));
							dismom.setActivityFlag(jsonObj
									.getString("activityflag"));
							dismom.setActivityMemberNumber(jsonObj
									.getString("activityalwaysjoinperson"));
							dismom.setActivityMaxMemberNumber(jsonObj
									.getString("maxmembernumber"));
							dismom.setActivityDescribe(jsonObj
									.getString("describe"));
							dismom.setActivityName(jsonObj
									.getString("activity"));
							dismom.setActivityStartTime(jsonObj
									.getString("starttime"));
							dismom.setActivityEndTime(jsonObj
									.getString("endtime"));
							dismom.setActivityHoldPlace(jsonObj
									.getString("activityholdplace"));
							dismom.setHelpPhone(jsonObj
									.getString("mobilephone"));
							dismom.setActivityBelongClass(jsonObj
									.getString("tags"));
							dismom.setActivityTags(jsonObj.getString("tags"));
							dismom.setActivityAddress(jsonObj
									.getString("address"));
							dismom.setActivityLogo(jsonObj
									.getString("activitylogo"));
							dismom.setActivityHoldPlace(jsonObj
									.getString("location"));

							int myIndex = RemoveDuplicate(lists, dismom);
							if (myIndex == -1) {
								lists.add(dismom);
								boolean flag = false;
								for (int q = 0; q < MainActivity.MapActivityID
										.size(); q++) {
									if ((String
											.valueOf(MainActivity.MapActivityID
													.get("MapActivityID" + q)))
											.equals(dismom.getActivityID() + "")) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									ContentValues newValues = ContentValuesChange
											.contentInsertActivityBase(dismom);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.Activity_Table,
											null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
								}

								InsertFlag = true;
							} else {
								if (lists != null) {
									if (lists.size() >= myIndex) {
										lists.remove(myIndex);
										lists.add(dismom);

										int MapSize = MainActivity.MapActivityID
												.size();
										MainActivity.MapActivityID.put(
												"MapActivityID" + MapSize,
												dismom.getActivityID());

										SQLInfo sqlInfoUpdate = new SQLInfo();
										sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
										ContentValues newValues = ContentValuesChange
												.contentUpdateOneActivityByID(dismom);
										String whereClause = ContentValuesChange.Key_ActivityID
												+ "=? ";
										String[] whereArgs = new String[] { dismom
												.getActivityID() + "" };
										sqlInfoUpdate.SQLInfo = new UpdateObject(
												ContentValuesChange.Activity_Table,
												newValues, whereClause,
												whereArgs);
										sqlSecurityThread
												.handleSQl(sqlInfoUpdate);
									}
								}
							}

						}
						removeOutOfTimeActivity(lists);
						lists = sortActivityByStartTime(lists);
						if (!InsertFlag) {
							// Toast.makeText(mContext, "没有更多数据了", 1).show();
							// imgNoRefreshData.setVisibility(View.VISIBLE);
							// new Handler().postDelayed(new Runnable() {
							// @Override
							// public void run() {
							// imgNoRefreshData.setVisibility(View.GONE);
							// }
							//
							// }, 3000);
						}
						if (mListView == null) {
							if (viewAbc != null) {
								mListView = (XListView) viewAbc
										.findViewById(R.id.z_list_activity);
							}
						}

						if (lists.size() <= 0) {
							if (relNoData != null)
								relNoData.setVisibility(View.GONE);
						}
						mListView.setAdapter(mAdapter);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;

			case z_baiduprotocol.baiduSomeActivitiesOnLoadmore:
				String resultOnLoadmore = String.valueOf(msg.obj);
				if (!resultOnLoadmore.equals("")) {
					LoadDataOnlyOne = true;
					onLoadM();
					try {
						JSONObject json = new JSONObject(resultOnLoadmore);
						JSONArray jsonArray = new JSONArray(
								json.getString("contents"));
						int jsonLength = jsonArray.length();
						if (jsonLength > 8) {
							// 如果有超过八条的数据量，就把本地的数据删除
							// dbAdapter.deleteAllActivitys();
						}
						boolean InsertFlag = false;// 记录是否插入数据的标志
						for (int index = 0; index < jsonLength; index++) {
							JSONObject jsonObj = (JSONObject) jsonArray
									.get(index);

							ActivitySelectData dismom = new ActivitySelectData();

							dismom.setActivityID(jsonObj
									.getLong("univeralindex"));
							dismom.setBuildActivityUserID(jsonObj
									.getLong("launchman"));
							dismom.setActivityManagerID(jsonObj
									.getLong("managerid"));
							dismom.setUploadTime(jsonObj
									.getString("uploadingtime"));
							// dismom.setIsDirectJoinIn
							// (jsonObj.getString("geotable_id"));
							dismom.setActivityFlag(jsonObj
									.getString("activityflag"));
							dismom.setActivityMemberNumber(jsonObj
									.getString("activityalwaysjoinperson"));
							dismom.setActivityMaxMemberNumber(jsonObj
									.getString("maxmembernumber"));
							dismom.setActivityDescribe(jsonObj
									.getString("describe"));
							dismom.setActivityName(jsonObj
									.getString("activity"));
							dismom.setActivityStartTime(jsonObj
									.getString("starttime"));
							dismom.setActivityEndTime(jsonObj
									.getString("endtime"));
							dismom.setActivityHoldPlace(jsonObj
									.getString("activityholdplace"));
							dismom.setHelpPhone(jsonObj
									.getString("mobilephone"));
							dismom.setActivityBelongClass(jsonObj
									.getString("tags"));
							dismom.setActivityTags(jsonObj.getString("tags"));
							dismom.setActivityAddress(jsonObj
									.getString("address"));
							dismom.setActivityLogo(jsonObj
									.getString("activitylogo"));
							dismom.setActivityHoldPlace(jsonObj
									.getString("location"));

							int myIndex = RemoveDuplicate(lists, dismom);
							if (myIndex == -1) {
								lists.add(dismom);

								boolean flag = false;
								for (int q = 0; q < MainActivity.MapActivityID
										.size(); q++) {
									if ((String
											.valueOf(MainActivity.MapActivityID
													.get("MapActivityID" + q)))
											.equals(dismom.getActivityID() + "")) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									ContentValues newValues = ContentValuesChange
											.contentInsertActivityBase(dismom);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.Activity_Table,
											null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
								}

								InsertFlag = true;
							} else {
								if (lists != null) {
									if (lists.size() >= myIndex) {
										lists.remove(myIndex);
										lists.add(dismom);

										int MapSize = MainActivity.MapActivityID
												.size();
										MainActivity.MapActivityID.put(
												"MapActivityID" + MapSize,
												dismom.getActivityID());

										SQLInfo sqlInfoUpdate = new SQLInfo();
										sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
										ContentValues newValues = ContentValuesChange
												.contentUpdateOneActivityByID(dismom);
										String whereClause = ContentValuesChange.Key_ActivityID
												+ "=? ";
										String[] whereArgs = new String[] { dismom
												.getActivityID() + "" };
										sqlInfoUpdate.SQLInfo = new UpdateObject(
												ContentValuesChange.Activity_Table,
												newValues, whereClause,
												whereArgs);
										sqlSecurityThread
												.handleSQl(sqlInfoUpdate);
									}
								}

							}
						}
						removeOutOfTimeActivity(lists);
						lists = sortActivityByStartTime(lists);
						if (!InsertFlag) {
							// Toast.makeText(mContext, "没有更多数据了", 1).show();
						}
						if (lists.size() <= 0) {
							if (relNoData != null)
								relNoData.setVisibility(View.GONE);
						}
						mAdapter.notifyDataSetChanged();
						PageIndex++;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;

			}
		}
	};

	/**
	 * 通过时间经行排序，每次更新数据时
	 * 
	 * @param lists
	 * @return
	 */
	public List<ActivitySelectData> sortActivityByStartTime(
			List<ActivitySelectData> lists) {
		if (lists == null) {
			return null;
		}
		// if(lists.size() <= 1){
		// return lists;
		// }
		Comparator comUploadTime = new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				ActivitySelectData a1 = (ActivitySelectData) o1;
				ActivitySelectData a2 = (ActivitySelectData) o2;
				return a1.getActivityStartTime().compareTo(
						a2.getActivityStartTime());
			}

		};

		Collections.sort(lists, comUploadTime);
		return lists;
	}

	// ...
	public static Messenger remoteMessenger = null;
	public static Messenger myMessenger = null;

	public static void SearchActivity() {
		new Thread(new SearchActivities("0", mContext, myMessenger,
				z_baiduprotocol.baiduSomeActivitiesOnCreate)).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	boolean flagFirstOnResume = false;

	@Override
	public void onResume() {
		super.onResume();
		remoteMessenger = SEMapApplication.serviceMessenger;

		//改变距离后产生的效果//如果用了的话，每次ListView的位置就会置顶
		if(MainActivity.modifyActivityDistanceFlag){

			if (lists.size() > 0) {
				relNoData.setVisibility(View.GONE);
				removeOutOfDistanceActivity(lists);
				mListView.setAdapter(mAdapter);
			} else {
				 if (flagFirstOnResume)
				 relNoData.setVisibility(View.VISIBLE);
			}
			MainActivity.modifyActivityDistanceFlag = false;
		}

		flagFirstOnResume = true;

	}

	public static void refreshAdapter() {
		if (lists.size() > 0) {
			if (mListView != null)
				mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void getDataFromMobile(ActivitySelectData[] datas) {

		if (datas == null)
			return;
		if (datas.length == 0)
			return;

		for (int index = 0; index < datas.length; index++) {
			MainActivity.MapActivityID.put("MapActivityID" + index,
					datas[index].getActivityID() + "");
		}

		// 只取十条数据
		if (datas.length >= 10) {
			for (int index = datas.length - 1; index >= datas.length - 10; index--) {
				lists.add(datas[index]);
			}
		} else {
			for (int index = 0; index < datas.length; index++) {
				lists.add(datas[index]);
			}
		}

		if (lists.size() > 0) {
			lists = sortActivityByStartTime(lists);
			if (mListView != null)
				mListView.setAdapter(mAdapter);
		}

	}

	private void onLoadR() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		Calendar c = Calendar.getInstance();
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
			new Thread(new SearchActivities("0", mContext, myMessenger,
					z_baiduprotocol.baiduSomeActivitiesOnRefresh)).start();
			LoadDataOnlyOne = false;
			// 如果刷新超时就停止刷新
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					LoadDataOnlyOne = true;
					onLoadM();// 如果网络没有进来。HeadView 的刷新时间不许重新设置
					// Toast.makeText(mContext, "网络连接异常，请稍后再试", 1).show();
				}
			}, loadMillis);
		} else {
			mListView.stopRefresh();
			return;
		}

	}

	/**
	 * 如果List中已经存在了一样的ActivitySelectData,则返回index 如果不存在则返回 -1
	 */
	public int RemoveDuplicate(List<ActivitySelectData> myList,
			ActivitySelectData myAct) {
		int flag = -1;
		if (myList == null)
			return flag;
		if (myList.size() <= 0)
			return flag;
		if (myList.size() > 0) {
			for (int index = 0; index < myList.size(); index++) {
				boolean flag1 = myAct.getActivityName().equals(
						myList.get(index).getActivityName());
				boolean flag2 = myAct.getUploadTime().equals(
						myList.get(index).getUploadTime());

				if (flag1 && flag2) {
					flag = index;
					break;
				}
			}
		}

		return flag;
	}

	@Override
	public void onLoadMore() {

		if (LoadDataOnlyOne) {
			new Thread(new SearchActivities(PageIndex + "", mContext,
					myMessenger, z_baiduprotocol.baiduSomeActivitiesOnLoadmore))
					.start();
			LoadDataOnlyOne = false;
			// 如果刷新超时就停止刷新
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					LoadDataOnlyOne = true;
					onLoadM();// 如果网络没有进来。HeadView 的刷新时间不许重新设置
					// Toast.makeText(mContext, "网络连接异常，请稍后再试", 1).show();
				}
			}, loadMillis);
		} else {
			mListView.stopLoadMore();
			return;
		}
	}

	/**
	 * 消除已经过了开始时间的活动 超过开始时间两个小时以后的数据
	 * 
	 * @param lists
	 */
	private void removeOutOfTimeActivity(List<ActivitySelectData> lists) {
		if (lists == null) {
			return;
		}
		if (lists.size() > 0) {
			// String timeNow = FormatTime.getFormatTime();
			String timeNow = UtilsTrans.getFormatTimeBefore1Hour();
			// List从后往前删
			for (int index = lists.size() - 1; index >= 0; index--) {
				if (lists.get(index).getActivityStartTime().compareTo(timeNow) < 0) {
					lists.remove(index);
				}
			}
		}
	}

	/**
	 * 消除超出范围的数据
	 * 
	 * @param lists
	 */
	private void removeOutOfDistanceActivity(List<ActivitySelectData> lists) {

		if (lists == null) {
			return;
		}
		if (lists.size() > 0) {
			// List从后往前删
			for (int index = lists.size() - 1; index >= 0; index--) {

				String momentLocation = lists.get(index).getActivityHoldPlace();
				String myLocation = "[" + MainActivity.a + "," + MainActivity.b
						+ "]";
				double finalDistance = UtilsZZK.getDistanceByLocation(
						myLocation, momentLocation);

				if (finalDistance > (MainActivity.SearchActivityDistance / 1000)) {
					lists.remove(index);
				}

			}
		}
	}

	private static BaseAdapter mAdapter = new BaseAdapter() {
		final class ViewHolder {
			public ImageView actLogo;
			public ImageView imgActFlag;
			public TextView actName;
			public TextView actPersonNum;
			public TextView actTime;
			public TextView actDistance;
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
			// TODO Auto-generated method stub

			View vi = convertView;
			ViewHolder holder = null;
			ActivitySelectData act = lists.get(position);
			final int myPosition = position;
			// 样式1
			if (vi == null) {

				holder = new ViewHolder();

				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				vi = inflater.inflate(R.layout.z_itemof_activity, null);

				holder.actLogo = (ImageView) vi
						.findViewById(R.id.z_itemActLogo);
				holder.imgActFlag = (ImageView) vi
						.findViewById(R.id.z_activityItemFlag);
				holder.actName = (TextView) vi
						.findViewById(R.id.w_textActivityName);
				holder.actTime = (TextView) vi
						.findViewById(R.id.z_ActivityStartTime);
				holder.actPersonNum = (TextView) vi
						.findViewById(R.id.w_textActivityPersonNum);
				holder.actDistance = (TextView) vi
						.findViewById(R.id.z_textActivityDistance);
				vi.setTag(holder);

			} else {
				holder = (ViewHolder) vi.getTag();
			}
			ImageManager2.from(mContext).displayImage(
					holder.actLogo,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderActivityLogoDatu + "/"
							+ act.getActivityID() + ".png",
					R.drawable.z_logindefault);

			holder.actName.setText(act.getActivityName());
			holder.actTime.setText(timeCha.MillisToWordInActivity(timeCha
					.TimeCha(FormatTime.getFormatTime(),
							act.getActivityStartTime())));
			// 突出进行中
			if (holder.actTime.getText().toString().equals("进行中")) {
				holder.actTime.setTextColor(context.getResources().getColor(
						R.color.z_color_time_running));
			} else {
				holder.actTime.setTextColor(context.getResources().getColor(
						R.color.z_color_time));
			}
			holder.actPersonNum.setText(act.getActivityMemberNumber() + "/"
					+ act.getActivityMaxMemberNumber() + "人");

			// 0代表审核中,1代表通过,,2代表不通过,3.官方认证活动,4.正规民间活动,5.私人活动
			String activityFlag = act.getActivityFlag();
			if (activityFlag.equals("0")) {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_activityflag0);
			} else if (activityFlag.equals("1")) {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_activityflag1);
			} else if (activityFlag.equals("2")) {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_activityflag2);
			} else if (activityFlag.equals("3")) {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_activityflag3);
			} else if (activityFlag.equals("4")) {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_activityflag4);
			} else if (activityFlag.equals("5")) {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_activityflag5);
			} else {
				holder.imgActFlag
						.setBackgroundResource(R.drawable.z_logindefault);
			}

			String momentLocation = act.getActivityHoldPlace();
			String myLocation = "[" + MainActivity.a + "," + MainActivity.b
					+ "]";
			double finalDistance = UtilsZZK.getDistanceByLocation(myLocation,
					momentLocation);
			holder.actDistance.setText(finalDistance + " km");

			final String activityId = act.getActivityID() + "";
			vi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context, ActivityDetail.class);
					intent.putExtra("ActivityDetailActivityID", activityId);
					context.startActivity(intent);
					((Activity) context).overridePendingTransition(
							R.anim.z_push_left_in, R.anim.z_push_left_out);

				}

			});
			return vi;
		}

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// The view is not scrolling.
//			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
//				// scrollPos
//				if (imgAddActivity != null) {
//					imgAddActivity.setVisibility(View.VISIBLE);
//				}
//			} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//				if (imgAddActivity != null) {
//					imgAddActivity.setVisibility(View.GONE);
//				}
//			}
		}
	};

}
