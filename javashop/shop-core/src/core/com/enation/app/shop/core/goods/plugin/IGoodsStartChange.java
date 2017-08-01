 package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

/**
 * 商品变化事件
 * 当商品发生变化对商品进行处理，例：商品生成静态页面当商品优惠时需要去更新商品的静态页面。
 * @author kanon
 * @version v1.0,2015-9-18
 * @since   v1.0
 */
public interface IGoodsStartChange {

	/**
	 * 商品开始变化
	 * @param goods 商品
	 */
	public void startChange(Map goods);
}
