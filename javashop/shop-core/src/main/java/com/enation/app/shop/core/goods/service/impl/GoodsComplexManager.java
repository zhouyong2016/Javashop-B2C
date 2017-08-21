package com.enation.app.shop.core.goods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.model.GoodsComplex;
import com.enation.app.shop.core.goods.service.IGoodsComplexManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.IDaoSupport;

public class GoodsComplexManager   implements
		IGoodsComplexManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	public List listAllComplex(int goods_id) {
		//String sql = "select r.*, g.goods_id, g.sn, g.name, g.image_default image, g.price, g.mktprice from " + this.getTableName("goods_complex") + " r, " + this.getTableName("goods") + " g  where ((goods_2 = goods_id AND goods_1="+goods_id+") or (goods_1 = goods_id and goods_2 ="+goods_id+" and manual='both')) and rate > 99";
		
		//查询单项关联的商品(goods1=goods_id) 或者是双向关联的商品(goods2=goods_id and manual='both')
		String sql ="select   g.goods_id, g.sn, g.name, g.image_default image, g.price, g.mktprice from es_goods g where g.goods_id in (select goods_2 from es_goods_complex where goods_1="+goods_id+" )"+  
		" or goods_id in( select goods_1 from es_goods_complex where goods_2="+goods_id+" and manual='both'  )";
		
		List<Map> list = this.daoSupport.queryForList(sql);
		for(Map map :list){
			String image  =(String) map.get("image");
			image = StaticResourcesUtil.convertToUrl(image);
			map.put("image", image);
		}
		return list;
	}

	
	public void addCoodsComplex(GoodsComplex complex) {
		this.daoSupport.insert("es_goods_complex", complex);
	}

	
	@Transactional(propagation = Propagation.REQUIRED)
	public void globalGoodsComplex(int goods_1, List<GoodsComplex> list) {
		//删除原有
		String deleteSql = "delete from goods_complex where goods_1 = ?";
		this.daoSupport.execute(deleteSql, goods_1);
		
		//依次加入
		for(GoodsComplex complex:list){
			this.addCoodsComplex(complex);
		}
		
	}


	public List listComplex(int goods_id) {
		//查询单项关联的商品(goods1=goods_id)
		String sql ="select * from es_goods g, es_goods_complex  c where goods_2 =goods_id and  goods_1=?";
		List<Map> list = this.daoSupport.queryForList(sql,goods_id);
		for(Map map :list){
			String image  =(String) map.get("image");
			image = StaticResourcesUtil.convertToUrl(image);
			map.put("image", image);
		}
		return list;
		 
	}

}
