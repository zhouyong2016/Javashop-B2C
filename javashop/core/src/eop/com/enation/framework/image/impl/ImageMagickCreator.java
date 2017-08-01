package com.enation.framework.image.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import magick.CompositeOperator;
import magick.ImageInfo;
import magick.MagickApiException;
import magick.MagickException;
import magick.MagickImage;

import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.image.ImageRuntimeException;


/**
 * 使有ImageMagick组件生成缩略图
 * @author kingapex
 * 2010-7-10下午11:43:24
 */
public class ImageMagickCreator implements IThumbnailCreator {
	static {
		System.setProperty("jmagick.systemclassloader", "no");
	} 
   
	private String source;

	private String target;

	private ImageInfo info;

	private MagickImage image;

	private double width;

	private double height;

	/***
	 * 
	 * @param _source 图片路径
	 * @param _target 图片名称
	 * @throws IOException
	 */
	public ImageMagickCreator(String _source, String _target) throws IOException{
		source = _source;
		target = _target;
		File f = new File(_source);
		FileInputStream fis = new FileInputStream(f);
		try {

			byte[] b = new byte[(int)f.length()];
			fis.read(b);
			
			info = new ImageInfo();
			image = new MagickImage(info,b);

			// 得到原图的宽和高
			width = image.getDimension().getWidth();
			height = image.getDimension().getHeight();
		} catch (Exception e) {

			e.printStackTrace();
			throw new ImageRuntimeException(source, "构造jmagickutils");
		}finally{
			if(fis!=null)
				fis.close();
			fis=null;
		}

	}
	/**
	 * @param w 宽度
	 * @param h 高度
	 */
	public void resize(int w, int h) {

		int target_w, target_h; // 目标宽高
		int x = 0, y = 0; // 缩略图在背景的座标
		x = y = 0;
		target_w = w;
		target_h = h;

		MagickImage scaled = null;
		try {

			/* 计算目标宽高 */
			if (width / height > w / h) { // 原图长:上下补白
				target_w = w;
				target_h = (int) (target_w * height / width);
				x = 0;
				y = (int) (h - target_h) / 2;

			}
			
			// 原图高:左右补白
			if (width / height < w / h) { 
				target_h = h;
				target_w = (int) (target_h * width / height);
				y = 0;
				x = (int) (w - target_w) / 2;
			}
			
			//改变图片大小
			MagickImage thumb_img = image.scaleImage(target_w, target_h);
			MagickImage blankImage = new MagickImage();

			byte[] pixels = new byte[w * h * 4];
			
			for (int i = 0; i < w * h; i++) {
				pixels[4 * i] = (byte) 255;
				pixels[4 * i + 1] = (byte) 255;
				pixels[4 * i + 2] = (byte) 255;
				pixels[4 * i + 3] = (byte) 255;
			}
			
			//组成图片
			blankImage.constituteImage(w, h, "RGBA", pixels);
			
			//合成图片
			blankImage.compositeImage(CompositeOperator.AtopCompositeOp,thumb_img, x, y);
			
			//文件名称
			blankImage.setFileName(target);
			
			//生成缩略图
			blankImage.writeImage(info);

		} catch (MagickApiException ex) {
			ex.printStackTrace();
			throw new ImageRuntimeException(source, "生成缩略图");
		} catch (MagickException ex) {
			ex.printStackTrace();
			throw new ImageRuntimeException(source, "生成缩略图");
		} finally {
			if (scaled != null) {
				scaled.destroyImages();
			}
			if(image!=null){
				image.destroyImages();
			}
		}
	}

	
//	public static void main(String args[]) throws IOException{
//		ImageMagickCreator creator = new ImageMagickCreator("d:/1.jpg", "d:/2.jpg");
//		creator.resize(200, 200);
//		
//	}
	
	
}
