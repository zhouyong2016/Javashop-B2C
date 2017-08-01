package com.enation.app.shop.core.order.plugin.order;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;


/**
 * 修改发货仓库事件
 * @author kingapex
 *2014-5-27下午4:14:32
 */
public interface IOrderChangeDepotEvent {
	
	/**
	 * 订单修改发货仓库事件
	 * @param order 订单详细
	 * @param newdepotid 新仓库id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void chaneDepot(Order order,int newdepotid,List<OrderItem> itemList);
}
