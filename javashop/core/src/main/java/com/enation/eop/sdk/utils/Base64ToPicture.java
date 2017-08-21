package com.enation.eop.sdk.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import sun.misc.BASE64Decoder;

/**
 * 
 * base64 图片转换
 * @author Chopper
 * @version v1.0
 * @since v6.2
 * 2016年12月23日 上午10:40:36
 */
public class Base64ToPicture {

//	public static void main(String[] args) {
//		String strImg = GetImageStr();
//		System.out.println(strImg);
//		GenerateImage(strImg,"/Users/HaoBeck/Documents/workspace/trunk/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/b2b2c/statics/attachment/2.jpg");
//	}

	// 图片转化成base64字符串
//	public static String GetImageStr() {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//		String imgFile = "/Users/HaoBeck/Documents/workspace/trunk/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/b2b2c/statics/attachment/1.jpg";// 待处理的图片
//		InputStream in = null;
//		byte[] data = null;
//		// 读取图片字节数组
//		try {
//			in = new FileInputStream(imgFile);
//			data = new byte[in.available()];
//			in.read(data);
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// 对字节数组Base64编码
//		BASE64Encoder encoder = new BASE64Encoder();
//		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
//	}

	/**
	 * base64字符串转化成图片
	 * @param imgStr base64 字节码
	 * @param path	存储路径
	 * @return 返回成功/失败
	 */
	public static boolean GenerateImage(String imgStr, String path) { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			
			//文件夹存在判定
			File parent = new File(path).getParentFile(); 
			if(parent!=null&&!parent.exists()){ 
				parent.mkdirs(); 
			} 
			
			// 生成jpeg图片
			OutputStream out = new FileOutputStream(path);
			
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
