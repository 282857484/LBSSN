package pri.z.show;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pri.z.main.XListView;
import pri.z.main.XListView.IXListViewListener;
import pri.z.utils.DiscussMoment;
import pri.z.utils.GetTime;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.myserver.protocol.getRelaion_C;
import pub.infoclass.myserver.protocol.getRelaion_S;
import pub.infoclass.myserver.protocol.getUserInfo_C;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import pub.util.ImageManager2;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

public class CenterActivity extends Activity implements IXListViewListener {

	Context mContext;
	public static final String TAG = "哈哈哈哈";
	List<MomentBaseInfo> momentLists = new ArrayList<MomentBaseInfo>();
	List<DiscussMoment> DiscussLists = new ArrayList<DiscussMoment>();
	// 动态的详情的请求码和返回码
	public static int MomentDetailRequest = 604;
	public static int MomentDetail_OK = 704;// 有数据返回
	TextView tvMomentPraise;// 点赞的数目
	TextView tvMomentCriticizen;// 点踩的数目
	ProgressBar progressBar;
	XListView activityListView;

	List<ActivitySelectData> listsActivity = new ArrayList<ActivitySelectData>();// 活动详细
	List<RelationSelectData> listsRelation = new ArrayList<RelationSelectData>();// 关注详细
	boolean relationNoFirstTime = false;
	String momentCenterUserId;
	int pageIndexActivities = 0;// 加载更多数据的page_index
	ImageView imgHasNoData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = getBaseContext();
		setContentView(R.layout.z_centeractivity);

		momentCenterUserId = getIntent().getStringExtra("momentCenterUserId");

