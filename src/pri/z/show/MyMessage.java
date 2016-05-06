package pri.z.show;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pri.z.mydb.GradeInfo;
import pri.z.mydb.PushMessage;
import pri.z.mydb.RelationActivity;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DBAdapter;
import pri.z.sqlite.DeleteObject;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.UpdateObject;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.GetTime;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.UtilsTrans;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.httptransfer.SearchMomentDiscussByMessageId;
import pub.httptransfer.SearchOneMomentDetailById;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.changRelation_C;
import pub.infoclass.myserver.protocol.changRelation_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.updateActivityOpinion_independent_C;
import pub.infoclass.myserver.protocol.updateActivityOpinion_independent_S;
import pub.infoclass.pushservice.h_protocol_pusher;
import pub.util.FormatTime;
import pub.util.ImageManager2;
import pub.util.ImageManager3;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyMessage extends Activity {

	Context mContext;
	EditText editAdvice;
	ListView mListView;
	String TAG = "哈哈哈哈";
	List<PushMessage> listsMessage = new ArrayList<PushMessage>();

	// 保存审核活动的组件
	TextView myTvJoinDealAgree;
	TextView myTvJoinDealDisAgree;

	// 用户对活动的评分
	TextView myTvUserGradeDeal;// 评分/已处理的按钮
	GradeInfo gradeInfo;// 评分的信息类
	RatingBar ratUserGrade;// 评分的评分条
	TextView myTvUserGradeShowAve;// 显示活动的平均分

	String dealActivityJoinPushID = "0";// 用来保存用户处理的互动参加审核消息的pushid

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_mymessage);
		mContext = getBaseContext();

		// Intent intent = new Intent();
		// intent.setAction("com.example.semap.NetService");
		// boolean isSuccess = bindService(intent, connection,
		// Service.BIND_AUTO_CREATE);
		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		QueryUserRequest.qureyAllPushMessages();

	}

	private void initMessageQueue(PushMessage[] pushs) {
		mListView = (ListView) findViewById(R.id.z_moreMessageLisView);
		// 得到数据库中的消息
		// DBAdapter dbAdapter = new DBAdapter(mContext);
		// dbAdapter.open();
		// PushMessage[] pushs = dbAdapter.queryAllPushMessages();
		// //关闭数据库连接
		// dbAdapter.close();

		// dbAdapter.updateAllPushMessageByPushId();

		SQLInfo sqlInfoUpdate = new SQLInfo();
		sqlInfoUpdate.p = SQLiteProtocol.updateCommonData;
		ContentValues newValues = ContentValuesChange
				.contentUpdateAllPushMessageByPushId();
		String whereClause = null;
		String[] whereArgs = null;
		sqlInfoUpdate.SQLInfo = new UpdateObject(
				ContentValuesChange.PushMessage_Table, newValues, whereClause,
				whereArgs);
		sqlSecurityThread.handleSQl(sqlInfoUpdate);

		// 更新HashMap
		Map<String, Object> mapPushs = SEMapApplication.MapPushMessage;
		if (mapPushs != null) {
			if (mapPushs.size() > 0) {
				for (int i = 0; i < mapPushs.size(); i++) {
					PushMessage push = (PushMessage) mapPushs
							.get("MapPushMessage" + i);
					// 标记为已读
					push.PushStatue = "2";
					mapPushs.put("MapPushMessage" + i, push);
				}
			}
		}

		if (pushs != null) {
			if (pushs.length > 0) {
				for (int index = pushs.length - 1; index >= 0; index--) {
					listsMessage.add(pushs[index]);
				}
			}
		}

		if (listsMessage.size() <= 0) {
			mListView.setVisibility(View.GONE);
			ImageView img = (ImageView) findViewById(R.id.z_msgListHasNoDataImg);
			img.setVisibility(View.VISIBLE);
		} else {
			mListView.setAdapter(adapter);
		}

	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SQLiteProtocol.qureyAllPushMessages:
				SQLResponse responseAct = (SQLResponse) msg.obj;
				// if (responseAct.mark == 2)
				// return;
				PushMessage[] pushs = (PushMessage[]) responseAct.result;
				// if (rels.length <= 0)
				// return;
				initMessageQueue(pushs);
				break;

			case protocolfromserver.changRelation_S:
				changRelation_S chanRe = (changRelation_S) msg.obj;
				String markR = chanRe.getMark();
				if (markR.equals("1")) {// 审核消息发送成功
					if (myTvJoinDealAgree != null
							&& myTvJoinDealDisAgree != null) {
						myTvJoinDealAgree.setVisibility(View.VISIBLE);
						myTvJoinDealAgree.setVisibility(View.GONE);
						myTvJoinDealDisAgree.setText("已处理");
						// 不可点击
						myTvJoinDealAgree.setEnabled(false);
						myTvJoinDealDisAgree.setEnabled(false);
					}
					// // 如果已经存在参加审核的pushid
					// if (!dealActivityJoinPushID.equals("0")) {
					// // 修改本地数据库：设置该消息为已读
					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter
					// .updateOnePushMessageByPushId(dealActivityJoinPushID);
					// //关闭数据库连接
					// dbAdapter.close();
					// }
				}
				break;
			case protocolfromserver.updateActivityOpinion_independent_S:
				updateActivityOpinion_independent_S aos = (updateActivityOpinion_independent_S) msg.obj;
				String aosMark = aos.getMark();
				if (aosMark.equals("1")) {
					int numPerson = Integer.valueOf(aos.getTotalFullGrade());
					float totalGrade = Float.valueOf(aos.getGotGrade());
					float average = (float) (totalGrade / numPerson);
					if (myTvUserGradeDeal != null
							&& myTvUserGradeShowAve != null
							&& ratUserGrade != null) {
						myTvUserGradeDeal.setText("已处理");
						myTvUserGradeDeal.setTextColor(Color.RED);
						myTvUserGradeDeal.setEnabled(false);
						myTvUserGradeShowAve.setText("活动得分： " + average + " 分");
						ratUserGrade.setEnabled(false);
					}
					// DBAdapter dbAdapter = new DBAdapter(mContext);
					// dbAdapter.open();
					// dbAdapter.insertGradeInfo(gradeInfo);
					// //关闭数据库连接
					// dbAdapter.close();

					ContentValues newValues = ContentValuesChange
							.contentInsertGradeInfo(gradeInfo);
					SQLInfo sqlInfo = new SQLInfo();
					sqlInfo.p = SQLiteProtocol.insertCommonData;
					sqlInfo.SQLInfo = new InsertObject(
							ContentValuesChange.Grade_Table, null, newValues);
					sqlSecurityThread.handleSQl(sqlInfo);

				} else {
					Toast.makeText(MyMessage.this, "评分失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listsMessage.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listsMessage.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;// 当前的View
			ViewHolder holder;
			int p = 0;
			PushMessage myPush = listsMessage.get(position);
			String pushID = myPush.PushID;
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(myPush.PushMessage);
				p = jsonObj.getInt("p");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String myPushID = pushID;
			String PushStatue = myPush.PushStatue;
			switch (p) {
			/*** 群主收到有人参加/关注活动 ***/
			case h_protocol_pusher.SEND_Activity_Join:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_activityjoin, parent, false);
				ImageView imgJoinUserHead = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityJoinUserHead);
				TextView tvJoinUserName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinUserName);
				TextView tvJoinStatue = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinStatue);
				TextView tvJoinTime = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinTime);
				ImageView imgJoinActLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityJoinActLogo);
				TextView tvJoinActivityName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinActivityName);
				RelativeLayout relJoinActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_itemMsgActivityJoinItemRel);
				// 下面的3个处理、
				LinearLayout liDeal = (LinearLayout) view
						.findViewById(R.id.z_itemMsgActivityJoinDealLi);
				final TextView tvJoinDealAgree = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinDealAgreeTv);
				final TextView tvJoinDealDisAgree = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinDealDisAgreeTv);

				// 解析数据
				JSONObject rsdjson;
				JSONObject uisdjson;
				JSONObject asdjson;
				RelationSelectData rsdJoin = null;
				UserInfoSelectData uisdJoin = null;
				ActivitySelectData asdJoin = null;
				try {
					rsdjson = jsonObj.getJSONObject("rsd");
					uisdjson = jsonObj.getJSONObject("uisd");
					asdjson = jsonObj.getJSONObject("asd");
					rsdJoin = new RelationSelectData(
							rsdjson.getLong("RelationID"),
							rsdjson.getLong("ActivityID"),
							rsdjson.getLong("UserID"),
							rsdjson.getString("UploadTime"),
							rsdjson.getString("Status"));

					uisdJoin = new UserInfoSelectData(
							uisdjson.getLong("UserID"),
							uisdjson.getString("UserName"),
							uisdjson.getString("UploadTime"),
							uisdjson.getString("Code"),
							uisdjson.getString("UserPhone"),
							uisdjson.getString("UserJoinActivity"),
							uisdjson.getString("UserAttentionClass"),
							uisdjson.getString("UserQQ"),
							uisdjson.getString("UserWeiChat"),
							uisdjson.getString("UserTags"),
							uisdjson.getString("UserClass"),
							uisdjson.getString("UserDescribe"),
							uisdjson.getString("UserLevel"),
							uisdjson.getString("UserLogo"),
							uisdjson.getString("UserAge"),
							uisdjson.getString("UserSex"),
							uisdjson.getString("School"),
							uisdjson.getString("Profession"),
							uisdjson.getString("Birthday"),
							uisdjson.getString("Home"));

					asdJoin = new ActivitySelectData(
							asdjson.getLong("ActivityID"),
							asdjson.getLong("BuildActivityUserID"),
							asdjson.getLong("ActivityManagerID"),
							asdjson.getString("UploadTime"),
							asdjson.getString("IsDirectJoinIn"),
							asdjson.getString("ActivityFlag"),
							asdjson.getString("ActivityMemberNumber"),
							asdjson.getString("ActivityMaxMemberNumber"),
							asdjson.getString("ActivityDescribe"),
							asdjson.getString("ActivityName"),
							asdjson.getString("ActivityStartTime"),
							asdjson.getString("ActivityEndTime"),
							asdjson.getString("ActivityHoldPlace"),
							asdjson.getString("HelpPhone"),
							asdjson.getString("ActivityBelongClass"),
							asdjson.getString("ActivityTags"),
							asdjson.getString("ActivityOpinion"),
							asdjson.getString("ActivityAddress"),
							asdjson.getString("ActivityLogo"));
					// 得到活动信息类 uisd
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ImageManager3.from(mContext).displayImage(
						imgJoinUserHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadXiaotu + "/"
								+ rsdJoin.getUserID() + ".png",
						R.drawable.z_logindefault);
				ImageManager2.from(mContext).displayImage(
						imgJoinActLogo,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderActivityLogoXiaotu
								+ "/" + rsdJoin.getActivityID() + ".png",
						R.drawable.z_logindefault);
				tvJoinUserName.setText(UtilsTrans.getUserName(uisdJoin
						.getUserName()));
				tvJoinStatue.setText(UtilsTrans.getStatusMean(rsdJoin
						.getStatus()));
				tvJoinTime.setText(GetTime.getNoYearTime(rsdJoin
						.getUploadTime()));
				tvJoinActivityName.setText(asdJoin.getActivityName() + "");

				imgJoinUserHead.setOnClickListener(new UserMomentInfoListener(
						"" + rsdJoin.getUserID()));
				tvJoinUserName.setOnClickListener(new UserMomentInfoListener(""
						+ rsdJoin.getUserID()));
				final String activityIdJoin = rsdJoin.getActivityID() + "";
				relJoinActivityItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MyMessage.this,
								ActivityDetail.class);
						intent.putExtra("ActivityDetailActivityID",
								activityIdJoin);
						startActivity(intent);
						overridePendingTransition(R.anim.z_push_left_in,
								R.anim.z_push_left_out);
					}
				});

				if (asdJoin.getIsDirectJoinIn().equals("1")) {
					tvJoinDealAgree.setVisibility(View.GONE);
					tvJoinDealDisAgree.setVisibility(View.GONE);
					break;
				}

				if (rsdJoin.getStatus().equals("4")) {// 关注
					tvJoinDealAgree.setVisibility(View.GONE);
					tvJoinDealDisAgree.setVisibility(View.GONE);
					break;
				}
				if (PushStatue.equals("2")) {// 如果已经处理了
					tvJoinDealAgree.setVisibility(View.GONE);
					tvJoinDealDisAgree.setText("已处理");
					// tvJoinDealDisAgree.setTextColor(Color.RED);
					// 不可点击
					tvJoinDealAgree.setEnabled(false);
					tvJoinDealDisAgree.setEnabled(false);
				} else {// 为了避免数据冲突
					tvJoinDealAgree.setVisibility(View.VISIBLE);
					tvJoinDealDisAgree.setText("拒绝");
				}
				final String myPushMessage = myPush.PushMessage;
				tvJoinDealAgree.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						sendAgreeAttendMsg(myPushMessage, "1");
						dealActivityJoinPushID = myPushID;
						tvJoinDealAgree.setEnabled(false);
						tvJoinDealDisAgree.setEnabled(false);
						myTvJoinDealAgree = tvJoinDealAgree;
						myTvJoinDealDisAgree = tvJoinDealDisAgree;
					}
				});
				tvJoinDealDisAgree.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						sendAgreeAttendMsg(myPushMessage, "5");
						dealActivityJoinPushID = myPushID;
						tvJoinDealAgree.setEnabled(false);
						tvJoinDealDisAgree.setEnabled(false);
						myTvJoinDealAgree = tvJoinDealAgree;
						myTvJoinDealDisAgree = tvJoinDealDisAgree;
					}
				});
				break;

			/** 群主审核用户申请的反馈 **/
			case h_protocol_pusher.SEND_Activity_Join_Checked:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_activityjoincheched, parent, false);
				ImageView imgCheckedUserHead = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityJoinCheckedUserHead);
				TextView tvCheckedUserName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinCheckedUserName);
				TextView tvCheckedStatue = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinCheckedStatue);
				TextView tvCheckedTime = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinCheckedTime);
				ImageView imgCheckedActLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityJoinCheckedActLogo);
				TextView tvCheckedActivityName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityJoinCheckedActivityName);
				RelativeLayout relCheckedJoinActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_itemMsgJoinCheckedActivityItemRel);
				// 解析数据
				JSONObject rsdjson_Checked;
				JSONObject asdjson_Checked;
				RelationSelectData rsd_Checked = null;
				ActivitySelectData asd_Checked = null;
				try {
					rsdjson_Checked = jsonObj.getJSONObject("rsd");
					asdjson_Checked = jsonObj.getJSONObject("asd");
					rsd_Checked = new RelationSelectData(
							rsdjson_Checked.getLong("RelationID"),
							rsdjson_Checked.getLong("ActivityID"),
							rsdjson_Checked.getLong("UserID"),
							rsdjson_Checked.getString("UploadTime"),
							rsdjson_Checked.getString("Status"));

					asd_Checked = new ActivitySelectData(
							asdjson_Checked.getLong("ActivityID"),
							asdjson_Checked.getLong("BuildActivityUserID"),
							asdjson_Checked.getLong("ActivityManagerID"),
							asdjson_Checked.getString("UploadTime"),
							asdjson_Checked.getString("IsDirectJoinIn"),
							asdjson_Checked.getString("ActivityFlag"),
							asdjson_Checked.getString("ActivityMemberNumber"),
							asdjson_Checked
									.getString("ActivityMaxMemberNumber"),
							asdjson_Checked.getString("ActivityDescribe"),
							asdjson_Checked.getString("ActivityName"),
							asdjson_Checked.getString("ActivityStartTime"),
							asdjson_Checked.getString("ActivityEndTime"),
							asdjson_Checked.getString("ActivityHoldPlace"),
							asdjson_Checked.getString("HelpPhone"),
							asdjson_Checked.getString("ActivityBelongClass"),
							asdjson_Checked.getString("ActivityTags"),
							asdjson_Checked.getString("ActivityOpinion"),
							asdjson_Checked.getString("ActivityAddress"),
							asdjson_Checked.getString("ActivityLogo"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tvCheckedUserName.setText("活动主办方");
				String statue = rsd_Checked.getStatus();
				if (statue.equals("1")) {
					tvCheckedStatue.setText("通过审核");
				}else if (statue.equals("2")) {
					tvCheckedStatue.setText("正在审核您的参加请求");
				}else if (statue.equals("3")) {
					tvCheckedStatue.setText("邀请您参加");
				}else if (statue.equals("4")) {
					tvCheckedStatue.setText("关注了活动");
				} else if (statue.equals("5")) {
					tvCheckedStatue.setText("拒绝了您的参加请求");
				} else if (statue.equals("6")) {
					tvCheckedStatue.setText("将您踢出了活动名单");
				}else{
					tvCheckedStatue.setText("");
				}

				tvCheckedTime.setText(GetTime.getNoYearTime(rsd_Checked
						.getUploadTime()));
				tvCheckedActivityName.setText(asd_Checked.getActivityName()
						+ "");

				ImageManager3.from(mContext)
						.displayImage(
								imgCheckedUserHead,
								"http://" + addressInfo.localIP + ":"
										+ addressInfo.visitPort + "/"
										+ addressInfo.visitFolderUserHeadXiaotu
										+ "/"
										+ asd_Checked.getBuildActivityUserID()
										+ ".png", R.drawable.z_logindefault);
				ImageManager2.from(mContext).displayImage(
						imgCheckedActLogo,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderActivityLogoXiaotu
								+ "/" + asd_Checked.getActivityID() + ".png",
						R.drawable.z_logindefault);
				// 点击群主的头像可到他的个人资料
				String buildUserIdChecked = asd_Checked
						.getBuildActivityUserID() + "";
				imgCheckedUserHead
						.setOnClickListener(new UserMomentInfoListener(
								buildUserIdChecked));
				tvCheckedUserName
						.setOnClickListener(new UserMomentInfoListener(
								buildUserIdChecked));

				final String activityIdChecked = asd_Checked.getActivityID()
						+ "";
				relCheckedJoinActivityItem
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(MyMessage.this,
										ActivityDetail.class);
								intent.putExtra("ActivityDetailActivityID",
										activityIdChecked);
								startActivity(intent);
								overridePendingTransition(
										R.anim.z_push_left_in,
										R.anim.z_push_left_out);
							}
						});

				break;

			/** 有人评论活动 **/
			case h_protocol_pusher.SEND_Activity_Leave_word:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_activityleaveword, parent, false);

				TextView tvLeaveUserName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityLeaveWordUserName);
				TextView tvLeaveTime = (TextView) view
						.findViewById(R.id.z_itemMsgActivityLeaveWordTime);
				TextView tvLeaveComment = (TextView) view
						.findViewById(R.id.z_itemMsgActivityLeaveWordComment);
				TextView tvLeaveActivityName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityLeaveWordActivityName);
				ImageView imgLeaveUserHead = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityLeaveWordUserHead);
				ImageView imgLeaveUserLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityLeaveWordActLogo);
				RelativeLayout relLeaveActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_itemMsgActivityItemRel);
				// 解析数据
				JSONObject rsdjson_Act_Leave_word;
				ActivityDiscussSelectData rsd_Act_Leave_word = null;
				try {
					rsdjson_Act_Leave_word = jsonObj.getJSONObject("adsd");
					rsd_Act_Leave_word = new ActivityDiscussSelectData(
							rsdjson_Act_Leave_word.getLong("DiscussID"),
							rsdjson_Act_Leave_word.getLong("ActivityID"),
							rsdjson_Act_Leave_word.getLong("UserID"),
							rsdjson_Act_Leave_word.getLong("PointDiscussID"),
							rsdjson_Act_Leave_word.getLong("ThisUserID"),
							rsdjson_Act_Leave_word.getString("UploadTime"),
							rsdjson_Act_Leave_word.getString("ActivityName"),
							rsdjson_Act_Leave_word.getString("UserName"),
							rsdjson_Act_Leave_word.getString("DiscussContent"),
							rsdjson_Act_Leave_word.getString("Photo"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 基本消息
				ImageManager3.from(mContext).displayImage(
						imgLeaveUserHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadXiaotu + "/"
								+ rsd_Act_Leave_word.getUserID() + ".png",
						R.drawable.z_logindefault);
				ImageManager2.from(mContext).displayImage(
						imgLeaveUserLogo,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderActivityLogoXiaotu
								+ "/" + rsd_Act_Leave_word.getActivityID()
								+ ".png", R.drawable.z_logindefault);

				tvLeaveUserName.setText(UtilsTrans
						.getUserName(rsd_Act_Leave_word.getUserName()));
				tvLeaveTime.setText(GetTime.getNoYearTime(rsd_Act_Leave_word
						.getUploadTime()));
				tvLeaveComment.setText(rsd_Act_Leave_word.getDiscussContent());
				tvLeaveActivityName.setText(rsd_Act_Leave_word
						.getActivityName());

				String useridasd = rsd_Act_Leave_word.getUserID() + "";
				imgLeaveUserHead.setOnClickListener(new UserMomentInfoListener(
						useridasd));
				tvLeaveUserName.setOnClickListener(new UserMomentInfoListener(
						useridasd));

				final String activityIdLeave_word = rsd_Act_Leave_word
						.getActivityID() + "";
				relLeaveActivityItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MyMessage.this,
								ActivityDetail.class);
						intent.putExtra("ActivityDetailActivityID",
								activityIdLeave_word);
						// //删除该数据
						// DBAdapter dbAdapter = new DBAdapter(mContext);
						// dbAdapter.open();
						// dbAdapter.deleteOnePushMessage(myPushID);
						// //关闭数据库连接
						// dbAdapter.close();

						SQLInfo sqlInfoDelete = new SQLInfo();
						sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
						String whereClause = ContentValuesChange.Key_PushID
								+ "=?";
						String[] whereArgs = new String[] { myPushID };
						sqlInfoDelete.SQLInfo = new DeleteObject(
								ContentValuesChange.PushMessage_Table,
								whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoDelete);

						startActivity(intent);
						overridePendingTransition(R.anim.z_push_left_in,
								R.anim.z_push_left_out);
					}
				});
				if (PushStatue.equals("2")) {// 如果已经处理了
					// view.setEnabled(false);
				} else {// 为了避免数据冲突

					final ActivityDiscussSelectData aaa = rsd_Act_Leave_word;
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(MyMessage.this,
									MsgActivityDiscuss.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("MsgActivityDiscussDetail",
									aaa);
							intent.putExtras(bundle);

							// DBAdapter dbAdapter = new DBAdapter(mContext);
							// dbAdapter.open();
							// dbAdapter.updateOnePushMessageByPushId(myPushID);
							// //关闭数据库连接
							// dbAdapter.close();

							startActivity(intent);
							overridePendingTransition(R.anim.z_push_left_in,
									R.anim.z_push_left_out);
						}
					});
				}

				break;

			/** 有人评论动态 **/
			case h_protocol_pusher.SEND_USER_WANTTO_PUSH:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_momentcomment, parent, false);

				TextView tvmomentUserName = (TextView) view
						.findViewById(R.id.z_itemMsgMomentCommentUserName);
				TextView tvmomentTime = (TextView) view
						.findViewById(R.id.z_itemMsgMomentCommentTime);
				TextView tvmomentComment = (TextView) view
						.findViewById(R.id.z_itemMsgMomentCommentComment);
				TextView tvmomentName = (TextView) view
						.findViewById(R.id.z_itemMsgMomentCommentName);
				ImageView imgmomentUserHead = (ImageView) view
						.findViewById(R.id.z_itemMsgMomentCommentUserHead);
				ImageView imgmomentLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgMomentCommentLogo);
				// 解析数据
				JSONObject mbijson;
				MomentBaseInfo mbi = null;
				try {
					mbijson = jsonObj.getJSONObject("mbi");

					mbi = new MomentBaseInfo(mbijson.getInt("MomentId"),
							mbijson.getString("MessageId"),
							mbijson.getString("UserId"),
							mbijson.getString("UserName"),
							mbijson.getString("UploadTime"),
							mbijson.getString("MomentContent"),
							mbijson.getString("PraiseNum"),
							mbijson.getString("CritizenNum"),
							mbijson.getString("MyUserId"),
							mbijson.getString("MyUserName"),
							mbijson.getString("HasOrNo"),
							mbijson.getString("Location"),
							mbijson.getString("Sex"), mbijson.getString("Age"),
							mbijson.getString("ShowPhotos"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 基本消息
				ImageManager3.from(mContext).displayImage(
						imgmomentUserHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadXiaotu + "/"
								+ mbi.MyUserId + ".png",
						R.drawable.z_logindefault);

				// 得到动态的图片，如果没有隐藏ImageView
				String photo1 = mbi.ShowPhotos;
				String photoLogo = "123";
				imgmomentLogo.setVisibility(View.GONE);
				if (photo1 != null) {
					if (!photo1.equals("0")) {
						String[] strs = photo1.split("#");
						if (strs.length > 0) {
							// 有图片才显示
							imgmomentLogo.setVisibility(View.VISIBLE);
							photoLogo = strs[0];
							ImageManager2
									.from(mContext)
									.displayImage(
											imgmomentLogo,
											"http://"
													+ addressInfo.localIP
													+ ":"
													+ addressInfo.visitPort
													+ "/"
													+ addressInfo.visitFolderMomentXiaotu
													+ "/" + photoLogo + ".png",
											R.drawable.z_logindefault);
						}
					}
				}

				tvmomentUserName
						.setText(UtilsTrans.getUserName(mbi.MyUserName));
				tvmomentTime.setText(GetTime.getNoYearTime(mbi.UploadTime));
				// MyUserName作为保存评论内容的字段
				tvmomentComment.setText(mbi.HasOrNo);
				// 原来的动态内容
				tvmomentName.setText(mbi.MomentContent);
				// 进入动态详情做评论

				String useridMbi = mbi.MyUserId + "";
				imgmomentUserHead
						.setOnClickListener(new UserMomentInfoListener(
								useridMbi));
				tvmomentUserName.setOnClickListener(new UserMomentInfoListener(
						useridMbi));

				final String messageId = mbi.MessageId;
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new Thread(
								new SearchMomentDiscussByMessageId(messageId))
								.start();
						new Thread(new SearchOneMomentDetailById(messageId))
								.start();
						// //删除该数据
						// DBAdapter dbAdapter = new DBAdapter(mContext);
						// dbAdapter.open();
						// dbAdapter.deleteOnePushMessage(myPushID);
						// //关闭数据库连接
						// dbAdapter.close();

						SQLInfo sqlInfoDelete = new SQLInfo();
						sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
						String whereClause = ContentValuesChange.Key_PushID
								+ "=?";
						String[] whereArgs = new String[] { myPushID };
						sqlInfoDelete.SQLInfo = new DeleteObject(
								ContentValuesChange.PushMessage_Table,
								whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoDelete);

						Intent intent = new Intent(MyMessage.this,
								MomentDetail.class);
						intent.putExtra("MomentDetailId", messageId);
						startActivity(intent);
						overridePendingTransition(R.anim.z_push_left_in,
								R.anim.z_push_left_out);
					}
				});

				break;

			/** 活动结束：用户给活动评分后提醒群主 **/
			case h_protocol_pusher.SEND_UserOpinion:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_activitymanagergrade, parent, false);
				TextView tvManagerGradeUserName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeUserName);
				TextView tvManagerGradeStatue = (TextView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeStatue);
				TextView tvManagerGradeTime = (TextView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeTime);
				RatingBar ratManagerGrade = (RatingBar) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeRating);
				TextView tvManagerGradeActivityName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeActivityName);
				TextView tvManagerGradeDealShowAll = (TextView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeShowAll);
				ImageView imgManagerGradeUserHead = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeUserHead);
				ImageView imgManagerGradeActLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeActLogo);
				RelativeLayout relManagerGradeActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_itemMsgActivityManagerGradeActivityItemRel);

				// 解析数据
				JSONObject rsdjson_UserOpinion_C;
				JSONObject rsdjson_UserOpinion_S;
				JSONObject uisdjson_UserOpinion = null;
				JSONObject asdjson_UserOpinion = null;
				updateActivityOpinion_independent_C rsd_UserOpinion_C = null;
				updateActivityOpinion_independent_S rsd_UserOpinion_S = null;
				UserInfoSelectData uisd_UserOpinion = null;
				ActivitySelectData asd_UserOpinion = null;
				try {
					// C:
					rsdjson_UserOpinion_C = jsonObj.getJSONObject("uaoic");
					// S:
					rsdjson_UserOpinion_S = jsonObj.getJSONObject("uaois");
					// User
					uisdjson_UserOpinion = jsonObj.getJSONObject("uisd");
					// Activity
					asdjson_UserOpinion = jsonObj.getJSONObject("asd");
					rsd_UserOpinion_C = new updateActivityOpinion_independent_C(
							rsdjson_UserOpinion_C.getString("UserID"),
							rsdjson_UserOpinion_C.getString("ActivityID"),
							rsdjson_UserOpinion_C.getString("Opinion"),
							rsdjson_UserOpinion_C.getString("UploadTime"));

					rsd_UserOpinion_S = new updateActivityOpinion_independent_S(
							rsdjson_UserOpinion_S.getString("UserID"),
							rsdjson_UserOpinion_S.getString("ActivityID"),
							rsdjson_UserOpinion_S.getString("UploadTime"),
							rsdjson_UserOpinion_S.getString("GotGrade"),
							rsdjson_UserOpinion_S.getString("TotalFullGrade"),
							rsdjson_UserOpinion_S.getString("Mark"),
							rsdjson_UserOpinion_S.getString("Content"));

					uisd_UserOpinion = new UserInfoSelectData(
							uisdjson_UserOpinion.getLong("UserID"),
							uisdjson_UserOpinion.getString("UserName"),
							uisdjson_UserOpinion.getString("UploadTime"),
							uisdjson_UserOpinion.getString("Code"),
							uisdjson_UserOpinion.getString("UserPhone"),
							uisdjson_UserOpinion.getString("UserJoinActivity"),
							uisdjson_UserOpinion
									.getString("UserAttentionClass"),
							uisdjson_UserOpinion.getString("UserQQ"),
							uisdjson_UserOpinion.getString("UserWeiChat"),
							uisdjson_UserOpinion.getString("UserTags"),
							uisdjson_UserOpinion.getString("UserClass"),
							uisdjson_UserOpinion.getString("UserDescribe"),
							uisdjson_UserOpinion.getString("UserLevel"),
							uisdjson_UserOpinion.getString("UserLogo"),
							uisdjson_UserOpinion.getString("UserAge"),
							uisdjson_UserOpinion.getString("UserSex"),
							uisdjson_UserOpinion.getString("School"),
							uisdjson_UserOpinion.getString("Profession"),
							uisdjson_UserOpinion.getString("Birthday"),
							uisdjson_UserOpinion.getString("Home"));

					asd_UserOpinion = new ActivitySelectData(
							asdjson_UserOpinion.getLong("ActivityID"),
							asdjson_UserOpinion.getLong("BuildActivityUserID"),
							asdjson_UserOpinion.getLong("ActivityManagerID"),
							asdjson_UserOpinion.getString("UploadTime"),
							asdjson_UserOpinion.getString("IsDirectJoinIn"),
							asdjson_UserOpinion.getString("ActivityFlag"),
							asdjson_UserOpinion
									.getString("ActivityMemberNumber"),
							asdjson_UserOpinion
									.getString("ActivityMaxMemberNumber"),
							asdjson_UserOpinion.getString("ActivityDescribe"),
							asdjson_UserOpinion.getString("ActivityName"),
							asdjson_UserOpinion.getString("ActivityStartTime"),
							asdjson_UserOpinion.getString("ActivityEndTime"),
							asdjson_UserOpinion.getString("ActivityHoldPlace"),
							asdjson_UserOpinion.getString("HelpPhone"),
							asdjson_UserOpinion
									.getString("ActivityBelongClass"),
							asdjson_UserOpinion.getString("ActivityTags"),
							asdjson_UserOpinion.getString("ActivityOpinion"),
							asdjson_UserOpinion.getString("ActivityAddress"),
							asdjson_UserOpinion.getString("ActivityLogo"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				tvManagerGradeUserName.setText(UtilsTrans
						.getUserName(uisd_UserOpinion.getUserName()));
				// tvManagerGradeUserName.setText(uisdjson_UserOpinion.toString());
				tvManagerGradeStatue.setText("评分");
				tvManagerGradeTime.setText(GetTime
						.getNoYearTime(rsd_UserOpinion_C.getUploadTime()));
				String opinion = rsd_UserOpinion_C.getOpinion();
				double op = Double.valueOf(opinion);
				ratManagerGrade.setRating((float) (op / 2));
				ratManagerGrade.setEnabled(false);
				tvManagerGradeActivityName.setText(asd_UserOpinion
						.getActivityName());
				// tvManagerGradeActivityName.setText(asdjson_UserOpinion.toString());

				String allPerson = rsd_UserOpinion_S.getTotalFullGrade();
				double num = Double.valueOf(allPerson);
				double allGrade = Double.valueOf(rsd_UserOpinion_S
						.getGotGrade());
				double average = allGrade / num;
				tvManagerGradeDealShowAll.setText("参评人数: " + allGrade
						+ "   平均分： " + average);

				// 基本消息
				ImageManager3.from(mContext).displayImage(
						imgManagerGradeUserHead,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderUserHeadXiaotu + "/"
								+ uisd_UserOpinion.getUserID() + ".png",
						R.drawable.z_logindefault);
				ImageManager2.from(mContext).displayImage(
						imgManagerGradeActLogo,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderActivityLogoXiaotu
								+ "/" + asd_UserOpinion.getActivityID()
								+ ".png", R.drawable.z_logindefault);

				String useridMana = uisd_UserOpinion.getUserID() + "";
				imgManagerGradeUserHead
						.setOnClickListener(new UserMomentInfoListener(
								useridMana));
				tvManagerGradeUserName
						.setOnClickListener(new UserMomentInfoListener(
								useridMana));

				final String activityIdManagerGrade = asd_UserOpinion
						.getActivityID() + "";
				relManagerGradeActivityItem
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(MyMessage.this,
										ActivityDetail.class);
								intent.putExtra("ActivityDetailActivityID",
										activityIdManagerGrade);
								startActivity(intent);
								overridePendingTransition(
										R.anim.z_push_left_in,
										R.anim.z_push_left_out);
							}
						});
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// 删除该数据
						// DBAdapter dbAdapter = new DBAdapter(mContext);
						// dbAdapter.open();
						// dbAdapter.deleteOnePushMessage(myPushID);
						// //关闭数据库连接
						// dbAdapter.close();

						SQLInfo sqlInfoDelete = new SQLInfo();
						sqlInfoDelete.p = SQLiteProtocol.deleteCommonData;
						String whereClause = ContentValuesChange.Key_PushID
								+ "=?";
						String[] whereArgs = new String[] { myPushID };
						sqlInfoDelete.SQLInfo = new DeleteObject(
								ContentValuesChange.PushMessage_Table,
								whereClause, whereArgs);
						sqlSecurityThread.handleSQl(sqlInfoDelete);

					}
				});

				break;

			/** 活动开始的提醒消息 **/
			case h_protocol_pusher.Z_Send_ActivityStart:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_activitystart, parent, false);
				TextView tvStartUserName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityStartUserName);
				TextView tvStartNoticeContent = (TextView) view
						.findViewById(R.id.z_itemMsgActivityStartNoticeContent);
				TextView tvStartActivityName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityStartActivityName);

				ImageView imgStartLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityStartActLogo);
				RelativeLayout relStartActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_itemMsgActivityStartItemRel);
				RelationActivity reStart = null;
				try {
					reStart = new RelationActivity(
							jsonObj.getInt("RelationActivityDBID"),
							jsonObj.getString("RelationUserID"),
							jsonObj.getString("RelationActivityID"),
							jsonObj.getString("RelationActivityName"),
							jsonObj.getString("RelationActivityBuilderUserID"),
							jsonObj.getString("RelationActivityStartTime"),
							jsonObj.getString("RelationActivityEndTime"),
							jsonObj.getString("RelationActivityStartNotifyFlag"),
							jsonObj.getString("RelationActivityEndNotifyFlag"),
							jsonObj.getString("RelationActivityStatus"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tvStartUserName.setText("系统提示");
				tvStartNoticeContent
						.setText("活动将于 "
								+ GetTime
										.getNoYearTime(reStart.RelationActivityStartTime)
								+ " 开始,赶快行动吧");
				tvStartActivityName.setText(reStart.RelationActivityName);
				ImageManager2.from(mContext).displayImage(
						imgStartLogo,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderActivityLogoXiaotu
								+ "/" + reStart.RelationActivityID + ".png",
						R.drawable.z_logindefault);
				final String activityIdStart = reStart.RelationActivityID + "";
				relStartActivityItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MyMessage.this,
								ActivityDetail.class);
						intent.putExtra("ActivityDetailActivityID",
								activityIdStart);
						startActivity(intent);
						overridePendingTransition(R.anim.z_push_left_in,
								R.anim.z_push_left_out);
					}
				});
				break;

			/** 活动结束：提示用户给活动评分 **/
			case h_protocol_pusher.Z_Send_ActivityEnd:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_activityusergrade, parent, false);
				TextView tvUserUserName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityUserUserName);
				TextView tvUserNoticeContent = (TextView) view
						.findViewById(R.id.z_itemMsgActivityUserNoticeContent);
				TextView tvUserActivityName = (TextView) view
						.findViewById(R.id.z_itemMsgActivityUserActivityName);
				ratUserGrade = (RatingBar) view
						.findViewById(R.id.z_itemMsgActivityUserRatingBar);
				TextView tvUserGradeDeal = (TextView) view
						.findViewById(R.id.z_itemMsgActivityUserGradeDealTv);
				myTvUserGradeShowAve = (TextView) view
						.findViewById(R.id.z_itemMsgActivityUserGradeShowTv);
				ImageView imgUserLogo = (ImageView) view
						.findViewById(R.id.z_itemMsgActivityUserActLogo);
				RelativeLayout relUserActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_itemMsgActivityUserItemRel);
				RelationActivity reEnd = null;
				try {
					reEnd = new RelationActivity(
							jsonObj.getInt("RelationActivityDBID"),
							jsonObj.getString("RelationUserID"),
							jsonObj.getString("RelationActivityID"),
							jsonObj.getString("RelationActivityName"),
							jsonObj.getString("RelationActivityBuilderUserID"),
							jsonObj.getString("RelationActivityStartTime"),
							jsonObj.getString("RelationActivityEndTime"),
							jsonObj.getString("RelationActivityStartNotifyFlag"),
							jsonObj.getString("RelationActivityEndNotifyFlag"),
							jsonObj.getString("RelationActivityStatus"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tvUserUserName.setText("系统提示:");
				tvUserNoticeContent.setText("活动已经结束，赶快评分吧");
				tvUserActivityName.setText(reEnd.RelationActivityName);
				// 将分数附上
				DBAdapter dbAdapter = new DBAdapter(mContext);
				dbAdapter.open();
				GradeInfo[] grades = dbAdapter
						.queryOneGradeInfos(reEnd.RelationActivityID);
				dbAdapter.close();
				if (grades != null) {
					if (grades.length > 0) {
						float r = Float.valueOf(grades[0].getGradeNum());
						ratUserGrade.setRating(r / 2);
						ratUserGrade.setEnabled(false);
						tvUserGradeDeal.setText("已处理");
						tvUserGradeDeal.setEnabled(false);
					}
				} else {
					final String ActivityID = reEnd.RelationActivityID;
					myTvUserGradeDeal = tvUserGradeDeal;
					tvUserGradeDeal.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							double rat = ratUserGrade.getRating();
							int finalRat = (int) (rat * 2);
							// 评分
							sendUserActivityOpinionMsg(ActivityID, finalRat
									+ "");
							gradeInfo = new GradeInfo(0,
									SEMapApplication.AccountNumber, ActivityID,
									finalRat + "", FormatTime.getFormatTime());
						}
					});
				}

				ImageManager2.from(mContext).displayImage(
						imgUserLogo,
						"http://" + addressInfo.localIP + ":"
								+ addressInfo.visitPort + "/"
								+ addressInfo.visitFolderActivityLogoXiaotu
								+ "/" + reEnd.RelationActivityID + ".png",
						R.drawable.z_logindefault);
				final String activityIdUser = reEnd.RelationActivityID + "";
				relUserActivityItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MyMessage.this,
								ActivityDetail.class);
						intent.putExtra("ActivityDetailActivityID",
								activityIdUser);
						startActivity(intent);
						overridePendingTransition(R.anim.z_push_left_in,
								R.anim.z_push_left_out);
					}
				});
				break;

			/** 客服反馈 **/
			case h_protocol_pusher.SEND_UserFeedback_Response:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_feedback, parent, false);
				TextView tvFeedBackUserName = (TextView) view
						.findViewById(R.id.z_itemMsgFeedBackUserName);
				TextView tvFeedBackTime = (TextView) view
						.findViewById(R.id.z_itemMsgFeedBackTime);
				TextView tvFeedBackContent = (TextView) view
						.findViewById(R.id.z_itemMsgFeedBackContent);

				// 解析数据
				String feedBackStr = "";
				try {
					feedBackStr = jsonObj.getString("Feedback_Response");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				tvFeedBackUserName.setText("客服  回复您：");
				tvFeedBackContent.setText(feedBackStr);
				break;

			/** 普通文本 **/
			case h_protocol_pusher.SEND_Text:
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_msg_sendtext, parent, false);
				TextView tvCommonTextUserName = (TextView) view
						.findViewById(R.id.z_itemMsgCommonTextUserName);
				TextView tvCommonTextTime = (TextView) view
						.findViewById(R.id.z_itemMsgCommonTextTime);
				TextView tvCommonTextContent = (TextView) view
						.findViewById(R.id.z_itemMsgCommonTextContent);

				// 解析数据
				String TextStr = "";
				try {
					TextStr = jsonObj.getString("Text");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				tvCommonTextUserName.setText("系统悄悄话：");
				// tvCommonTextTime.setText("16:52");
				tvCommonTextContent.setText(TextStr);
				break;
			}

			return view;
		}

	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}

	public static class ViewHolder {
		TextView pushUserName;
		TextView pushDoWhat;
		TextView pushTime;
		TextView pushDoDetailThing;

	}

	/**
	 * 同意参加：或者是不同意
	 * 
	 * @param message
	 * @param statue
	 */
	public void sendAgreeAttendMsg(String message, String statue) {
		Message msg = Message.obtain();

		RelationSelectData rsd = null;
		try {
			JSONObject jsonObj = new JSONObject(message);
			// int SEND_p = jsonObj.getInt("p");
			JSONObject rsdjson = jsonObj.getJSONObject("rsd");
			rsd = new RelationSelectData(rsdjson.getLong("RelationID"),
					rsdjson.getLong("ActivityID"), rsdjson.getLong("UserID"),
					rsdjson.getString("UploadTime"),
					rsdjson.getString("Status"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		changRelation_C li = new changRelation_C();
		if (rsd == null)
			return;
		li.setUserID(SEMapApplication.AccountNumber);
		li.setStandardUploadTime();
		li.setActivityID(rsd.getActivityID() + "");
		// li.setRelationID(rsd.getRelationID() + "");
		li.setStatus(statue);// 设置状态
		li.setSUserID(SEMapApplication.AccountNumber);
		li.setUserID(rsd.getUserID() + "");

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
	 * 对活动的评分
	 * 
	 * @param ActivityId
	 *            :活动ID
	 * @param Opinion
	 *            :分数
	 */
	public void sendUserActivityOpinionMsg(String ActivityId, String Opinion) {
		Message msg = Message.obtain();

		updateActivityOpinion_independent_C li = new updateActivityOpinion_independent_C();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setStandardUploadTime();
		li.setOpinion(Opinion);
		li.setActivityID(ActivityId);

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

	private class UserMomentInfoListener implements OnClickListener {
		String momentinfoUserId;

		public UserMomentInfoListener(String momentinfoUserId) {
			this.momentinfoUserId = momentinfoUserId;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, CenterInfo.class);
			intent.putExtra("momentCenterUserId", momentinfoUserId);
			startActivity(intent);
			overridePendingTransition(R.anim.z_push_left_in,
					R.anim.z_push_left_out);
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