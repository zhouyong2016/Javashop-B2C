package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

public class Address  extends BaseAddress {

	/**
	 * Phone number in E.123 format.
	 */
	private String phone;
	
	/**
	 * Setter for phone
	 */
	public Address setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	/**
	 * Getter for phone
	 */
	public String getPhone() {
		return this.phone;
	}
}
