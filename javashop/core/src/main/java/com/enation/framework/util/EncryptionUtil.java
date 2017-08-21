package com.enation.framework.util;

import java.util.Date;

import org.apache.commons.codec.binary.Base64;

/**
 * 加密工具
 * @author kingapex
 *2010-3-22下午03:41:11
 */
public class EncryptionUtil {
	/**
	 * all set GLOBAL_AUTH_KEY
	 */
	public static String GLOBAL_AUTH_KEY = "e317b362fafa0c96c20b8543d054b850";

	/**
	 * discuz's cookie encode decode in java
	 * 
	 * @param str
	 *            String
	 * @param operation
	 *            String
	 * @param key
	 *            String
	 * @return String
	 */
	public static final String authCode(String str, String operation) {

		String key = GLOBAL_AUTH_KEY;
		String coded = "";
		int keylength = key.length();
		try {
			str = operation.equals( "DECODE") ? new String(Base64.decodeBase64(str
					.getBytes())) : str;
			int tmp;
			byte[] codeList = new byte[str.getBytes().length];
			int index = 0;
			for (int i = 0; i < str.length(); i += keylength) {
				tmp = (i + keylength) < str.length() ? (i + keylength) : str
						.length();
				byte[] codedbyte = phpXor(str.substring(i, tmp), key);
				coded += codedbyte;
				System.arraycopy(codedbyte, 0, codeList, index,
						codedbyte.length);
				index = codedbyte.length;
			}
			coded = operation.equals("ENCODE") ? new String(Base64.encodeBase64(codeList))
					: new String(codeList);
			return coded;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * php's XOR in Java
	 * 
	 * @param a
	 *            String
	 * @param b
	 *            String
	 * @return String
	 */
	public static final byte[] phpXor(String a, String b) {
		byte[] as;
		byte[] bs;
		try {
			as = a.getBytes();
			bs = b.getBytes();
			int len = 0;
			if (as.length > bs.length) {
				len = bs.length;
			} else {
				len = as.length;
			}
			byte[] cs = new byte[len];
			for (int i = 0; i < len; ++i) {
				cs[i] = (byte) ((int) as[i] ^ (int) bs[i]);
			}
			return cs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	public static void main(String[] args){
//		for(int i = 0; i < 100; i++){
//			String number = "" + Math.random()+ Math.random()+ Math.random()+ Math.random();
//			//System.out.println("加密前：" + number);
//			String number2 = EncryptionUtil.authcode(number, "ENCODE","",0);
//			//System.out.println("加密后：" + number2);
//			//System.out.println("解密后：" + EncryptionUtil.authcode(number2, "DECODE","",0));
//			//System.out.println();
//		}
		long start  = System.currentTimeMillis();
		//System.out.println(authCode("http://www.javamall.com.cn/version4/api/base/record-installer.do", "ENCODE"));
		////System.out.println(authCode("VAEGGVIdBhxX", "DECODE"));
		//System.out.println( System.currentTimeMillis()-start);
		////System.out.println(authCode("CUFS", "DECODE"));
	}
}
