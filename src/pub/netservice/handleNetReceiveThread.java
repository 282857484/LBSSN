//package pub.netservice;
//
//import java.io.IOException;
//import java.io.InterruptedIOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.util.ArrayList;
//import java.util.List;
//
//import pub.myserverjsontool.MyJsonTool;
//import pub.myserverjsontool.ObjAndWhat;
//import android.os.Message;
//import android.os.RemoteException;
//import android.util.Log;
//
////public class handleNetReceiveThread implements Callable<Boolean> {
//public class handleNetReceiveThread implements Runnable {
//
//	// private Messenger clientMessenger;
//	DatagramSocket socket;
//	// statusRow statusrow;
//	// String formatTime;
////	boolean receivedResponse = false;
//	statusRow statusrow;
////	private final int MAX_TRIES = -1;
//
////	public handleNetReceiveThread(DatagramSocket socket) {
////		this.socket = socket;
////	}
//	public handleNetReceiveThread(DatagramSocket socket, statusRow statusrow) {
//		this.socket = socket;
//		this.statusrow = statusrow;
//	}
//
//	@Override
//	public void run() {
//		DatagramPacket receivePacket = new DatagramPacket(
//				new byte[NetConfig.ReceiveDatagramPacketBufferSize],
//				NetConfig.ReceiveDatagramPacketBufferSize);
////		int trytime = statusrow.sendTime;
//		// pub.netservice.NetService.statusMap.get(formatTime).recevieTime++;
//		do {
//			try {
//				Log.e("handleNetReceiveThread", "准备接收");
//				socket.receive(receivePacket);
//				statusrow.isSuccessed = true;
//				if (statusrow.isSuccessed) { // 成功收到
//
//					Log.e("RECEIVE", "接收到了!!!!");
//					byte[] b = new byte[NetConfig.ReceiveDatagramPacketBufferSize];
//					b = receivePacket.getData();
//					String s = new String(b, 0, b.length,"UTF-8");
//					
//					String dataString = s.trim();
//
//					Log.e("RECEIVE", "RECEIVE SUCCESSFUL" + dataString);
//
//					List<ObjAndWhat> owList = new ArrayList<ObjAndWhat>();
//					owList = MyJsonTool.dealJSON(dataString);
//
//					for (int fence = 0; fence < owList.size(); fence++) {
//						Message msg = Message.obtain();
//						ObjAndWhat ow = new ObjAndWhat();
//						ow = owList.get(fence);
//						msg.obj = ow.getObj();
//						msg.what = ow.getWhat();
//						msg.replyTo = ow.getReplyTo();
//						if (ow.getReplyTo() != null) {
//							try {
//								msg.replyTo.send(msg);
//							} catch (RemoteException e) {
//								Log.e("handleNetReceiveThread",
//										"远程通讯出现异常(非当前context)");
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//			} catch (InterruptedIOException e) {
//				// deal with receive false or
//				// Log.e("handleNetReceiveThread", "接收周期结束");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				Log.e("handleNetReceiveThread", "输入输出异常");
//				e.printStackTrace();
//			}
//			statusrow.sendTime++;
//		} while ((statusrow.sendTime < NetConfig.reReceiveTime) && (statusrow.isSuccessed == false)); 
//
//		socket.close();
//	}
//
//}
