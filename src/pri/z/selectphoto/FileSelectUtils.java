package pri.z.selectphoto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pri.z.main.MainActivity;
import pri.z.utils.UtilsTrans;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileSelectUtils {

	// public static String SDPATH = Environment.getExternalStorageDirectory()
	// + "/formats/";

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	public static List<String> ListsUpload = new ArrayList<String>();

	public static void saveBitmap(Bitmap bm, String picName) {
		try {
			File fileabc = new File(MainActivity.UserMomentUpload);
			if (!fileabc.exists()) {
				fileabc.mkdirs();
			}

			String filePath = MainActivity.UserMomentUpload + picName + ".JPEG";
			File f = new File(filePath);
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			if (!UtilsTrans.IfExitStringInLists(ListsUpload, filePath))
				ListsUpload.add(filePath);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delFile(String fileName) {
		File file = new File(MainActivity.UserMomentUpload + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(MainActivity.UserMomentUpload);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

}
