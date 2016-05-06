package pub.netservice;

public interface NetConfig {

	public int POOL_SCALE = 15;
	public int HeartBreakFrequency = 6;
	public long CheckTimerRunFreuency = 3000;
	public int ReSendTime = 3;
	public int UDPReceiveSoTimeout = 3000;
	public String UDPMainHostName = "121.40.123.240";
//	public String UDPMainHostName = "192.168.1.101";
	public int UDPMainHostPort = 12345;
	public int ReceiveDatagramPacketBufferSize = 1024 * 60;
	public int SendDatagramPacketBufferSize = 1024 * 60;
	public int SendACKBufferSize = 1024 * 2;
	public int reReceiveTime = 3;
	public String TCPHOME = "121.40.123.240";
//	public String TCPHOME = "192.168.1.101";
	public int TCPHOMEPORT = 12345;
	public int TCPHOMEPORTFILE = 12346;
}
