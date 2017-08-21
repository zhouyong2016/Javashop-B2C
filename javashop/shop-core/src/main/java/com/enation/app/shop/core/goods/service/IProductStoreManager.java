package com.enation.app.shop.core.goods.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 产品库存管理
 * @author kingapex
 *2014-1-1下午4:17:13
 */
public interface IProductStoreManager {
	
	/**
	 * 获取某商品在某仓库中的库存
	 * @param goodsid
	 * @param depotid
	 * @return
	 */
	public int getEnableStroe(int goodsid,int depotid);
	
	
	
	/**
	 * 减少某产品的可用库存，一般用于订单创建时
	 * @param goodsid 商品id
	 * @param productid 产品id
	 * @param depotid  仓库id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void decreaseEnable(int goodsid,int productid,int depotid,int num);
	
	
	
	
	/**
	 * 增加可用库存,一般用于取消订单时
	 * @param goodsid
	 * @param productid
	 * @param depotid
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseEnable(int goodsid,int productid,int depotid,int num);
	
	
	
	/**
	 * 减少库存 ，一般用于发货
	 * @param goodsid
	 * @param productid
	 * @param depotid
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void decreaseStroe(int goodsid,int productid,int depotid,int num);
	
	
	/**
	 * 增加库存，一般用于进货
	 * 同时增加了可用库存
	 * @param goodsid
	 * @param productid
	 * @param depotid
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseStroe(int goodsid,int productid,int depotid,int num);
	
	
	
}
