package com.enation.app.shop.core.order.plugin.order;

import com.enation.app.shop.core.order.model.Order;


/**
 * 订单详细页面显示事件
 * @author kingapex
 *2012-2-13下午10:15:51
 */
public interface IShowOrderDetailHtmlEvent {
	
	
	/**
	 * 返回要在订单详细页面显示的HTML
	 * @param order
	 * @return
	 */
	public String onShowOrderDetailHtml(Order order);
	
 
	
	
}
