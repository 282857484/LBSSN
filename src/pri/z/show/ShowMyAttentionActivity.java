package pri.z.show;

import java.util.ArrayList;
import java.util.List;

import pri.z.mydb.RelationActivity;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.DeleteObject;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.QueryUserRequest;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLResponse;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.GetTime;
import pri.z.utils.UtilsZZK;
import pri.z.utils.addressInfo;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.myserver.protocol.getRelaion_C;
import pub.infoclass.myserver.protocol.getRelaion_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.netservice.GsonInstance;
import pub.util.FormatTime;
import pub.util.ImageManager2;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

/**
 * @author 祝侦科 2014-5-20
 */
public class ShowMyAttentionActivity extends Activity {
	private ListView listView;

	List<RelationActivity> lists = new ArrayList<RelationActivity>();
	Context mContext;
	int pageIndex_His = 0;
	Button btnSync;
	ImageView imgHasNoData;
	RelativeLayout relLoading;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.z_showattentionacvitity);
		mContext = getBaseContext();
		
		QueryUserRequest.queryRelationActivitysDataInHis();
		imgHasNoData = (ImageView) findViewById(R.id.z_myAttentionActHasNoDataImg);
		relLoading = (RelativeLayout) findViewById(R.id.z_myAttentionActivityLoadingRel);
		btnSync = (Button)findViewById(R.id.z_showAttentionActivityTitleSync);
		btnSync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userid = SEMapApplication.AccountNumber;
				if(userid.equals("0")){
					LoginDialog.showLoginDialog(ShowMyAttentionActivity.this);
					return;
				}
				if (!UtilsZZK.checkNetworkState(ShowMyAttentionActivity.this)) {
					return;
				}
				sendActivityRelationMsg(userid,pageIndex_His+"");
				imgHasNoData.setVisibility(View.GONE);
				relLoading.setVisibility(View.VISIBLE);
			}
		});
	}
	/**
	 * 得到用户的关联活动
	 * 
	 * @param li
	 */
	public void sendActivityRelationMsg(String SearchId,String pageIndex) {
		Message msg = Message.obtain();
		// 参加或者关注信息
		getRelaion_C li = new getRelaion_C();
		li.setStandardUploadTime();
		li.setUserID(SEMapApplication.AccountNumber);
		li.setSearchUserID(SearchId);
		li.setPageIndex(pageIndex);
		li.setPageSize("10000");
		li.setExtraMark("3");
		ArrayList<Object> list = new ArrayList<Object>();
		
		list.add(li);
		
		btnSync.setEnabled(false);
		btnSync.setText("正在同步");
		
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

	/**
	 * show  myAttentionActivity
	 */
	private void showMyAttentionActivity(RelationActivity[] acts) {
		listView = (ListView) findViewById(R.id.z_myAttentionActivityListView);
//		DBAdapter dbAdapter = new DBAdapter(mContext);
//		dbAdapter.open();
//		RelationActivity[] acts = dbAdapter.queryAllRelationActivitys();
//		//关闭数据库连接
//		dbAdapter.close();
		if(acts == null){
			imgHasNoData.setVisibility(View.VISIBLE);
			return;
		}
			
		if(acts.length <= 0){
			imgHasNoData.setVisibility(View.VISIBLE);
			return;
		}
		imgHasNoData.setVisibility(View.GONE);
		
		//得到用户statue为：参加1、被邀请3、关注的活动4:
		for (int index = 0; index < acts.length; index++) {
			if (acts[index].RelationActivityStatus.equals("4")) {
				if(!IfExitTheSameActivity(lists,acts[index]))
					lists.add(acts[index]);
			}
		}
		if (lists.size() <= 0) {
			imgHasNoData.setVisibility(View.VISIBLE);
			return;
		}
		listView.setAdapter(adapter);

	}

	private boolean IfExitTheSameActivity(List<RelationActivity> myList,RelationActivity rel){
		boolean flag = false;
		if(myList.size() <= 0)
			return false;
		for(int index = 0;index < myList.size();index++){
			if(myList.get(index).RelationActivityID.equals(rel.RelationActivityID)){
				flag = true;
				break;
			}
		}
		return flag;
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
			return 0;
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
			RelationActivity rela = lists.get(position);
			final int myPosition = position;
			// 样式1
			if (vi == null) {

				holder = new ViewHolder();

				LayoutInflater inflater = getLayoutInflater();
				vi = inflater.inflate(R.layout.z_item_hisactivity, null);

				holder.actLogo = (ImageView) vi
						.findViewById(R.id.z_itemHisActLogo);
				holder.actName = (TextView) vi
						.findViewById(R.id.z_itemHisActivityName);
				holder.actStartTime = (TextView) vi
						.findViewById(R.id.z_itemHisActivityStartTime);
				vi.setTag(holder);

			} else {
				holder = (ViewHolder) vi.getTag();
			}
			
			ImageManager2.from(mContext).displayImage(
					holder.actLogo,
					"http://" + addressInfo.localIP + ":" + addressInfo.visitPort
							+ "/" + addressInfo.visitFolderActivityLogoDatu + "/"
							+ rela.RelationActivityID + ".png", R.drawable.z_logindefault);
			holder.actName.setText(rela.RelationActivityName);
			holder.actStartTime.setText(GetTime.getNoYearChineseTime(rela.RelationActivityStartTime)+" 开始");
			
			final String activityId = rela.RelationActivityID;
			vi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ShowMyAttentionActivity.this, ActivityLocal.class);
					intent.putExtra("ActivityLocalActivityID", activityId);
					//传递类型：-1主办，1参加，4关注
					intent.putExtra("ActivityLocalType", 4);
					
		    		startActivity(intent);
		    		overridePendingTransition(R.anim.z_push_left_in,
							R.anim.z_push_left_out);
				}

			});
			return vi;
		}

	};

	public final class ViewHolder {
		public ImageView actLogo;//活动Logo
		public TextView actName;//活动名称
		public TextView actStartTime;//活动开始时间

	}

	

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SQLiteProtocol.queryRelationActivitysDataInHis:
				SQLResponse responseAct = (SQLResponse) msg.obj;
