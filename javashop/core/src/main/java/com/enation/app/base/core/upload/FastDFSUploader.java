package com.enation.app.base.core.upload;


import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.utils.IClusterFileManager;
import com.enation.framework.context.spring.SpringContextHolder;
/**
 * 上传文件到远程服务器Fdfs
 * @author zh
 *
 */
@Component
public class FastDFSUploader implements IUploader{

	/**
	 * 上传图片到远程服务器
	 */
	@Override
	public String upload(InputStream stream, String subFolder,String fileName) {
		IClusterFileManager fileManager =(IClusterFileManager)SpringContextHolder.getBean("clusterFileManager");
		return fileManager.upload(stream, subFolder,fileName);
	}

	/**
	 * 删除远程服务器图片
	 */
	@Override
	public void deleteFile(String filePath) {
		IClusterFileManager fileManager =(IClusterFileManager)SpringContextHolder.getBean("clusterFileManager");
		if(StringUtils.isEmpty(filePath))
			return;
		if(fileManager != null){
			String[] temp = filePath.split("\\/");
			fileManager.delete(temp[temp.length-1]);
		}

	}

	@Override
	public String[] upload(InputStream stream, String subFolder, String fileName, int width, int height) {
		String [] path = new String[1];
		path[0]=this.upload(stream, subFolder, fileName);
		return path;
	}

}
