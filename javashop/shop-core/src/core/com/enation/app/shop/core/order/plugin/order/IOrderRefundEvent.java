package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.RefundLog;

/**
 * 订单退款事件
 * @author jianghongyan
 *
 */
public interface IOrderRefundEvent {
	
	public void onRefund(Order order, Refund refund);
}
