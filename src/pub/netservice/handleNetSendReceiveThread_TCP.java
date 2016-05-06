package pub.netservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import pub.myserverjsontool.MyJsonTool;
import pub.myserverjsontool.ObjAndWhat;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class handleNetSendReceiveThread_TCP implements Runnable {
	private static final int RECEIVE = 1;
	private static final int READYTRECEIVE = 0;
	private static final boolean UNSUCCESSFUL = false;
	private static final boolean SUCCESSFUL = true;

	private final int port = NetConfig.TCPHOMEPORT;
	private final String ServerAddress = NetConfig.TCPHOME;

	private String formatTime;
	private statusRow statusRow;


	public handleNetSendReceiveThread_TCP(statusRow statusrow, String formatTime) {
		this.statusRow = statusrow;
		this.formatTime = formatTime;
	}

	@Override
	public void run() {
		String command = null;
		SocketChannel channel = null;
		Selector selector = null;
		Object obj;

		obj = statusRow.getMsgReplace().msgObj;
		Log.e("handleNetSendReceiveThread_TCP", "Thread Start......");

		SocketAddress target = new InetSocketAddress(ServerAddress, port);
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(target);
			selector = Selector.open();
			channel.socket().setSoTimeout(10000);
			// 这里含绑定信息
			channel.register(selector, SelectionKey.OP_CONNECT);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		command = GsonInstance.getG().toJson(obj);
		if (command == null) {
			command = "FUCK Client SEND some EMPTY tHing";
		}

		statusRow.sendTime++;
//		Log.e("channel", "" + channel.isOpen() + channel.isRegistered()
//				+ channel.isConnectionPending());
//		Log.e("command", command);
//		Log.e("target", "" + target);
//		Log.e("statusrow", "" + statusRow.isSuccessed + statusRow.sendTime);
//		Log.e("formatTime", formatTime);
		int flag = READYTRECEIVE;
		boolean ReceiveStatus = UNSUCCESSFUL; 
		int Count = 0;
		
		ByteBuffer Sbuffer = Charset.forName("UTF-8").encode(command);
		ByteBuffer Rbuffer = ByteBuffer.allocate(1024 * 16);
		
		String ReceiveString = "";
		int totalBytesRecv = 0;
		int bytesRecv = 0;
		
		boolean runFlag = true;
		
		if((selector == null) || (channel == null)){
//			Log.e("NetTCP", "网络连接异常...");
			runFlag = false;
		} else if(selector.isOpen() && channel.isOpen() && (flag == READYTRECEIVE)
			&& (Count < 100)) {
			runFlag = true;
		} else {
			runFlag = false;
		}
		
		while (runFlag) {

			int nKeys = 0;
			try {
				nKeys = selector.select(100);
				Count++;
				Log.e("handleSRTCP", "SELECTOR -Count : " + Count);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nKeys > 0) {
				Log.e("handleSRTCP", "select");
				for (SelectionKey key : selector.selectedKeys()) {
					Log.e("handleSRTCP", "SelectionKey key");

					if (key.isConnectable()) {
						Log.e("handleSRTCP", "isConnectable");
						SocketChannel sc = (SocketChannel) key.channel();
						try {
							sc.configureBlocking(false);
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							sc.register(selector, SelectionKey.OP_WRITE);
						} catch (ClosedChannelException e) {
							e.printStackTrace();
						}
						try {
							sc.finishConnect(); // true if, and only if, this
												// channel's socket is now
												// connected
						} catch (IOException e) {
							e.printStackTrace();
							key.cancel();
							try {
								channel.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
					}

					if (key.isValid() && key.isWritable()) {
						Log.e("command", command);
						SocketChannel sc = (SocketChannel) key.channel();
						try {
							int countSend = 0;
							while(Sbuffer.hasRemaining()){
								countSend += sc.write(Sbuffer);
							}
							Log.e("handleSRTCP", "write");
							if (countSend == Sbuffer.limit()) {
								Log.e("handleSRTCP", "write Over");
								flag = READYTRECEIVE;
							}
							sc.register(selector, SelectionKey.OP_READ);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (key.isValid() && key.isReadable()) {
						
						SocketChannel sc = (SocketChannel) key.channel();

//						int readBytes = 0;
//						int ret = 0;
						Log.e("handleSRTCP", "read");
						try {
							if((bytesRecv = sc.read(Rbuffer)) > 0){
								totalBytesRecv += bytesRecv;
								String plusStr = new String(Rbuffer.array(), 0,
										Rbuffer.position()).trim();
								ReceiveString += plusStr;
								Rbuffer.clear();
								
								if(checkLegitimacy(ReceiveString)) { 
									// successful receive
									Log.e("handleSRTCP", "RECEIVE SUCCESSFUL");
									Log.e("handleSRTCP", "totalBytesRecv : " + totalBytesRecv);
									Log.e("handleSRTCP", "ReceiveString : " + ReceiveString);
									
									// mark status
									ReceiveStatus = SUCCESSFUL;
									
									// over this while...
									flag = RECEIVE;
									
									key.cancel();
									try {
										selector.close();
										channel.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									// not receive finish...
									sc.register(selector, SelectionKey.OP_READ);
								}
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						} 
					}

				}
			}
			if((selector == null) || (channel == null)){
//				Log.e("NetTCP", "网络连接异常...");
				runFlag = false;
			} else if(selector.isOpen() && channel.isOpen() && (flag == READYTRECEIVE)
				&& (Count < 100)) {
				runFlag = true;
			} else {
				runFlag = false;
			}
		}
		
		if(ReceiveStatus == SUCCESSFUL) {
			List<ObjAndWhat> owList = new ArrayList<ObjAndWhat>(1);
			owList = MyJsonTool.dealJSON(ReceiveString.substring(0, ReceiveString.length() - 17));
			statusRow.isSuccessed = true;
			for (int fence = 0; fence < owList.size(); fence++) {
				Message msg = Message.obtain();
				ObjAndWhat ow = new ObjAndWhat();
				ow = owList.get(fence);
				msg.obj = ow.getObj();
				msg.what = ow.getWhat();
				msg.replyTo = ow.getReplyTo();
				if (ow.getReplyTo() != null) {
					try {
						msg.replyTo.send(msg);
					} catch (RemoteException e) {
						Log.e("handleSRTCP",
								"远程通讯出现异常(非当前context)");
						e.printStackTrace();
					}
				}
			}
		} else {
			 // 
			Log.e("NetTCP", "网络连接异常...");
		}
		
		
		Log.e("handleNetSendReceiveThread_TCP", "THREAD OVER");
	}

	private static boolean checkLegitimacy(String receiveString) {
		// TODO Auto-generated method stub
		if (receiveString.length() < 20) {
			return false;
		}
		if (receiveString.substring(receiveString.length() - 17).equals("FUCKBITCHEVERYDAY")) {
			Log.e("handleSRTCP", "RECEIVE_ALL");
			return true;
		} else {
			Log.e("handleSRTCP", "RECEIVE_NOT_ALL");
			return false;
		}

	}
}
