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

import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import android.os.Message;
import android.util.Log;

public class SearchMomentsByUserId implements Runnable{
	
	HttpClient httpClient = new DefaultHttpClient();
	String strResult = "doGetError";
	String SearchPoiByIdURL = "http://api.map.baidu.com/geodata/v3/poi/list";
	HttpResponse httpResponse = null;
	int retrytime = 3;
	public static String ResultOneUserMomentsData = "";
	String userid;
	String page_index;
	/**
	 * 查找数据
	 * @param geotable_id：表的序号
	 */
	public SearchMomentsByUserId(String userid,String page_index){
		this.userid  = userid;
		this.page_index = page_index;
	}
	

	public void SearchPOIDiscussById(String userid,String page_index){
		HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		String url = SearchPoiByIdURL;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		HttpPost httpRequest = new HttpPost(url);
		HttpGet httpRequest =  null;
		params.add(new BasicNameValuePair("ak", SEMapApplication.someStrkey));
		params.add(new BasicNameValuePair("geotable_id", String.valueOf(49442)));
		params.add(new BasicNameValuePair("univeralindex", userid+","+userid));
		params.add(new BasicNameValuePair("page_index", page_index));
		params.add(new BasicNameValuePair("page_size", "5"));
		
		httpRequest = new HttpGet(url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8));
		while (retrytime > 0) {
			try {
				httpResponse = httpClient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity());
					ResultOneUserMomentsData = strResult;
					Message msg = Message.obtain();
					msg.what = z_baiduprotocol.baiduOneUserAllMoments;
					msg.obj = strResult;
					/**
					 * 这里传递值会出现问题!!!!!!!
					 */
					if(SEMapApplication.currentMessenger != null)
					{
						SEMapApplication.currentMessenger.send(msg);
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
		SearchPOIDiscussById(userid,page_index);
	}
}
