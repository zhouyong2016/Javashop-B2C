package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class ShippingCost  extends PayPalModel {

	/**
	 * Shipping cost in amount. Range of 0 to 999999.99.
	 */
	private Currency amount;

	/**
	 * Tax percentage on shipping amount.
	 */
	private Tax tax;

	/**
	 * Default Constructor
	 */
	public ShippingCost() {
	}


	/**
	 * Setter for amount
	 */
	public ShippingCost setAmount(Currency amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * Getter for amount
	 */
	public Currency getAmount() {
		return this.amount;
	}


	/**
	 * Setter for tax
	 */
	public ShippingCost setTax(Tax tax) {
		this.tax = tax;
		return this;
	}

	/**
	 * Getter for tax
	 */
	public Tax getTax() {
		return this.tax;
	}


}
