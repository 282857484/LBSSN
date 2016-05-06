package pub.netservice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.google.gson.Gson;

import pub.infoclass.pushservice.ACK;

public class SendACKThread implements Runnable {

	private ACK ack;
	String hostName = new String(NetConfig.UDPMainHostName);
	int serverPort = NetConfig.UDPMainHostPort;
	private String sendData = "0";
	
	public SendACKThread(ACK ack) {
		// TODO Auto-generated constructor stub
		this.ack = ack;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DatagramSocket socket;
		
		sendData = GsonInstance.getG().toJson(ack);
		StringBuilder PacketSendData = new StringBuilder();
		PacketSendData.append("[").append(sendData).append("]");
		Log.e("哈哈哈哈", "ACK: " + ack.toString());
		
		InetAddress serverAddress;
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(hostName);
			
			byte[] bytesToSend = new byte[NetConfig.SendACKBufferSize];
			bytesToSend = PacketSendData.toString().getBytes();
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
					bytesToSend.length, serverAddress, serverPort);
			socket.send(sendPacket);
			Log.e("SendACKThread", "SENDING SUCCESSFUL" + PacketSendData);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e("handleNetSendThread", "主机地址解析不明");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("handleNetSendThread", "输入输出设备异常");
			e.printStackTrace();
		}
	}
	
	
}
