package com.enation.app.shop.core.decorate.service;

import java.util.Map;

/**
 * 系统设置  购买商品金额与积分
 * @author liao
 * @since v621
 * @version 1.0
 * 2017年5月31日
 */
public interface ISettingManager {

	/**
	 * 获取系统设置  购买商品金额与积分比例值
	 * @return map
	 */
	public Map getSettingPoint();
}
