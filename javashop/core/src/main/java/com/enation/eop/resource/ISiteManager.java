package com.enation.eop.resource;
 
/**
 * 站点管理
 * @author kingapex
 *2015-3-5
 */
public interface ISiteManager {
	
	/**
	 * 将当前站点设置保存到数据库
	 * 从eopsite常量中返写回数据库
	 */
	public void saveToDB();
	
	
}
