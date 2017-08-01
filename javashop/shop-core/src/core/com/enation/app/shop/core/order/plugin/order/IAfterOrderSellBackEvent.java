package com.enation.app.shop.core.order.plugin.order;
 
import java.util.List;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.SellBackGoodsList;

/**
 * 订单退货申请后发生的事件
 * @author	Chopper
 * @version	v1.0, 2016-1-4 下午4:37:47
 * @since
 */
public interface IAfterOrderSellBackEvent {

	/**
	 * 退货申请后事件
	 * @param sellBackGoodsList 退货商品集合
	 * @param order	订单
	 */
	public void returnOrder(List<SellBackGoodsList> sellBackGoodsList,Order order); 
	
}
