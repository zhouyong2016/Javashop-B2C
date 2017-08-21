package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;

/**
 * 订单购物车选项卡显示事件
 * @author kingapex
 *2012-4-7下午8:56:02
 */
public interface IOrderTabShowEvent {

	/**
	 * 返回选项卡的名称
	 * @return
	 */
	public String getTabName(Order order);
	
	
	/**
	 * 返回排序
	 * @return
	 */
	public int getOrder();
	
	
	
	public boolean canBeExecute(Order order);
	
}
