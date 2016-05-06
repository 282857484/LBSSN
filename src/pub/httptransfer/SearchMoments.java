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
import pub.application.SEMapApplication;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class SearchMoments implements Runnable{
	
	HttpClient httpClient = new DefaultHttpClient();
	String strResult = "doGetError";
//	String SearchPoiByIdURL = "http://api.map.baidu.com/geodata/v3/poi/list";
	String SearchPoiByIdURL = "http://api.map.baidu.com/geosearch/v3/nearby";
	HttpResponse httpResponse = null;
	int retrytime = 3;
	public static String ResultMoments = "";
	String page_index;
	Context mContext;
	Messenger myMessenger;
	int what;
	/**
	 * 查找数据
	 * @param myMessenger 
	 * @param geotable_id：表的序号
	 */
	public SearchMoments(String page_index,Context mContext, Messenger myMessenger,int what){
		this.page_index  = page_index;
		this.mContext = mContext;
		this.what = what;
		ResultMoments = "";
		this.myMessenger = myMessenger;
	}
	

	public void SearchMomentsMethod(String page_index,Context mContext){
		HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		String url = SearchPoiByIdURL;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		HttpPost httpRequest = new HttpPost(url);
		HttpGet httpRequest =  null;
		params.add(new BasicNameValuePair("ak", SEMapApplication.strkey));
		params.add(new BasicNameValuePair("geotable_id", String.valueOf(49442)));
		params.add(new BasicNameValuePair("page_index", page_index));
		params.add(new BasicNameValuePair("coord_type", "3"));
		params.add(new BasicNameValuePair("sortby", "uploadingtime:-1"));//按发表时间进行排序
		
		String distance = String.valueOf(MainActivity.SearchMomentDistance);
//		Log.v("哈哈哈哈", "SearchMoments---MainActivity.SearchMomentDistance---"+MainActivity.SearchMomentDistance);
		params.add(new BasicNameValuePair("radius", distance));
		
		params.add(new BasicNameValuePair("location", MainActivity.a+","+MainActivity.b));
//		Log.v("哈哈哈哈", "动态distance-----"+distance);
//		Log.v("哈哈哈哈", "动态经纬度-----"+MainActivity.a+","+MainActivity.b);
		httpRequest = new HttpGet(url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8));
		while (retrytime > 0) {
			try {
				httpResponse = httpClient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity());
					ResultMoments = strResult;
//					Log.v("哈哈哈哈", "ResultMoments=== "+ResultMoments);
					Message msg = Message.obtain();
					msg.what = what;
					msg.obj = strResult;
					/**
					 * 这里传递值会出现问题!!!!!!!
					 */
					if(myMessenger != null)
					{
						myMessenger.send(msg);
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
		SearchMomentsMethod(page_index,mContext);
	}
}
