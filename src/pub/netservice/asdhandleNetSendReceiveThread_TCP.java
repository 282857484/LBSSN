package pub.netservice;
//package pub.netservice;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.SocketAddress;
//import java.net.SocketException;
//import java.nio.ByteBuffer;
//import java.nio.channels.ClosedChannelException;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.SocketChannel;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.os.Message;
//import android.os.RemoteException;
//import android.util.Log;
//import pub.myserverjsontool.MyJsonTool;
//import pub.myserverjsontool.ObjAndWhat;
//
//public class handleNetSendReceiveThread_TCP implements Runnable {
//	private static final boolean RECEIVE = true;
//	private static final boolean DONOTRECEIVE = false;
//
//	// private final int port = NetConfig.TCPHOMEPORT;
//	// private final String ServerAddress = NetConfig.TCPHOME;
//
//	// 服务器地址与端口号
//	private static final String SERVER_IP = NetConfig.TCPHOME;
//	private static final int SERVER_PORT = NetConfig.TCPHOMEPORT;
//	private Socket client;
//	// 输入输出流
//	private DataOutputStream dos;
//	private DataInputStream dis;
//
//	private String formatTime;
//	private statusRow statusRow;
//
//	public handleNetSendReceiveThread_TCP(statusRow statusrow, String formatTime) {
//		this.statusRow = statusrow;
//		this.formatTime = formatTime;
//	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		Object obj;
//		String command = null;
//		boolean isRec = DONOTRECEIVE;
//		int RSCount = 0;
//
//		Log.e("handleNetSendReceiveThread_TCP", "handleNetSendReceiveThread_TCP start ...");
//		while ((!isRec) && (RSCount < 3)) {
//			RSCount++;
//			Log.e("handleNetSendReceiveThread_TCP", "FUCK1");
//			try {
//				client = new Socket(SERVER_IP, SERVER_PORT);
//				client.setSoTimeout(10000);
//				// 向服务端传送文件
//				// File file =new
//				// File("F:\\apache\\apacheServer\\Apache2\\htdocs\\myimage\\6.jpg");
//				dos = new DataOutputStream(client.getOutputStream());
//				dis = new DataInputStream(client.getInputStream());
//
//				Log.e("handleNetSendReceiveThread_TCP", "FUCK2");
//				obj = statusRow.getMsgReplace().msgObj;
//				command = GsonInstance.getG().toJson(obj);
//				Log.e("handleNetSendReceiveThread_TCP", "FUCK3");
//				if (command == null) {
//					command = "FUCK Client SEND some EMPTY tHing";
//				}
//				Log.e("handleNetSendReceiveThread_TCP", "FUCK4");
//				ByteBuffer buffer = Charset.forName("UTF-8").encode(command);
//				// 文件名和长度
//				dos.write(buffer.array());
//				dos.flush();
//
//				Log.e("handleNetSendReceiveThread_TCP", "FUCK5");
//				// 传输文件
//				byte[] sendBytes = new byte[1024 * 16];
//
//				StringBuilder sb = new StringBuilder();
//
//				int total = 0;
//				int length = 0;
//				int countFUcK =0;
//				while ((length = dis.read(sendBytes)) != -1) {
//					Log.e("FUCKTHEWORLD", "countFUcK" + (++countFUcK));
//					total = total + length;
//					String str = new String(sendBytes, 0, sendBytes.length,
//							"UTF-8").trim();
//					Log.e("FUCKTHEWORLD", "str" + str);
//					sb.append(str);
//					Log.e("FUCKTHEWORLD", "sb" + sb);
//				}
//
//				Log.e("handleNetSendReceiveThread_TCP", "FUCK6");
//				if (total > 0) {
//					Log.e("handleNetSendReceiveThread_TCP", "FUCK7");
//					// String ReceiveString = Charset.forName("UTF-8")
//					// .decode(buffer).toString();
//					String ReceiveString = sb.toString();
//					Log.e("handleSRTCP", "ReceiveString : " + ReceiveString);
//					List<ObjAndWhat> owList = new ArrayList<ObjAndWhat>(1);
//					owList = MyJsonTool.dealJSON(ReceiveString);
//
//					Log.e("handleNetSendReceiveThread_TCP", "FUCK8");
//					if (checkLegitimacy(ReceiveString)) {
//						Log.e("handleNetSendReceiveThread_TCP", "FUCK9");
//						isRec = RECEIVE;
//						statusRow.isSuccessed = true;
//						for (int fence = 0; fence < owList.size(); fence++) {
//							Log.e("handleNetSendReceiveThread_TCP", "FUCK10");
//							Message msg = Message.obtain();
//							ObjAndWhat ow = new ObjAndWhat();
//							ow = owList.get(fence);
//							msg.obj = ow.getObj();
//							msg.what = ow.getWhat();
//							msg.replyTo = ow.getReplyTo();
//							if (ow.getReplyTo() != null) {
//								try {
//									msg.replyTo.send(msg);
//								} catch (RemoteException e) {
//									Log.e("handleSRTCP", "远程通讯出现异常(非当前context)");
//									e.printStackTrace();
//								}
//							}
//						}
//					}
//				}
//
//				Log.e("handleNetSendReceiveThread_TCP", "FUCK11");
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (dis != null)
//						dis.close();
//					if (dos != null)
//						dos.close();
//					if (client != null)
//						client.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private static boolean checkLegitimacy(String receiveString) {
//		// TODO Auto-generated method stub
//		if (receiveString.length() < 20) {
//			return false;
//		}
//		if ((!receiveString.contains("UserID"))
//				&& (!receiveString.contains("UploadTime"))) {
//			return false;
//		}
//		if (receiveString.contains("\"UploadTime\":\"0\"")) {
//			return false;
//		}
//
//		return true;
//	}
//
//}
