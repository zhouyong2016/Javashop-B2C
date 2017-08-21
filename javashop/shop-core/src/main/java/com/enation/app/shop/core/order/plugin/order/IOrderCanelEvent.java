package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;

/**
 * 订单取消事件
 * @author kingapex
 *2012-4-7下午8:58:18
 */
public interface IOrderCanelEvent {
	
	/**
	 * 订单取消事件
	 * @param order
	 */
	public void canel(Order order);
}
