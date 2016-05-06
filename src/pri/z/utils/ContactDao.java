/**
 * 
 */
package pri.z.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.telephony.TelephonyManager;

/**
 * 该类主要是处理一些联系人的插入、关于本机号码、还有对号码的一些转化
 * 
 * @author 祝侦科 2014-3-2
 */
public class ContactDao {
	Context mContext;

	public ContactDao(Context context) {
		mContext = context;
	}

	/**
	 * @author 祝侦科 获取本机号码
	 */
	public String getPhoneNumber() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = mTelephonyMgr.getLine1Number(); // 得到本机号码

		/** 将号码转化为正常 */
		// String[] array1 = phone.split("[\\D]+"); //去除汉字等
		// StringBuilder sb = new StringBuilder();
		// for(int i=0;i<array1.length;i++){
		// sb.append(array1[i]);
		// }
		// String phoneNormal = sb.toString();
		// String normal = phoneNormal.substring(2);
		return phone;
	}

	public String getPhoneToNormal(String phone) {
		String[] array1 = phone.split("[\\D]+"); // 去除汉字等
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array1.length; i++) {
			sb.append(array1[i]);
		}
		String phoneNormal = sb.toString();
		// String normal = phoneNormal.substring(2);

		return phoneNormal;
	}

	public void testInsert(Context context, String name, String phone) {
		ContentValues values = new ContentValues();
		// 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
		Uri rawContactUri = context.getContentResolver().insert(
				RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		// 往data表入姓名数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);// 内容类型
		values.put(StructuredName.GIVEN_NAME, name);
		context.getContentResolver().insert(
				android.provider.ContactsContract.Data.CONTENT_URI, values);

		// 往data表入电话数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, phone);
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		context.getContentResolver().insert(
				android.provider.ContactsContract.Data.CONTENT_URI, values);
	}

}
