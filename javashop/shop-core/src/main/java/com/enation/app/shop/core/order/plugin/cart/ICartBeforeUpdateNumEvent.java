package com.enation.app.shop.core.order.plugin.cart;

 
public interface ICartBeforeUpdateNumEvent {

	/**
	 * 修改购物车数量插件
	 * @param cart_id 购物车id
	 * @param num	修改后的数量
	 * @author Fenlongli
	 */
	public void updateBefore(Integer cart_id, Integer num);
}
