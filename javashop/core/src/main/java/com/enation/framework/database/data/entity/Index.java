package com.enation.framework.database.data.entity;

import java.util.List;

/**
 * 数据库索引
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日下午4:06:53
 */
public class Index {
	
	//索引名字
	private String name;
	
	//索引字段列表
	private List<Field> fieldList;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Field> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}
	
	
	
	
}
