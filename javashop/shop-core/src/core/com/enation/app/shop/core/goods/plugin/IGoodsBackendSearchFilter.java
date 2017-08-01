package com.enation.app.shop.core.goods.plugin;

/**
 * 后台商品搜索过滤器
 * @author kingapex
 *
 */
public interface IGoodsBackendSearchFilter {
	
	
	/**
	 * 在select * 在此位置插入字符
	 * @return
	 */
	public String getSelector();
	
	
	
	
	/**
	 * select * from 在此位置插入字串
	 * @return
	 */
	public String getFrom();
	
	
	
	/**
	 * 过滤搜索的sql语句
	 * @param sql
	 */
	public void filter(StringBuffer sql);
	
	
}
