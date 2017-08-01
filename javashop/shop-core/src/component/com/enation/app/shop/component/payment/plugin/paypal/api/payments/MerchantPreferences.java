package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class MerchantPreferences  extends PayPalModel {

	/**
	 * Identifier of the merchant_preferences. 128 characters max.
	 */
	private String id;

	/**
	 * Setup fee amount. Default is 0.
	 */
	private Currency setupFee;

	/**
	 * Redirect URL on cancellation of agreement request. 1000 characters max.
	 */
	private String cancelUrl;

	/**
	 * Redirect URL on creation of agreement request. 1000 characters max.
	 */
	private String returnUrl;

	/**
	 * Notify URL on agreement creation. 1000 characters max.
	 */
	private String notifyUrl;

	/**
	 * Total number of failed attempts allowed. Default is 0, representing an infinite number of failed attempts.
	 */
	private String maxFailAttempts;

	/**
	 * Allow auto billing for the outstanding amount of the agreement in the next cycle. Allowed values: `YES`, `NO`. Default is `NO`.
	 */
	private String autoBillAmount;

	/**
	 * Action to take if a failure occurs during initial payment. Allowed values: `CONTINUE`, `CANCEL`. Default is continue.
	 */
	private String initialFailAmountAction;

	/**
	 * Payment types that are accepted for this plan.
	 */
	private String acceptedPaymentType;

	/**
	 * char_set for this plan.
	 */
	private String charSet;

	/**
	 * Default Constructor
	 */
	public MerchantPreferences() {
	}

	/**
	 * Parameterized Constructor
	 */
	public MerchantPreferences(String cancelUrl, String returnUrl) {
		this.cancelUrl = cancelUrl;
		this.returnUrl = returnUrl;
	}


	/**
	 * Setter for id
	 */
	public MerchantPreferences setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Getter for id
	 */
	public String getId() {
		return this.id;
	}


	/**
	 * Setter for setupFee
	 */
	public MerchantPreferences setSetupFee(Currency setupFee) {
		this.setupFee = setupFee;
		return this;
	}

	/**
	 * Getter for setupFee
	 */
	public Currency getSetupFee() {
		return this.setupFee;
	}


	/**
	 * Setter for cancelUrl
	 */
	public MerchantPreferences setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
		return this;
	}

	/**
	 * Getter for cancelUrl
	 */
	public String getCancelUrl() {
		return this.cancelUrl;
	}


	/**
	 * Setter for returnUrl
	 */
	public MerchantPreferences setReturnUrl(String returnUrl) {
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
	 * Setter for notifyUrl
	 */
	public MerchantPreferences setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		return this;
	}

	/**
	 * Getter for notifyUrl
	 */
	public String getNotifyUrl() {
		return this.notifyUrl;
	}


	/**
	 * Setter for maxFailAttempts
	 */
	public MerchantPreferences setMaxFailAttempts(String maxFailAttempts) {
		this.maxFailAttempts = maxFailAttempts;
		return this;
	}

	/**
	 * Getter for maxFailAttempts
	 */
	public String getMaxFailAttempts() {
		return this.maxFailAttempts;
	}


	/**
	 * Setter for autoBillAmount
	 */
	public MerchantPreferences setAutoBillAmount(String autoBillAmount) {
		this.autoBillAmount = autoBillAmount;
		return this;
	}

	/**
	 * Getter for autoBillAmount
	 */
	public String getAutoBillAmount() {
		return this.autoBillAmount;
	}


	/**
	 * Setter for initialFailAmountAction
	 */
	public MerchantPreferences setInitialFailAmountAction(String initialFailAmountAction) {
		this.initialFailAmountAction = initialFailAmountAction;
		return this;
	}

	/**
	 * Getter for initialFailAmountAction
	 */
	public String getInitialFailAmountAction() {
		return this.initialFailAmountAction;
	}


	/**
	 * Setter for acceptedPaymentType
	 */
	public MerchantPreferences setAcceptedPaymentType(String acceptedPaymentType) {
		this.acceptedPaymentType = acceptedPaymentType;
		return this;
	}

	/**
	 * Getter for acceptedPaymentType
	 */
	public String getAcceptedPaymentType() {
		return this.acceptedPaymentType;
	}


	/**
	 * Setter for charSet
	 */
	public MerchantPreferences setCharSet(String charSet) {
		this.charSet = charSet;
		return this;
	}

	/**
	 * Getter for charSet
	 */
	public String getCharSet() {
		return this.charSet;
	}
	
}
