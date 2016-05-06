package pri.z.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class UtilsZZK {

//	/**
//	 * 在全部的评论回复中筛选包含指定messageid的List
//	 * @param messageId：筛选条件
//	 * @param lists：全部List
//	 * @return
//	 */
//	public static List<DiscussMoment> getDiscussMomentByMessageId(String messageId,List<DiscussMoment> lists){
//		List<DiscussMoment> myList= new ArrayList<DiscussMoment>();
//		for(int index =0;index < lists.size();index++){
//			if(messageId.equals(lists.get(index).DiscussMessageId)){
//				myList.add(lists.get(index));
//			}
//		}
//		return myList;
//	}
//	
//	/**
//	 * 得到相关Moment的最后一条评论的id
//	 * @param lists：该Moment的所有评论
//	 * @return 最后一条评论的id
//	 */
//	public static String getTheLastDiscussId(List<DiscussMoment> lists){
//		String str = "";
//		if(lists != null && lists.size() > 0){
//			str = lists.get(0).DiscussUid;
//		}
//		return str;
//	}
	
	/**
	 * 转换图片成圆形
	 * 0926:直接返回，不转成圆形的
	 * @param bitmap 传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		return bitmap;
//		int width = bitmap.getWidth();
//		int height = bitmap.getHeight();
//		float roundPx;
//		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
//		if (width <= height) {
//			roundPx = width / 2;
//
//			left = 0;
//			top = 0;
//			right = width;
//			bottom = width;
//
//			height = width;
//
//			dst_left = 0;
//			dst_top = 0;
//			dst_right = width;
//			dst_bottom = width;
//		} else {
//			roundPx = height / 2;
//
//			float clip = (width - height) / 2;
//
//			left = clip;
//			right = width - clip;
//			top = 0;
//			bottom = height;
//			width = height;
//
//			dst_left = 0;
//			dst_top = 0;
//			dst_right = height;
//			dst_bottom = height;
//		}
//
//		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//
//		final Paint paint = new Paint();
//		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
//		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
//		final RectF rectF = new RectF(dst);
//
//		paint.setAntiAlias(true);// 设置画笔无锯齿
//
//		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
//
//		// 以下有两种方法画圆,drawRounRect和drawCircle
//		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
//		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);
//
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
//		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
//
//		return output;
	}
	
	public  static double getDistanceByLocation(String location1,String location2){
		double la = Double.valueOf(deCodeToLongitude(location1));
		double lb = Double.valueOf(deCodeToLatitude(location1));
		double l1 = Double.valueOf(deCodeToLongitude(location2));
		double l2 = Double.valueOf(deCodeToLatitude(location2));
		
		double bdMi = getDistanceByLongitudeAndLatitude(lb, la, l2, l1);
		
		return changeMiToKm(bdMi);
	}
	
	//将米转化为KM:最多含有一位小数
	public static double changeMiToKm(double Mi){
		double Km = (Mi+100)/1000;
		String str = String.valueOf(Km);
		int pointId = str.indexOf(".");
		String sss = str.substring(0, pointId+2);
		return Double.valueOf(sss);
	}
	
	private final static double EARTH_RADIUS = 6378137.0;   

	private static double getDistanceByLongitudeAndLatitude(double lat_a, double lng_a, double lat_b, double lng_b) {

	       double radLat1 = (lat_a * Math.PI / 180.0);
	       double radLat2 = (lat_b * Math.PI / 180.0);
	       double a = radLat1 - radLat2;
	       double b = (lng_a - lng_b) * Math.PI / 180.0;
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	              + Math.cos(radLat1) * Math.cos(radLat2)
	              * Math.pow(Math.sin(b / 2), 2)));
	       s = s * EARTH_RADIUS;
	       
	       s = Math.round(s * 10000) / 10000;

	       return s;

	}

	/**
	 * String strs = "[112.950313,28.177646]";   解析类似这样的字符，
	 */
	public static String deCodeToLongitude(String strs){
		String str = "";
		if(strs.length() > 0){
			String strNow = strs.substring(1,strs.length()-1);
			String[] sss = strNow.split(",");
			str = sss[0];
		}
		return str;
	}
	/**
	 * String strs = "[112.950313,28.177646]";   解析类似这样的字符，
	 */
	public static String deCodeToLatitude(String strs){
		String str = "";
		if(strs.length() > 0){
			String strNow = strs.substring(1,strs.length()-1);
			String[] sss = strNow.split(",");
			str = sss[1];
		}
		return str;
	}
	
	
	/**
     * 检测网络是否连接
     * @return
     */
    public  static boolean checkNetworkState(Context mContext) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
                flag = manager.getActiveNetworkInfo().isAvailable();
        }

        if(!flag){
        	Toast.makeText(mContext, "请连接网络", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }
	
}
