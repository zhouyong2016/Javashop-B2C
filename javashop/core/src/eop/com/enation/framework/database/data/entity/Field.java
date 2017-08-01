package com.enation.framework.database.data.entity;


/**
 * 数据库字段
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日下午4:01:02
 */
public class Field {
	
	//字段名
	private String name;
	
	//类型
	private String type;
	
	//操作类型，如添加字段，或删除字段
	//add:添加，drop删除
	private String optype;
	
	
	//大小，like this: "10,2"
	private String size;
	
	
	//默认值 
	private Object defaultValue;
	
	//字段值 
	private Object value;
	
	//是否是主键
	private boolean isPrimaryKey =false;
	
	//是否允许为空
	private boolean isNotNull =false;
	
	
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
 
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Object getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public boolean isNotNull() {
		return isNotNull;
	}
	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}
	
	
}
