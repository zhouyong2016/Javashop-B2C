package com.enation.app.shop.core.goods.service;

import com.enation.framework.database.Page;

/**
 * 缺货登记
 * 
 * @author lzf<br/>
 *         2010-3-19 下午02:30:40<br/>
 *         version 1.0<br/>
 */
public interface IGnotifyManager {

	/**
	 * 当前登陆会员分页显示缺货登记
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pageGnotify(int pageNo, int pageSize);

	/**
	 * 删除登记
	 * 
	 * @param gnotify_id
	 */
	public void deleteGnotify(int gnotify_id);
	
	/**
	 * 登记
	 * @param productid
	 */
	public void addGnotify(Integer goodsid,Integer productid);
	
	/**
	 * 查询某个用户要登记的商品，是否已经登记过了
	 * @param goodsid
	 * @param productid
	 * @return
	 */
	public int getGnotifyBymember(Integer goodsid, Integer productid);
	
	/**
	 * 查询所有的商品的登记列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page getGnotifyList(int pageNo, int pageSize);

}
