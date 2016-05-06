/**
 * 2008-6-11
 */
package pri.z.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * DES安全编码组件校验
 * 
 * @author 梁栋
 * @version 1.0
 */
public class DESCoderTest {

	public static void main(String[] args) {
		try {
			test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String enCode(String str) {
		String s = "";
		byte[] inputData = str.getBytes();
		
		try {
			// 初始化密钥
			byte[] key = DESCoder.initKey();
			// 加密
			inputData = DESCoder.encrypt(inputData, key);
			s = Base64.encodeBase64String(inputData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return s;
	}

	/**
	 * 测试
	 * 
	 * @throws Exception
	 */
	public final static void test() throws Exception {
		String inputStr = "DES";
		byte[] inputData = inputStr.getBytes();
		System.err.println("原文:\t" + inputStr);

		// 初始化密钥
		byte[] key = DESCoder.initKey();
		System.err.println("密钥:\t" + Base64.encodeBase64String(key));

		// 加密
		inputData = DESCoder.encrypt(inputData, key);
		System.err.println("加密后:\t" + Base64.encodeBase64String(inputData));

		// 解密
		byte[] outputData = DESCoder.decrypt(inputData, key);

		String outputStr = new String(outputData);
		System.err.println("解密后:\t" + outputStr);

	}
}
