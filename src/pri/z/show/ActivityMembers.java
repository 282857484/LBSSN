/**
 * 
 */
package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.changRelation_C;
import pub.infoclass.myserver.protocol.changRelation_S;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.util.FormatTime;
import pub.util.ImageManager3;
import android.app.Activity;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMembers extends Activity {
	private ListView listView;
	private ListView listViewBuildUser;
	int IsBuildUser = 0;
	List<UserInfoSelectData> lists = new ArrayList<UserInfoSelectData>();
	List<UserInfoSelectData> listsBuildUser = new ArrayList<UserInfoSelectData>();
	Context mContext;
	String buildUserId;
	String ActivityID;
	int remoteMemberIndex = -1;// 记录要移除成员在lists中的下标
	ArrayList<String> listsUserId = new ArrayList<String>();

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);

		setContentView(R.layout.z_acvititymembers);

		Intent intent = getIntent();
		IsBuildUser = intent.getIntExtra("IsBuildUser", 0);

		ActivityID = intent.getStringExtra("ActivityID");
		buildUserId = intent.getStringExtra("BuildActivityUserID");
		// if(!buildUserId.equals("")){
		// sendGetMomentUserIdInfomationMsg(buildUserId);
		// }

		int UserSize = intent.getIntExtra("UserMemberListsSize", 0);
		// List<UserInfoSelectData> users = new ArrayList<UserInfoSelectData>();
		if (UserSize > 0) {
			for (int i = 0; i < UserSize; i++) {
				UserInfoSelectData user = (UserInfoSelectData) intent
						.getSerializableExtra("UserMemberLists" + i);
				if ((user.getUserID() + "").equals(buildUserId)) {
					listsBuildUser.add(user);
				} else {
					lists.add(user);
				}

			}
			showBuildUserInfo();
			showActivityMemberInfo();
		}

		// listsUserId =
		// getIntent().getStringArrayListExtra("ActivityMembersUserId");
		//
		// //请求得到用户的信息
		// if(listsUserId.size() > 0){
		// for(int i=0;i<listsUserId.size();i++){
		// sendGetMomentUserIdInfomationMsg(listsUserId.get(i));
		// }
		// }
	}

	public void sendGetMomentUserIdInfomationMsg(String searchUserId) {
		Message msg = Message.obtain();
		getUserInfo_C li = new getUserInfo_C();
		li.SearchUserID = searchUserId;
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

	private void showBuildUserInfo() {
		listViewBuildUser = (ListView) findViewById(R.id.z_ActivityMembersBuildUserListView);
		if (listsBuildUser == null) {
			return;
		}
		if (listsBuildUser.size() <= 0) {
			return;
		}
		listViewBuildUser.setAdapter(adapterBuildUser);
	}

	/**
	 * Show Members
	 */
	private void showActivityMemberInfo() {
		listView = (ListView) findViewById(R.id.z_ActivityMembersListView);
		if (lists == null) {
			return;
		}

		if (lists.size() <= 0) {
			return;
		}
		// if(lists.size() <= 1){
		listView.setAdapter(adapter);
		// }else{
		// adapter.notifyDataSetChanged();
		// }

	}

	@Override
	public void onResume() {
		super.onResume();
		SEMapApplication.currentMessenger = myMessenger;
		SEMapApplication.currentHandler = myHandler;
	}

	BaseAdapter adapter = new BaseAdapter() {

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
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View vi = convertView;
			ViewHolder holder = null;
			final int myPosition = position;
			UserInfoSelectData rela = lists.get(position);
			// 样式1
			if (vi == null) {

				holder = new ViewHolder();

				LayoutInflater inflater = getLayoutInflater();
				vi = inflater.inflate(R.layout.z_item_activitymember, null);

				holder.memberHead = (ImageView) vi
						.findViewById(R.id.z_itemActivityMemberHead);
				holder.memberName = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberName);
				holder.memberSexAndAge = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberSexAndAgeTv);
				holder.memberPhone = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberPhone);
				holder.memberDescribe = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberDescribe);
				holder.memberManager = (Button) vi
						.findViewById(R.id.z_activityMemberManagerTv);
				vi.setTag(holder);

			} else {
				holder = (ViewHolder) vi.getTag();
			}

			final String changeUserId = rela.getUserID() + "";

			ImageManager3.from(mContext).displayImage(
					holder.memberHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ changeUserId + ".png", R.drawable.z_logindefault);

			holder.memberName.setText(rela.getUserName());
			holder.memberPhone.setText(changeUserId);
			String userSex = rela.getUserSex();
			if (userSex.equals("1")) {
				holder.memberSexAndAge
						.setBackgroundResource(R.drawable.z_sex_man);
				holder.memberName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernameman));
			} else {
				holder.memberSexAndAge
						.setBackgroundResource(R.drawable.z_sex_woman);
				holder.memberName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernamewoman));
			}
			// setText() 里面不能为int
			holder.memberSexAndAge.setText(getUserAge(rela.getBirthday()) + "");

			holder.memberDescribe.setText(rela.getUserDescribe());
			final String userid = rela.getUserID() + "";

			if (IsBuildUser == 2) {
				holder.memberManager.setVisibility(View.VISIBLE);
				holder.memberPhone.setVisibility(View.VISIBLE);
			} else {
				holder.memberManager.setVisibility(View.GONE);
				holder.memberPhone.setVisibility(View.GONE);
			}
			holder.memberManager.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					sendChangeActivityRelationMsg(changeUserId, "6");
					// 记录要移除的用户在lists中的Index
					remoteMemberIndex = myPosition;
				}
			});
			vi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, CenterInfo.class);
					intent.putExtra("momentCenterUserId", userid);
					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}

			});
			return vi;
		}

	};

	/**
	 * 变更 参加或者关注活动
	 * 
	 * @param activityID
	 * @param status
	 *            :(1.用户参加;2.用申请;3.活动方邀请;4.用户关注)
	 */
	public void sendChangeActivityRelationMsg(String changeUserId, String status) {
		Message msg = Message.obtain();

		changRelation_C li = new changRelation_C();
		li.setActivityID(ActivityID);
		li.setStatus(status);
		li.setUserID(changeUserId);
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

	BaseAdapter adapterBuildUser = new BaseAdapter() {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listsBuildUser.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listsBuildUser.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View vi = convertView;
			ViewHolder holder = null;
			final int myPosition = position;
			UserInfoSelectData rela = listsBuildUser.get(position);
			// 样式1
			if (vi == null) {

				holder = new ViewHolder();

				LayoutInflater inflater = getLayoutInflater();
				vi = inflater.inflate(R.layout.z_item_activitymember, null);

				holder.memberHead = (ImageView) vi
						.findViewById(R.id.z_itemActivityMemberHead);
				holder.memberName = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberName);
				holder.memberSexAndAge = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberSexAndAgeTv);
				holder.memberDescribe = (TextView) vi
						.findViewById(R.id.z_itemActivityMemberDescribe);
				vi.setTag(holder);

			} else {
				holder = (ViewHolder) vi.getTag();
			}
			ImageManager3.from(mContext).displayImage(
					holder.memberHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ rela.getUserID() + ".png",
					R.drawable.z_logindefault);

			holder.memberName.setText(rela.getUserName());
			String userSex = rela.getUserSex();
			if (userSex.equals("1")) {
				holder.memberSexAndAge
						.setBackgroundResource(R.drawable.z_sex_man);
				holder.memberName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernameman));
			} else {
				holder.memberSexAndAge
						.setBackgroundResource(R.drawable.z_sex_woman);
				holder.memberName.setTextColor(getResources().getColor(
						R.color.z_color_moment_usernamewoman));
			}
			// setText() 里面不能为int
			holder.memberSexAndAge.setText(getUserAge(rela.getBirthday()) + "");

			holder.memberDescribe.setText(rela.getUserDescribe());
			final String userid = rela.getUserID() + "";
			vi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, CenterInfo.class);
					intent.putExtra("momentCenterUserId", userid);
					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}

			});
			return vi;
		}

	};

	public int getUserAge(String birthday) {
		String myage = birthday.substring(0, 4);// 得到出生年月
		String nowTime = FormatTime.getFormatTime();
		String myage2 = nowTime.substring(0, 4);
		int age = Integer.valueOf(myage2) - Integer.valueOf(myage);
		if (age < 0) {
			age = 0;
		}

		return age;
	}

	public final class ViewHolder {
		public TextView memberSexAndAge;
		public TextView memberPhone;
		public ImageView memberHead;
		public TextView memberName;
		public TextView memberDescribe;
		public Button memberManager;

	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case protocolfromserver.changRelation_S:
				changRelation_S chanrs = (changRelation_S) msg.obj;
				String arsChanMark = chanrs.getMark();// Mark表示该活动参加或者是关注消息已经发送成功，和能否参加成功没有关系
				String statusChan = chanrs.getStatus();
				if (arsChanMark.equals("1")) {
					if (statusChan.equals("6")) {
						UserInfoSelectData user = lists
								.remove(remoteMemberIndex);
						Toast.makeText(mContext,
								"踢除" + user.getUserName() + "成功",
								Toast.LENGTH_SHORT).show();
						adapter.notifyDataSetChanged();
					}
				} else {
					Toast.makeText(mContext, "踢除失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	/**
	 * 判断是否存在该用户
	 * 
	 * @param lists
	 * @param userid
	 * @return
	 */
	public boolean exitThisUser(List<UserInfoSelectData> lists, String userid) {
		if (lists == null)
			return false;
		if (lists.size() <= 0)
			return false;
		boolean flag = false;
		for (int i = 0; i < lists.size(); i++) {
			if ((lists.get(i).getUserID() + "").equals(userid)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

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
