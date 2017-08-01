package com.enation.app.shop.core.order.plugin.cart;

import java.util.List;

import com.enation.app.shop.core.order.model.support.CartItem;


/**
 * 购物车项过滤器
 * @author kingapex
 *
 */
public interface ICartItemFilter {
	
	/**
	 * 过滤购物车数据
	 * @param itemlist
	 * @param sessionid
	 */
	public void filter(List<CartItem>  itemlist,String sessionid);
}
