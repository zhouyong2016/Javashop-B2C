package com.enation.app.shop.core.order.service;

import java.util.List;

import com.enation.app.shop.core.order.model.OrderMeta;




/**
 * 订单扩展信息管理 
 * @author kingapex
 *
 */
public interface IOrderMetaManager {
	
	/**
	 * 添加一个订单扩展
	 * @param orderMeta
	 */
	public void add(OrderMeta orderMeta);
	
	
	/**
	 * 读取一个订单的扩展列表
	 * @param orderid
	 * @return
	 */
	public List<OrderMeta> list(int orderid);
	
	
	
	/**
	 * 根据meta_key 获取某个订单的扩展信息
	 * @param orderid
	 * @param meta_key 
	 * @return
	 */
	public OrderMeta get(int orderid,String meta_key);

	/**
	 * 删除一个订单扩展信息
	 * @param metaid
	 */
	public void delete(Integer metaid);
}	
