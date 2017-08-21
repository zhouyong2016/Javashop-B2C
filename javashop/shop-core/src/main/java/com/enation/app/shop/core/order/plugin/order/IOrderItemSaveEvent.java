package com.enation.app.shop.core.order.plugin.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;

/**
 * 订单项保存事件
 * @author kingapex
 *2014-1-1下午6:00:01
 */
public interface IOrderItemSaveEvent {
	
	/**
	 * item的id已经填充
	 * @param item
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void onItemSave(Order order,OrderItem item);
}