		remoteMessenger = SEMapApplication.serviceMessenger;
		myMessenger = new Messenger(myHandler);
		progressBar = (ProgressBar) findViewById(R.id.z_centerActivityProgressBar);
		imgHasNoData = (ImageView) findViewById(R.id.z_centerActivityHasNoDataImg);
		if(!UtilsZZK.checkNetworkState(CenterActivity.this)){
			progressBar.setVisibility(View.GONE);
			imgHasNoData.setVisibility(View.VISIBLE);
			return;
		}
		initView();
		sendGetMomentUserIdRelationActivitiesMsg("0");
	}

	private void initView() {
		// TODO Auto-generated method stub
		activityListView = (XListView) findViewById(R.id.z_centerActivitylistview);

		activityListView.setPullLoadEnable(true);
		activityListView.setXListViewListener(CenterActivity.this);
		activityListView.setFastScrollEnabled(true);
		activityListView.setCacheColorHint(1);

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

		if(remoteMessenger == null) {
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
		li.setExtraMark("3");
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

		if(remoteMessenger == null) {
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

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case protocolfromserver.getRelaion_S:
				getRelaion_S grr = (getRelaion_S) msg.obj;
				String mark = grr.getMark();
				String extraMark = grr.getExtraMark();
				String strList = grr.getRelationList();
				String activityList = grr.ActivityList;
				
				progressBar.setVisibility(View.GONE);
				if(mark.equals("2")){
					imgHasNoData.setVisibility(View.VISIBLE);
					activityListView.setVisibility(View.GONE);
					onLoadR();//停止刷新
					return;
				}
				if (!relationNoFirstTime) {// 如果是第一次刷新
					// 如果没有数据
					if(strList.length() < 20){
						imgHasNoData.setVisibility(View.VISIBLE);
						return;
					}
				} 
				if(!extraMark.equals("3"))
					return;
				if(strList == null)
					return;
				if(strList.length() < 20){
					return;
				}
				List<RelationSelectData> listsR = GsonInstance.getG().fromJson(strList,
						new TypeToken<List<RelationSelectData>>() {
						}.getType());
				for(int index = 0;index < listsR.size();index++){
					listsRelation.add(listsR.get(index));
				}
				if(activityList == null)
					return;
				if(activityList.length() < 20){
					return;
				}
				List<ActivitySelectData> listsA = GsonInstance.getG().fromJson(
						activityList, new TypeToken<List<ActivitySelectData>>() {
						}.getType());
				for(int index = 0;index < listsA.size();index ++){
					if (!RemoveDuplicateActivity(listsActivity, listsA.get(index))) {
						listsActivity.add(listsA.get(index));
					}
				}
				
				if (!relationNoFirstTime) {//第一次
					activityListView.setAdapter(adapter);
					onLoadR();
				} else {// 第二/n次
					adapter.notifyDataSetChanged();
					onLoadR();
				}
				relationNoFirstTime = true;
				pageIndexActivities++;
				break;
			
			}
		}

	};

	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);

	/**
	 * 如果List中已经存在了一样的ActivitySelectData,则返回True
	 */
	public boolean RemoveDuplicateActivity(List<ActivitySelectData> myList,
			ActivitySelectData myAct) {
		boolean flag = false;
		if (myList.size() > 0) {
			for (int index = 0; index < myList.size(); index++) {
				boolean flag1 = myAct.getUploadTime().equals(
						myList.get(index).getUploadTime());
				boolean flag2 = myAct.getActivityName().equals(
						myList.get(index).getActivityName());

				if (flag1 && flag2) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 如果List中已经存在了一样的ActivitySelectData,则返回True
	 */
	public boolean RemoveDuplicateMoment(List<MomentBaseInfo> myList,
			MomentBaseInfo myMon) {
		boolean flag = false;
		if (myList.size() > 0) {
			for (int index = 0; index < myList.size(); index++) {
				boolean flag1 = myMon.UploadTime
						.equals(myList.get(index).UploadTime);
				boolean flag2 = myMon.MomentContent
						.equals(myList.get(index).MomentContent);

				if (flag1 && flag2) {
					flag = true;
				}
			}
		}
		return flag;
	}


	BaseAdapter adapter = new  BaseAdapter() {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listsActivity.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listsActivity.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;// 当前的View
			ViewHolderActivity holder;
			ActivitySelectData myActivity = listsActivity.get(position);
			RelationSelectData myRelation = listsRelation.get(position);
			final int myPosition = position;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.z_item_momentcenteractivity, parent, false);
				holder = new ViewHolderActivity();
				holder.imgUserHead = (ImageView) view
						.findViewById(R.id.z_momentCenterItemActivityUserHead);
				holder.imgRelation = (ImageView) view
						.findViewById(R.id.z_commentCenterItemActivityRelation);
				holder.tvTime = (TextView) view
						.findViewById(R.id.z_commentCenterActivityItemTime);
				
				holder.relActivityItem = (RelativeLayout) view
						.findViewById(R.id.z_commentCenterItemActivityItem);
				holder.imgActivityLogo = (ImageView) view
						.findViewById(R.id.z_commentCenterItemActivityLogo);
				holder.tvActivityName = (TextView) view
						.findViewById(R.id.z_commentCenterItemActivityName);

				view.setTag(holder);
			} else {
				holder = (ViewHolderActivity) view.getTag();
			}
			// 设置内容
			ImageManager3.from(mContext).displayImage(
					holder.imgUserHead,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderUserHeadXiaotu + "/"
							+ momentCenterUserId + ".png",
					R.drawable.z_logindefault);

			//设置默认的
			holder.imgRelation.setBackgroundResource(R.drawable.z_logindefault);
			
			String status = myRelation.getStatus();
			if (status.equals("-1")) {//主办
				holder.imgRelation.setBackgroundResource(R.drawable.z_statue_attend);
			} else if(status.equals("1")) {//参加
				holder.imgRelation.setBackgroundResource(R.drawable.z_statue_attend);
			} else if (status.equals("3")) {//受邀
				holder.imgRelation.setBackgroundResource(R.drawable.z_statue_beinvited);
			} else if (status.equals("4")) {//关注
				holder.imgRelation.setBackgroundResource(R.drawable.z_statue_attention);
			}
			
			holder.tvTime.setText(GetTime.getNoYearTime(myRelation
					.getUploadTime()));

			ImageManager2.from(mContext).displayImage(
					holder.imgActivityLogo,
					"http://" + addressInfo.localIP + ":"
							+ addressInfo.visitPort + "/"
							+ addressInfo.visitFolderActivityLogoXiaotu + "/"
							+ myActivity.getActivityID() + ".png",
					R.drawable.z_logindefault);
			// int型的数要转化为字符串才能setText()
			holder.tvActivityName.setText(myActivity.getActivityName());
			
			holder.relActivityItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String activityId = listsActivity.get(myPosition)
							.getActivityID() + "";
					Intent intent = new Intent(CenterActivity.this,
							ActivityDetail.class);
					intent.putExtra("ActivityDetailActivityID", activityId);
					
					startActivity(intent);
					overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
					
				}
			});

			return view;
		}

	};

	public class ViewHolderActivity {
		ImageView imgUserHead;
		ImageView imgRelation;
		TextView tvTime;
		
		RelativeLayout relActivityItem;
		ImageView imgActivityLogo;
		TextView tvActivityName;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		sendGetMomentUserIdRelationActivitiesMsg("0");
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		sendGetMomentUserIdRelationActivitiesMsg(pageIndexActivities + "");
	}

	private void onLoadR() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
		String time = df.format(c.getTime());
		activityListView.stopRefresh();
		activityListView.stopLoadMore();
		activityListView.setRefreshTime(time);

	}

	private void onLoadM() {
		activityListView.stopRefresh();
		activityListView.stopLoadMore();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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