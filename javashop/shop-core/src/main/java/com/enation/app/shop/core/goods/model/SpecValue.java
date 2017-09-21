package com.enation.app.shop.core.goods.model;


import com.enation.framework.database.PrimaryKeyField;

/**
 * 规格值
 * @author kingapex
 *2010-3-7上午11:41:10
 */
public class SpecValue {
	private Integer spec_value_id;
	private Integer  spec_id;
	private String spec_value;
	private String spec_image;
	private Integer spec_order;
	private Integer spec_type;
	private Integer inherent_or_add;  //固有规格 “0”  添加商品修改规格“1”
	private String image;
	
	
	public Integer getInherent_or_add() {
		return inherent_or_add;
	}
	public void setInherent_or_add(Integer inherent_or_add) {
		this.inherent_or_add = inherent_or_add;
	}
	public Integer getSpec_type() {
		return spec_type;
	}
	public void setSpec_type(Integer specType) {
		spec_type = specType;
	}
	
	@PrimaryKeyField 
	public Integer getSpec_value_id() {
		return spec_value_id;
	}
	public void setSpec_value_id(Integer specValueId) {
		spec_value_id = specValueId;
	}
	public Integer getSpec_id() {
		return spec_id;
	}
	public void setSpec_id(Integer specId) {
		spec_id = specId;
	}
	public String getSpec_value() {
		return spec_value;
	}
	public void setSpec_value(String specValue) {
		spec_value = specValue;
	}
	public String getSpec_image() {
		return spec_image;
	}
	public void setSpec_image(String specImage) {
		spec_image = specImage;
	}
	public Integer getSpec_order() {
		return spec_order;
	}
	public void setSpec_order(Integer specOrder) {
		spec_order = specOrder;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
}
