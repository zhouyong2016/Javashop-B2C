package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 错误报告
 * @author kingapex
 * 2010-7-27下午06:13:49
 * @author kanon 2015-12-17 version 1.1 添加注释
 */
public class ErrorReport {
	
	/**
	 * 错误Id
	 */
	private Integer id;
	
	/**
	 * 错误
	 */
	private String error;
	
	/**
	 * 错误详细
	 */
	private String info;
	
	/**
	 * 时间
	 */
	private long dateline;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public long getDatelineLong() {
		return dateline;
	}
	public void setDateline(long dateline) {
		this.dateline = dateline;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	
	 
}
