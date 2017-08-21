package com.enation.app.shop.core.order.model;
/**
 * 退货日志
 * @author lina
 *2013-11-10上午10:20:54
 */
public class SellBackLog {
	
	private Integer id;
	private Integer recid;//退货单id
	private Long logtime;
	private String logdetail;//日志详细
	private String operator;//操作员
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public String getLogdetail() {
		return logdetail;
	}
	public void setLogdetail(String logdetail) {
		this.logdetail = logdetail;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Long getLogtime() {
		return logtime;
	}
	public void setLogtime(Long logtime) {
		this.logtime = logtime;
	}
	
	
}

