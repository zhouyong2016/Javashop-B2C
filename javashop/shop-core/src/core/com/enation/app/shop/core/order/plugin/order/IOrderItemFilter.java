package com.enation.app.shop.core.order.plugin.order;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.order.model.OrderItem;
/**
 * 订单项过滤器
 * @author kingapex
 *
 */
public interface IOrderItemFilter {
	/***
	 * 过滤订单项
	 * @param orderid 订单id
	 * @param itemList 订单项集合
	 */
	public void filter(Integer orderid,List<OrderItem> itemList);
}
