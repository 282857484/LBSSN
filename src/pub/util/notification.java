package pub.util;

import java.io.File;

import pri.z.main.MainActivity;
import pri.z.mydb.RelationActivity;
import pri.z.show.R;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.VersionUpdate;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;


public class notification {

	/** Notification管理 */
	public static NotificationManager mNotificationManager;
	/** 
	 * Notification构造器 
	 */
	public static NotificationCompat.Builder mBuilder;
	/** 
	 * Notification的ID 
	 */
	public static int notifyId = 100;
	
	private static Context context;
	/**
	 * 使用前需要先初始化
	 * @param context
	 */
	public static void initThisNoticification(Context currentcontext){
		context = currentcontext;
		initService();
		initNotify();
	}
	
	/** 初始化通知栏 */
	private static void initNotify(){
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setContentTitle("party")
				.setContentText("party")
				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
				.setTicker("...")//通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
//				.setPriority(Notification.PRIORITY_LOW)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消  
				.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.FLAG_AUTO_CANCEL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.z_logo);
	}
	
	/** 显示通知栏 */
	public void showNotify(){
		mBuilder.setContentTitle("party")
				.setContentText("party")
//				.setNumber(number)//显示数量
				.setTicker("...");//通知首次出现在通知栏，带上升动画效果的
		mNotificationManager.notify(notifyId, mBuilder.build());
//		mNotification.notify(getResources().getString(R.string.app_name), notiId, mBuilder.build());
	}
	
	/** 显示大视图风格通知栏 */
	public void showBigStyleNotify() {
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		String[] events = new String[5];
		// Sets a title for the Inbox style big view
		inboxStyle.setBigContentTitle("大视图内容:");
		// Moves events into the big view
		for (int i=0; i < events.length; i++) {
		    inboxStyle.addLine(events[i]);
		}
		mBuilder.setContentTitle("测试标题")
				.setContentText("测试内容")
//				.setNumber(number)//显示数量
				.setStyle(inboxStyle)//设置风格
				.setTicker("测试通知来啦");// 通知首次出现在通知栏，带上升动画效果的
		mNotificationManager.notify(notifyId, mBuilder.build());
		// mNotification.notify(getResources().getString(R.string.app_name),
		// notiId, mBuilder.build());
	}
	
