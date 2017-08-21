package com.enation.app.shop.core.goods.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @author lzf version 1.0<br/>
 *         2010-6-17&nbsp;下午02:31:13
 */
public class Brand implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 6694554184326836282L;

	private Integer brand_id;
	private String name;
	private String logo;
	private String keywords;
	private String brief;
	private String url;
	private Integer disabled;

	/**
	 * 排序
	 */
	private Integer ordernum;

	/** default constructor */
	public Brand() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getBrief() {
		return this.brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	public Integer getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}

	@PrimaryKeyField
	public Integer getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}
}