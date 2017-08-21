package com.enation.app.shop.core.goods.service;

import java.util.Map;

import com.enation.framework.database.Page;

/**
 * 商品搜索管理类
 * @author kingapex
 *2015-4-20
 */

public interface IGoodsSearchManager {
	
	/**
	 * 搜索
	 * @param pageNo 分页
	 * @param pageSize 每页显示数量
	 * @return 商品分页
	 */
	public Page search(int pageNo,int pageSize) ;
	
	/**
	 * 获取筛选器

	 * @return Map
	 */
	public Map<String,Object> getSelector();
	
	  
	
}
