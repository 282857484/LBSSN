package pub.myserverjsontool;

import android.os.Messenger;

public class ObjAndWhat {

	private Object obj = null;
	private int what = 0;
	private Messenger replyTo = null;
	
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public int getWhat() {
		return what;
	}
	public void setWhat(int what) {
		this.what = what;
	}
	public Messenger getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(Messenger replyTo) {
		this.replyTo = replyTo;
	}
	
}
