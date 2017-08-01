package com.enation.framework.model;

/**
 * 商家中心日志
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月13日 上午10:27:35
 */
public class LogStore extends Log{

	/**
	 * 店铺主键
	 */
	private Integer store_id;

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	
	
}
