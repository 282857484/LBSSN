//package pub.netservice;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//import android.util.Log;
//
//
//public class handleNetEchoSendThread implements Runnable {
//
////	DatagramSocket simpleSendSocket;
//	String hostName = new String(NetConfig.UDPMainHostName);
//	int serverPort = NetConfig.UDPMainHostPort;
//
//	Object sendObject;
//	String sendData;
//
//	public handleNetEchoSendThread(Object obj) {
//		this.sendObject = obj;
//	}
//
//	@Override
//	public void run() {
//		try {
//			sendData = GsonInstance.getG().toJson(sendObject);
//			Log.e("handleNetEchoSendThread", sendData);
//			byte[] bytesToSend = new byte[sendData.length() + 50];
//			bytesToSend = sendData.getBytes();
//			InetAddress serverAddress = InetAddress.getByName(hostName);
//
////			simpleSendSocket = NetService.getDatagramSocket();
//			DatagramPacket packet = new DatagramPacket(bytesToSend,
//					bytesToSend.length, serverAddress, serverPort);
//
//			simpleSendSocket.send(packet);
//
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
