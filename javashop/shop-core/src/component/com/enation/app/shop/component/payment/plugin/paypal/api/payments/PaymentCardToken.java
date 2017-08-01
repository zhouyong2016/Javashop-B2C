package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class PaymentCardToken  extends PayPalModel {

	/**
	 * ID of a previously saved Payment Card resource.
	 */
	private String paymentCardId;

	/**
	 * The unique identifier of the payer used when saving this payment card.
	 */
	private String externalCustomerId;

	/**
	 * Last 4 digits of the card number from the saved card.
	 */
	private String last4;

	/**
	 * Type of the Card.
	 */
	private String type;

	/**
	 * card expiry month from the saved card with value 1 - 12
	 */
	private int expireMonth;

	/**
	 * 4 digit card expiry year from the saved card
	 */
	private int expireYear;

	/**
	 * Default Constructor
	 */
	public PaymentCardToken() {
	}

	/**
	 * Parameterized Constructor
	 */
	public PaymentCardToken(String paymentCardId, String externalCustomerId, String type) {
		this.paymentCardId = paymentCardId;
		this.externalCustomerId = externalCustomerId;
		this.type = type;
	}


	/**
	 * Setter for paymentCardId
	 */
	public PaymentCardToken setPaymentCardId(String paymentCardId) {
		this.paymentCardId = paymentCardId;
		return this;
	}

	/**
	 * Getter for paymentCardId
	 */
	public String getPaymentCardId() {
		return this.paymentCardId;
	}


	/**
	 * Setter for externalCustomerId
	 */
	public PaymentCardToken setExternalCustomerId(String externalCustomerId) {
		this.externalCustomerId = externalCustomerId;
		return this;
	}

	/**
	 * Getter for externalCustomerId
	 */
	public String getExternalCustomerId() {
		return this.externalCustomerId;
	}


	/**
	 * Setter for last4
	 */
	public PaymentCardToken setLast4(String last4) {
		this.last4 = last4;
		return this;
	}

	/**
	 * Getter for last4
	 */
	public String getLast4() {
		return this.last4;
	}


	/**
	 * Setter for type
	 */
	public PaymentCardToken setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Getter for type
	 */
	public String getType() {
		return this.type;
	}


	/**
	 * Setter for expireMonth
	 */
	public PaymentCardToken setExpireMonth(int expireMonth) {
		this.expireMonth = expireMonth;
		return this;
	}

	/**
	 * Getter for expireMonth
	 */
	public int getExpireMonth() {
		return this.expireMonth;
	}


	/**
	 * Setter for expireYear
	 */
	public PaymentCardToken setExpireYear(int expireYear) {
		this.expireYear = expireYear;
		return this;
	}

	/**
	 * Getter for expireYear
	 */
	public int getExpireYear() {
		return this.expireYear;
	}


}
