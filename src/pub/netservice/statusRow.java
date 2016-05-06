package pub.netservice;

import android.os.Messenger;
import pub.util.MsgReplace;

public class statusRow {

	// public Message msg; // 信息
	private MsgReplace msgReplace;
	public int sendTime = 0; // 重发次数
	public boolean isSuccessed = false; // 接收成功标志

	public statusRow(MsgReplace msgReplace) {
		this.setMsgReplace(msgReplace);
	}

	public statusRow(Object msgObj, Messenger msgReplyTo, int msgWhat) {
		this.setMsgReplace(new MsgReplace(msgObj, msgReplyTo, msgWhat));
	}

	public MsgReplace getMsgReplace() {
		return msgReplace;
	}

	public void setMsgReplace(MsgReplace msgReplace) {
		this.msgReplace = msgReplace;
	}

}
