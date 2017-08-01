package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

/**
 * 库存退货入库事件
 * @author FengXingLong
 * 2015-07-23
 */
public interface IStockReturnEvent {
	
	/**
	 * 当退货入库时
	 * @param goods
	 */
	public void onStockReturn(Map goods);
	
}
