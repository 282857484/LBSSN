/**
 * 
 */
package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.h.semap.myMap;
import pri.z.mydb.RelationActivity;
import pri.z.photoshow.ShowDatuFromNet;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.GetTime;
import pri.z.utils.UtilsTrans;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.changRelation_C;
import pub.infoclass.myserver.protocol.changRelation_S;
import pub.infoclass.myserver.protocol.getActivityInfo_C;
import pub.infoclass.myserver.protocol.getActivityInfo_S;
import pub.infoclass.myserver.protocol.getRelaion_C;
import pub.infoclass.myserver.protocol.getRelaion_S;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import pub.util.ImageManager2;
import pub.util.ImageManager3;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.google.gson.reflect.TypeToken;

/**
 * @author 祝侦科 用户参加的活动历史的详细信息 2014-5-10
 */
public class ActivityLocal extends Activity {
	ActivitySelectData actLocal;
	boolean firstScrollViewPushToTop = true;// 只能是第一次true推至顶，其他时候false不推
	Context mContext;
	String activityId;
	RelationSelectData relationData;// 用户对该活动的参加或者是关注的数据
	String SEMapUserId = "0";
	ArrayList<String> listsMember = new ArrayList<String>();
	List<UserInfoSelectData> userLists = new ArrayList<UserInfoSelectData>();
	ScrollView scrollLayout;
	ProgressBar progressRel;
	ImageView hasNoNetImg;
	int ActivityLocalType = 0;
	Button btnAttend;

