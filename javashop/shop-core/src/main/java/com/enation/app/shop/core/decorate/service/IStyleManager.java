package com.enation.app.shop.core.decorate.service;

import com.enation.app.shop.core.decorate.model.Style;


/**
 * 
 * 风格管理接口
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
public interface IStyleManager {

	/**
	 * 获取楼层id对应的风格
	 * @param floor_id 楼层id
	 * @return Style 风格对象
	 * @throws RuntimeException
	 */
	Style getStyleByFloorId(Integer floor_id,Integer page_id) throws RuntimeException;
}
