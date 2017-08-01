package com.enation.app.shop.core.goods.plugin.search;

import com.enation.app.shop.core.goods.model.Cat;

/**
 * 
 * 商品前台搜索sql过滤器
 * @author    jianghongyan
 * @version   1.0.0,2016年7月14日
 * @since     v6.1
 */
public interface IGoodsFrontSearchSqlFilter {
	/**
	 * 对搜索条件进行前置过滤,比如左联右联
	 * @param sql 要过滤的sql语句
	 */
	public void filterFrontSql(StringBuffer sql);
}
