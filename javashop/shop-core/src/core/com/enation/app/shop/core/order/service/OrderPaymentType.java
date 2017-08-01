package com.enation.app.shop.core.order.service;

/**
 * 订单付款类型
 * @author xulipeng
 * @version v1.0 
 * @since v6.2
 * 2016年10月17日13:59:25
 */
public enum OrderPaymentType {
	
	online("在线支付","onlinePay"),cod("货到付款","cod"),offline("银行汇款","offline");
	
	private String name;
	private String value;
	

	private OrderPaymentType(String _name,String _value){
		this.name=_name;
		this.value= _value;
	}
	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	

}
