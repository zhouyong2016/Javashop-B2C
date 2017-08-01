package com.enation.app.shop.core.order.plugin.cart;

import com.enation.app.shop.core.goods.model.Product;


/**
 * 购物车修改事件
 * @author Sylow
 * @version v1.0,2015-09-28
 * @since v4.0
 */
public interface ICartUpdateEvent {
	
	/**
	 *  当更新前调用这个事件
	 * @author Kanon
	 * @param sessionid session Id
	 * @param cartid  购物车id
	 */
	public void onBeforeUpdate(String sessionId, Integer cartId,Integer num);
	
	/**
	 *  当更新时调用这个事件
	 * @param sessionid session Id
	 * @param cartid  购物车id
	 */
	public void onUpdate(String sessionId, Integer cartId);
	/**
	 * 添加货品到购物车时调用这个时间
	 * @param cart 购物车实体
	 * @param product 货品实体
	 * @param num 添加数量
	 */
	public void onAddProductToCart(Product product,Integer num);
	
}
