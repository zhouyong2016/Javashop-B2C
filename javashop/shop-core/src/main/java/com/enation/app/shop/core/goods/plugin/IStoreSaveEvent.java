package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

/**
 * 商品库存维护保存事件
 * @author FengXingLong
 *
 */
public interface IStoreSaveEvent {
	
	/**
	 * 库存维护保存时
	 * @param goods
	 */
	public void onStoreSave(Map goods);
}
