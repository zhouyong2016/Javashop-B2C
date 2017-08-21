package com.enation.app.shop.core.order.model;

import com.enation.framework.database.PrimaryKeyField;

public class PaymentDetail {
	
	private Integer paymentDetail_id;
	private Integer payment_id;
	private Double pay_money;
	private long pay_date;
	private String admin_user;
	@PrimaryKeyField
	public Integer getPaymentDetail_id() {
		return paymentDetail_id;
	}
	public void setPaymentDetail_id(Integer paymentDetail_id) {
		this.paymentDetail_id = paymentDetail_id;
	}
	public Integer getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
	}
	public Double getPay_money() {
		return pay_money;
	}
	public void setPay_money(Double pay_money) {
		this.pay_money = pay_money;
	}
	public long getPay_date() {
		return pay_date;
	}
	public void setPay_date(long pay_date) {
		this.pay_date = pay_date;
	}
	public String getAdmin_user() {
		return admin_user;
	}
	public void setAdmin_user(String admin_user) {
		this.admin_user = admin_user;
	}
	

}
