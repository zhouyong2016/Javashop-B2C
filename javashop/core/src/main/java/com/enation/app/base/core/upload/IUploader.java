 package com.enation.app.base.core.upload;

import java.io.InputStream;

/**
 * 文件上传接口
 * @author zh
 *
 */
public interface IUploader {
	/**
	 * 
	 * @param stream	流
	 * @param subFolder	子文件夹
	 * @param fileName	图片名称
	 * @return
	 */
	public String upload(InputStream  stream,String subFolder,String fileName);
	/**
	 * 删除图片
	 * @param filePath 文件全路径如：http://static.eop.com/user/1/1/attachment/goods/2001010101030.jpg
	 */
	public void deleteFile(String filePath);
	/**
	 * 上传图片到本地并生成缩略图
	 * @param stream
	 * @param subFolder
	 * @param fileName
	 * @param width
	 * @param height
	 * @return
	 */
	public String[] upload(InputStream  stream,String subFolder,String fileName,int width,int height);
	
	
}
