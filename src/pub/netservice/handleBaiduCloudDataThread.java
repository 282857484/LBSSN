package pub.netservice;

import pub.httptransfer.creatPoi;
import pub.httptransfer.deletePoi;
import pub.httptransfer.httpManager;
import pub.httptransfer.updatePoi;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import android.os.Messenger;
import android.util.Log;

public class handleBaiduCloudDataThread implements Runnable {

	httpManager httpmanager = new httpManager();

	// private Message msg = null;
	// Context context = null;
	// public handleBaiduCloudDataThread(Message msg ) {
	// this.msg = msg;
	// this.context = context;
	// }

	private int what = 0;
	private Object obj = null;
	Messenger replyTo = null;
	private int arg1 = 0;
	private int arg2 = 0;

	// ...

	public handleBaiduCloudDataThread(int what, Object obj, Messenger replyTo,
			int arg1, int arg2) {
		// TODO Auto-generated constructor stub
		this.what = what;
		this.obj = obj;
		this.replyTo = replyTo;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (what) {
		// creat
		case protocolwithbaidustore.baidutableactivityPOI:
		case protocolwithbaidustore.baidutableactivityRoute:
		case protocolwithbaidustore.baidutablearroundpersonPOI:
		case protocolwithbaidustore.baidutablemessagePOI:
		case protocolwithbaidustore.baidutableschoolRoute:
		case protocolwithbaidustore.baidutablestorePOI:
		case protocolwithbaidustore.baidutableteammatePOI:
		case protocolwithbaidustore.baidutableteamPOI:
		case protocolwithbaidustore.baidutablemessagediscuss:
			
			
			Log.e("sendMessageToBDStore", "sendMessageToBDStore**************");
			httpmanager.sendMessageToBDStore((creatPoi) obj, replyTo , what);
			break;

		case protocolwithbaidustore.baidutabledeleteactivityPOI:
		case protocolwithbaidustore.baidutabledeleteactivityRoute:
		case protocolwithbaidustore.baidutabledeletearroundpersonPOI:
		case protocolwithbaidustore.baidutabledeletemessagePOI:
		case protocolwithbaidustore.baidutabledeleteschoolRoute:
		case protocolwithbaidustore.baidutabledeletestorePOI:
		case protocolwithbaidustore.baidutabledeleteteammatePOI:
		case protocolwithbaidustore.baidutabledeleteteamPOI:
		case protocolwithbaidustore.baidutabledeletemessagediscuss:
			httpmanager.sendMessageToBDStore((deletePoi) obj, replyTo , what);
			break;

		case protocolwithbaidustore.baidutableudpateactivityPOI:
		case protocolwithbaidustore.baidutableudpateactivityRoute:
		case protocolwithbaidustore.baidutableudpatemessagePOI:
		case protocolwithbaidustore.baidutableudpateschoolRoute:
		case protocolwithbaidustore.baidutableudpatearroundpersonPOI:
		case protocolwithbaidustore.baidutableudpatestorePOI:
		case protocolwithbaidustore.baidutableudpateteammatePOI:
		case protocolwithbaidustore.baidutableudpateteamPOI:
		case protocolwithbaidustore.baidutableupdatemessagediscuss:
			httpmanager.sendMessageToBDStore((updatePoi) obj, replyTo , what);
			break;
		}

	}

}
