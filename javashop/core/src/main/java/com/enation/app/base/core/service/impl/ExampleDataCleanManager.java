/**
 * 
 */
package com.enation.app.base.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.service.IExampleDataCleanManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * 示例数据清除管理
 * @author kingapex
 *2015-6-3
 */
@Service("exampleDataCleanManager")
public class ExampleDataCleanManager implements IExampleDataCleanManager {
	
	@Autowired
	private IDaoSupport  daoSupport;
 
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IExampleDataCleanManager#clean(java.lang.String[])
	 */
	@Override
	public void clean(String[] moudels) {
		
		
		if( this.hasMoudel("goods", moudels)){
			this.cleanGoods();
			this.cleanOrder();
		}
		
		if( this.hasMoudel("order", moudels)){
			this.cleanOrder();
		}
		
		if( this.hasMoudel("member", moudels)){
			this.cleanMember();
			this.cleanOrder();
		}
		
		
		if( this.hasMoudel("goodscat", moudels)){
			this.cleanGoodsCat();
		}
		
		
		if( this.hasMoudel("goodstype", moudels)){
			this.cleanGoodsType();
			this.cleanGoodsCat();
			this.cleanGoods();
		}
		
	
		if( this.hasMoudel("brand", moudels)){
			this.cleanBrand();
		}
		
		if( this.hasMoudel("store", moudels)){
			this.cleanStore();
			this.cleanOrder();
		}
		if(this.hasMoudel("decorate", moudels)){
			this.cleanDecorate();
		}
		
	}
 
	private void cleanDecorate() {
		String sql="TRUNCATE table es_floor";
		this.daoSupport.execute(sql);
		
		sql="TRUNCATE table es_showcase";
		this.daoSupport.execute(sql);
		
		sql="TRUNCATE table es_subject";
		this.daoSupport.execute(sql);
		
		
	}

	/**
	 * 检测某个模块是否存在
	 * @param m
	 * @param moudels
	 * @return
	 */
	private boolean hasMoudel(String m,String[] moudels){
		for (String s : moudels) {
			if(s.equals(m)){
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/***
	 * 清空商品
	 */
	private void cleanGoods(){
		
		String sql ="TRUNCATE table es_goods";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_goods_gallery";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_product";
		this.daoSupport.execute(sql);
		

		sql ="TRUNCATE table es_product_store";
		this.daoSupport.execute(sql);
		
		
		sql ="TRUNCATE table es_store_log";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_tag_rel";
		this.daoSupport.execute(sql);
		

		sql ="TRUNCATE table es_cart";
		this.daoSupport.execute(sql);
	
	}
	
	
	
	
	/**
	 * 清空订单
	 */
	private void cleanOrder(){
	
		String sql ="TRUNCATE table es_order";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_items";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_log";
		this.daoSupport.execute(sql);

		sql ="TRUNCATE table es_cart";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_meta";
		this.daoSupport.execute(sql);
	}
	
	
	/**
	 * 清空商品分类
	 */
	private void cleanGoodsCat(){
		String sql ="TRUNCATE table es_goods_cat";
		this.daoSupport.execute(sql);
		ICache cache = CacheFactory.getCache("goods_cat");
		cache.remove("goods_cat_0");
	}

	
	/**
	 * 清空商品类型
	 */
	private void cleanGoodsType(){
		String sql ="TRUNCATE table es_goods_type";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_type_brand";
		this.daoSupport.execute(sql);
	}

	
	/**
	 * 清除品牌
	 */
	private void cleanBrand(){
		String sql ="TRUNCATE table es_brand";
		this.daoSupport.execute(sql);
		
		//清除品牌的时候，要将商品中所有品牌id置为null add by DMRain 2016-5-13
		sql = "update es_goods set brand_id = null";
		this.daoSupport.execute(sql);
		
	}
	
	
	
	
	/**
	 * 清除店铺
	 */
	private void cleanStore(){
		if("b2b2c".equals(EopSetting.PRODUCT)){
			//清楚店铺需要将member表关联的店铺删除
			String updateSql = "update es_member set disabled=0,store_id=0,is_store=0 where store_id!=?";
			this.daoSupport.execute(updateSql, 1);
			String sql = "delete from es_store where store_id!=?";
			this.daoSupport.execute(sql, 1);
			sql = "delete from es_store_silde where store_id!=?";
			this.daoSupport.execute(sql, 1);
		}else{
			String sql ="TRUNCATE table es_store";
			this.daoSupport.execute(sql);
			
			sql ="TRUNCATE table es_store_silde";
			this.daoSupport.execute(sql);
		}
		

		
	}
	
	/**
	 * 清空会员
	 */
	private void cleanMember(){
//		String sql ="TRUNCATE table es_member";
		String sql="update es_member set disabled=1";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_member_address";
		this.daoSupport.execute(sql);
		
	}
	
	

}
