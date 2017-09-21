package com.enation.app.base.core.upload;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
/**
 * 上传文件到本地
 * @author zh
 *
 */
@Component
public class LocalUploader implements IUploader{


	/**
	 * 上传图片到本地
	 */
	@Override
	public String upload(InputStream stream, String subFolder,String fileName) {
		/**
		 * 参数校验
		 */
		if(stream==null){
			throw new IllegalArgumentException("file or filename object is null");
		}
		if(subFolder == null){
			throw new IllegalArgumentException("subFolder is null");
		}
		if(!FileUtil.isAllowUpImg(fileName)){
			throw new IllegalArgumentException("不被允许的上传文件类型");
		}
		/**
		 * 组合文件名
		 */
		String ext = FileUtil.getFileExt(fileName);
		fileName = DateUtil.toString(new Date(), "mmss") + StringUtil.getRandStr(4) + "." + ext;
		String static_server_path= SystemSetting.getStatic_server_path();
		String filePath =  static_server_path + "/attachment/";
		
		if(subFolder!=null){
			filePath+=subFolder +"/";
		}
		
		String timePath=this.getTimePath();
		String path  = EopSetting.FILE_STORE_PREFIX+ "/attachment/" +(subFolder==null?"":subFolder)+"/"+timePath+"/"+fileName;
		filePath +=timePath;
		filePath += fileName;
		/**
		 * 写入文件
		 */
		FileUtil.write(stream, filePath);
		return path;
	}
	/**
	 * 删除本地图片
	 */
	@Override
	public void deleteFile(String filePath) {
		FileUtil.delete(filePath);	
		FileUtil.delete(StaticResourcesUtil.getThumbPath(filePath,"_thumbnail"));
	}

	/**
	 * 上传缩略图
	 * @param stream
	 * @param fileFileName
	 * @param subFolder
	 * @param width
	 * @param height
	 * @return
	 */
	public String[] upload (InputStream stream,String fileFileName,String subFolder,int width,int height ){
		if(stream==null || fileFileName==null){
			throw new IllegalArgumentException("file or filename object is null");
		}
		if(subFolder == null){
			throw new IllegalArgumentException("subFolder is null");
		}
		String fileName = null;
		String filePath = "";
		String [] path = new String[2];
		if(!FileUtil.isAllowUpImg(fileFileName)){
			throw new IllegalArgumentException("不被允许的上传文件类型");
		}
		String ext = FileUtil.getFileExt(fileFileName);
		fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;
		String static_server_path = SystemSetting.getStatic_server_path();

		filePath = static_server_path+ "/attachment/";
		if(subFolder!=null){
			filePath+=subFolder +"/";
		}

		path[0]  = static_server_path+ "/attachment/" +(subFolder==null?"":subFolder)+ "/"+fileName;
		filePath += fileName;
		FileUtil.createFile(stream, filePath);
		String thumbName= StaticResourcesUtil.getThumbPath(filePath,"_thumbnail");

		IThumbnailCreator thumbnailCreator = ThumbnailCreatorFactory.getCreator( filePath, thumbName);
		thumbnailCreator.resize(width, height);	
		path[1] = StaticResourcesUtil.getThumbPath(path[0], "_thumbnail");


		return path;
	}
	

	
	private String getTimePath(){
		Calendar now = Calendar.getInstance(); 
		int year=now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH) + 1;
		int date=now.get(Calendar.DAY_OF_MONTH);
		int minute=now.get(Calendar.HOUR_OF_DAY);
		String filePath="";
		if(year!=0){
			filePath+=year+"/";;
		}
		if(month!=0){
			filePath+=month+"/";;
		}
		if(date!=0){
			filePath+=date+"/";;
		}
		if(minute!=0){
			filePath+=minute+"/";;
		}
		return  filePath;
	}


}
