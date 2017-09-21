package com.enation.app.shop.core.other.model;

/**
 * 促销活动商品实体类
 * 2016-5-23
 * @author DMRain
 * @version 1.0
 */
public class ActivityGoods {

	/** 主键ID */
	private Integer ag_id;
	
	/** 促销活动ID */
	private Integer activity_id;
	
	/** 商品ID */
	private Integer goods_id;
	
	/** 货品ID */
	private Integer product_id;

	public Integer getAg_id() {
		return ag_id;
	}

	public void setAg_id(Integer ag_id) {
		this.ag_id = ag_id;
	}

	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	
	
}
