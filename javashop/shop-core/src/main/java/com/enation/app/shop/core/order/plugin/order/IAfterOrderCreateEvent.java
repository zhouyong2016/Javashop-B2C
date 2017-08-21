package com.enation.app.shop.core.order.plugin.order;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.support.CartItem;




/**
 * 订单创建完成事件
 * @author kingapex
 *
 */
public interface IAfterOrderCreateEvent {
	
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void onAfterOrderCreate(Order order ,List<CartItem>   itemList,String sessionid);
	
	
}
