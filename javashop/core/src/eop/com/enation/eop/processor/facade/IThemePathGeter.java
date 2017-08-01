package com.enation.eop.processor.facade;

import org.springframework.stereotype.Component;

/***
 * 模板路径处理器
 * @author Kanon
 */
@Component
public interface IThemePathGeter {
	
	/**
	 * 获取模板位置
	 * @param url url连接
	 * @return 模板位置
	 */
	public String  getThemespath(String url);

	/***
	 * 获取文件夹位置
	 * @param url url连接
	 * @return 模板文件夹位置
	 */
	public String getTplFileName(String url);
}
