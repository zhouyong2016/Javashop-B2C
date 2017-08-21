package com.enation.app.base.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 权限点实体
 * @author kingapex
 * 2010-10-24下午12:38:51
 * @author kanon 2015-12-17 version 1.1 添加注释
 */
public class AuthAction implements Serializable{


	private static final long serialVersionUID = 4831369457068242964L;
	
	/**
	 * 权限点Id
	 */
	private Integer actid;
	
	/**
	 * 权限的名称
	 */
	private String name;
	
	/**
	 * 权限类型
	 */
	private String type;
	
	/**
	 * 对象值
	 */
	private String objvalue;	
	
	/**
	 * 是否为系统默认权限
	 */
	private int choose;
	
	@PrimaryKeyField
	public Integer getActid() {
		return actid;
	}
	public void setActid(Integer actid) {
		this.actid = actid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getObjvalue() {
		return objvalue;
	}
	public void setObjvalue(String objvalue) {
		this.objvalue = objvalue;
	}
	public int getChoose() {
		return choose;
	}
	public void setChoose(int choose) {
		this.choose = choose;
	}
}
