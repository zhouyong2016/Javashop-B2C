package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;

/**
 * 订单支付事件
 * @author kingapex
 * @date 2011-10-28 下午4:13:00 
 * @version V1.0
 */
public interface IOrderPayEvent {
	
	public void pay(Order order,boolean isOnline);
	
	
}
