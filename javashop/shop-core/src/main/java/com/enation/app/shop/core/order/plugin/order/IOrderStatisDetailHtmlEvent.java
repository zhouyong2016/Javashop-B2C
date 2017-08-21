package com.enation.app.shop.core.order.plugin.order;

import java.util.Map;

/**
 * 订单金额—选项卡读取对应html事件
 * @author xulipeng
 *
 */
public interface IOrderStatisDetailHtmlEvent {

	/**
	 * 返回要在订单详细页面显示的HTML
	 * @param order
	 * @return
	 */
	public String onShowOrderDetailHtml(Map map);
}
