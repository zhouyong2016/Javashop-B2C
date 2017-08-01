package com.enation.app.base.core.plugin.fdfs;

import java.io.File;

/**
 * 文件上传事件
 * @author Chopper 
 */
public interface IFileUploadEvent{
	
	public String BeAfterUpload(File file);

}
