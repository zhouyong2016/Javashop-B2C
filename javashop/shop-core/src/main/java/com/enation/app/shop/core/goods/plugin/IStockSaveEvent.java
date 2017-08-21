package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

/**
 * 进货事件
 * @author FengXingLong
 * 2015-07-08
 */
public interface IStockSaveEvent {
	
	/**
	 * 当库存保存时
	 * @param goods
	 */
	public void onStockSave(Map goods);
}
