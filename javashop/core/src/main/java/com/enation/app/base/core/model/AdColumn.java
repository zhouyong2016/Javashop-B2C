package com.enation.app.base.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 广告位
 * 
 * @author 李志富 lzf<br/>
 *         2010-2-4 下午03:24:48<br/>
 *         version 1.0<br/>
 * <br/>
 * @author kanon 2015-9-21 添加 广告位注释
 */
public class AdColumn {
	private Integer acid;	//广告位id
	private String cname;	//名称
	private String width;	//宽度
	private String height;	//高度
	private String description;	//描述
	private Integer anumber;	//数量
	private Integer atype;	//类型
	private Integer rule;	//规则
	private Integer userid;
	private Integer siteid;
	private String disabled;	//是否删除

	@PrimaryKeyField
	public Integer getAcid() {
		return acid;
	}

	public void setAcid(Integer acid) {
		this.acid = acid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAnumber() {
		return anumber;
	}

	public void setAnumber(Integer anumber) {
		this.anumber = anumber;
	}

	public Integer getAtype() {
		return atype;
	}

	public void setAtype(Integer atype) {
		this.atype = atype;
	}

	public Integer getRule() {
		return rule;
	}

	public void setRule(Integer rule) {
		this.rule = rule;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getSiteid() {
		return siteid;
	}

	public void setSiteid(Integer siteid) {
		this.siteid = siteid;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	@NotDbField
	public String getWidth_num() {
		return width.replace("px", "");
	}
	
	@NotDbField
	public String getHeight_num() {
		return height.replace("px", "");
	}

}
