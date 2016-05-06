/**
 * 
 */
package pri.z.show;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pri.h.semap.myMap;
import pri.z.main.MainActivity;
import pri.z.mydb.RelationActivity;
import pri.z.photoshow.ShowDatuFromNet;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.GetTime;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.addActivityDiscuss_C;
import pub.infoclass.myserver.protocol.addActivityDiscuss_S;
import pub.infoclass.myserver.protocol.addRelation_C;
import pub.infoclass.myserver.protocol.addRelation_S;
import pub.infoclass.myserver.protocol.changRelation_C;
import pub.infoclass.myserver.protocol.changRelation_S;
import pub.infoclass.myserver.protocol.getActivityDiscussList_C;
import pub.infoclass.myserver.protocol.getActivityDiscussList_S;
import pub.infoclass.myserver.protocol.getActivityInfo_C;
import pub.infoclass.myserver.protocol.getActivityInfo_S;
import pub.infoclass.myserver.protocol.getRelaion_C;
import pub.infoclass.myserver.protocol.getRelaion_S;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import pub.util.FormatTime;
import pub.util.ImageManager2;
import pub.util.ImageManager3;
import pub.util.timeCha;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.google.gson.reflect.TypeToken;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

/**
 * @author 祝侦科 用户参加的活动历史的详细信息 2014-5-10
 */
public class ActivityDetail extends Activity {
	private int scrollPos;
	private int scrollTop;
	private int abc = 0;
	ActDiscussAdapter disAdapter;
	ListView disListView;
	ActivitySelectData actDetail;
	boolean firstScrollViewPushToTop = true;// 只能是第一次true推至顶，其他时候false不推
	Context mContext;
	List<ActivityDiscussSelectData> DisLists;
	List<UserInfoSelectData> userLists = new ArrayList<UserInfoSelectData>();// 活动成员的用户信息
	String activityId;
	RelationSelectData relationData = null;// 用户对该活动的参加或者是关注的数据
	ArrayList<String> listsMember = new ArrayList<String>();
	ScrollView scrollLayout;
	ProgressBar progressRel;
	ImageView hasNoNetImg;

	Button btnAttend;
	Button btnAttention;
	Button btnComment;

	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getApplicationContext();
		setContentView(R.layout.z_activitydetail);

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		activityId = getIntent().getStringExtra("ActivityDetailActivityID");

		sendActivityDetailMsg(activityId);
		initActivityLogo();

		scrollLayout = (ScrollView) findViewById(R.id.z_activityDetailScrollView);
		progressRel = (ProgressBar) findViewById(R.id.z_detailActProgressBar);
		hasNoNetImg = (ImageView) findViewById(R.id.z_activityDetailNoNetImg);

		setNetView();
		hasNoNetImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendActivityDetailMsg(activityId);
				initActivityLogo();

