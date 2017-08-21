package com.enation.app.shop.core.order.plugin.cart;

import com.enation.app.shop.core.order.model.support.OrderPrice;

/**
 * 计算价格事件
 * @author kingapex
 *
 */
public interface ICountPriceEvent {
	
	public OrderPrice countPrice(OrderPrice orderprice);
	
	
}
