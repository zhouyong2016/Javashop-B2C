package com.enation.app.shop.core.goods.plugin.search;

import java.util.Map;

import com.enation.app.shop.core.goods.model.Cat;


/**
 * 
 * 商品搜索过滤器
 * @author kingapex
 *
 */
public interface IGoodsSearchFilter {
	
 
	/**
	 * 生成 选择器列表
	 * @param catid 当前搜索的分类，如果为null则搜索全部类别
	 * @param url
	 * @param urlFragment
	 * @return
	 */
	public void createSelectorList(Map map,Cat cat);
	
	
	/**
	 * 对搜索条件进行过滤
	 * @param sql 要过滤的sql语句
	 * @param cat 当前搜索的分类，如果为null则搜索全部类别
	 * @pa urlFragment 当前属性的地址栏字串片断,如brand{1}
	 */
	public void filter(StringBuffer sql,Cat cat);
	
	
	
	
}
