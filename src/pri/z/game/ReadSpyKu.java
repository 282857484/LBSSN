package pri.z.game;

/**
 *
 * <p>Title: KeywordFliter.java</p>
 * <p>Description: 文本关键字过滤类</p>
 * @author jwbbwjjb
 * @version 1.0 9:30:01 AMMar 16, 2011
 * 修改日期  修改人  修改目的
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.content.Context;

public class ReadSpyKu {

	public static HashMap<Integer, String> SpyMap;
	
	//保存用户已经用过的SpyMap中的index  
	public static HashMap<String, Integer> SpyMapHasUse = new HashMap<String, Integer>();

	public static String[] getOneWords(Context mContext) {
		String[] strs = null;
		getSpyMap(mContext);
		if (SpyMap == null)
			return null;
		if (SpyMap.size() <= 0)
			return null;
		
		int count = SpyMap.size();
		int random = getIndex(count);
		String words = SpyMap.get(random);
		if(words != null){
			if(words.contains("%")){
				strs = words.split("%");
			}
		}
		return strs;
	}

	public static int getIndex(int count){
		int random = 0;
		//如果全部词库都已经读完了：就清零
		if(SpyMapHasUse.size() == SpyMap.size()){
			SpyMapHasUse.clear();
		}
		boolean flag = true;
		while(flag){
			random = new Random().nextInt(count);
			boolean canInsert = true;
			if(SpyMapHasUse.size() <= 0){//退出
				flag = false;
			}else{//遍历查找
				for(int index = 0;index < SpyMapHasUse.size();index++){
					if(random == SpyMapHasUse.get("SpyMapHasUse"+index)){
						//如果有相同的话
						canInsert = false;
						break;
					}
				}
			}
			if(canInsert){
				//加入到已经用过的Map中
				SpyMapHasUse.put("SpyMapHasUse"+SpyMapHasUse.size(), random);
				flag = false;
			}
			
		}
		
		
		return random;
	}
	public static HashMap<Integer, String> getSpyMap(Context mContext) {
		if (SpyMap == null) {
			SpyMap = new HashMap<Integer, String>();
			addKeywords(mContext);
		}
		return SpyMap;
	}

	public static void addKeywords(Context mContext) {

		String[] strs = null;
		try {
			InputStream input = mContext.getAssets().open("spyku.txt");
			// FileInputStream in = (FileInputStream)input;
			input.available();
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			String strNow = new String(buffer);

			strs = strNow.split("#");

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (strs == null) {
			return;
		}
		if (strs.length <= 0) {
			return;
		}
		for (int i = 0; i < strs.length; i++) {
			String key = strs[i];
			SpyMap.put(i, key);
		}
	}

}