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
import org.json.JSONObject;

import pub.application.SEMapApplication;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class SearchOneMomentDetailById implements Runnable{
	
	HttpClient httpClient = new DefaultHttpClient();
	String strResult = "doPostError";
	String SearchPoiByIdURL = "http://api.map.baidu.com/geodata/v3/poi/detail";
	HttpResponse httpResponse = null;
	int retrytime = 3;
	String id;
	/**
	 * 查找数据detail
	 * @param id：表的索引
	 */
	public SearchOneMomentDetailById(String id){
		this.id  = id;
	}
	

	public void SearchMomentByIdDeal(String id){
		HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		String url = SearchPoiByIdURL;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		HttpPost httpRequest = new HttpPost(url);
		HttpGet httpRequest =  null;
		
		params.add(new BasicNameValuePair("ak", SEMapApplication.someStrkey));
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("geotable_id", "49442"));
		
		httpRequest = new HttpGet(url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8));
		
		while (retrytime > 0) {

			try {
				httpResponse = httpClient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					strResult = EntityUtils.toString(httpResponse.getEntity());
					JSONObject json = new JSONObject(strResult);
					
					Message msg = Message.obtain();
					msg.what = z_baiduprotocol.baiduOneMomentDetail;
					
					msg.obj = json.get("poi");
//					Log.v("哈哈哈哈", "Receive Msg is :"+json.get("poi").toString());
					Messenger	remoteMessenger = SEMapApplication.currentMessenger;
					if(remoteMessenger == null) {
						remoteMessenger = SEMapApplication.serviceMessenger;
						try {
							remoteMessenger.send(msg);
//							Log.v("哈哈哈哈", "remoteMessenger send to moment detail11111");
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					} else {
						try {
							remoteMessenger.send(msg);
//							Log.v("哈哈哈哈", "remoteMessenger send to moment detail222222");
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
//					/**
//					 * 这里传递值会出现问题!!!!!!!
//					 */
//					if(SEMapApplication.currentMessenger != null)
//					{
//						SEMapApplication.currentMessenger.send(msg);
//					}
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
		SearchMomentByIdDeal(id);
	}
}
