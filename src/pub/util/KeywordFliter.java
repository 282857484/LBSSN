package pub.util;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class KeywordFliter {

	private static HashMap keysMap = new HashMap();
	private int matchType = 1; //1:最小长度匹配 2：最大长度匹配
	
	/**
	 * 构造关键字树状DFA图
	 */
	public static void addKeywords(String[]  keywords){
		for(int i=0;i<keywords.length;i++){
			String key = keywords[i];
			
			HashMap nowhash = keysMap;
			for(int j=0;j<key.length();j++){
				char word = key.charAt(j);
				Object wordMap = nowhash.get(word);
				if(wordMap!=null){
					nowhash = (HashMap)wordMap;
				}else{
					HashMap newWordHash = new HashMap();
					newWordHash.put("isEnd", "0");
					nowhash.put(word,newWordHash);
					nowhash = newWordHash;
				}
				if(j==key.length()-1){
					nowhash.put("isEnd", "1");
				}
			}
		}
	}
	
	public  static void getMingGanStrs(Context mConext){
		String[] strs = null;
//		List<String> lists = new ArrayList<String>();
		try {
			InputStream input = mConext.getAssets().open("minganci.txt");
//			FileInputStream in = (FileInputStream)input;
			input.available();
			int size = input.available();
			
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();
			
			String strNow = new String(buffer);
//			if(strNow != null){
//				int count = strNow.length();
//				if(count >= 2){
//					strNow = strNow.substring(0, count-1);
////					lists.add(strNow);
//				}
//			}
			
			strs = strNow.split("#");
			addKeywords(strs);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 重置关键词
	 */
	public static void clearKeywords(){
		keysMap = new HashMap();
	}
	
	/**
	 * 检查一个字符串从begin位置起开始是否有keyword符合，
	 * 如果有符合的keyword值，返回值为匹配keyword的长度，否则返回零
	 * flag 1:最小长度匹配 2：最大长度匹配
	 */

	public static  int checkKeyWords(String txt,int begin,int flag){	
		HashMap nowhash = keysMap;		
		int maxMatchRes = 0;
		int res = 0;
		for(int i=begin;i<txt.length();i++){
			char word = txt.charAt(i);
			Object wordMap = nowhash.get(word);
			if(wordMap!=null){
				res++;
				nowhash = (HashMap)wordMap;
				if(((String)nowhash.get("isEnd")).equals("1")){
					if(flag==1){
						return res;
					}else{
						maxMatchRes = res;
					}
				}
			}else{
				return maxMatchRes;
			}
		}
		return maxMatchRes;
	}
	
	/**
	 * 返回txt中关键字的列表
	 */
	public HashMap getTxtKeyWords(String txt){		
		HashMap res =new HashMap();
		for(int i=0;i<txt.length();){
			int len = checkKeyWords(txt,i,matchType);
			if(len>0){
				Object obj = res.get(txt.substring(i, i+len));
				if(obj==null){
					res.put(txt.substring(i, i+len),new Integer(1));
				}else{
					Integer count = new Integer

(((Integer)obj).intValue()+1);
					res.put(txt.substring(i, i+len),count);
				}
				i+=len;
			}else{
				i++;
			}
		}
		return res;
	}

	/**
	 * 仅判断txt中是否有关键字
	 */
	public static boolean isContentKeyWords(String txt){
		for(int i=0;i<txt.length();i++){
			int len = checkKeyWords(txt,i,1);
			if(len>0){
				return true;
			}
		}
		return false;
	}

	/**
	 * flag:
	 * 1:将txt中的关键字替换成指定字符串
	 * 2：将txt中的关键字每个字都替换成指定的字符串
	 */
	public String getReplaceStrTxtKeyWords(String txt,String replacestr,int flag){
		StringBuffer res = new StringBuffer();
		for(int i=0;i<txt.length();){
			int len = checkKeyWords(txt,i,matchType);
			if(len>0){
				if(flag==2)
				for(int j=0;j<len;j++){
					res.append(replacestr);
				}
				if(flag==1)
				res.append(replacestr);
				i+=len;
			}else{
				res.append(txt.charAt(i));
				i++;
			}
		}
		return res.toString();
	}
	

	public HashMap getKeysMap() {
		return keysMap;
	}

	public void setKeysMap(HashMap keysMap) {
		this.keysMap = keysMap;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	/**
	 * Description: 测试程序
	 * 1、在处理小数据量该关键词处理程序与传统遍历字符串效率等同
	 * 2、处理大数据量数据时，传统比较方式时间开销取决于关键字库长度与文本长度的乘积
	 * 、而用本关键词法时间开销基本上只取决于文本长度
	 */
//	public static void main(String[] args) {
//		//虚拟10000个关键字
//		String[] keyWords = new String[10000];
//		
//		//虚拟200000字符的文本
//		String txt = "这是一段测试文本，在这个文本中含有的关键字文本关键字11关键字1没有其他关键字";
//		StringBuffer res = new StringBuffer();
//		txt = res.toString();
//		
//		Date begin =new Date();
//		/*
//		for(int i=0;i<keyWords.length;i++){
//			if(txt.indexOf(keyWords[i])!=0){
//				
//			}
//		}
//		printCostTime("传统比较时间开销(毫秒)：",begin);
//		*/
//		
//		KeywordFliter filter = new KeywordFliter();
//		//增加敏感词组
//		filter.addKeywords(keyWords);	
//		printCostTime("增减关键词组DFA时间(毫秒)：",begin);
//		//设置匹配方式
//		filter.setMatchType(2);
//		//判断文本是否含有关键字
//		System.out.println(filter.isContentKeyWords(txt));
//		printCostTime("判断时候含有关键字时间(毫秒)：",begin);
//		//返回关键字替换处理后的文本
//		filter.getReplaceStrTxtKeyWords(txt, "*", 2);
//		//System.out.println(filter.getReplaceStrTxtKeyWords(txt, "*", 2));
//		printCostTime("关键字替换处理后时间(毫秒)：",begin);
//		//返回文本中关键词次数统计
//		System.out.println(filter.getTxtKeyWords(txt));
//		printCostTime("关键词次数统计时间(毫秒)：",begin);
//	}
	
	public static void printCostTime(String name,Date begin){
		Date now =new Date();
		System.out.println(name+(now.getTime()-begin.getTime()));
	}
}