				setNetView();
			}
		});
	}

	/**
	 * 设置有无网络的View
	 */
	public void setNetView() {
		if (!UtilsZZK.checkNetworkState(ActivityDetail.this)) {
			progressRel.setVisibility(View.GONE);
			hasNoNetImg.setVisibility(View.VISIBLE);
		} else {
			progressRel.setVisibility(View.VISIBLE);
			hasNoNetImg.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;

	}

	/**
	 * 请求活动详情
	 * 
	 * @param ActivityId
	 */
	public void sendActivityDetailMsg(String ActivityId) {
		Message msg = Message.obtain();
		getActivityInfo_C li = new getActivityInfo_C();
		li.setActivityID(ActivityId);
		// li.setStandardUploadTime();
		String UploadTime = FormatTime.getFormatTime();
		String UploadTime11 = UploadTime.substring(0, 12);
		String upload = UploadTime11 + "888";
		li.setUploadTime(upload);

		li.setUserID(SEMapApplication.AccountNumber);

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
	 * 先加载活动Logo
	 */
	private void initActivityLogo() {
		// TODO Auto-generated method stub

		ImageView img = (ImageView) findViewById(R.id.z_actDetailLogo);
		ImageManager2.from(mContext).displayImage(
				img,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderActivityLogoDatu + "/"
						+ activityId + ".png", R.drawable.z_logindefault);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityDetail.this,
						ShowDatuFromNet.class);
				intent.putExtra("ShowDatuFromNetType",
						addressInfo.FileTypeActivityLogo);
				intent.putExtra("CurrentItem", 0);// 起始为0
				intent.putExtra("ShowDatuFromNet", activityId + "");
				startActivity(intent);
			}
		});
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case protocolfromserver.getActivityInfo_S:
				// 请求活动的详细评论和关注情况
				sendActivityAttendOrAttentionMsg(activityId);

				getActivityInfo_S as = (getActivityInfo_S) msg.obj;
				String astr = as.getActivityInfoList();
				// Gson gson = new Gson();
				if (astr == null)
					return;
				if (astr.length() < 20) {
					return;
				}
				List<ActivitySelectData> lists = GsonInstance.getG().fromJson(
						astr, new TypeToken<List<ActivitySelectData>>() {
						}.getType());
				actDetail = lists.get(0);
				initView(actDetail);
				initListener();

				break;
			case protocolfromserver.getActivityDiscussList_S:
				getActivityDiscussList_S aas = (getActivityDiscussList_S) msg.obj;
				String markAas = aas.getMark();
				if (markAas.equals("2"))
					return;
				// String content = aas.getContent();

				String sstr = aas.getActivityDiscussList();
				if (DisLists == null)
					DisLists = new ArrayList<ActivityDiscussSelectData>();
				if (sstr.length() < 20) {
					return;
				}
				DisLists = GsonInstance.getG().fromJson(sstr,
						new TypeToken<List<ActivityDiscussSelectData>>() {
						}.getType());
				initDiscuss();
				break;

			case protocolfromserver.addActivityDiscuss_S:
				addActivityDiscuss_S aass = (addActivityDiscuss_S) msg.obj;
				String strMark = aass.getMark();
				if (strMark.equals("1")) {
					ActivityDiscussSelectData data = new ActivityDiscussSelectData(
							Long.valueOf(aass.getDiscussID()),
							Integer.valueOf(myAddDiscuss.getActivityID()),
							Long.valueOf(myAddDiscuss.getUserID()),
							Long.valueOf(myAddDiscuss.PointDiscussID),
							Long.valueOf(myAddDiscuss.getUserID()),
							myAddDiscuss.getUploadTime(),
							actDetail.getActivityName(), myAddDiscuss.UserName,
							disComment, "");
					if (DisLists == null)
						DisLists = new ArrayList<ActivityDiscussSelectData>();
					if (DisLists.size() == 0) {// 如果原来没有评论
						Button btnNoComment = (Button) findViewById(R.id.z_activityDetailNoComment);
						btnNoComment.setVisibility(View.GONE);
					}
					DisLists.add(data);

					// 不能写if (disAdapter == null)这句，否则第二条的单独的评论不能加上
					// if (disAdapter == null)
					disAdapter = new ActDiscussAdapter(getBaseContext(),
							DisLists);
					// if (disListView == null)
					disListView = (ListView) findViewById(R.id.z_actDetailDiscuss);
					disAdapter.notifyDataSetChanged();
					disListView.setAdapter(disAdapter);
					setLvHeight(disListView);
					disListView.setSelectionFromTop(scrollPos, scrollTop);

					// disAdapter = null;//清空
					// initDiscuss(DisLists);
					// disAdapter.notifyDataSetChanged();//更新ListView
				}

				break;
			case protocolfromserver.addRelation_S:
				addRelation_S chanrs = (addRelation_S) msg.obj;
				String arsChanMark = chanrs.getMark();// Mark表示该活动参加或者是关注消息已经发送成功，和能否参加成功没有关系
				String statusChan = chanrs.getStatus();
				if (arsChanMark.equals("1")) {
					// Log.v("哈哈哈哈", "changRelation_S:status--- " + statusChan);

					// 更新到最新的relationData中
					if (relationData == null) {
						relationData = new RelationSelectData(100,
								Long.valueOf(actDetail.getActivityID() + ""),
								Long.valueOf(SEMapApplication.AccountNumber),
								FormatTime.getFormatTime(), statusChan);
					}
					if (statusChan.equals("1")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "1");

						// dbAdapter.insertRelationActivity(re);
						ContentValues newValues = ContentValuesChange
								.contentInsertRelationActivity(re);
						SQLInfo sqlInfo = new SQLInfo();
						sqlInfo.p = SQLiteProtocol.insertCommonData;
						sqlInfo.SQLInfo = new InsertObject(
								ContentValuesChange.RelationActivity_Table,
								null, newValues);
						sqlSecurityThread.handleSQl(sqlInfo);

						btnAttend.setText("取消参加");
						// btnAttend.setEnabled(false);
						btnAttention.setEnabled(false);
					} else if (statusChan.equals("2")) {
						btnAttend.setText("审核中");
						btnAttend.setEnabled(false);
						// btnAttention.setEnabled(false);
					} else if (statusChan.equals("3")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "3");
						// dbAdapter.insertRelationActivity(re);

						ContentValues newValues = ContentValuesChange
								.contentInsertRelationActivity(re);
						SQLInfo sqlInfo = new SQLInfo();
						sqlInfo.p = SQLiteProtocol.insertCommonData;
						sqlInfo.SQLInfo = new InsertObject(
								ContentValuesChange.RelationActivity_Table,
								null, newValues);
						sqlSecurityThread.handleSQl(sqlInfo);

						btnAttend.setText("已被邀");
						btnAttend.setEnabled(false);
						btnAttention.setEnabled(false);
					} else if (statusChan.equals("4")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "4");
						// dbAdapter.insertRelationActivity(re);

						ContentValues newValues = ContentValuesChange
								.contentInsertRelationActivity(re);
						SQLInfo sqlInfo = new SQLInfo();
						sqlInfo.p = SQLiteProtocol.insertCommonData;
						sqlInfo.SQLInfo = new InsertObject(
								ContentValuesChange.RelationActivity_Table,
								null, newValues);
						sqlSecurityThread.handleSQl(sqlInfo);

						btnAttention.setText("取消关注");
						// btnAttention.setEnabled(false);
					}
				} else {

				}
				break;
			case protocolfromserver.changRelation_S:
				changRelation_S ars = (changRelation_S) msg.obj;
				String arsMark = ars.getMark();// Mark表示该活动参加或者是关注消息已经发送成功，和能否参加成功没有关系
				String status = ars.getStatus();
				if (arsMark.equals("1")) {
					if (relationData != null) {
						relationData.setStatus(status);
					}
					// Log.v("哈哈哈哈", "changRelation_S:status--- " + status);
					if (status.equals("1")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "1");

						SQLInfo sqlInfoUpdate = new SQLInfo();
						sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
						ContentValues newValues = ContentValuesChange
								.contentUpdateOneRelationActivityStatue(arsMark);
						String whereClause = ContentValuesChange.Key_RelationActivityID
								+ "=? ";
						String[] whereArgs = new String[] { actDetail
								.getActivityID() + "" };
						sqlInfoUpdate.SQLInfo = new UpdateObject(
								ContentValuesChange.RelationActivity_Table,
								newValues, whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoUpdate);

						btnAttend.setText("取消参加");
						// btnAttend.setEnabled(false);
						btnAttention.setEnabled(false);
					} else if (status.equals("2")) {
						btnAttend.setText("审核中");
						btnAttend.setEnabled(false);
						// btnAttention.setEnabled(false);
					} else if (status.equals("3")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "3");
						// dbAdapter.insertRelationActivity(re);

						SQLInfo sqlInfoUpdate = new SQLInfo();
						sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
						ContentValues newValues = ContentValuesChange
								.contentUpdateOneRelationActivityStatue(arsMark);
						String whereClause = ContentValuesChange.Key_RelationActivityID
								+ "=? ";
						String[] whereArgs = new String[] { actDetail
								.getActivityID() + "" };
						sqlInfoUpdate.SQLInfo = new UpdateObject(
								ContentValuesChange.RelationActivity_Table,
								newValues, whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoUpdate);

						btnAttend.setText("已被邀");
						btnAttend.setEnabled(false);
						btnAttention.setEnabled(false);
					} else if (status.equals("4")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "4");
						// dbAdapter.insertRelationActivity(re);

						SQLInfo sqlInfoUpdate = new SQLInfo();
						sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
						ContentValues newValues = ContentValuesChange
								.contentUpdateOneRelationActivityStatue(arsMark);
						String whereClause = ContentValuesChange.Key_RelationActivityID
								+ "=? ";
						String[] whereArgs = new String[] { actDetail
								.getActivityID() + "" };
						sqlInfoUpdate.SQLInfo = new UpdateObject(
								ContentValuesChange.RelationActivity_Table,
								newValues, whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoUpdate);

						btnAttention.setText("取消关注");
						// btnAttention.setEnabled(false);
					} else if (status.equals("7")) {// 取消参加
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "7");
						// dbAdapter.insertRelationActivity(re);

						SQLInfo sqlInfoUpdate = new SQLInfo();
						sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
						ContentValues newValues = ContentValuesChange
								.contentUpdateOneRelationActivityStatue(arsMark);
						String whereClause = ContentValuesChange.Key_RelationActivityID
								+ "=? ";
						String[] whereArgs = new String[] { actDetail
								.getActivityID() + "" };
						sqlInfoUpdate.SQLInfo = new UpdateObject(
								ContentValuesChange.RelationActivity_Table,
								newValues, whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoUpdate);

						btnAttend.setText("参加");
						btnAttention.setText("关注");
						// btnAttention.setEnabled(false);
					} else if (status.equals("8")) {// 取消关注
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapApplication.AccountNumber,
								actDetail.getActivityID() + "",
								actDetail.getActivityName(),
								actDetail.getBuildActivityUserID() + "",
								actDetail.getActivityStartTime(),
								actDetail.getActivityEndTime(), "1", "1", "8");
						// dbAdapter.insertRelationActivity(re);

						SQLInfo sqlInfoUpdate = new SQLInfo();
						sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
						ContentValues newValues = ContentValuesChange
								.contentUpdateOneRelationActivityStatue(arsMark);
						String whereClause = ContentValuesChange.Key_RelationActivityID
								+ "=? ";
						String[] whereArgs = new String[] { actDetail
								.getActivityID() + "" };
						sqlInfoUpdate.SQLInfo = new UpdateObject(
								ContentValuesChange.RelationActivity_Table,
								newValues, whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoUpdate);

						btnAttend.setText("参加");
						btnAttention.setText("关注");
						// btnAttention.setEnabled(false);
					}
				} else {

				}
				break;
			case protocolfromserver.getRelaion_S:
				getRelaion_S grs = (getRelaion_S) msg.obj;
				String grsMark = grs.getMark();
				String grsExtraMark = grs.getExtraMark();
				String RelationList = grs.getRelationList();
				// Log.v("哈哈哈哈", "grsMark---  "+grsMark);
				// Log.v("哈哈哈哈", "grsExtraMark---  "+grsExtraMark);
				// String UserIDList = grs.UserIDList;
				if (grsMark.equals("1")) {
					progressRel.setVisibility(View.GONE);
					//
					LinearLayout btnLayout = (LinearLayout) findViewById(R.id.z_downBtnLayout);
					btnLayout.setVisibility(View.VISIBLE);
					// 得到活动成员
					if (grsExtraMark.equals("2")) {
						// Log.v("哈哈哈哈", "RelationList---  "+RelationList);
						if (RelationList == null)
							return;
						if (RelationList.length() < 20) {
							return;
						}
						List<RelationSelectData> rsMember = GsonInstance
								.getG()
								.fromJson(
										RelationList,
										new TypeToken<List<RelationSelectData>>() {
										}.getType());

						// Log.v("哈哈哈哈",
						// "rsMember.size()---  "+rsMember.size());

						if (rsMember == null)
							return;
						if (rsMember.size() <= 0)
							return;
						for (int index = 0; index < rsMember.size(); index++) {
							String userId = rsMember.get(index).getUserID()
									+ "";
							String statue = rsMember.get(index).getStatus();
							// 参加的
							if ((statue.equals("1"))) {
								if (!exitThisString(listsMember, userId))
									listsMember.add(userId);
							}
							// 邀请的
							else if ((statue.equals("3"))) {
								if (!exitThisString(listsMember, userId))
									listsMember.add(userId);
							}

							// Log.v("哈哈哈哈",
							// "listsMember.size()---  "+listsMember.size());
						}
						initActivityMembers();

						// 得到用户信息
						String userListStrs = grs.UserIDList;
						if (userListStrs == null)
							return;
						if (userListStrs.length() < 20) {
							return;
						}
						userLists = GsonInstance.getG().fromJson(userListStrs,
								new TypeToken<List<UserInfoSelectData>>() {
								}.getType());

						// Log.v("哈哈哈哈",
						// "rsMember.size()---  "+rsMember.size());
					}
					// 得到自己和活动的关系
					else if (grsExtraMark.equals("4")) {
						sendActivityMembers(activityId);
						sendActivityDetailDiscussMsg(activityId);

						// initActivityModify();
						if (RelationList.length() < 20) {
							return;
						}
						List<RelationSelectData> rs = GsonInstance
								.getG()
								.fromJson(
										RelationList,
										new TypeToken<List<RelationSelectData>>() {
										}.getType());
						if (rs != null) {
							if (rs.size() > 0)
								relationData = rs.get(0);
							initRelationDataToBtn();
						}
					}
				}

				break;
			}
		}
	};

	/**
	 * 判断去重
	 * 
	 * @param lists
	 * @param str
	 * @return
	 */
	public boolean exitThisString(List<String> lists, String str) {
		boolean flag = false;
		if (lists == null)
			return false;
		if (lists.size() <= 0)
			return false;
		for (int index = 0; index < lists.size(); index++) {
			if (lists.get(index).equals(str)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 用按钮表明用户与活动的关系
	 */
	private void initRelationDataToBtn() {
		if (relationData == null)
			return;
		if (relationData.getStatus() == null)
			return;
		if (SEMapApplication.AccountNumber.equals("0"))// 如果用户没有登录
			return;
		String statue = relationData.getStatus();
		// 如果是群主的话
		if ((actDetail.getBuildActivityUserID() + "")
				.equals(SEMapApplication.AccountNumber)) {
			btnAttend.setText("已参加");
			btnAttend.setEnabled(false);
			btnAttention.setEnabled(false);
		} else if (statue.equals("1")) {
			btnAttend.setText("取消参加");
			// btnAttend.setEnabled(false);
			btnAttention.setEnabled(false);
		} else if (statue.equals("2")) {
			btnAttend.setText("审核中");
			btnAttend.setEnabled(false);
			// btnAttention.setEnabled(false);
		} else if (statue.equals("3")) {
			btnAttend.setText("已被邀");
			btnAttend.setEnabled(false);
			btnAttention.setEnabled(false);
		} else if (statue.equals("4")) {
			btnAttention.setText("取消关注");
			// btnAttention.setEnabled(false);
		} else if (statue.equals("5")) {
			btnAttend.setText("未通过");
			btnAttend.setEnabled(false);
			// btnAttention.setEnabled(false);
		} else if (statue.equals("6")) {
			btnAttend.setText("被踢除");
			btnAttend.setEnabled(false);
			// btnAttention.setEnabled(false);
		} else if (statue.equals("7")) {// 取消参加
			btnAttend.setText("参加");
			btnAttention.setText("关注");
		} else if (statue.equals("8")) {
			btnAttend.setText("参加");
			btnAttention.setText("关注");
		} else {

		}
	}

	PhotoGridView memberGridView;

	/**
	 * 加载活动成员gridview
	 */
	private void initActivityMembers() {
		if (memberGridView == null)
			memberGridView = (PhotoGridView) findViewById(R.id.z_actdetail_MemberGridView);
		if (listsMember != null) {
			if (listsMember.size() > 1) {// 其他成员进入时
				memberAdapter.notifyDataSetChanged();
			} else {// 只有群主时
				memberGridView.setAdapter(memberAdapter);
			}
		}

		TextView tvManagerMember = (TextView) findViewById(R.id.z_actdetail_numberManagerTv);
		final String buildUser = actDetail.getBuildActivityUserID() + "";
		if (buildUser.equals(SEMapApplication.AccountNumber)) {
			tvManagerMember.setVisibility(View.VISIBLE);
		}
		memberGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (SEMapApplication.AccountNumber.equals("0")) {
					LoginDialog.showLoginDialog(ActivityDetail.this);
					return;
				} else {
					List<UserInfoSelectData> listsSend = new ArrayList<UserInfoSelectData>();
					// 过滤用户信息：过滤掉关注和申请中的用户
					if (userLists != null) {
						if (userLists.size() > 0) {
							for (int q = 0; q < listsMember.size(); q++) {
								String userID = listsMember.get(q);
								for (int i = 0; i < userLists.size(); i++) {
									if (userID.equals(userLists.get(i)
											.getUserID() + "")) {
										listsSend.add(userLists.get(i));
										break;
									}
								}
							}
						}
					}
					Intent intent = new Intent(ActivityDetail.this,
							ActivityMembers.class);
					// 先发送群主的资料msg
					intent.putExtra("BuildActivityUserID",
							actDetail.getBuildActivityUserID() + "");

					intent.putStringArrayListExtra("ActivityMembersUserId",
							listsMember);

					intent.putExtra("ActivityID", actDetail.getActivityID()
							+ "");

					intent.putExtra("UserMemberListsSize", listsSend.size());
					for (int i = 0; i < listsSend.size(); i++) {
						intent.putExtra("UserMemberLists" + i, listsSend.get(i));
					}

					// 2表示群主,1表示普通，0默认
					if (buildUser.equals(SEMapApplication.AccountNumber)) {
						intent.putExtra("IsBuildUser", 2);
					} else {
						intent.putExtra("IsBuildUser", 1);
					}

					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}
			}
		});

		LinearLayout limembers = (LinearLayout) findViewById(R.id.z_actdetailMembersLi);
		limembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SEMapApplication.AccountNumber.equals("0")) {
					LoginDialog.showLoginDialog(ActivityDetail.this);
					return;
				} else {

					// 过滤用户信息：过滤掉关注和申请中的用户
					if (userLists != null) {
						if (userLists.size() > 0) {
							for (int i = userLists.size() - 1; i >= 0; i--) {
								String userID = userLists.get(i).getUserID()
										+ "";
								boolean flag = false;
								for (int q = 0; q < listsMember.size(); q++) {
									if (listsMember.get(q).equals(userID)) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									userLists.remove(i);
								}
							}
						}
					}
					Intent intent = new Intent(ActivityDetail.this,
							ActivityMembers.class);
					// 先发送群主的资料msg
					intent.putExtra("BuildActivityUserID",
							actDetail.getBuildActivityUserID() + "");

					intent.putStringArrayListExtra("ActivityMembersUserId",
							listsMember);

					intent.putExtra("ActivityID", actDetail.getActivityID()
							+ "");

					intent.putExtra("UserMemberListsSize", userLists.size());
					for (int i = 0; i < userLists.size(); i++) {
						intent.putExtra("UserMemberLists" + i, userLists.get(i));
					}

					// 2表示群主,1表示普通，0默认
					if (buildUser.equals(SEMapApplication.AccountNumber)) {
						intent.putExtra("IsBuildUser", 2);
					} else {
						intent.putExtra("IsBuildUser", 1);
					}

					startActivity(intent);

					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
					// sendGetMomentUserIdInfomationMsg(actDetail
					// .getBuildActivityUserID() + "");
				}
			}
		});
	}

	public Messenger remoteMessenger = null;
	public static Messenger myMessenger = null;

	public void sendGetMomentUserIdInfomationMsg(String searchUserId) {
		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();
		li.SearchUserID = searchUserId;
		if (searchUserId.equals("")) {
			li.SearchUserID = "0";
		}
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
	 * 设置评论列表
	 * 
	 * @param lists
	 */
	private void initDiscuss() {
		Button btnNoComment = (Button) findViewById(R.id.z_activityDetailNoComment);
		btnNoComment.setVisibility(View.GONE);

		disListView = (ListView) findViewById(R.id.z_actDetailDiscuss);

		disListView.setOnScrollListener(scrollListener);
		disAdapter = new ActDiscussAdapter(getBaseContext(), DisLists);
		disListView.setAdapter(disAdapter);
		setLvHeight(disListView);
		// disListView.setOnItemClickListener(disListViewListener);

		// 让屏幕数据滑到最上端
		if (firstScrollViewPushToTop) {
			scrollLayout.fullScroll(ScrollView.FOCUS_UP);
			firstScrollViewPushToTop = false;
		}
	}

	private final OnItemClickListener disListViewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			// 得到pointdiscussid为0 的部分
			List<ActivityDiscussSelectData> ll = getListSome(DisLists);
			// 得到所加评论所在的第一条评论的discussid和userid
			// 因为可能有的活动只有一条评论，那么添加的评论的discussid和userid就要有初值
			long discussId = ll.get(position).getDiscussID();
			long userid = ll.get(position).getUserID();

			// 得到最后一条评论的discussid作为pointdiscussid
			// 得到最后一条评论的userid
			String[] strs = getLastDiscussIdAndThisUserId(discussId, userid);
			showPopUpWindow(view, strs[0], strs[1]);
		}

	};

	/**
	 * 得到该相关评论的最后一条DiscussId
	 * 
	 * @param discussId
	 * @return:第一条数据为discussId，第二条数据为ThisUserId
	 */
	private String[] getLastDiscussIdAndThisUserId(long discussId, long userid) {
		long resId = discussId;
		long thisUserId = userid;
		for (int index = 0; index < DisLists.size(); index++) {
			long pointId = DisLists.get(index).getPointDiscussID();
			if (resId == pointId) {
				ActivityDiscussSelectData dd = DisLists.get(index);
				resId = dd.getDiscussID();
				thisUserId = dd.getUserID();
			}
		}
		String[] strs = new String[] { resId + "", thisUserId + "" };
		return strs;
	}

	/**
	 * 发送活动具体评论消息
	 * 
	 * @param ActivityId
	 */
	public void sendActivityDetailDiscussMsg(String ActivityId) {
		Message msg = Message.obtain();
		getActivityDiscussList_C li = new getActivityDiscussList_C();
		li.setActivityID(ActivityId);
		// li.setStandardUploadTime();
		String UploadTime = FormatTime.getFormatTime();
		String UploadTime11 = UploadTime.substring(0, 12);
		String upload = UploadTime11 + "222";
		li.setUploadTime(upload);
		li.setUserID(SEMapApplication.AccountNumber);
		ArrayList<Object> list = new ArrayList<Object>();
		li.setPageSize("10000");

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
	 * 得到用户自己和活动的关系
	 * 
	 * @param li
	 */
	public void sendActivityAttendOrAttentionMsg(String activityId) {
		Message msg = Message.obtain();
		// 参加或者关注信息
		getRelaion_C li = new getRelaion_C();
		li.setActivityID(activityId);
		// li.setStandardUploadTime();
		String UploadTime = FormatTime.getFormatTime();
		String UploadTime11 = UploadTime.substring(0, 12);
		String upload = UploadTime11 + "000";
		li.setUploadTime(upload);
		li.setUserID(SEMapApplication.AccountNumber);
		li.setExtraMark("4");
		li.setSearchUserID(SEMapApplication.AccountNumber);
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
	 * 活动成员
	 * 
	 * @param li
	 */
	public void sendActivityMembers(String activityId) {
		Message msg = Message.obtain();
		// 参加或者关注信息
		getRelaion_C li = new getRelaion_C();
		li.setActivityID(activityId);
		String UploadTime = FormatTime.getFormatTime();
		String UploadTime11 = UploadTime.substring(0, 12);
		String upload = UploadTime11 + "111";
		li.setUploadTime(upload);
		li.setUserID(SEMapApplication.AccountNumber);
		li.setExtraMark("2");
		li.setPageSize("10000");
		// li.setSearchUserID(SEMapApplication.AccountNumber);
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
	 * 得到部分List：只有PointDiscussID为0 的List
	 * 
	 * @param lists
	 *            :全部的List
	 * @return：PointDiscussID为0 的List
	 */
	private List<ActivityDiscussSelectData> getListSome(
			List<ActivityDiscussSelectData> lists) {
		// 正序的
		List<ActivityDiscussSelectData> listsomeInv = new ArrayList<ActivityDiscussSelectData>();
		for (int index = lists.size() - 1; index >= 0; index--) {
			if (lists.get(index).getPointDiscussID() == 0) {
				listsomeInv.add(lists.get(index));
			}
		}
		return listsomeInv;
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
			totalHeight += itemView.getMeasuredHeight();
		}
		ViewGroup.LayoutParams layoutParams = mListView.getLayoutParams();
		// 为了在最下面还能多出来一些空间显示按钮
		layoutParams.height = totalHeight
				+ (mListView.getDividerHeight() * (adapter.getCount() - 1));// 总行高+每行的间距
		mListView.setLayoutParams(layoutParams);
	}

	// TextView tv_grade;//活动评论TextView
	private void initView(ActivitySelectData asd) {
		// TODO Auto-generated method stub
		scrollLayout.setVisibility(View.VISIBLE);
		progressRel.setVisibility(View.GONE);
		// 加载组件
		TextView tv_name = (TextView) findViewById(R.id.z_actdetail_name);
		TextView tv_address = (TextView) findViewById(R.id.z_actdetail_address);
		RelativeLayout relHost = (RelativeLayout) findViewById(R.id.z_actDetailHostRel);
		TextView tv_host = (TextView) findViewById(R.id.z_actdetail_host);// 主办方
		TextView tv_phone = (TextView) findViewById(R.id.z_actdetail_phone);// 主办方联系方式
		TextView tv_starttime = (TextView) findViewById(R.id.z_actdetail_starttime);
		TextView tv_class = (TextView) findViewById(R.id.z_actdetail_class);
		TextView tv_number = (TextView) findViewById(R.id.z_actdetail_number);
		TextView tv_attention = (TextView) findViewById(R.id.z_actdetail_describe);
		TextView tvDetailTitleName = (TextView) findViewById(R.id.z_activityDetailTitleName);
		ImageView img_address = (ImageView) findViewById(R.id.z_actdetail_addressImg);
		tv_phone.setOnClickListener(callListener);
		tvDetailTitleName.setText(UtilsTrans.getActivityName(asd
				.getActivityName()));

		LinearLayout liBuildUserHead = (LinearLayout) findViewById(R.id.z_actDetailBuildUserHeadLi);
		ImageView imgBuildHead = (ImageView) findViewById(R.id.z_actDetailBuildUserHead);
		ImageManager3.from(mContext).displayImage(
				imgBuildHead,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderUserHeadXiaotu + "/"
						+ asd.getBuildActivityUserID() + ".png",
				R.drawable.z_logindefault);
		final String buildUserId = asd.getBuildActivityUserID() + "";
		liBuildUserHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CenterInfo.class);
				intent.putExtra("momentCenterUserId", buildUserId);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		});

		tv_name.setText(asd.getActivityName());

		tv_address.setText(asd.getActivityAddress());
		// 地址加下划线
		tv_address.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//
		final String holdPlace = asd.getActivityHoldPlace();
		OnClickListener addressListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (holdPlace == null)
					return;
				String[] strs = holdPlace.split(",");
				if (strs == null)
					return;
				if (strs.length < 2)
					return;

				CloudPoiInfo routePlanPoiInfo = new CloudPoiInfo();
				routePlanPoiInfo.latitude = Double.valueOf(strs[0]);
				routePlanPoiInfo.longitude = Double.valueOf(strs[1]);

				SEMapApplication.getInstance().transferObj
						.setRoutePlanPoiInfo(routePlanPoiInfo);

				Intent intent = new Intent(ActivityDetail.this, myMap.class);
				Bundle bundle = new Bundle();
				bundle.putInt("mark", 2);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
			}
		};

		tv_address.setOnClickListener(addressListener);
		img_address.setOnClickListener(addressListener);

		// 主办单位
		tv_host.setText(asd.getActivityBelongClass());
		if (asd.getActivityBelongClass().equals("0")) {
			relHost.setVisibility(View.GONE);
		}

		// tv_phone.setText(asd.getHelpPhone());

		String endtime = "";
		if (asd.getActivityEndTime().equals("0")) {
			endtime = "";
		} else {
			endtime = " - "
					+ GetTime.getNoYearChineseTime(asd.getActivityEndTime());
		}

		tv_starttime.setText(GetTime.getNoYearChineseTime(asd
				.getActivityStartTime()) + endtime);

		// 活动类型
		tv_class.setText(asd.getActivityTags());
		tv_number.setText(asd.getActivityMemberNumber() + " / "
				+ asd.getActivityMaxMemberNumber() + " 人");
		tv_attention.setText(asd.getActivityDescribe());

		// 将群主添加到参加人员中
		listsMember.add(asd.getBuildActivityUserID() + "");
		initActivityMembers();
	}

	BaseAdapter memberAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;// 当前的View
			ViewHolderMember holder;
			final int myPosition = position;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_item_member, parent, false);
				holder = new ViewHolderMember();
				holder.memberHead = (ImageView) view
						.findViewById(R.id.z_itemMemberHead);

				view.setTag(holder);
			} else {
				holder = (ViewHolderMember) view.getTag();
			}

			ImageManager3.from(mContext).displayImage(
					holder.memberHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ listsMember.get(position) + ".png",
					R.drawable.z_logindefault);
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listsMember.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (listsMember.size() > 8) {
				return 8;
			}
			return listsMember.size();
		}
	};

	private class ViewHolderMember {
		public ImageView memberHead;
	}

	private void initListener() {
		// TODO Auto-generated method stub
		btnAttend = (Button) findViewById(R.id.z_actdetail_attend);
		btnAttention = (Button) findViewById(R.id.z_actdetail_attention);
		btnComment = (Button) findViewById(R.id.z_actCommentBtn);
		Button btnShare = (Button) findViewById(R.id.z_actdetail_share);
		btnShare.setOnClickListener(new shareListener());
		Button btnMsgBuilder = (Button) findViewById(R.id.z_actDetailMsgBuilderBtn);
		btnMsgBuilder.setOnClickListener(new msgBuilderListener());

		btnAttend.setOnClickListener(attendListener);
		btnAttention.setOnClickListener(attentionListener);
		btnComment.setOnClickListener(commentListener);

		ImageView img_Call = (ImageView) findViewById(R.id.z_activitydetail_callphone);
		img_Call.setOnClickListener(callListener);
	}

	/**
	 * 私信主办方
	 * 
	 * @author zhuzhenke
	 * 
	 */
	class msgBuilderListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(ActivityDetail.this, ChatActivity.class)
					.putExtra("userId", "yiweima"));
			overridePendingTransition(R.anim.z_push_left_in,
					R.anim.z_push_left_out);
		}
	}

	class shareListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String myStr = "share";
			// 先将图片下载下来
			String url = "http://" + addressInfo.localIP + ":"
					+ addressInfo.visitPort + "/"
					+ addressInfo.visitFolderActivityLogoDatu + "/"
					+ activityId + ".png";
			File fileImgDownLoad = new File(MainActivity.UserFolderImgDownLoad);
			if (!fileImgDownLoad.exists()) {
				fileImgDownLoad.mkdirs();
			}
			String path = fileImgDownLoad + "/" + myStr + ".png";

			savePhoto(url, path);

			// 分享
			String content = "";
			if (actDetail != null) {
				content = getActivityContent(actDetail);
				// Intent it = new Intent(Intent.ACTION_SEND);
				// it.putExtra(Intent.EXTRA_TEXT, content);
				// it.setType("text/plain");
				// startActivity(Intent.createChooser(it, "将活动分享到"));

				shareMsg("来此party", "将活动分享到", "" + content, path);
			}
		}
	};

	/**
	 * 将指定的图片路径保存到指定的文件下
	 * 
	 * @param imgUrl
	 *            ：图片地址
	 * @param saveFilePath
	 *            ：图片保存路径
	 */
	public void savePhoto(final String imgUrl, final String saveFilePath) {

		new Thread() {
			public void run() {
				Looper.prepare();
				try {
					FileOutputStream fos = new FileOutputStream(saveFilePath);
					InputStream is = new URL(imgUrl).openStream();
					int data = is.read();
					while (data != -1) {
						fos.write(data);
						data = is.read();
					}
					is.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Looper.loop();
			};
		}.start();

	}

	/**
	 * 分享功能
	 * 
	 * @param activityTitle
	 *            Activity的名字
	 * @param msgTitle
	 *            消息标题
	 * @param msgText
	 *            消息内容
	 * @param imgPath
	 *            图片路径，不分享图片则传null
	 */
	public void shareMsg(String activityTitle, String msgTitle, String msgText,
			String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				// 图文类型
				intent.setType("image/*");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.putExtra("ShareDetailMoment", imgPath);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, msgTitle));
	}

	public String getActivityContent(ActivitySelectData actDetail) {
		String str = "";

		if (actDetail == null) {
			return str;
		}
		String user = SEMapApplication.LoginName;
		if (user.equals("0")) {
			user = "您的朋友";
		}
		str = user
				+ "  分享了一个活动: \n"
				+ "活动 “"
				+ actDetail.getActivityName()
				+ "” 将于 "
				+ GetTime
						.getNoYearChineseTime(actDetail.getActivityStartTime())
				+ "开始， "
				+ GetTime.getNoYearChineseTime(actDetail.getActivityEndTime())
				+ "结束。\n 这将在 " + actDetail.getActivityAddress() + " 举行。"
				+ "\n该活动由 “" + actDetail.getActivityBelongClass() + "” 主办，目前已有"
				+ actDetail.getActivityMemberNumber() + "人参加。\n活动简介："
				+ actDetail.getActivityDescribe() + "。\n主办方联系方式为： "
				+ actDetail.getHelpPhone() + "。\n分享自 来此Party";
		return str;
	}

	private OnClickListener attendListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 如果用户没有登录
			if (SEMapApplication.AccountNumber.equals("0")) {
				LoginDialog.showLoginDialog(ActivityDetail.this);
			} else {
				if (relationData != null) {// 如果已经参加，，，
					if (relationData.getStatus().equals("1")) {
						sendChangeActivityRelationMsg("7");
						return;
					} else if (relationData.getStatus().equals("2")) {
						return;
					} else if (relationData.getStatus().equals("3")) {
						return;
					} else if (relationData.getStatus().equals("4")) {
						// 关注的活动还可以改为参加
						sendChangeActivityRelationMsg("1");
						return;
					} else if (relationData.getStatus().equals("5")) {
						sendChangeActivityRelationMsg("1");
						return;
					} else if (relationData.getStatus().equals("6")) {
						sendChangeActivityRelationMsg("1");
						return;
					} else if (relationData.getStatus().equals("7")) {
						sendChangeActivityRelationMsg("1");
						return;
					} else if (relationData.getStatus().equals("8")) {
						sendChangeActivityRelationMsg("1");
						return;
					} else {
						sendChangeActivityRelationMsg("1");
					}
				} else {
					sendAddActivityRelationMsg("1");
				}

			}

		}
	};

	// (1.用户参加;2.用申请;3.活动方邀请;4.用户关注;5.未审核通过;6.被踢出用户)
	private OnClickListener attentionListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (SEMapApplication.AccountNumber.equals("0")) {
				LoginDialog.showLoginDialog(ActivityDetail.this);
			} else {
				if (relationData != null) {
					if (relationData.getStatus().equals("1")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else if (relationData.getStatus().equals("2")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else if (relationData.getStatus().equals("3")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else if (relationData.getStatus().equals("4")) {
						sendChangeActivityRelationMsg("8");
						return;
					} else if (relationData.getStatus().equals("5")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else if (relationData.getStatus().equals("6")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else if (relationData.getStatus().equals("7")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else if (relationData.getStatus().equals("8")) {
						sendChangeActivityRelationMsg("4");
						return;
					} else {
						sendChangeActivityRelationMsg("4");
					}
				} else {
					sendAddActivityRelationMsg("4");
				}

			}
		}
	};

	/**
	 * 参加或者关注活动
	 * 
	 * @param activityID
	 * @param status
	 *            :(1.用户参加;2.用申请;3.活动方邀请;4.用户关注)
	 */
	public void sendAddActivityRelationMsg(String status) {
		Message msg = Message.obtain();

		addRelation_C li = new addRelation_C();
		li.setActivityID(actDetail.getActivityID() + "");
		li.setStatus(status);
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
	 * 变更 参加或者关注活动
	 * 
	 * @param activityID
	 * @param status
	 *            :(1.用户参加;2.用申请;3.活动方邀请;4.用户关注)
	 */
	public void sendChangeActivityRelationMsg(String status) {
		Message msg = Message.obtain();

		changRelation_C li = new changRelation_C();
		li.setActivityID(actDetail.getActivityID() + "");
		li.setStatus(status);
		li.setUserID(SEMapApplication.AccountNumber);
		li.setSUserID(SEMapApplication.AccountNumber);
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

	private InputMethodManager imm;
	private OnClickListener commentListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (SEMapApplication.AccountNumber.equals("0")) {
				LoginDialog.showLoginDialog(ActivityDetail.this);
			} else {
				showPopUpWindow(v, "0", "0");
				// imm = (InputMethodManager) getBaseContext().getSystemService(
				// Service.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
			}
		}
	};

	private OnClickListener callListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String hostPhone = "";
			if (actDetail != null) {
				hostPhone = actDetail.getHelpPhone().trim();
			}

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + hostPhone));
			startActivity(intent);
			overridePendingTransition(R.anim.z_push_left_in,
					R.anim.z_push_left_out);
		}
	};

	String disComment;
	addActivityDiscuss_C myAddDiscuss;// 我自己新加的评论
	RelativeLayout relComment;
	EditText editComment;
	Button btnCommentSend;

	/**
	 * disCussId为0时表示是自己新的评论，否则是回复别人的评论Id
	 * 
	 * @param v
	 * @param PointDiscussID
	 * @param thisUserID
	 */
	public void showPopUpWindow(View v, final String PointDiscussID,
			final String thisUserID) {
		relComment = (RelativeLayout) findViewById(R.id.z_activityDetailCommentRel);
		relComment.setVisibility(View.VISIBLE);

		imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);

		editComment = (EditText) findViewById(R.id.z_activityDetailCommentSendMessage);
		editComment.requestFocus();
		editComment.addTextChangedListener(EditTextChangerListener);

		btnCommentSend = (Button) findViewById(R.id.z_activityDetailCommentBtnSend);
		btnCommentSend.setText("取消");
		btnCommentSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				disComment = editComment.getText().toString().trim();
				if (disComment.length() > 0) {
					if (SEMapApplication.AccountNumber.equals("0")) {
						LoginDialog.showLoginDialog(ActivityDetail.this);
						return;
					} else {
						if (PointDiscussID.equals("0")) {
							// 评论的是自己新的评论
							myAddDiscuss = new addActivityDiscuss_C();
							myAddDiscuss.setActivityID(actDetail
									.getActivityID() + "");
							myAddDiscuss
									.setUserID(SEMapApplication.AccountNumber);
							myAddDiscuss.PointDiscussID = "0";
							myAddDiscuss.DiscussContent = disComment;
							myAddDiscuss.setStandardUploadTime();
							myAddDiscuss.ThisUserID = thisUserID;
							myAddDiscuss.ActivityName = actDetail
									.getActivityName();
							myAddDiscuss.UserName = SEMapApplication.LoginName;
							sendAddActivityDiscussMsg(myAddDiscuss);
						} else {// 评论别人的评论
							// if(disCussId < 0)
							// return;
							myAddDiscuss = new addActivityDiscuss_C();
							myAddDiscuss.setActivityID(actDetail
									.getActivityID() + "");
							myAddDiscuss
									.setUserID(SEMapApplication.AccountNumber);
							myAddDiscuss.PointDiscussID = PointDiscussID;
							myAddDiscuss.DiscussContent = disComment;
							myAddDiscuss.setStandardUploadTime();
							myAddDiscuss.ThisUserID = thisUserID;
							myAddDiscuss.ActivityName = actDetail
									.getActivityName();
							myAddDiscuss.UserName = SEMapApplication.LoginName;
							sendAddActivityDiscussMsg(myAddDiscuss);
						}
					}
				} else {

				}
				// 点击发送按钮就隐藏评论条
				editComment.setText("");
				imm.hideSoftInputFromWindow(btnCommentSend.getWindowToken(), 0);
				relComment.setVisibility(View.GONE);

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

	public void sendAddActivityDiscussMsg(addActivityDiscuss_C dis) {
		Message msg = Message.obtain();
		addActivityDiscuss_C li = new addActivityDiscuss_C();

		li.setActivityID(dis.getActivityID());
		li.setUserID(dis.getUserID());
		li.setStandardUploadTime();
		li.DiscussContent = dis.DiscussContent;
		li.PointDiscussID = dis.PointDiscussID;
		li.UserName = dis.UserName;
		li.ThisUserID = dis.ThisUserID;
		li.ActivityName = dis.ActivityName;
		li.Photo = "0";
		SEMapApplication.AccountNumber = li.getUserID();

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

	private OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// scrollPos
				scrollPos = disListView.getFirstVisiblePosition();
			}
			if (DisLists != null) {
				View v = disListView.getChildAt(0);
				scrollTop = (v == null) ? 0 : v.getTop();
			}
		}
	};

	class ActDiscussAdapter extends BaseAdapter {

		List<ActivityDiscussSelectData> lists;// 包括所有的List
		List<ActivityDiscussSelectData> listsome = new ArrayList<ActivityDiscussSelectData>();// 部分List：只有PointDiscussID为0
																								// 的List
		Context mContext;
		List<String> commentRepeat = new ArrayList<String>();

		public ActDiscussAdapter(Context mContext,
				List<ActivityDiscussSelectData> lists) {
			this.mContext = mContext;
			this.lists = lists;
			getListSome(lists);
		}

		private List<ActivityDiscussSelectData> getListSome(
				List<ActivityDiscussSelectData> lists) {
			List<ActivityDiscussSelectData> listsomeInv = new ArrayList<ActivityDiscussSelectData>();
			for (int index = 0; index < lists.size(); index++) {
				if (lists.get(index).getPointDiscussID() == 0) {
					listsomeInv.add(lists.get(index));
				}
			}

			// 将listsome的顺序换一下
			for (int index2 = listsomeInv.size() - 1; index2 >= 0; index2--) {
				listsome.add(listsomeInv.get(index2));
			}

			return listsome;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listsome.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listsome.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolderDiscuss viewHolder;
			final int myPosition = position;
			// 第二版本
			// int myPosition = lists.size()-1-position;//为了使最新评论的加载在最前面
			ActivityDiscussSelectData discuss = listsome.get(position);

			if (view == null) {
				viewHolder = new ViewHolderDiscuss();
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_item_actdiscuss, parent, false);

				viewHolder.imgManHead = (ImageView) view
						.findViewById(R.id.z_actdiscuss_manhead);
				viewHolder.tvMan = (TextView) view
						.findViewById(R.id.z_actdiscuss_man);
				viewHolder.tvTime = (TextView) view
						.findViewById(R.id.z_actdiscuss_time);
				viewHolder.tvComment = (TextView) view
						.findViewById(R.id.z_actdiscuss_comment);
				viewHolder.tvHuifu = (TextView) view
						.findViewById(R.id.z_actdiscuss_huifu);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolderDiscuss) view.getTag();
			}

			/** 加载用户头像 **/
			ImageManager3.from(mContext).displayImage(
					viewHolder.imgManHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ discuss.getUserID() + ".png",
					R.drawable.z_logindefault);
			viewHolder.tvMan.setText(UtilsTrans.getUserName(discuss
					.getUserName()));

			String TimeStart = discuss.getUploadTime();
			String TimeEnd = FormatTime.getFormatTime();

			viewHolder.tvTime.setText(timeCha.MillisToWord(timeCha.TimeCha(
					TimeStart, TimeEnd)));

			// viewHolder.tvTime.setText(GetTime.getNoYearTime(discuss
			// .getUploadTime()));

			long pointId = discuss.getPointDiscussID();
			String disMsg = discuss.getDiscussContent();
			if (pointId == 0) {
				long disId = discuss.getDiscussID();
				for (int index = position; index < lists.size(); index++) {
					long myPointId = lists.get(index).getPointDiscussID();
					if (disId == myPointId) {
						disMsg += "\n"
								+ UtilsTrans.getUserName(lists.get(index)
										.getUserName()) + " 回复: "
								+ lists.get(index).getDiscussContent();
						disId = lists.get(index).getDiscussID();
					}
				}
			}
			viewHolder.tvComment.setText(disMsg);

			final String userid = discuss.getUserID() + "";
			viewHolder.imgManHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, CenterInfo.class);
					intent.putExtra("momentCenterUserId", userid);
					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}
			});

			final View viewAbc = view;
			viewHolder.tvHuifu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 得到pointdiscussid为0 的部分
					List<ActivityDiscussSelectData> ll = getListSome(DisLists);
					// 得到所加评论所在的第一条评论的discussid和userid
					// 因为可能有的活动只有一条评论，那么添加的评论的discussid和userid就要有初值
					long discussId = ll.get(myPosition).getDiscussID();
					long userid = ll.get(myPosition).getUserID();

					// 得到最后一条评论的discussid作为pointdiscussid
					// 得到最后一条评论的userid
					String[] strs = getLastDiscussIdAndThisUserId(discussId,
							userid);
					showPopUpWindow(viewAbc, strs[0], strs[1]);
				}
			});

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 得到pointdiscussid为0 的部分
					List<ActivityDiscussSelectData> ll = getListSome(DisLists);
					// 得到所加评论所在的第一条评论的discussid和userid
					// 因为可能有的活动只有一条评论，那么添加的评论的discussid和userid就要有初值
					long discussId = ll.get(myPosition).getDiscussID();
					long userid = ll.get(myPosition).getUserID();

					// 得到最后一条评论的discussid作为pointdiscussid
					// 得到最后一条评论的userid
					String[] strs = getLastDiscussIdAndThisUserId(discussId,
							userid);
					showPopUpWindow(viewAbc, strs[0], strs[1]);
				}
			});
			return view;

		}

	}

	public static class ViewHolderDiscuss {
		ImageView imgManHead;
		TextView tvMan;
		TextView tvTime;
		TextView tvComment;
		TextView tvHuifu;
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
