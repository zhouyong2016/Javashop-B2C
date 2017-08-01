package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 友情链接
 * @author kanon 2015-9-21 添加友情链接注释
 */

public class FriendsLink implements java.io.Serializable {
	private Integer link_id;
	private String name;	//名称
	private String url;		//链接地址
	private String logo;	//图标
	private Integer sort;	//排序

	@PrimaryKeyField
	public Integer getLink_id() {
		return link_id;
	}

	public void setLink_id(Integer link_id) {
		this.link_id = link_id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}