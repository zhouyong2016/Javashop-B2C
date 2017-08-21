package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class Cost  extends PayPalModel {

	/**
	 * Cost in percent. Range of 0 to 100.
	 */
	private float percent;

	/**
	 * Cost in amount. Range of 0 to 999999.99.
	 */
	private Currency amount;

	/**
	 * Default Constructor
	 */
	public Cost() {
	}


	/**
	 * Setter for percent
	 */
	public Cost setPercent(float percent) {
		this.percent = percent;
		return this;
	}

	/**
	 * Getter for percent
	 */
	public float getPercent() {
		return this.percent;
	}


	/**
	 * Setter for amount
	 */
	public Cost setAmount(Currency amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * Getter for amount
	 */
	public Currency getAmount() {
		return this.amount;
	}


}
