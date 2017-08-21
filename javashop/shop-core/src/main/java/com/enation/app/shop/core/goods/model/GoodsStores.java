package com.enation.app.shop.core.goods.model;

import java.util.Date;


/**
 * dable
 */
public class GoodsStores  implements java.io.Serializable {

     private Integer goods_id;
     private String name;
     private String sn;
     private Integer brand_id;
     private Integer cat_id;
     private Integer type_id;
     private String goods_type;  //enum('normal', 'bind') default 'normal'
     private String unit;
     private Double weight;
     private Integer market_enable;
     private Double price;
     private Double mktprice;
     private Integer store;
     private Long create_time;
     private Long last_modify;
     private Integer disabled;
     private Integer point; //积分

     private String color;
     private Double sphere;
     private Integer realstore;//库存表库存量
     private Double cylinder;
     private String brandname;
     private String catname;
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}
	public Integer getCat_id() {
		return cat_id;
	}
	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
	}
	public Integer getType_id() {
		return type_id;
	}
	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}
	public String getGoods_type() {
		return goods_type;
	}
	public void setGoods_type(String goods_type) {
		this.goods_type = goods_type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Integer getMarket_enable() {
		return market_enable;
	}
	public void setMarket_enable(Integer market_enable) {
		this.market_enable = market_enable;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getMktprice() {
		return mktprice;
	}
	public void setMktprice(Double mktprice) {
		this.mktprice = mktprice;
	}
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getLast_modify() {
		return last_modify;
	}
	public void setLast_modify(Long last_modify) {
		this.last_modify = last_modify;
	}
	public Integer getDisabled() {
		return disabled;
	}
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Integer getRealstore() {
		return realstore;
	}
	public void setRealstore(Integer realstore) {
		this.realstore = realstore;
	}
	public String getBrandname() {
		return brandname;
	}
	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}
	public String getCatname() {
		return catname;
	}
	public void setCatname(String catname) {
		this.catname = catname;
	}
	public Double getSphere() {
		return sphere;
	}
	public void setSphere(Double sphere) {
		this.sphere = sphere;
	}
	public Double getCylinder() {
		return cylinder;
	}
	public void setCylinder(Double cylinder) {
		this.cylinder = cylinder;
	}
     
     
 
}