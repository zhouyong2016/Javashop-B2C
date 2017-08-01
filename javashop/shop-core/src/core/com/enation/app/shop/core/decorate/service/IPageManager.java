package com.enation.app.shop.core.decorate.service;

import java.util.List;
/**
 * 
 * 页面管理接口
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@SuppressWarnings(value={"rawtypes"})
public interface IPageManager {

	/**
	 * 获取所有页面信息
	 * @param pageid 页面id
	 * @return 页面列表
	 */
	List listPage(int pageid);


}
