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

public class ReadTruthKu {

	public static HashMap<Integer, String> TruthMap;
	
	//保存用户已经用过的TruthMap中的index  
	public static HashMap<String, Integer> TruthMapHasUse = new HashMap<String, Integer>();

	public static String getOneTruth(Context mContext) {
		getTruthMap(mContext);
		if (TruthMap == null)
			return null;
		if (TruthMap.size() <= 0)
			return null;
		
		int count = TruthMap.size();
		int random = getIndex(count);
		String words = TruthMap.get(random);
		return words;
	}

	public static int getIndex(int count){
		int random = 0;
		//如果全部词库都已经读完了：就清零
		if(TruthMapHasUse.size() == TruthMap.size()){
			TruthMapHasUse.clear();
		}
		boolean flag = true;
		while(flag){
			random = new Random().nextInt(count);
			boolean canInsert = true;
			if(TruthMapHasUse.size() <= 0){//退出
				flag = false;
			}else{//遍历查找
				for(int index = 0;index < TruthMapHasUse.size();index++){
					if(random == TruthMapHasUse.get("TruthMapHasUse"+index)){
						//如果有相同的话
						canInsert = false;
						break;
					}
				}
			}
			if(canInsert){
				//加入到已经用过的Map中
				TruthMapHasUse.put("TruthMapHasUse"+TruthMapHasUse.size(), random);
				flag = false;
			}
			
		}
		
		
		return random;
	}
	public static HashMap<Integer, String> getTruthMap(Context mContext) {
		if (TruthMap == null) {
			TruthMap = new HashMap<Integer, String>();
			addKeywords(mContext);
		}
		return TruthMap;
	}

	public static void addKeywords(Context mContext) {

		String[] strs = null;
		try {
			InputStream input = mContext.getAssets().open("tellthetruth.txt");
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
			TruthMap.put(i, key);
		}
	}

}