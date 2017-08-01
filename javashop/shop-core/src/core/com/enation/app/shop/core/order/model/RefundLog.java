package com.enation.app.shop.core.order.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


/**
 * 退款记录
 * @author kingapex
 *2012-3-9下午4:22:21
 */
public class RefundLog {

	//private Integer payment_id;
	private Integer refund_id;
	private int order_id;	
	private String order_sn;
	private Integer member_id;
	private int type; //1在线支付，2线下支付
	private String pay_method; //支付方式 ,如支付宝，或工商银行
	private String pay_user; //户名,线下支付有效
	private String account; //账户
	private Double money; 
	private long  pay_date; //支付日期
	private String remark;	
	private String op_user; //退款人 
	private Long create_time; 
	private String sn;
	private String pay_date_str; //非数据库字段，接收日期字串用，将转换为时间戳赋给pay_date
	
	
//	public Integer getPayment_id() {
//		return payment_id;
//	}
//	public void setPayment_id(Integer payment_id) {
//		this.payment_id = payment_id;
//	}
	
	@PrimaryKeyField
	public Integer getRefund_id() {
		return refund_id;
	}
	public void setRefund_id(Integer refundId) {
		refund_id = refundId;
	}
	public int getOrder_id() {
		return order_id;
	}
	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPay_method() {
		return pay_method;
	}
	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}
	public String getPay_user() {
		return pay_user;
	}
	public void setPay_user(String pay_user) {
		this.pay_user = pay_user;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public long getPay_date() {
		return pay_date;
	}
	public void setPay_date(long pay_date) {
		this.pay_date = pay_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOp_user() {
		return op_user;
	}
	public void setOp_user(String op_user) {
		this.op_user = op_user;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	@NotDbField
	public String getPay_date_str() {
		return pay_date_str;
	}
	public void setPay_date_str(String pay_date_str) {
		
		this.pay_date_str = pay_date_str;
		
		if(!StringUtil.isEmpty(pay_date_str)){
			this.pay_date = DateUtil.getDateline(pay_date_str);
		}
		
		
	}
	
	
}
