package com.enation.framework.database;

import java.util.HashMap;
import java.util.Map;


/**
 * 动态字段
 * @author kingapex
 *2012-5-5下午12:46:37
 */
public class DynamicField {
	
	private Map<String,Object> fields;
	public DynamicField(){
		fields = new HashMap<String, Object>();
	}
	
	public void addField(String name,Object value){
		fields.put(name, value);
	}
	
	@NotDbField
	public Map<String,Object> getFields(){
		return fields;
	}
}
