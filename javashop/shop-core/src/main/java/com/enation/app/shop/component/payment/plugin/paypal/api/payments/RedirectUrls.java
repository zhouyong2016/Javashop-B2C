package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class RedirectUrls  extends PayPalModel {

	/**
	 * Url where the payer would be redirected to after approving the payment.
	 */
	private String returnUrl;

	/**
	 * Url where the payer would be redirected to after canceling the payment.
	 */
	private String cancelUrl;

	/**
	 * Default Constructor
	 */
	public RedirectUrls() {
	}


	/**
	 * Setter for returnUrl
	 */
	public RedirectUrls setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
		return this;
	}

	/**
	 * Getter for returnUrl
	 */
	public String getReturnUrl() {
		return this.returnUrl;
	}


	/**
	 * Setter for cancelUrl
	 */
	public RedirectUrls setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
		return this;
	}

	/**
	 * Getter for cancelUrl
	 */
	public String getCancelUrl() {
		return this.cancelUrl;
	}


}
