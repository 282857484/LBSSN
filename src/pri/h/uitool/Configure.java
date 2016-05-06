package pri.h.uitool;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;

public class Configure implements RadialMenuEntry {

	private Context context;

	public Configure(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "配置";
	}

	@Override
	public int getIcon() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<RadialMenuEntry> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void menuActiviated() {
		// TODO Auto-generated method stub
//		Log.e("12345","测试设备与mac地址**********************************************************************************" + getDeviceInfo(context));
		
	}

	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);
	  
	      String device_id = tm.getDeviceId();
	      
	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	          
	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }
	      
	      json.put("device_id", device_id);
	      
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
}
