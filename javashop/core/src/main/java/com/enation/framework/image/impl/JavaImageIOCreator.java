package com.enation.framework.image.impl;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.util.FileUtil;

/**
 * 使用javax image io生成缩略图
 * @author kingapex
 * 2010-7-10下午11:43:05
 */
public class JavaImageIOCreator implements IThumbnailCreator {
	private String srcFile;
	private String destFile;

	private static Map<String, String> extMap;
	static {
		extMap = new HashMap<String, String>(5);
		extMap.put("jpg", "JPEG");
		extMap.put("jpeg", "JPEG");
		extMap.put("gif", "GIF");
		extMap.put("png", "PNG");
		extMap.put("bmp", "BMP");

	}

	public JavaImageIOCreator(String sourcefile, String targetFile) {
		this.srcFile =sourcefile;
		this.destFile = targetFile;
	}
	/**
	 * 生成缩略图
	 * @param w 宽度
	 * @param h 高度
	 */
	public void resize(int w, int h) {

		//获取文件拓展名
		String ext = FileUtil.getFileExt(srcFile).toLowerCase();

		BufferedImage image;
		try {
			//获取图片
			Image img = Toolkit.getDefaultToolkit().getImage(srcFile);
			//image = ImageIO.read(new File(srcFile));

			//创建BufferedImage（内存中创建一张图片）
			image=FileUtil.toBufferedImage(img);

			//生成缩略图
			//ImageIO.write(Lanczos.resizeImage(image, w, h), ext, new File(destFile));

			BufferedImage thumbImg = Scalr.resize(image, Method.QUALITY, Mode.AUTOMATIC, w, h,Scalr.OP_ANTIALIAS);
			ImageIO.write(thumbImg, "PNG", new File(destFile));
		} catch (IOException e) {
			throw new RuntimeException("生成缩略图错误",e);
		}
	}

	public static void main(String args[]){
		JavaImageIOCreator creator = new JavaImageIOCreator("d:/1.jpg", "d:/1_j_180.jpg");
		creator.resize(180, 180);
	}

}
