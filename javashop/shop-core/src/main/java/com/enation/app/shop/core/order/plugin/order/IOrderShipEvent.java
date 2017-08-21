package com.enation.app.shop.core.order.plugin.order;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.Order;

/**
 * 订单发退货事件
 * @author kingapex
 *
 */
public interface IOrderShipEvent {
	
	/**
	 * 订单某个货物项发货事件
	 * @param deliveryItem
	 * @param allocationItem
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void itemShip(Order order,DeliveryItem deliveryItem);
	
	
	/**
	 * 订单发货事件
	 * @param delivery
	 * @param itemList
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void ship(Delivery delivery ,List<DeliveryItem> itemList);
	

	/**
	 * 定义事件是否被执行 
	 * @param catid 商品分类id
	 * @return
	 */
	public boolean canBeExecute(int catid);
}
