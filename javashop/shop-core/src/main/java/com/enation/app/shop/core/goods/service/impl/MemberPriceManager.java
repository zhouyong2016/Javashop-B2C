package com.enation.app.shop.core.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.GoodsLvPrice;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.framework.database.IDaoSupport;


/**
 * 会员价格管理
 * @author kingapex
 *
 */
@Service("memberPriceManager")
public class MemberPriceManager   implements IMemberPriceManager {
	@Autowired
	private IDaoSupport  daoSupport;
	
	/**
	 * 读取某个商品的所有规格的会员价格。
	 * @param goodsid
	 * @return
	 */
	public List<GoodsLvPrice> listPriceByGid(int goodsid) {
		String sql  ="select * from es_goods_lv_price where goodsid =?";
		return this.daoSupport.queryForList(sql, GoodsLvPrice.class, goodsid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IMemberPriceManager#listPriceByPid(int)
	 */
	public List<GoodsLvPrice> listPriceByPid(int productid){
		String sql  ="select * from es_goods_lv_price where productid =?";
		return this.daoSupport.queryForList(sql, GoodsLvPrice.class, productid);
		
	}
	
	/**
	 * 读取某会员级别的商品价格
	 * @param lvid
	 * @return
	 */
	public List<GoodsLvPrice> listPriceByLvid(int lvid) {
		String sql  ="select * from es_goods_lv_price where lvid =?";
		return this.daoSupport.queryForList(sql, GoodsLvPrice.class, lvid);
	}

	
	/**
	 * 添加会价格
	 */
	public void save(List<GoodsLvPrice> goodsPriceList) {
		
		if(goodsPriceList!=null && goodsPriceList.size()>0){
			this.daoSupport.execute("delete from  es_goods_lv_price  where goodsid=?", goodsPriceList.get(0).getGoodsid());
		 
		 for(GoodsLvPrice goodsPrice:goodsPriceList){
			 this.daoSupport.insert("es_goods_lv_price", goodsPrice);
		 }
		}
		
	}
	
	
	

}
