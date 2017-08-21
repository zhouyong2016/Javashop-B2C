package com.enation.app.shop.core.other.model;

import java.io.Serializable;

/**
 * 促销活动赠品实体类
 * 2016-5-24
 * @author DMRain
 * @version 1.0
 */
public class ActivityGift implements Serializable{

	/** 赠品ID */
	private Integer gift_id;
	
	/** 赠品名称 */
	private String gift_name;
	
	/** 赠品价格 */
	private Double gift_price;
	
	/** 赠品图片 */
	private String gift_img;
	
	/** 赠品类型 0:代表纯赠品，1:代表商品(1.0版本暂时还不能将商品转为赠品)*/
	private Integer gift_type;
	
	/** 赠品实际库存 */
	private Integer actual_store;
	
	/** 赠品可用库存 */
	private Integer enable_store;
	
	/** 添加时间 */
	private Long create_time;
	
	/** 商品ID */
	private Integer goods_id;

	/** 是否禁用 0：否，1：是 */
	private Integer disabled;
	
	public Integer getGift_id() {
		return gift_id;
	}

	public void setGift_id(Integer gift_id) {
		this.gift_id = gift_id;
	}

	public String getGift_name() {
		return gift_name;
	}

	public void setGift_name(String gift_name) {
		this.gift_name = gift_name;
	}

	public Double getGift_price() {
		return gift_price;
	}

	public void setGift_price(Double gift_price) {
		this.gift_price = gift_price;
	}

	public String getGift_img() {
		return gift_img;
	}

	public void setGift_img(String gift_img) {
		this.gift_img = gift_img;
	}

	public Integer getGift_type() {
		return gift_type;
	}

	public void setGift_type(Integer gift_type) {
		this.gift_type = gift_type;
	}

	public Integer getActual_store() {
		return actual_store;
	}

	public void setActual_store(Integer actual_store) {
		this.actual_store = actual_store;
	}

	public Integer getEnable_store() {
		return enable_store;
	}

	public void setEnable_store(Integer enable_store) {
		this.enable_store = enable_store;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

}
