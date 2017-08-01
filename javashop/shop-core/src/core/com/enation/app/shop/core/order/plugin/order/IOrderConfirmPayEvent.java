package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;

/**
 * 订单确认付款事件
 * @author kingapex
 *2012-4-22下午11:44:10
 */
public interface IOrderConfirmPayEvent {
	public void confirmPay(Order order);
}
