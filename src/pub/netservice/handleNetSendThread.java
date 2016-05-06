//package pub.netservice;
//
//import java.io.IOException;
//import java.io.InterruptedIOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//
//import pub.infoclass.pushservice.h_protocol_pusher;
//import pub.myserverjsontool.MyJsonTool;
//import pub.myserverjsontool.ObjAndWhat;
//import android.os.Message;
//import android.os.RemoteException;
//import android.util.Log;
//
//public class handleNetSendThread implements Runnable {
//
//	private String sendData;
//	// 这里的地址应该作为在application中的全局变量
//	private static String hostName = new String(NetConfig.UDPMainHostName);
//	private static int serverPort = NetConfig.UDPMainHostPort;
////	private DatagramSocket socket;
//
//	String formatTime;
//	statusRow statusrow;
//
//	Object obj;
//
//	// public handleNetSendThread(DatagramSocket socket, statusRow statusrow,
//	// String formatTime) {
//	// // TODO Auto-generated constructor stub
//	// this.socket = socket;
//	// this.statusrow = statusrow;
//	// this.formatTime = formatTime;
//	// }
//	public handleNetSendThread(statusRow statusrow, String formatTime) {
//		// TODO Auto-generated constructor stub
//
//		this.statusrow = statusrow;
//		this.formatTime = formatTime;
//	}
//
//	@Override
//	public void run() {
////		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//
//		DatagramSocket socket = null;
//		statusrow.sendTime++;
//		// this.obj = msg.obj;
//		this.obj = this.statusrow.getMsgReplace().msgObj;
//		Log.e("handleNetSendThread", this.obj.toString());
//
//		sendData = GsonInstance.getG().toJson(this.obj);
//		// Log.e("handleNetSendThread", "sendData : " + sendData);
//
//		InetAddress serverAddress;
//		try {
//			socket = new DatagramSocket();
//			socket.setSoTimeout(NetConfig.UDPReceiveSoTimeout);
//
//			serverAddress = InetAddress.getByName(hostName);
//
//			byte[] bytesToSend = new byte[NetConfig.SendDatagramPacketBufferSize];
//			bytesToSend = sendData.getBytes("UTF-8");
//
//			DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
//					bytesToSend.length, serverAddress, serverPort);
//			
//			socket.send(sendPacket);
//			Log.e("handleNetSendThread", "SENDING SUCCESSFUL" + sendData);
//			// if ((statusrow.getMsgReplace().msgWhat !=
//			// h_protocol_pusher.REQ_Heart_Beat)
//			// && (statusrow.getMsgReplace().msgWhat != h_protocol_pusher.ACK))
//			// NetService.ReceivePool.execute(new handleNetReceiveThread(
//			// socket, statusrow));
//
//			if ((statusrow.getMsgReplace().msgWhat != h_protocol_pusher.REQ_Heart_Beat)
//					&& (statusrow.getMsgReplace().msgWhat != h_protocol_pusher.ACK)) {
//				DatagramPacket receivePacket = new DatagramPacket(
//						new byte[NetConfig.ReceiveDatagramPacketBufferSize],
//						NetConfig.ReceiveDatagramPacketBufferSize);
////				byte[] bbb = new byte[8000];
////				DatagramPacket receivePacket = new DatagramPacket(bbb,8000);
//				// int trytime = statusrow.sendTime;
//				// pub.netservice.NetService.statusMap.get(formatTime).recevieTime++;
//				do {
//					try {
////						Log.e("socket.LocalPort", "-LocalPort" + socket.getLocalPort());
//						Log.e("handleNetReceiveThread", "准备接收");
//						socket.receive(receivePacket);
//						statusrow.isSuccessed = true;
//						if (statusrow.isSuccessed) { // 成功收到
//
//							Log.e("RECEIVE", "接收到了!!!!");
//							byte[] b = new byte[NetConfig.ReceiveDatagramPacketBufferSize];
////							byte[] b = new byte[8000];
//							b = receivePacket.getData();
//							String s = new String(b, 0, b.length, "UTF-8");
//
//							String dataString = s.trim();
//
//							Log.e("RECEIVE", "RECEIVE SUCCESSFUL" + dataString);
//
//							List<ObjAndWhat> owList = new ArrayList<ObjAndWhat>();
//							owList = MyJsonTool.dealJSON(dataString);
//
//							for (int fence = 0; fence < owList.size(); fence++) {
//								Message msg = Message.obtain();
//								ObjAndWhat ow = new ObjAndWhat();
//								ow = owList.get(fence);
//								msg.obj = ow.getObj();
//								msg.what = ow.getWhat();
//								msg.replyTo = ow.getReplyTo();
//								if (ow.getReplyTo() != null) {
//									try {
//										msg.replyTo.send(msg);
//									} catch (RemoteException e) {
//										Log.e("handleNetReceiveThread",
//												"远程通讯出现异常(非当前context)");
//										e.printStackTrace();
//									}
//								}
//							}
//						}
//					} catch (InterruptedIOException e) {
//						// deal with receive false or
//						// Log.e("handleNetReceiveThread", "接收周期结束");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						Log.e("handleNetReceiveThread", "输入输出异常");
//						e.printStackTrace();
//					}
//					statusrow.sendTime++;
//				} while ((statusrow.sendTime < NetConfig.reReceiveTime)
//						&& (statusrow.isSuccessed == false));
//
//			}
//			
//
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			Log.e("handleNetSendThread", "主机地址解析不明");
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Log.e("handleNetSendThread", "输入输出设备异常");
//			e.printStackTrace();
//		} finally {
//			if(socket != null)
//				socket.close();
//		}
//
//	}
//}
