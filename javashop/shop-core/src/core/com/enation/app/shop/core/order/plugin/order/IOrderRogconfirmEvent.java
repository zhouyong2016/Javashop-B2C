package com.enation.app.shop.core.order.plugin.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Order;

/**
 * 退货单事件
 * @author chopper
 * @date 2015-01-15 03:23:23
 */
 
public interface IOrderRogconfirmEvent {
	/**
	 * 确认结算事件
	 * @param order
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void rogConfirm(Order order);
}
