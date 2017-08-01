package com.enation.app.shop.core.goods.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.goods.model.support.GoodsView;
import com.enation.eop.sdk.utils.StaticResourcesUtil;

/**
 * 商品列表mapper
 * @author kingapex
 * 2010-7-16下午01:48:59
 */
public class GoodsListMapper implements RowMapper {

	/**
	 * 返回{@link GooodsView}
	 * 在本方法中对属性进行了读取和处理，并放入到了 propMap属性
	 */
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		
		GoodsView  goods = new GoodsView();
		goods.setName(rs.getString("name"));
		goods.setGoods_id(rs.getInt("goods_id"));
		goods.setMktprice(rs.getDouble("mktprice"));
		goods.setPrice(rs.getDouble("price"));
		goods.setCreate_time(rs.getLong("create_time"));
		goods.setLast_modify(rs.getLong("last_modify"));
		goods.setType_id(rs.getInt("type_id"));
		goods.setPoint(rs.getInt("point"));
		goods.setStore(rs.getInt("store"));
		goods.setCat_id(rs.getInt("cat_id"));
		
		goods.setSn(rs.getString("sn"));
		goods.setIntro(rs.getString("intro"));
		goods.setStore(rs.getInt("store"));
		String temp = rs.getString("thumbnail");
		goods.setOriginal(StaticResourcesUtil.convertToUrl(rs.getString("original")));
		goods.setThumbnail(StaticResourcesUtil.convertToUrl(temp));
		goods.setSmall(StaticResourcesUtil.convertToUrl(rs.getString("small")));
		goods.setBig(StaticResourcesUtil.convertToUrl(rs.getString("big")));

		
		Map propMap = new HashMap();
		
		for(int i=0;i<20;i++){
			String value = rs.getString("p" + (i+1));
			propMap.put("p"+(i+1),value);
		}
		goods.setPropMap(propMap);
		
		goods.setBuy_count(rs.getInt("buy_count"));
	
		return goods;
	}

} 
