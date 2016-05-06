package pri.z.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import pri.z.show.MyDialog;
import pri.z.show.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
/**
 * 版本更新类的方法
 * 包括下载和安装
 * @author zhuzhenke
 *
 */
public class VersionUpdate {
	
	MyDialog dialogIfUpdate;//询问是否更新的Dialog
	/**
	 * 新版本的下载和安装
	 * @param url 下载路径
	 * @param filePath 保存的路径：（文件夹）
	 */
	public  void showDownLoadDialog(final Context mContext ,final String url,final String filePath) {
		// TODO Auto-generated method stub
		final LinearLayout wayplanForm = (LinearLayout) ((Activity) mContext)
				.getLayoutInflater().inflate(R.layout.z_dialog_versionupdate, null);
		Button btnSure = (Button) wayplanForm
				.findViewById(R.id.z_dialogVersionUpdateOK);
		Button btnCansel = (Button) wayplanForm
				.findViewById(R.id.z_dialogVersionUpdateNO);
		dialogIfUpdate = new MyDialog(mContext, R.style.z_myDialog);
		dialogIfUpdate.setContentView(wayplanForm);
		dialogIfUpdate.show();
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogIfUpdate.dismiss();
				beginDownLoad(mContext,url,filePath);
				beginLoadProgressDialog(mContext);
			}
		});
		btnCansel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogIfUpdate.dismiss();
			}
		});
		
	}
	
	/**
	 * 开始下载新版本
	 * @param urlPath：新版本路径
	 * @param loadPath:下载到本地的路径/文件夹
	 */
	public void beginDownLoad(final Context mContext,final String urlPath,final String loadPath){
	new Thread(new Runnable(){

		@Override
		public void run() {
			Looper.prepare();
			// TODO Auto-generated method stub
			//判断该文件夹是否存在
			File loadPathFile = new File(loadPath);
			if(!loadPathFile.exists()){
				loadPathFile.mkdir();
			}
			
			//要保存到的文件路径（包括文件名）
			String fileName = urlPath.substring(urlPath.lastIndexOf("/")+1);
			String finalPath = loadPath+"/"+fileName;
//			Log.v(TAG, "----finalPath-----"+finalPath);
			
			File finalFile = new File(finalPath);
			//如果文件存在，则先删除
			if(finalFile.exists()){
				finalFile.delete();
			}
			URLConnection con;
			try {
				URL url = new URL(urlPath);
				
//				Log.v(TAG, "----urlPath-----"+urlPath);
				// 打开连接
				con = url.openConnection();
				InputStream is = con.getInputStream();
				//读取文件长度
				int fileLength = con.getContentLength();
//				Log.v(TAG, "----fileLength-----"+fileLength);
				//创建缓冲区
				byte[] bytes = new byte[1024];
				
				//读取到的数据长度
				int len;
				int hasRead = 0;
				//输出的文件流
				OutputStream os = new FileOutputStream(finalFile);
				
				//开始读取
				while((len = is.read(bytes)) != -1){
					os.write(bytes, 0, len);
//					Log.v(TAG, "----is.read(bytes)--正在读取流---");
					//得到完成比例
					hasRead += len;
//					Log.v(TAG, "----hasRead--正在读取流---"+hasRead);
					Message msg = loadHandler.obtainMessage();
					msg.arg1 = (int)((double)hasRead/(double)fileLength*100);
					msg.sendToTarget();
				}
				
				//读取完成，关闭所有流
				is.close();
				os.close();
				
				//提示用户安装
				dialogLoading.dismiss();
				Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
				beginOpenAndInstall(mContext,finalFile);
				Looper.loop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}).start();;

	}
	
	SeekBar seekBar;
	Button showLoadProgress;
	MyDialog dialogLoading;
	/**
	 * 下载进度条
	 */
	public void beginLoadProgressDialog(Context mContext){
		final LinearLayout wayplanForm = (LinearLayout) ((Activity) mContext)
				.getLayoutInflater().inflate(R.layout.z_dialog_downloading, null);
		seekBar = (SeekBar) wayplanForm
				.findViewById(R.id.z_dialogDownloadingSeekBar);
		showLoadProgress = (Button) wayplanForm
				.findViewById(R.id.z_dialogDownloadingShow);
		dialogLoading = new MyDialog(mContext, R.style.z_myDialog);
		dialogLoading.setContentView(wayplanForm);
		seekBar.setEnabled(false);
		dialogLoading.setCancelable(false);
		dialogLoading.show();
		
	}
	
	/**
	 * Handler调节进度条
	 */
	
	private Handler loadHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			
			int progress = msg.arg1;
			seekBar.setProgress(progress);
			showLoadProgress.setText("已完成： "+progress+"%");
		}
		
	};
	
	/**
	 * 打开文件并安装
	 */
	public void beginOpenAndInstall(final Context mContext,final File finalFile){
		final LinearLayout wayplanForm = (LinearLayout) ((Activity) mContext)
				.getLayoutInflater().inflate(R.layout.z_dialog_install, null);
		Button btnSure = (Button) wayplanForm
				.findViewById(R.id.z_dialogInstallOK);
		Button btnCansel = (Button) wayplanForm
				.findViewById(R.id.z_dialogInstallNO);
		final MyDialog dialog = new MyDialog(mContext, R.style.z_myDialog);
		dialog.setContentView(wayplanForm);
		dialog.show();
		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openFile2(mContext,finalFile);
				dialog.dismiss();
			}
		});
		btnCansel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
	}
	
	/**
	 * 打开apk文件
	 * @param file
	 */
	private void openFile2(Context mContext,File file){
		Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(android.content.Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(file),  
                        "application/vnd.android.package-archive");  
        mContext.startActivity(intent);  
	}
}
