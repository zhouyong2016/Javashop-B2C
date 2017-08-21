package com.enation.app.shop.core.order.plugin.order;

import java.util.Map;


/**
 * 订单统计选项卡显示事件
 * @author xulipeng
 * 2015年05月13日19:25:51
 */

public interface IOrderStatisTabShowEvent {
	
	/**
	 * 返回选项卡的名称
	 * @return
	 */
	public String getTabName();
	
	
	/**
	 * 返回排序
	 * @return
	 */
	public int getOrder();
}
