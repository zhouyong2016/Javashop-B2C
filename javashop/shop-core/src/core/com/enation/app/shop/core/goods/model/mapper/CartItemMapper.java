package com.enation.app.shop.core.goods.model.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.eop.sdk.utils.StaticResourcesUtil;

public class CartItemMapper implements RowMapper {

	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CartItem cartItem = new CartItem();
		cartItem.setId(rs.getInt("cart_id"));
		cartItem.setProduct_id(rs.getInt("product_id"));
		cartItem.setGoods_id(rs.getInt("goods_id"));
		cartItem.setName(rs.getString("name"));
		cartItem.setMktprice(rs.getDouble("mktprice"));
		cartItem.setPrice(rs.getDouble("price"));
		double price  = rs.getDouble("price");
		cartItem.setCoupPrice(price); //优惠价格默认为销售价，则优惠规则去改变
		cartItem.setCatid(rs.getInt("catid"));
		

		if(this.isExistColumn(rs,"exchange")){
			String exchange=rs.getString("exchange");
			cartItem.setExchange(exchange);//积分商品积分json  add by jianghongyan 2016-06-15  有问题
		}
		
		String is_check =  rs.getString("is_check");
		if(is_check!=null){
			cartItem.setIs_check(Integer.parseInt(is_check));
		}
//		String image_default =  rs.getString("image_default");
//		
//		if(image_default!=null ){
//			image_default  =UploadUtil.replacePath(image_default);
//		}
//		cartItem.setImage_default(image_default);
		
		String thumbnail =  rs.getString("thumbnail");
		
		if(thumbnail!=null ){
			thumbnail  =StaticResourcesUtil.convertToUrl(thumbnail);
		}
		cartItem.setImage_default(thumbnail);
		
		cartItem.setNum(rs.getInt("num"));
		cartItem.setPoint(rs.getInt("point"));
		cartItem.setItemtype(rs.getInt("itemtype"));
		//if( cartItem.getItemtype().intValue() ==  0){
			cartItem.setAddon(rs.getString("addon"));
	//	}
		//赠品设置其限购数量
		if( cartItem.getItemtype().intValue() ==  2){
			cartItem.setLimitnum(rs.getInt("limitnum"));
		}
		 
		cartItem.setSn(rs.getString("sn"));
		
		if( cartItem.getItemtype().intValue() ==  0){
			String specs = rs.getString("specs");
			cartItem.setSpecs(specs);
//			if(StringUtil.isEmpty(specs)) 
//				cartItem.setName(  cartItem.getName() );
//			else
//				cartItem.setName(  cartItem.getName() +"("+ specs +")" );
		}
		
		cartItem.setUnit(rs.getString("unit"));
		cartItem.setWeight(rs.getDouble("weight"));
		
		//购物车选项添加促销活动ID add by DMRain 2016-6-6
		cartItem.setActivity_id(rs.getInt("activity_id"));
		return cartItem;
	}

	
	private boolean isExistColumn(ResultSet rs,String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count=rsmd.getColumnCount();
		List<String> columnNameList=new ArrayList<String>();
		for(int i=0;i<count;i++){
			columnNameList.add(rsmd.getColumnName(i+1));
		}
		return columnNameList.contains(columnName);
	}
}
