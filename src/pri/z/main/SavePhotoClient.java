package pri.z.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;

public class SavePhotoClient extends Thread {

	String imgUrl;//图片地址
	String saveFilePath;//图片保存路径
	public SavePhotoClient(String imgUrl,String saveFilePath){
		this.imgUrl = imgUrl;
		this.saveFilePath = saveFilePath;
	}
	
	@Override
	public void run(){
		savePhoto(this.imgUrl,this.saveFilePath);
	}
	/**第一种方式**/
	/**
	 * 将指定的图片路径保存到指定的文件下
	 * @param imgUrl：图片地址
	 * @param saveFilePath：图片保存路径
	 */
	public void savePhoto(String imgUrl,String saveFilePath){

		try {
			   
			FileOutputStream fos = new   FileOutputStream(saveFilePath);
			InputStream is = new URL(imgUrl).openStream();
			int   data = is.read(); 
			while(data!=-1){ 
			        fos.write(data); 
			        data=is.read(); 
			} 
			is.close();
			fos.close();			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} 	
	}
	
	/**第二种方式**/
	public void savePhotoToMobile(String imageURL){
//		InputStream bitmapIs = HttpUtils.getStreamFromURL(imageURL);
//	    Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);
	    Bitmap bitmap = getBitmapFromUrl();
	 
	    String path = Environment.getExternalStorageDirectory().toString()+"/head0819.png";  // 这个就是你存放的路径了。
	    File bitmapFile = new File(path);
	    FileOutputStream fos = null;
	    if (!bitmapFile.exists()) {
	     try{
	      bitmapFile.createNewFile();
	      fos = new FileOutputStream(bitmapFile);
	      bitmap.compress(Bitmap.CompressFormat.PNG,100, fos);
	     }catch (IOException e) {
	      e.printStackTrace();
	     }finally {
	      try {
	       if (fos != null) {
	        fos.close();
	       }
	      } catch (IOException e) {
	       e.printStackTrace();
	      }
	     }
	    }
	}
	
	public Bitmap getBitmapFromUrl(){
		Bitmap bitmap = null;
				String imageUrl = "http://photocdn.sohu.com/20111123/Img326603573.jpg";
				/**********************??*/
				//httpGet连接对象
				HttpGet httpRequest = new HttpGet(imageUrl);
				//取得HttpClient 对象
				HttpClient httpclient = new DefaultHttpClient();
				try {
					//请求httpClient ，取得HttpRestponse
					HttpResponse httpResponse = httpclient.execute(httpRequest);
					if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
						//取得相关信息 取得HttpEntiy
						HttpEntity httpEntity = httpResponse.getEntity();
						//获得一个输入流
						InputStream is = httpEntity.getContent();
						 bitmap = BitmapFactory.decodeStream(is);
						is.close();
						
//						iv.setImageBitmap(bitmap);
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return bitmap;
	}
}
