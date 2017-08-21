package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

/**
 * 商品被浏览事件
 * @author lzf
 * 2012-4-17上午11:06:25<br/>
 * v1.0
 */
public interface IGoodsVisitEvent {
	
	/**
	 * 商品被浏览时激发此事件
	 * @param goods
	 */
	public void onVisit(Map goods);

}
