package com.enation.app.shop.component.spec.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品规格库存管理接口
 * @author kingapex
 *2012-3-22下午5:10:43
 */
public interface IGoodsSpecStoreManager {
	
	/**
	 * 
	 * @param goodsid 要获取库存的商品id
	 * 
	 * 
	 * Map实体列层级关系为：
	 * depotid depotname
	 *    storeid productid  store
	 *       specid spec_value
	 *       
	 * 如  ：
	 * 1  -- 海淀仓库
	 *       1 --  1 --  100(库存)
	 *             红色    S号
	 *       1 --  2  -- 100(库存)
	 *             红色    L号
	 * 2  -- 昌平库存
	 *       1 --  1 --  200(库存)
	 *             红色    S号
	 *       1 --  2  -- 200(库存)
	 *             红色    L号
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public List<Map> listGoodsStore(int goodsid);
	
	
	
	
	
	/**
	 * 为某个商品进货
	 * 直接对product_store表进行更新。
	 * 如果id不为0进行更新
	 * 如果id为0，则说明有新货品，插入新货品的库存。
	 * @param goodsid
	 * @param idAr
	 * @param productidAr
	 * @param depotidAr
	 * @param storeAr
	 * 
	 * @return 本次进货总数 
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int stock(int goodsid,String[] idAr,String[] productidAr,String[] depotidAr ,String[] storeAr);
	
	
	/**
	 * 维护某个商品的库存情况
	 * 直接对product_store表进行更新。
	 * 如果id不为0进行更新
	 * 如果id为0，则说明有新货品，插入新货品的库存。
	 * @param goodsid
	 * @param idAr
	 * @param productidAr
	 * @param depotidAr
	 * @param storeAr
	 * 
	 * @return 库存总数 
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int saveStore(int goodsid,String[] idAr,String[] productidAr,String[] depotidAr ,String[] storeAr);
	
	
	
	
	/**
	 * 为某个商品出货
	 * 直接对product_store表进行更新。
	 * 如果id不为0进行更新
	 * 如果id为0，则说明有新货品，插入新货品的库存。
	 * @param goodsid
	 * @param idAr
	 * @param productidAr
	 * @param depotidAr
	 * @param storeAr
	 * 
	 * @return 库存总数 
	 */	
	@Transactional(propagation = Propagation.REQUIRED)
	public int ship(int goodsid,String[] idAr,String[] productidAr,String[] depotidAr ,String[] storeAr);
	
	
	/**
	 * 
	 * @param goodsid 要获取库存的商品id
	 * 
	 * 
	 * Map实体列层级关系为：
	 * depotid depotname
	 *    storeid productid  store
	 *       specid spec_value
	 *       
	 * 如  ：
	 * 1  -- 海淀仓库
	 *       1 --  1 --  100(库存)
	 *             红色    S号
	 *       1 --  2  -- 100(库存)
	 *             红色    L号
	 * 2  -- 昌平库存
	 *       1 --  1 --  200(库存)
	 *             红色    S号
	 *       1 --  2  -- 200(库存)
	 *             红色    L号
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public List<Map> listGoodsWarningStore(int goodsid);
	
}
