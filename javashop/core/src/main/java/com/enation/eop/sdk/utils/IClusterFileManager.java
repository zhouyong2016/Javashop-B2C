package com.enation.eop.sdk.utils;

import java.io.File;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface IClusterFileManager {

	/**
	 * 上传文件到远程服务器
	 * @param file		本地文件
	 * @param relativePath	相对路径，如fs://attachment/a.jpg
	 * @return
	 */
	public String upload(InputStream file, String relativePath,String fileName);
	
	/**
	 * 上传文件到远程服务器
	 * @param file		本地文件
	 * @param fileName	原始文件名
	 * @param relativePath	相对路径，如fs://attachment/a.jpg
	 * @return
	 */
	public String upload(File file, String fileName, String relativePath);
	
	/**
	 * 删作远程服务器的文件
	 * @param fileName	文件名称，如2001010101030.jpg
	 */
	public void delete(String fileName);
	
}
