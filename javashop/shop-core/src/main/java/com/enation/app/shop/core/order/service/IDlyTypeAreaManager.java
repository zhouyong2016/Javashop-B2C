package com.enation.app.shop.core.order.service;

import java.util.Map;


/**
 * 
 * @author dable
 *
 */
public interface IDlyTypeAreaManager{
	
	
	/**
	 * 根据区域查运费
	 * @param type_id
	 * @return
	 */
	public Map listAllByRegion(String regionid);
	
	
	public int queryByrdgion(String regionid);
	
	public Map queryOtherRegions(Integer type_id,String regionid);
	
}