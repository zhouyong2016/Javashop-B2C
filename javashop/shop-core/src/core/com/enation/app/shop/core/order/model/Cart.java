package com.enation.app.shop.core.order.model;

import com.enation.framework.database.DynamicField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 购物车实体
 * @author kingapex
 *2010-3-23下午03:34:04
 */
public class Cart extends DynamicField {
	
	private Integer cart_id;
	private Integer goods_id;
	private Integer product_id;
	private Integer num;
	private Double weight;
	private String session_id;
	private Integer itemtype;
	private String name;
	private Double price;
	private String addon; //附件字串
	
	private Integer activity_id;	//促销活动ID add by DMRain 2016-1-29
	private Integer member_id;
	private Integer is_check;
	private String exchange;// 积分商城 add by jianghongyan 2016-6-15
	
	/**
	 * 商品是否被修改 决定是否执行从新加入购物车
	 */
	private Integer is_change;		 
	/**
	 * 商品活动结束时间 结束之后需要重新加入购物车
	 */
	private Long activity_end_time;	
	
	@PrimaryKeyField
	public Integer getCart_id() {
		return cart_id;
	}
	public void setCart_id(Integer cartId) {
		cart_id = cartId;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer productId) {
		product_id = productId;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String sessionId) {
		session_id = sessionId;
	}
	public Integer getItemtype() {
		return itemtype;
	}
	public void setItemtype(Integer itemtype) {
		this.itemtype = itemtype;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getAddon() {
		return addon;
	}
	public void setAddon(String addon) {
		this.addon = addon;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getActivity_id() {
		return activity_id;
	}
	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}
	public Integer getIs_check() {
		return is_check;
	}
	public void setIs_check(Integer is_check) {
		this.is_check = is_check;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	
	public Integer getIs_change() {
		return is_change;
	}
	public void setIs_change(Integer is_change) {
		this.is_change = is_change;
	}
	public Long getActivity_end_time() {
		return activity_end_time;
	}
	public void setActivity_end_time(Long activity_end_time) {
		this.activity_end_time = activity_end_time;
	}
	
}
