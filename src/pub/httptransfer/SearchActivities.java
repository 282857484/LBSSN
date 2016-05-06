package pub.httptransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import pri.z.main.MainActivity;
import pri.z.utils.UtilsTrans;
import pub.application.SEMapApplication;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class SearchActivities implements Runnable{
	
	HttpClient httpClient = new DefaultHttpClient();
	String strResult = "doGetError";
//	String SearchPoiByIdURL = "http://api.map.baidu.com/geodata/v3/poi/list";
	String SearchPoiByIdURL = "http://api.map.baidu.com/geosearch/v3/nearby";
	HttpResponse httpResponse = null;
	int retrytime = 3;
	String page_index;
	Context mContext;
	Messenger RepleTo;
	int what;
	/**
	 * 查找数据
	 * @param myMessenger 
	 * @param myMessenger 
	 * @param geotable_id：表的序号
	 */
	public SearchActivities(String page_index,Context mContext, Messenger myMessenger,int what){
		this.page_index  = page_index;
		this.mContext = mContext;
		this.RepleTo = myMessenger;
		this.what = what;
	}
	

	public void SearchActivitiesMethod(String page_index,Context mContext){
		HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		String url = SearchPoiByIdURL;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		HttpPost httpRequest = new HttpPost(url);
		HttpGet httpRequest =  null;
		
		params.add(new BasicNameValuePair("ak", SEMapApplication.strkey));
		params.add(new BasicNameValuePair("geotable_id", "49436"));
		params.add(new BasicNameValuePair("page_index", page_index));
		params.add(new BasicNameValuePair("coord_type", "3"));
//		//得到用户设置的搜索范围
//		SearchDistance myDistance = new SettingInfo(mContext).getMySearchDistance();
//		String distance = "10000";//默认的是10000
//		if(myDistance != null){
//			if(myDistance.DistanceActivityDis != null){
//				if(! myDistance.DistanceActivityDis.equals("")){
//					int dis = Integer.valueOf(myDistance.DistanceActivityDis);
//					distance = dis*1000+"";//转化为m
//				}
//			}
//		}
		String distance = String.valueOf(MainActivity.SearchActivityDistance);
//		Log.v("哈哈哈哈", "SearchActivities---MainActivity.SearchActivityDistance---"+MainActivity.SearchActivityDistance);
		params.add(new BasicNameValuePair("radius", distance));
//		Log.e("哈哈哈哈", "radius is : "  + distance);
		params.add(new BasicNameValuePair("sortby", "starttime:1"));//按开始时间进行排序
		/***上线前要修改的搜索时间限制**/
		params.add(new BasicNameValuePair("filter", 
				"starttime:"+UtilsTrans.getFormatTimeBefore1Hour()+",299912312359999"));//筛选条件:从现在到以后的以后
		params.add(new BasicNameValuePair("location", MainActivity.a+","+MainActivity.b));
//		Log.e("哈哈哈哈", "location is : "  + MainActivity.a+","+MainActivity.b);
		
		httpRequest = new HttpGet(url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8));
		while (retrytime > 0) {
			try {
				httpResponse = httpClient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity());
//					Log.v("哈哈哈哈", "Received strResult is:  "  + strResult);
					
					Message msg = Message.obtain();
					msg.what = what;
					msg.obj = strResult;
					/**
					 * 这里传递值会出现问题!!!!!!!
					 */
					if(RepleTo != null)
					{
						RepleTo.send(msg);
					}
					retrytime = 0;

				} else {
					strResult = "Error Response:"
							+ httpResponse.getStatusLine().toString();
					System.out.println("strResult:  " + strResult
							+ "  STATUS :"
							+ httpResponse.getStatusLine().getStatusCode());

				}
			} catch (ClientProtocolException e) {
				strResult = e.getMessage().toString();
				e.printStackTrace();

			} catch (IOException e) {
				strResult = e.getMessage().toString();
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}
			retrytime--;
		}
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		SearchActivitiesMethod(page_index,mContext);
	}
}
