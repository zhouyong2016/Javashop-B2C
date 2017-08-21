package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

/**
 * 库存变更事件
 * @author FengXingLong
 * 2015-07-22
 */
public interface IStockChangeEvent {
	
	/**
	 * 当库存变更时
	 * @param goods
	 */
	public void onStockChange(Map goods);
	
}
