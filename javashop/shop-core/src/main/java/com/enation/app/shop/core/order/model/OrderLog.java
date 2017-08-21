package com.enation.app.shop.core.order.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 订单日志
 * @author kingapex
 *2010-4-6下午05:24:16
 */
public class OrderLog {
	 	
	private Integer  log_id;	//日志Id
	private Integer order_id;	//订单Id
	private Integer op_id;		//会员Id
	private String op_name;		//管理员／会员名
	private String message;		//日志信息
	private Long op_time;		//创建时间
	@PrimaryKeyField
	public Integer getLog_id() {
		return log_id;
	}
	public void setLog_id(Integer logId) {
		log_id = logId;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer orderId) {
		order_id = orderId;
	}
	public Integer getOp_id() {
		return op_id;
	}
	public void setOp_id(Integer opId) {
		op_id = opId;
	}
	public String getOp_name() {
		return op_name;
	}
	public void setOp_name(String opName) {
		op_name = opName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getOp_time() {
		return op_time;
	}
	public void setOp_time(Long opTime) {
		op_time = opTime;
	}
}
