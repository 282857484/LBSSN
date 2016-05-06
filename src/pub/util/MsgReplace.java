package pub.util;

import android.os.Messenger;

public class MsgReplace {
	public Object msgObj;
	public Messenger msgReplyTo;
	public int msgWhat;

	public MsgReplace(Object msgobj, Messenger msgReplyTo, int msgWhat) {
		this.msgObj = msgobj;
		this.msgReplyTo = msgReplyTo;
		this.msgWhat = msgWhat;
	}

//	public String toString() {
//		return this.msgObj.toString() + this.msgReplyTo.toString()
//				+ this.msgWhat;
//	}
}