	protected void onCreate(final Bundle inState) {
		super.onCreate(inState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getApplicationContext();
		setContentView(R.layout.z_activitylocal);

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		activityId = getIntent().getStringExtra("ActivityLocalActivityID");
		ActivityLocalType = getIntent().getIntExtra("ActivityLocalType", 0);
		SEMapUserId = SEMapApplication.AccountNumber;

		sendActivityDetailMsg(activityId);
		initActivityLogo();

		scrollLayout = (ScrollView) findViewById(R.id.z_activityLocalScrollView);
		progressRel = (ProgressBar) findViewById(R.id.z_LocalActProgressBar);
		hasNoNetImg = (ImageView) findViewById(R.id.z_activityLocalNoNetImg);

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
		if (!UtilsZZK.checkNetworkState(ActivityLocal.this)) {
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
		li.setStandardUploadTime();

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

		ImageView img = (ImageView) findViewById(R.id.z_actLocalLogo);
		ImageManager2.from(mContext).displayImage(
				img,
				"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
						+ "/" + addressInfo.visitFolderActivityLogoDatu + "/"
						+ activityId + ".png", R.drawable.z_logindefault);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityLocal.this,
						ShowDatuFromNet.class);
				intent.putExtra("ShowDatuFromNetType",
						addressInfo.FileTypeActivityLogo);
				intent.putExtra("CurrentItem", 0);// 起始为0
				intent.putExtra("ShowDatuFromNet", activityId + "");
				startActivity(intent);
			}
		});
		TextView tvShowDetail = (TextView) findViewById(R.id.z_actLocalShowDetailTv);
		tvShowDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityLocal.this,
						ActivityDetail.class);
				intent.putExtra("ActivityDetailActivityID", activityId);
				startActivity(intent);

				overridePendingTransition(R.anim.z_push_left_in,
						R.anim.z_push_left_out);
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

				sendActivityMembers(activityId);

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
				actLocal = lists.get(0);
				initView(actLocal);

				break;
			case protocolfromserver.getRelaion_S:
				getRelaion_S grs = (getRelaion_S) msg.obj;
				String grsMark = grs.getMark();
				String grsExtraMark = grs.getExtraMark();
				String RelationList = grs.getRelationList();
				// String UserIDList = grs.UserIDList;
				if (grsMark.equals("1")) {

					progressRel.setVisibility(View.GONE);
					// 得到活动成员
					if (grsExtraMark.equals("2")) {
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

						if (rsMember == null)
							return;
						if (rsMember.size() <= 0)
							return;
						for (int index = 0; index < rsMember.size(); index++) {
							if ((rsMember.get(index).getStatus().equals("1"))
									|| (rsMember.get(index).getStatus()
											.equals("3"))) {
								if (!exitThisString(listsMember,
										rsMember.get(index).getUserID() + ""))
									listsMember.add(rsMember.get(index)
											.getUserID() + "");
							}
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
					}
				}

				break;

			case protocolfromserver.changRelation_S:
				changRelation_S chanrs = (changRelation_S) msg.obj;
				String arsChanMark = chanrs.getMark();// Mark表示该活动参加或者是关注消息已经发送成功，和能否参加成功没有关系
				String statusChan = chanrs.getStatus();
				if (arsChanMark.equals("1")) {
					if (statusChan.equals("1")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapUserId, actLocal.getActivityID() + "",
								actLocal.getActivityName(),
								actLocal.getBuildActivityUserID() + "",
								actLocal.getActivityStartTime(),
								actLocal.getActivityEndTime(), "1", "1", "1");

						// dbAdapter.insertRelationActivity(re);
						ContentValues newValues = ContentValuesChange
								.contentInsertRelationActivity(re);
						SQLInfo sqlInfo = new SQLInfo();
						sqlInfo.p = SQLiteProtocol.insertCommonData;
						sqlInfo.SQLInfo = new InsertObject(
								ContentValuesChange.RelationActivity_Table,
								null, newValues);
						sqlSecurityThread.handleSQl(sqlInfo);

						btnAttend.setText("已参加");
						btnAttend.setEnabled(false);
					} else if (statusChan.equals("2")) {
						btnAttend.setText("审核中");
						btnAttend.setEnabled(false);
					} else if (statusChan.equals("3")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapUserId, actLocal.getActivityID() + "",
								actLocal.getActivityName(),
								actLocal.getBuildActivityUserID() + "",
								actLocal.getActivityStartTime(),
								actLocal.getActivityEndTime(), "1", "1", "3");
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
					} else if (statusChan.equals("4")) {
						// 保存到本地
						RelationActivity re = new RelationActivity(0,
								SEMapUserId, actLocal.getActivityID() + "",
								actLocal.getActivityName(),
								actLocal.getBuildActivityUserID() + "",
								actLocal.getActivityStartTime(),
								actLocal.getActivityEndTime(), "1", "1", "4");
						// dbAdapter.insertRelationActivity(re);

						ContentValues newValues = ContentValuesChange
								.contentInsertRelationActivity(re);
						SQLInfo sqlInfo = new SQLInfo();
						sqlInfo.p = SQLiteProtocol.insertCommonData;
						sqlInfo.SQLInfo = new InsertObject(
								ContentValuesChange.RelationActivity_Table,
								null, newValues);
						sqlSecurityThread.handleSQl(sqlInfo);

						btnAttend.setText("已关注");
						btnAttend.setEnabled(false);
					}
				} else {

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
		for (int index = 0; index < lists.size(); index++) {
			if (lists.get(index).equals(str)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 加载活动成员gridview
	 */
	private void initActivityMembers() {
		PhotoGridView memberGridView = (PhotoGridView) findViewById(R.id.z_actLocal_MemberGridView);
		memberGridView.setAdapter(memberAdapter);
		TextView tvShowAllMembers = (TextView) findViewById(R.id.z_actLocalShowAllMembersTv);

		TextView tv_number = (TextView) findViewById(R.id.z_actLocal_number);
		tv_number.setText(listsMember.size() + " 人");
		final String buildUser = actLocal.getBuildActivityUserID() + "";
		tvShowAllMembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SEMapUserId.equals("0")) {
					LoginDialog.showLoginDialog(ActivityLocal.this);
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

					Intent intent = new Intent(ActivityLocal.this,
							ActivityMembers.class);
					// 先发送群主的资料msg
					intent.putExtra("BuildActivityUserID",
							actLocal.getBuildActivityUserID() + "");
					intent.putStringArrayListExtra("ActivityMembersUserId",
							listsMember);

					intent.putExtra("ActivityID", actLocal.getActivityID() + "");

					intent.putExtra("UserMemberListsSize", listsSend.size());
					for (int i = 0; i < listsSend.size(); i++) {
						intent.putExtra("UserMemberLists" + i, listsSend.get(i));
					}
					// 2表示群主,1表示普通，0默认
					if (buildUser.equals(SEMapUserId)) {
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
	 * 活动成员
	 * 
	 * @param li
	 */
	public void sendActivityMembers(String activityId) {
		Message msg = Message.obtain();
		// 参加或者关注信息
		getRelaion_C li = new getRelaion_C();
		li.setActivityID(activityId);
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setExtraMark("2");
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

	// TextView tv_grade;//活动评论TextView
	private void initView(final ActivitySelectData asd) {
		// TODO Auto-generated method stub
		scrollLayout.setVisibility(View.VISIBLE);
		progressRel.setVisibility(View.GONE);

		Button btnShare = (Button) findViewById(R.id.z_actLocalDetail_Share);
		btnAttend = (Button) findViewById(R.id.z_actLocalDetail_Attend);

		if (ActivityLocalType == 4) {
			// 如果是关注的话，就可以参加
			btnAttend.setText("参加");
			btnAttend.setEnabled(true);
		}
		btnAttend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (SEMapUserId.equals("0")) {
					LoginDialog.showLoginDialog(ActivityLocal.this);
				} else {
					sendChangeActivityRelationMsg(asd.getActivityID() + "", "1");
				}

			}
		});
		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = "";
				if (asd != null) {
					content = getActivityContent(asd);
					Intent it = new Intent(Intent.ACTION_SEND);
					it.putExtra(Intent.EXTRA_TEXT, content);
					it.setType("text/plain");
					startActivity(Intent.createChooser(it, "将活动分享到"));
				}

				// if (SEMapUserId.equals("0")) {
				// LoginDialog.showLoginDialog(ActivityLocal.this);
				// } else {
				//
				// }

			}
		});
		// 加载组件
		TextView tv_name = (TextView) findViewById(R.id.z_actLocal_name);
		TextView tv_address = (TextView) findViewById(R.id.z_actLocal_address);
		RelativeLayout relHost = (RelativeLayout) findViewById(R.id.z_actLocalHostRel);
		TextView tv_host = (TextView) findViewById(R.id.z_actLocal_host);// 主办方
		TextView tv_starttime = (TextView) findViewById(R.id.z_actLocal_starttime);

		TextView tvDetailTitleName = (TextView) findViewById(R.id.z_activityLocalTitleName);
		ImageView img_address = (ImageView) findViewById(R.id.z_actLocal_addressImg);

		tvDetailTitleName.setText(UtilsTrans.getActivityName(asd
				.getActivityName()));

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

				Intent intent = new Intent(ActivityLocal.this, myMap.class);
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
		
		String endtime = "";
		if (asd.getActivityEndTime().equals("0")) {
			endtime = "";
		} else {
			endtime = " - " + GetTime.getNoYearChineseTime(asd.getActivityEndTime());
		}

		tv_starttime.setText(GetTime.getNoYearChineseTime(asd
				.getActivityStartTime()) + endtime);

		// 将群主添加到参加人员中
		listsMember.add(asd.getBuildActivityUserID() + "");
		initActivityMembers();
	}

	/**
	 * 变更 参加或者关注活动
	 * 
	 * @param activityID
	 * @param status
	 *            :(1.用户参加;2.用申请;3.活动方邀请;4.用户关注)
	 */
	public void sendChangeActivityRelationMsg(String activityId, String status) {
		Message msg = Message.obtain();

		changRelation_C li = new changRelation_C();
		li.setActivityID(activityId);
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

	/**
	 * 分享活动信息
	 * 
	 * @param actDetail
	 * @return
	 */
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

	BaseAdapter memberAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;// 当前的View
			ViewHolderMember holder;
			final int myPosition = position;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_item_mymember, parent, false);
				holder = new ViewHolderMember();
				holder.memberHead = (ImageView) view
						.findViewById(R.id.z_itemMyMemberHead);
				holder.tvMemberPhone = (TextView) view
						.findViewById(R.id.z_itemMyMemberPhone);

				view.setTag(holder);
			} else {
				holder = (ViewHolderMember) view.getTag();
			}

			final String memberUserId = listsMember.get(position);
			ImageManager3.from(mContext).displayImage(
					holder.memberHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ memberUserId + ".png", R.drawable.z_logindefault);
			holder.tvMemberPhone.setText(memberUserId);
			holder.memberHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, CenterInfo.class);
					intent.putExtra("momentCenterUserId", memberUserId);
					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}
			});
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
			if (listsMember.size() > 30) {
				return 30;
			}
			return listsMember.size();
		}
	};

	private class ViewHolderMember {
		public ImageView memberHead;
		public TextView tvMemberPhone;

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
