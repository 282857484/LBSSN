package pub.util;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import pri.z.show.CenterInfo;
import pub.infoclass.myserver.protocol.z_baiduprotocol;
import pub.netservice.NetConfig;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
 


/**
 * 文件上传客户端
 */
public class Client2 extends Socket implements Runnable{
    
	// 服务器地址与端口号
    private static final String SERVER_IP = new String(NetConfig.UDPMainHostName);
    private static final int SERVER_PORT = NetConfig.TCPHOMEPORTFILE;
     
    private Socket client;
    // 文件输入流
    private FileInputStream fis;

    // 输入输出流

    private DataOutputStream dos;
    private DataInputStream dis;
    /**
     * fileType 文件类型
     * 设计协议:
     * 1代表头像图片
     * 2代表动态发布的图片
     * 3代表发送的表情
     */
    private int fileType;
    String filepath;
    String fileName;
    /**
     * 上传文件
     * @param path:要上传的文件
     * @param fileName：要上传到服务器后文件的命名，不含“.jpg”的后缀
     */
    public Client2(String path,String fileName,int fileType)
    {
    	this.filepath = path;
    	this.fileName = fileName;
    	this.fileType = fileType;
    }
    
    public boolean sendFileClient(String filePath,String fileName,int myFileType){
//        try {
            try {
                client =new Socket(SERVER_IP, SERVER_PORT);
                client.setSoTimeout(5000);
                //向服务端传送文件                
//                File file =new File("F:\\apache\\apacheServer\\Apache2\\htdocs\\myimage\\6.jpg");
                File file = new File(filePath);
                fis =new FileInputStream(file);
                dos =new DataOutputStream(client.getOutputStream());
                dis =new DataInputStream(client.getInputStream());
                 
                //文件名和长度
                dos.writeUTF(fileName+".png");
                
//                dos.flush();
                dos.writeLong(file.length());
//                dos.flush();
                dos.writeInt(myFileType);
                dos.flush();
                boolean mark = dis.readBoolean();
                System.out.println("mark: " + mark);
                
                //传输文件
                byte[] sendBytes =new byte[1024 * 16];
                int length =0;
                while((length = fis.read(sendBytes,0, sendBytes.length)) >= 0){
                    dos.write(sendBytes,0, length);
                    dos.flush();
                }
            }catch (IOException e) {
                e.printStackTrace();
                return false;
            }finally{
            	try {
	                if(fis !=null)
						fis.close();
	                if(dis !=null)
	                	dis.close();
	                if(dos !=null)
	                    dos.close();
	                if(client != null)
	                	client.close();
            	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
//        }catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        
        return true;
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sendFileClient(this.filepath,this.fileName,this.fileType);
		
	}
     
    
}