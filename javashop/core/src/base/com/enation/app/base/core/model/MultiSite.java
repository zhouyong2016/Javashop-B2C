package com.enation.app.base.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class MultiSite implements Serializable {
	private Integer siteid;
	private Integer parentid;
	private Integer code;
	private String name;
	private String domain;
	private Integer themeid;
	private Integer sitelevel; // 站点级别 lzf注：level为oracle保留字

	@PrimaryKeyField
	public Integer getSiteid() {
		return siteid;
	}

	public void setSiteid(Integer siteid) {
		this.siteid = siteid;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getSitelevel() {
		return sitelevel;
	}

	public void setSitelevel(Integer sitelevel) {
		this.sitelevel = sitelevel;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getThemeid() {
		return themeid;
	}

	public void setThemeid(Integer themeid) {
		this.themeid = themeid;
	}

}