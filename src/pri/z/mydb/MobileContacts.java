package pri.z.mydb;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author 祝侦科 2014-12-5
 */
public class MobileContacts {

	
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;
	private static List<ContactPerson> list = null;

	/** 得到手机通讯录联系人信息 **/
	public static List<ContactPerson> getContacts(Context mContext) {
		if(list != null){
			return list;
		}
		list = new ArrayList<ContactPerson>();
		
		ContentResolver resolver = mContext.getContentResolver();
		ContactPerson person = null;
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				person = new ContactPerson();
				// 得到手机号码
				String contactPhone = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(contactPhone))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				person.setContactName(contactName);
				person.setContactPhone(contactPhone);
				list.add(person);
			}

			phoneCursor.close();
		}
		return list;
	}
}
