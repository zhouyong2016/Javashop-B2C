package com.enation.app.shop.core.order.model;


/**
 * 订单grid数据源url map的 key<br>
 * @see  {@link OrderAction#getGridUrl()} 
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年11月9日下午2:12:14
 */
public enum OrderGridUrlKeyEnum {
	
	
	the_all("the_all"), //所有订单
	not_pay("not_pay"), //待付款订单
	not_ship("not_ship"),//待发货订单
	detail("detail");  //订单详细页面
	
	
	private String key;
	private  OrderGridUrlKeyEnum(String _key){
		this.key=_key;
	}
	
	/**
	 * 提供toString 方法，可以直接通过OrderGridUrlKeyEnum.not_pay.toString来获得key
	 */
	public String toString(){
		return this.key;
	}
	
	
}
