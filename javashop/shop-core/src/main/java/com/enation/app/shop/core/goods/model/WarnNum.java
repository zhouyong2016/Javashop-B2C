package com.enation.app.shop.core.goods.model;

import java.util.Date;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 报警实体
 * 
 * @author dable 2011-11-28
 */
public class WarnNum implements java.io.Serializable {

	private Integer id;
	private Integer goods_id;
	private Double sphere;
	private Double cylinder;
	private Integer product_id;
	private String color;
	private Integer warn_num;

	@PrimaryKeyField
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
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

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getWarn_num() {
		return warn_num;
	}

	public void setWarn_num(Integer warn_num) {
		this.warn_num = warn_num;
	}

}