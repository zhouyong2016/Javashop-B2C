package com.enation.app.shop.component.activity.plugin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.plugin.order.IOrderShipEvent;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单发货时减少赠品实际库存插件
 * @author DMRain
 * @date 2016-6-13
 * @version 1.0
 */
@Component
public class OrderShipReduceGiftStorePlugin extends AutoRegisterPlugin implements IOrderShipEvent{

	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	@Override
	public void itemShip(Order order, DeliveryItem deliveryItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ship(Delivery delivery, List<DeliveryItem> itemList) {
		Integer order_id = delivery.getOrder_id();
		Order order = this.orderManager.get(order_id);
		
		//如果订单中的赠品id不等于0并且不为空
		if (order.getGift_id() != null && order.getGift_id() != 0) {
			this.activityGiftManager.updateGiftStore(order.getGift_id(), 2, 1);
		}
		
	}

	@Override
	public boolean canBeExecute(int catid) {
		// TODO Auto-generated method stub
		return true;
	}

}