//	/** 显示常驻通知栏 */
//	public void showCzNotify(){
////		Notification mNotification = new Notification();//为了兼容问题，不用该方法，所以都采用BUILD方式建立
////		Notification mNotification  = new Notification.Builder(this).getNotification();//这种方式已经过时
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
////		//PendingIntent 跳转动作
//		PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, getIntent(), 0);  
//		mBuilder.setSmallIcon(R.drawable.ic_launcher)
//				.setTicker("常驻通知来了")
//				.setContentTitle("常驻测试")
//				.setContentText("使用cancel()方法才可以把我去掉哦")
//				.setContentIntent(pendingIntent);
//		Notification mNotification = mBuilder.build();
//		//设置通知  消息  图标  
//		mNotification.icon = R.drawable.ic_launcher;
//		//在通知栏上点击此通知后自动清除此通知
//		mNotification.flags = Notification.FLAG_ONGOING_EVENT;//FLAG_ONGOING_EVENT 在顶部常驻，可以调用下面的清除方法去除  FLAG_AUTO_CANCEL  点击和清理可以去调
//		//设置显示通知时的默认的发声、震动、Light效果  
//		mNotification.defaults = Notification.DEFAULT_VIBRATE;
//		//设置发出消息的内容
//		mNotification.tickerText = "通知来了";
//		//设置发出通知的时间  
//		mNotification.when=System.currentTimeMillis(); 
////		mNotification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
////		mNotification.setLatestEventInfo(this, "常驻测试", "使用cancel()方法才可以把我去掉哦", null); //设置详细的信息  ,这个方法现在已经不用了 
//		mNotificationManager.notify(notifyId, mNotification);
//	}
	
	/**
	 * 显示通知栏点击跳转到指定Activity
	 * @param cls 界面类,比如: myMap.class;表示要跳转的界面
	 * @param rsd 
	 */
	public static void showIntentActivityNotify(Class<?> cls, RelationSelectData rsd,
			UserInfoSelectData uisd ,ActivitySelectData asd){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
				.setContentTitle("活动信息")
				.setContentText(uisd.getUserName()  + getStatusMean(rsd.getStatus()) + "活动： " + asd.getActivityName())
				.setTicker(uisd.getUserName()+"活动请求");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	private static String getStatusMean(String status) {
		// TODO Auto-generated method stub
		int Status = Integer.valueOf(status);
		switch(Status) {
		case 1:
			return "参加";
		case 2:
			return "申请";
		case 3:
			return "邀请您参加";
		case 4:
			return "关注";
		default:
			return "unknown packet";
		}
	}

	/** 显示通知栏点击打开Apk */
	public void showIntentApkNotify(){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
				.setContentTitle("下载完成")
				.setContentText("点击安装")
				.setTicker("下载完成！");
		//我们这里需要做的是打开一个安装包
		Intent apkIntent = new Intent();
		apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		apkIntent.setAction(android.content.Intent.ACTION_VIEW);
		//注意：这里的这个APK是放在assets文件夹下，获取路径不能直接读取的，要通过COYP出去在读或者直接读取自己本地的PATH，这边只是做一个跳转APK，实际打不开的
		String apk_path = "file:///android_asset/cs.apk";
//		Uri uri = Uri.parse(apk_path);
		Uri uri = Uri.fromFile(new File(apk_path));
		apkIntent.setDataAndType(uri, "application/vnd.android.package-archive");
		// context.startActivity(intent);
		PendingIntent contextIntent = PendingIntent.getActivity(context, 0,apkIntent, 0);
		mBuilder.setContentIntent(contextIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		
		mNotificationManager.notify(notifyId, notification);
	}
	
	public static void showActivity_Join_CheckedNotify(Class<?> cls, RelationSelectData rsd,
			ActivitySelectData asd_Checked){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
				.setContentTitle("活动审核")
				.setContentText(getStatusActivity_Join_CheckedMean(rsd.getStatus(),asd_Checked.getActivityName()+""))
				.setTicker("活动申请结果");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	
	public static void showUserFeedback_ResponseNotify(Class<?> cls, String rsd){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
				.setContentTitle("客服反馈")
				.setContentText(rsd)
				.setTicker("客服反馈："+rsd);
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	public static void showUserCommonTextNotify(Class<?> cls, String rsd){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
		.setContentTitle("系统提示")
		.setContentText(rsd)
		.setTicker("系统提示："+rsd);
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	/**
	 * 提示用户版本更新
	 * @param cls
	 * @param url
	 */
	public static void showVersionUpdateNotify(Class<?> cls, String url){
		File fileAppDownload = new File(
				MainActivity.UserFolderAppDownload);
		if (!fileAppDownload.exists()) {
			fileAppDownload.mkdirs();
		}
		String path = fileAppDownload + "";
		new VersionUpdate().showDownLoadDialog(context,url,path);
	}
	
	/**
	 * 
	 * (1.用户参加;2.用申请;3.活动方邀请;4.用户关注;5.未审核通过;6.被踢出用户)
	 * @param status
	 * @param ActivityName
	 * @return
	 */
	private static String getStatusActivity_Join_CheckedMean(String status,String ActivityName) {
		// TODO Auto-generated method stub
		int Status = Integer.valueOf(status);
		switch(Status) {
		case 1:
			return "恭喜您通过了活动 "+ActivityName +" 主办方的审核";
		case 2:
			return "主办方正在审核 "+ActivityName +"";
		case 3:
			return "主办方邀请您参加 "+ActivityName +"";
		case 4:
			return "关注了"+ActivityName;
		case 5:
			return "您的申请未通过审核 "+ActivityName;
		case 6:
			return "您已经被踢出活动 "+ActivityName;
		default:
			return "unknown packet";
		}
	}
	
	
	/**活动评论**/
	public static void showActivity_Leave_wordNotify(Class<?> cls, ActivityDiscussSelectData rsd){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
		.setContentTitle(rsd.getUserName()+"回复了你的评论")
		.setContentText(rsd.getDiscussContent())
		.setTicker("与你有关的评论");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(notifyId, mBuilder.build());///////
	}
	
	/**动态评论推送**/
	public static void showMomentCommentNotify(Class<?> cls,MomentBaseInfo mbi){
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
		.setContentTitle(mbi.UserName+" 评论了你的动态")
		.setContentText(mbi.MomentContent)
		.setTicker("有人评论了你的动态，快去看看吧");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	/**用户评分后通知群主**/
	public static void showSend_UserOpinionNotify(Class<?> cls,UserInfoSelectData uisd_UserOpinion,
			ActivitySelectData asd_UserOpinion){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
		.setContentTitle(uisd_UserOpinion.getUserName()+"给你的活动评分了")
		.setContentText(asd_UserOpinion.getActivityName()+" 分数为...")
		.setTicker("有人给你的活动评分了，快去看看吧");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	/**
	 * 显示通知栏：活动即将开始
	 * @param cls 界面类,比如: myMap.class;表示要跳转的界面
	 * @param rsd 
	 */
	public static void showActivityStartInHourNotify(Class<?> cls, RelationActivity rsd){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setAutoCancel(true)//点击后让通知将消失    
				.setContentTitle("活动开始提示")
				.setContentText(rsd.RelationActivityName+" 将于 "+rsd.RelationActivityStartTime+" 开始")
				.setTicker("活动快开始了,赶紧动身吧");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId, notification);///////
	}
	
	
	/**
	 * 显示通知栏：活动即将开始
	 * @param cls 界面类,比如: myMap.class;表示要跳转的界面
	 * @param rsd 
	 */
	public static void showActivityEndGradeNotify(Class<?> cls, RelationActivity rsd){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		
		mBuilder.setAutoCancel(true)//点击后让通知将消失  
				.setContentTitle("活动评分提示")
				.setContentText(rsd.RelationActivityName)
				.setTicker("活动已经结束，快来评分吧");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(context, cls);
		
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		
		Notification notification =  mBuilder.build();
		//使用系统默认的声音等
		notification.defaults = Notification.DEFAULT_VIBRATE;
		mNotificationManager.notify(notifyId,notification);///////
	}
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	/**
	 * 初始化要用到的系统服务
	 */
	private static void initService() {
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	/** 
	 * 清除当前创建的通知栏 
	 */
	public void clearNotify(int notifyId){
		mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//		mNotification.cancel(getResources().getString(R.string.app_name));
	}
	
	/**
	 * 清除所有通知栏
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// 删除你发的所有通知
	}
	
	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:  
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT  
	 * 点击去除： Notification.FLAG_AUTO_CANCEL 
	 */
	public static PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(), flags);
		return pendingIntent;
	}
}
