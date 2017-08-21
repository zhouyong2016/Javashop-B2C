package com.enation.app.shop.core.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.service.IProductStoreManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;

/**
 * 产品库存管理
 * @author kingapex
 *2014-1-1下午4:29:41
 *@author Kanon 2016-2-26;6.0版本升级
 */
@Service("productStoreManager")
public class ProductStoreManager implements IProductStoreManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductStoreManager#decreaseEnable(int, int, int, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void decreaseEnable(int goodsid, int productid, int depotid,int num) {
		
		
		/**
		 * 多店系统不维护分仓库存
		 */
//	if(!"b2b2c".equals(EopSetting.PRODUCT)){
			this.daoSupport.execute("update es_product_store set enable_store=enable_store-? where depotid=? and productid=?", num,depotid,productid);
//		}
		
		this.daoSupport.execute("update es_product set enable_store=enable_store-? where product_id=?", num,productid);
		this.daoSupport.execute("update es_goods set enable_store=enable_store-? where goods_id=?", num,goodsid);

		int enable_store = this.daoSupport.queryForInt("select enable_store from es_goods where goods_id=?", goodsid);
		if(enable_store==0){
			this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 0,goodsid);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductStoreManager#increaseEnable(int, int, int, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseEnable(int goodsid, int productid, int depotid,int num) {
		
		/**
		 * 多店系统不维护分仓库存,  但是自营又需要维护分仓库存  
		 */
//		if(!"b2b2c".equals(EopSetting.PRODUCT)){
		
			if(this.checkExists(goodsid, depotid)){
				this.daoSupport.execute("update es_product_store set enable_store =enable_store+? where goodsid=? and depotid=? and productid=?", num,goodsid,depotid,productid);
					}else{
				this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,enable_store)values(?,?,?,?)",goodsid,productid, depotid,num);
	
			}
//		}
		
		this.daoSupport.execute("update es_product set enable_store=enable_store+? where product_id=?", num,productid);
		this.daoSupport.execute("update es_goods set enable_store=enable_store+? where goods_id=?", num,goodsid);
		
		this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 1,goodsid);//自动上架
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductStoreManager#decreaseStroe(int, int, int, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void decreaseStroe(int goodsid, int productid, int depotid,int num) {
		
		/**
		 * 多店铺不负责分仓库存
		 */
		//if(!"b2b2c".equals(EopSetting.PRODUCT)){
			this.daoSupport.execute("update es_product_store set store=store-? where depotid=? and productid=?", num,depotid,productid);
		//}
		
		this.daoSupport.execute("update es_product set store=store-? where product_id=?", num,productid);
		this.daoSupport.execute("update es_goods set store=store-? where goods_id=?", num,goodsid);
		
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IProductStoreManager#increaseStroe(int, int, int, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseStroe(int goodsid, int productid, int depotid,int num) {
		
		/**
		 * 多店系统不维护分仓库存
		 */
		//if(!"b2b2c".equals(EopSetting.PRODUCT)){
			if(this.checkExists(goodsid, depotid)){
				this.daoSupport.execute("update es_product_store set enable_store=enable_store+?,store =store+? where goodsid=? and depotid=?", num,num,goodsid,depotid);
			}else{
				this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)",goodsid,productid, depotid,num,num);
	
			}
		//}
		
		this.daoSupport.execute("update es_product set enable_store=enable_store+?, store=store+?  where product_id=?", num, num,productid);
		this.daoSupport.execute("update es_goods set enable_store=enable_store+?,store=store+?  where goods_id=?", num, num,goodsid);
		this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 1,goodsid);//自动上架
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IProductStoreManager#getStroe(int, int)
	 */
	@Override
	public int getEnableStroe(int goodsid, int depotid) {
		String sql ="select enable_store from es_product_store where goodsid=? and depotid=?";
		List<Integer> storeList  = this.daoSupport.queryForList(sql, new IntegerMapper(),goodsid,depotid);
		int store=0;
		if(!storeList.isEmpty()){
			store=storeList.get(0);
		}
		return store;
	}
	/**
	 * 查询某个仓库的商品库存是否存在
	 * @param goodsid
	 * @param depotid
	 * @return
	 */
	private boolean checkExists(int goodsid,int depotid){
		int count = this.daoSupport.queryForInt("select count(0) from es_product_store where goodsid=? and depotid=?", goodsid,depotid) ;
		return count==0?false:true;
	}
 

}
