package com.enation.app.shop.core.order.service.impl;

import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.support.CartItem;

/**
 * 
 * 订单项保存前置事件
 * @author    jianghongyan
 * @version   1.0.0,2016年7月11日
 * @since     v6.1
 */
public interface IBeforeOrderItemSaveEvent {

	/**
	 * 订单项保存前置操作
	 * @param orderItem 订单项
	 * @param cartItem 购物车项
	 */
	void beforeItemSave(OrderItem orderItem, CartItem cartItem);

}
