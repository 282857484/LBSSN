package pub.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import pri.z.main.MainActivity;
import pri.z.sqlite.DBAdapter;
import pub.httptransfer.creatPoi;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.protocolwithbaidustore;
import pub.netservice.NetConfig;
import pub.util.CustomMultipartEntity.ProgressListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

	private Context context;
//	private String filePath;
	private ProgressDialog pd;
	private long totalSize;

	// 服务器地址与端口号
    private static final String SERVER_IP = new String(NetConfig.UDPMainHostName);
    private static final int SERVER_PORT =12346;
     
    private Socket client;
    // 文件输入流
    private FileInputStream fis;

    // 输入输出流

    private DataOutputStream dos;
    private DataInputStream dis;
	
	 private int fileType;
	    String filepath;
	    String fileName;
	public HttpMultipartPost(Context context,String path,String fileName,int fileType) {
		this.context = context;
		this.filepath = path;
    	this.fileName = fileName;
    	this.fileType = fileType;
	}

	public boolean sendFileClient(String filePath,String fileName,int myFileType){
        try {
            try {
                client =new Socket(SERVER_IP, SERVER_PORT);
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
                byte[] sendBytes =new byte[1024];
                int length =0;
                while((length = fis.read(sendBytes,0, sendBytes.length)) >0){
                    dos.write(sendBytes,0, length);
                    dos.flush();
                }
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }finally{
                if(fis !=null)
                    fis.close();
                if(dos !=null)
                    dos.close();
                client.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
	
	@Override
	protected void onPreExecute() {
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd.setMessage("正在上传图片...");
//		pd.setCancelable(false);
//		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
//		String serverResponse = null;
//
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpContext httpContext = new BasicHttpContext();
//		HttpPost httpPost = new HttpPost("上传URL， 如：http://www.xx.com/upload.php");
		
		new Thread(new Client2(this.filepath,this.fileName,this.fileType)).start();
//		sendFileClient(this.filepath,this.fileName,this.fileType);
		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			multipartContent.addPart("data", new FileBody(new File(
					filepath)));
			totalSize = multipartContent.getContentLength();

			// Send it
//			httpPost.setEntity(multipartContent);
//			HttpResponse response = httpClient.execute(httpPost, httpContext);
//			serverResponse = EntityUtils.toString(response.getEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
		pd.dismiss();
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
	}

}
