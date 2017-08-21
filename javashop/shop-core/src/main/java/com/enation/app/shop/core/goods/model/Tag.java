package com.enation.app.shop.core.goods.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @author lzf
 * version 1.0<br/>
 * 2010-6-17&nbsp;下午02:31:00
 */
public class Tag implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1561976738637630304L;
	private Integer tag_id;
	private String tag_name;
	private Integer rel_count;
	/**
	 * add by Chopper
	 * 区分标签商品 还是 标签 品牌
	 */
	private Integer type;
	
	@PrimaryKeyField
	public Integer getTag_id() {
		return tag_id;
	}
	public void setTag_id(Integer tagId) {
		tag_id = tagId;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tagName) {
		tag_name = tagName;
	}
	public Integer getRel_count() {
		return rel_count;
	}
	public void setRel_count(Integer relCount) {
		rel_count = relCount;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}
