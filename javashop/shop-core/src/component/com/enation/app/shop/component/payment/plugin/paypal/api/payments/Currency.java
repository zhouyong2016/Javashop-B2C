package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class Currency  extends PayPalModel {

	/**
	 * 3 letter currency code
	 */
	private String currency;

	/**
	 * amount upto 2 decimals represented as string
	 */
	private String value;

	/**
	 * Default Constructor
	 */
	public Currency() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Currency(String currency, String value) {
		this.currency = currency;
		this.value = value;
	}


	/**
	 * Setter for currency
	 */
	public Currency setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	/**
	 * Getter for currency
	 */
	public String getCurrency() {
		return this.currency;
	}


	/**
	 * Setter for value
	 */
	public Currency setValue(String value) {
		this.value = value;
		return this;
	}

	/**
	 * Getter for value
	 */
	public String getValue() {
		return this.value;
	}
	
}
