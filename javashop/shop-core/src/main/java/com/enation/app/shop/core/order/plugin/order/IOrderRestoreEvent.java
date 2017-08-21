package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;

public interface IOrderRestoreEvent {
	public void restore(Order order);
}
