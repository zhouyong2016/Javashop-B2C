package com.enation.app.base.core.upload;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ClusterSetting;
import com.enation.app.base.core.model.FastDfsSetting;
import com.enation.framework.context.spring.SpringContextHolder;
@Component
public class UploadFacatory {

	private UploadFacatory(){}
	
	
	/**
	 * 上传图片
	 * @return
	 */
	public static IUploader getUploaer(){
		IUploader uploade =(IUploader)SpringContextHolder.getBean("localUploader");
		
		//如果开启fastdfs
		if(ClusterSetting.getFdfs_open()==1){
			return new FastDFSUploader();
		}else if(FastDfsSetting.getFdfs_open()==1){
			return new FDFSUploader();
		}
		return uploade;
	}


}
