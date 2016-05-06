package pri.z.mydb;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ContactPerson implements Serializable {
	
	
	private String contactName;
	
	private String contactPhone;
	
	
	public ContactPerson(){}


	public ContactPerson(String contactName, String contactPhone) {
		super();
		this.contactName = contactName;
		this.contactPhone = contactPhone;
	}


	public String getContactName() {
		return contactName;
	}


	public void setContactName(String contactName) {
		this.contactName = contactName;
	}


	public String getContactPhone() {
		return contactPhone;
	}


	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	
}
