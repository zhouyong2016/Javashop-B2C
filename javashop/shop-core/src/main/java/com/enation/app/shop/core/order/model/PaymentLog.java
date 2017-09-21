package com.enation.app.shop.core.order.model;


/**
 * 收款单
 * @author kingapex
 *2010-4-8上午09:09:43
 *
 *2012-03-07重构此实体：用户提交支付记录也写入此表，字段重构-by kingapex
 *20120309重构此实体，只将收款记录写入此表 -by kingapex
 */
public class PaymentLog implements java.io.Serializable {


	private Integer payment_id;	
	private int order_id;	
	private String order_sn;
	private Integer member_id;
	private String pay_user;
	private Double money;
	private long  pay_date; 	//支付日期
	private String pay_method;
	private String remark;
	private int type; 			//1付款单，2退款单
	private Integer status;
	private Long create_time; 
	private String sn;
	private Double market_point;
	private Double credit;
	private String admin_user;
	private Double paymoney; //已结算金额
	/**
	 * 支付交易单号
	 */
	private String trasaction_id; 
	
	public Integer getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
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

	public String getPay_user() {
		return pay_user;
	}

	public void setPay_user(String pay_user) {
		this.pay_user = pay_user;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public long getPay_date() {
		return pay_date;
	}

	public void setPay_date(long pay_date) {
		this.pay_date = pay_date;
	}

	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Double getMarket_point() {
		return market_point;
	}

	public void setMarket_point(Double market_point) {
		this.market_point = market_point;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public String getAdmin_user() {
		return admin_user;
	}

	public void setAdmin_user(String admin_user) {
		this.admin_user = admin_user;
	}

	public Double getPaymoney() {
		return paymoney;
	}

	public void setPaymoney(Double paymoney) {
		this.paymoney = paymoney;
	}
	
	public String getTrasaction_id() {
		return trasaction_id;
	}

	public void setTrasaction_id(String trasaction_id) {
		this.trasaction_id = trasaction_id;
	}

	
}