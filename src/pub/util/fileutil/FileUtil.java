package pub.util.fileutil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;

public class FileUtil {

	public int isReadableorWriteable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
			return 1; // readable and writeable
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
			return 2; // read only
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we //need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
			return 3; // you can do nothing
		}

	}

//	public void getSDPath() {
//		File sdDir = null;
//		File sdDir1 = null;
//		File sdDir2 = null;
//		boolean sdCardExist = Environment.getExternalStorageState().equals(
//				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//		if (sdCardExist) {
//			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
//			sdDir1 = Environment.getDataDirectory();
//			sdDir2 = Environment.getRootDirectory();
//		}
//		System.out
//				.println("getExternalStorageDirectory(): " + sdDir.toString());
//		System.out.println("getDataDirectory(): " + sdDir1.toString());
//		System.out.println("getRootDirectory(): " + sdDir2.toString());
//	}

	/**
	 * 
	 * @param path
	 *            文件夹路径
	 */
	public void isExist(String path) {
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	/**
     * Save Bitmap to a file.保存图片到SD卡。
     * 
     * @param bitmap
     * @param file
     * @return error message if the saving is failed. null if the saving is
     *         successful.
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            // String _filePath_file.replace(File.separatorChar +
            // file.getName(), "");
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                	
                }
            }
        }
    }

}
