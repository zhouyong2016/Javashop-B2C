package com.enation.app.shop.core.order.plugin.order;

/**
 * 订单删除事件
 * @author kingapex
 *
 */
public interface IOrderDeleteEvent {
	public void delete(Integer[] orderId);
}
