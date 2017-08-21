package com.enation.framework.image;
/**
 * 缩略图处理器
 * @author kanon 2015-12-22 version 1.1 添加注释
 *
 */
public interface IThumbnailCreator {
	
	/**
	 * 生成缩略图
	 * @param w 宽度
	 * @param h 高度
	 */
	public void resize(int w, int h) ;
	
}
