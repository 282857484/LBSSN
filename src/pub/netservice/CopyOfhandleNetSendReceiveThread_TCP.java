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

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import pub.myserverjsontool.MyJsonTool;
import pub.myserverjsontool.ObjAndWhat;

public class CopyOfhandleNetSendReceiveThread_TCP implements Runnable {
	private static final int RECEIVE = 1;
	private static final int DONOTRECEIVE = 0;

	private final int port = NetConfig.TCPHOMEPORT;
	private final String ServerAddress = NetConfig.TCPHOME;

	private String formatTime;
	private statusRow statusRow;

	// public handleNetSendReceiveThread_TCP() {
	// this.command =
	// "[{\"ActivityID\":\"8\",\"PageIndex\":\"0\",\"PageSize\":\"10\",\"UploadTime\":\"201409152224270\",\"UserID\":\"15273131134\",\"p\":53}]";
	// try {
	// channel = SocketChannel.open();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// try {
	// channel.configureBlocking(false);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// SocketAddress target = new InetSocketAddress(ServerAddress, port);
	// try {
	// channel.connect(target);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// selector = Selector.open();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// // 用于套接字连接操作的操作集位
	// try {
	// channel.register(selector, SelectionKey.OP_CONNECT);
	// } catch (ClosedChannelException e) {
	// e.printStackTrace();
	// }
	// }

	public CopyOfhandleNetSendReceiveThread_TCP(statusRow statusrow, String formatTime) {
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
		Log.e("channel", "" + channel.isOpen() + channel.isRegistered()
				+ channel.isConnectionPending());
		Log.e("command", command);
		Log.e("target", "" + target);
		Log.e("statusrow", "" + statusRow.isSuccessed + statusRow.sendTime);
		Log.e("formatTime", formatTime);
		int flag = DONOTRECEIVE;
		int Count = 0;
		while (selector.isOpen() && channel.isOpen() && (flag == DONOTRECEIVE)
				&& (Count < 100)) {

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
						ByteBuffer buffer = Charset.forName("UTF-8").encode(
								command);
						SocketChannel sc = (SocketChannel) key.channel();
						try {
							int countSend = sc.write(buffer);
							Log.e("handleSRTCP", "write");
							if (countSend == command.length()) {
								// command = null;
								flag = DONOTRECEIVE;
							}
							sc.register(selector, SelectionKey.OP_READ);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (key.isValid() && key.isReadable()) {
						flag = RECEIVE;
						ByteBuffer buffer = ByteBuffer.allocate(1024 * 64);
						SocketChannel sc = (SocketChannel) key.channel();

						int readBytes = 0;
						int ret = 0;
						Log.e("handleSRTCP", "read");
						try {
							if((ret = sc.read(buffer)) > 0)
								readBytes += ret;
							
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							buffer.flip();
						}
						if (readBytes > 0) {
							String ReceiveString = Charset.forName("UTF-8")
									.decode(buffer).toString();
							Log.e("handleSRTCP", "ReceiveString : "
									+ ReceiveString);
							List<ObjAndWhat> owList = new ArrayList<ObjAndWhat>(
									1);
							owList = MyJsonTool.dealJSON(ReceiveString);

							if (checkLegitimacy(ReceiveString)) {
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
							}

							buffer.clear();
							buffer = null;
							
							
							key.cancel();
							try {
								selector.close();
								channel.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							break;
						}
					}

				}
			}
		}
		Log.e("handleNetSendReceiveThread_TCP", "THREAD OVER");
	}

	private static boolean checkLegitimacy(String receiveString) {
		// TODO Auto-generated method stub
		if (receiveString.length() < 20) {
			return false;
		}
		if ((!receiveString.contains("UserID"))
				&& (!receiveString.contains("UploadTime"))) {
			return false;
		}
		if (receiveString.contains("\"UploadTime\":\"0\"")) {
			return false;
		}

		return true;
	}
}
