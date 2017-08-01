package com.enation.app.shop.core.goods.model.support;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.goods.model.Goods;
import com.enation.framework.util.CurrencyUtil;

/**
 * 商品视图，对商品图片和属性做了处理,
 * 并且包含了一些冗余信息。
 * @author kingapex
 * 2010-7-16下午02:05:11
 */
public class GoodsView extends Goods  {

	private Double save_price;// 节省金额

	private Double agio; // 折扣

	private String brand_name; // 所属的品牌名称

	private Map propMap; // 商品属性，以p<index>为key，属性值为value的map

	private int hasSpec; // 是否有规格

	private List specList; // 规格列表
	
	private Integer productid; //如果没有规格时唯一的货品id
	
 
	
	private boolean isLast=false;//是否是列表中的最后一个
	private boolean isFirst=false;//是否是列表中的第一个
	
 
	
	
	
	public Double getAgio() {
//		agio = this.getPrice() / this.getMktprice();
		agio = CurrencyUtil.div(this.getPrice(), this.getMktprice());
		return agio;
	}

	public void setAgio(Double agio) {
		this.agio = agio;
	}

	public Double getSave_price() {
//		save_price = this.getMktprice() - this.getPrice();
		save_price = CurrencyUtil.sub(this.getMktprice(), this.getPrice());
		return save_price;
	}

	public void setSave_price(Double save_price) {
		this.save_price = save_price;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public Map getPropMap() {
		return propMap;
	}

	public void setPropMap(Map propMap) {
		this.propMap = propMap;
	}

	public List getSpecList() {
		return specList;
	}

	public void setSpecList(List specList) {
		this.specList = specList;
	}

	public int getHasSpec() {
		return hasSpec;
	}

	public void setHasSpec(int hasSpec) {
		this.hasSpec = hasSpec;
	}

	public Integer getProductid() {
		return productid;
	}

	public void setProductid(Integer productid) {
		this.productid = productid;
	}

	public boolean getIsLast() {
		return isLast;
	}

	public void setIsLast(boolean isLast) {
		this.isLast = isLast;
	}

	public boolean getIsFirst() {
		return isFirst;
	}

	public void setIsFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

 
}