//				if (responseAct.mark == 2)
//					return;
				RelationActivity[] rels = (RelationActivity[]) responseAct.result;
//				if (rels.length <= 0)
//					return;
				showMyAttentionActivity(rels);
				break;
				
			case protocolfromserver.getRelaion_S:
				getRelaion_S grs = (getRelaion_S) msg.obj;
				String strsRel = grs.getRelationList();
				String strsAct = "";
				relLoading.setVisibility(View.GONE);
				btnSync.setText("云同步");
				btnSync.setEnabled(true);
				String extraMark = grs.getExtraMark();
				if(extraMark.equals("3")){
					strsAct = grs.ActivityList;
				}
				if(strsAct.equals("")){
					imgHasNoData.setVisibility(View.VISIBLE);
					return;
				}
					
				if(strsRel == null){
					imgHasNoData.setVisibility(View.VISIBLE);
					return;
				}
					
				if(strsRel.length() < 20){
					imgHasNoData.setVisibility(View.VISIBLE);
					return;
				}
				List<RelationSelectData> listsRel = GsonInstance.getG().fromJson(
						strsRel, new TypeToken<List<RelationSelectData>>() {
						}.getType());
				
				if(strsAct == null)
					return;
				if(strsAct.length() < 20){
					return;
				}
				List<ActivitySelectData> listsAct = GsonInstance.getG().fromJson(
						strsAct, new TypeToken<List<ActivitySelectData>>() {
						}.getType());
				if(listsAct != null){
					if(listsAct.size() > 0){
//						DBAdapter dbAdapter = new DBAdapter(mContext);
//						dbAdapter.open();
//						dbAdapter.deleteUserRelationActivitysOne();
//						dbAdapter.deleteUserRelationActivitysFour();
//						dbAdapter.deleteUserRelationActivitysThree();
//						//关闭数据库连接
//						dbAdapter.close();
						
						String whereClause = ContentValuesChange.Key_RelationActivityStatus +"=? "; 
						
						//删除用户关注的
						SQLInfo sqlInfoDeleteFour = new SQLInfo();
						sqlInfoDeleteFour.p = SQLiteProtocol.deleteCommonData;
						String[] whereArgsFour = new String[]{"4"};
						sqlInfoDeleteFour.SQLInfo = new DeleteObject(
								ContentValuesChange.RelationActivity_Table, whereClause, whereArgsFour);
						sqlSecurityThread.handleSQl(sqlInfoDeleteFour);
						
						//删除用户被邀的
						SQLInfo sqlInfoDeleteThree = new SQLInfo();
						sqlInfoDeleteThree.p = SQLiteProtocol.deleteCommonData;
						String[] whereArgsThree = new String[]{"3"};
						sqlInfoDeleteThree.SQLInfo = new DeleteObject(
								ContentValuesChange.RelationActivity_Table, whereClause, whereArgsThree);
						sqlSecurityThread.handleSQl(sqlInfoDeleteThree);
						
						//删除用户参加的
						SQLInfo sqlInfoDeleteOne = new SQLInfo();
						sqlInfoDeleteOne.p = SQLiteProtocol.deleteCommonData;
						String[] whereArgsOne = new String[]{"1"};
						sqlInfoDeleteOne.SQLInfo = new DeleteObject(
								ContentValuesChange.RelationActivity_Table, whereClause, whereArgsOne);
						sqlSecurityThread.handleSQl(sqlInfoDeleteOne);
						
						
						for(int index = 0;index < listsAct.size();index++){
							ActivitySelectData act = listsAct.get(index);
							String noeTime = FormatTime.getFormatTime();
							String StartNotifyFlag = "2";
							String EndNotifyFlag = "2";
							String statue = "";
							boolean conFlag = false;
							for(int i = 0;i <listsRel.size();i++){
								RelationSelectData rr = listsRel.get(i);
								if(rr.getActivityID() == act.getActivityID()){
									statue = rr.getStatus();
									conFlag = true;
									break;
								}
							}
							if(! conFlag){
								break;
							}
							if(act.getActivityStartTime().compareTo(noeTime) > 0){
								StartNotifyFlag  = "1";
							}
							if(act.getActivityEndTime().compareTo(noeTime) > 0){
								EndNotifyFlag  = "1";
							}
							RelationActivity re = new RelationActivity(
									1, 
									act.getBuildActivityUserID()+"", 
									act.getActivityID()+"",
									act.getActivityName(), 
									act.getBuildActivityUserID()+"", 
									act.getActivityStartTime(), 
									act.getActivityEndTime(),
									StartNotifyFlag, EndNotifyFlag,statue);
							
//							dbAdapter.insertRelationActivity(re);
							ContentValues newValues = ContentValuesChange
									.contentInsertRelationActivity(re);
							SQLInfo sqlInfo = new SQLInfo();
							sqlInfo.p = SQLiteProtocol.insertCommonData;
							sqlInfo.SQLInfo = new InsertObject(
									ContentValuesChange.RelationActivity_Table, null, newValues);
							sqlSecurityThread.handleSQl(sqlInfo);
						}
						QueryUserRequest.queryRelationActivitysDataInHis();
//						btnSync.setText("同步更多");
//						pageIndex_His ++;
					}else{
						imgHasNoData.setVisibility(View.VISIBLE);
					}
				}else{
					imgHasNoData.setVisibility(View.VISIBLE);
				}

				break;
			}
		}
	};
	public Messenger remoteMessenger = SEMapApplication.serviceMessenger;
	public Messenger myMessenger = new Messenger(myHandler);
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.z_push_myleft_in, R.anim.z_push_myleft_out);
			return false;
		}
		return false;
	}

}
