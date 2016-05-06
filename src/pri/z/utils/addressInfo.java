/**
 * 
 */
package pri.z.utils;


/**
 * @author 祝侦科
 *2014-3-30
 */
public interface addressInfo {
	
	String localIP = "121.40.123.240";
	String visitPort = "5984";
	
	//用户头像
	String visitFolderUserHeadDatu = "_utils/image/se/datu/touxiang";
	String visitFolderUserHeadXiaotu = "_utils/image/se/xiaotu/touxiang";
	//动态图片
	String visitFolderMomentDatu = "_utils/image/se/datu/dongtai";
	String visitFolderMomentXiaotu = "_utils/image/se/xiaotu/dongtai";
	//活动LOGO  
	String visitFolderActivityLogoDatu = "_utils/image/se/datu/activitylogo";
	String visitFolderActivityLogoXiaotu = "_utils/image/se/xiaotu/activitylogo";
	//活动项对应的图片
	String visitFolderActivityItemDatu = "_utils/image/se/datu/activityitem";
	String visitFolderActivityItemXiaotu = "_utils/image/se/xiaotu/activityitem";
	//活动项对应的图片
	String visitFolderPhotoWallDatu = "_utils/image/se/datu/tupianqiang";
	String visitFolderPhotoWallXiaotu = "_utils/image/se/xiaotu/tupianqiang";
	
//	//用户头像
//	String visitFolderUserHeadDatu = "image/se/datu/touxiang";
//	String visitFolderUserHeadXiaotu = "image/se/xiaotu/touxiang";
//	//动态图片
//	String visitFolderMomentDatu = "image/se/datu/dongtai";
//	String visitFolderMomentXiaotu = "image/se/xiaotu/dongtai";
//	//活动LOGO  
//	String visitFolderActivityLogoDatu = "image/se/datu/activitylogo";
//	String visitFolderActivityLogoXiaotu = "image/se/xiaotu/activitylogo";
//	//活动项对应的图片
//	String visitFolderActivityItemDatu = "image/se/datu/activityitem";
//	String visitFolderActivityItemXiaotu = "image/se/xiaotu/activityitem";
//	//活动项对应的图片
//	String visitFolderPhotoWallDatu = "image/se/datu/tupianqiang";
//	String visitFolderPhotoWallXiaotu = "image/se/xiaotu/tupianqiang";
	
	// 活动项图片展示(命名规则: ActivityID + "," + ItemNumber)
	final int  FileTypeActivityItem = 1;
	// 活动logo(命名规则: ActivityID)
	final int FileTypeActivityLogo = 2;
	// 表情(命名规则: UserID + "," + ???)
	final int FileTypebiaoqing = 3;
	// 动态(命名规则:　id)
	final int FileTypedongtai = 4;
	// 头像
	final int FileTypetouxiang = 5;
	//图片墙
	final int FileTypetupianqiang = 6;
	
	
	
	/**
	 * 地图截图
	 */
	
//	String defaultHead = Environment.getExternalStorageDirectory().toString()+"/SEApplication/ImgHead/head.jpg";
	/**
	 * 用户头像保存的文件夹
	 */
//	String infoHeadFolder = Environment.getExternalStorageDirectory().toString()+"/SEApplication/ImgHead/";
	
}
