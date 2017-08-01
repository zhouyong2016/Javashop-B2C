package com.enation.app.shop.core.order.model;

/**
 * 订单扩展信息键(key)名称枚举类
 * @author DMRain
 * @date 2016-6-13
 * @version 1.0
 */
public enum OrderMetaEnumKey {

	activity_reduce_price("activity_reduce_price"), 
	get_point("get_point");
	
	private String key;

	private OrderMetaEnumKey(String key) {
		this.key = key;
	}
	
	@Override
	public String toString(){
		return this.key;
	}
}
