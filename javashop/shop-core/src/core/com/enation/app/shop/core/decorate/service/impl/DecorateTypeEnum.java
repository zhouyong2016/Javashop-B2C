package com.enation.app.shop.core.decorate.service.impl;
/**
 * 
 * 装修修改类型的枚举
 * @author    jianghongyan
 * @version   1.0.0,2016年7月15日
 * @since     v6.1
 */
public enum DecorateTypeEnum {
	
	
	
	FLOOR("floor"), //楼层
	SHOWCASE("showcase"), //橱窗
	SUBJECT("subject");//专题
	
	
	private String key;
	private  DecorateTypeEnum(String _key){
		this.key=_key;
	}
	
	/**
	 * 提供toString 方法，可以直接通过OrderGridUrlKeyEnum.not_pay.toString来获得key
	 */
	public String toString(){
		return this.key;
	}
	
}
