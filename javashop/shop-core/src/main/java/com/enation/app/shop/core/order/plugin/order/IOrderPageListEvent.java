package com.enation.app.shop.core.order.plugin.order;

import com.enation.framework.database.Page;

/**
 * 
 * 过滤订单分页查询数据事件
 * @author    jianghongyan
 * @version   1.0.0,2016年7月11日
 * @since     v6.1
 */
public interface IOrderPageListEvent {

	/**
	 * 过滤订单分页查询数据
	 * @param webPage 分页数据
	 */
	void onfilterPage(Page webPage);

}
