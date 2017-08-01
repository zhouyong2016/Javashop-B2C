package com.enation.framework.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 操作日志
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月8日 上午10:36:12
 */
public class Log {

	/**
	 * 主键
	 */
	private Long log_id;
	
	/**
	 * 日志类型   member/order/...
	 */
	private String log_type;
	
	/**
	 * 操作人名
	 */
	private String operator_name;
	
	/**
	 * 操作人标号
	 */
	private Integer operator_id;
	
	/**
	 * 操作详情
	 */
	private String log_detail;
	
	/**
	 * 操作时间
	 */
	private Long log_time;

	
	
	@PrimaryKeyField
	public Long getLog_id() {
		return log_id;
	}

	public String getLog_type() {
		return log_type;
	}
	
	public void setLog_id(Long log_id) {
		this.log_id = log_id;
	}

	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public Integer getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(Integer operator_id) {
		this.operator_id = operator_id;
	}

	public String getLog_detail() {
		return log_detail;
	}

	public void setLog_detail(String log_detail) {
		this.log_detail = log_detail;
	}

	public Long getLog_time() {
		return log_time;
	}

	public void setLog_time(Long log_time) {
		this.log_time = log_time;
	}
	
	
	
}
