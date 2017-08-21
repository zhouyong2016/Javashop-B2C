package com.enation.app.shop.core.goods.plugin.search;

import java.util.List;
import java.util.Map;

/**
 * 商品列表数据过滤器
 * @author kingapex
 *2013-10-3下午5:39:16
 */
public interface IGoodsDataFilter {
	
	public void filter(List<Map> goods);
	
	
}
