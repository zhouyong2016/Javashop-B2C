package com.enation.framework.image;

/**
 * 图片运行异常
 * @author kanon 2015-12-22 version 1.1 添加注释
 *
 */
public class ImageRuntimeException extends RuntimeException {
	public ImageRuntimeException(String path,String proesstype){
		super("对图片"+path +"进行"  + proesstype +"出错" );
	}
}
