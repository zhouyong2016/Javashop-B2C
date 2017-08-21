package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class Terms  extends PayPalModel {

	/**
	 * Identifier of the terms. 128 characters max.
	 */
	private String id;

	/**
	 * Term type. Allowed values: `MONTHLY`, `WEEKLY`, `YEARLY`.
	 */
	private String type;

	/**
	 * Max Amount associated with this term.
	 */
	private Currency maxBillingAmount;

	/**
	 * How many times money can be pulled during this term.
	 */
	private String occurrences;

	/**
	 * Amount_range associated with this term.
	 */
	private Currency amountRange;

	/**
	 * Buyer's ability to edit the amount in this term.
	 */
	private String buyerEditable;

	/**
	 * Default Constructor
	 */
	public Terms() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Terms(String type, Currency maxBillingAmount, String occurrences, Currency amountRange, String buyerEditable) {
		this.type = type;
		this.maxBillingAmount = maxBillingAmount;
		this.occurrences = occurrences;
		this.amountRange = amountRange;
		this.buyerEditable = buyerEditable;
	}


	/**
	 * Setter for id
	 */
	public Terms setId(String id) {
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
	 * Setter for type
	 */
	public Terms setType(String type) {
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
	 * Setter for maxBillingAmount
	 */
	public Terms setMaxBillingAmount(Currency maxBillingAmount) {
		this.maxBillingAmount = maxBillingAmount;
		return this;
	}

	/**
	 * Getter for maxBillingAmount
	 */
	public Currency getMaxBillingAmount() {
		return this.maxBillingAmount;
	}


	/**
	 * Setter for occurrences
	 */
	public Terms setOccurrences(String occurrences) {
		this.occurrences = occurrences;
		return this;
	}

	/**
	 * Getter for occurrences
	 */
	public String getOccurrences() {
		return this.occurrences;
	}


	/**
	 * Setter for amountRange
	 */
	public Terms setAmountRange(Currency amountRange) {
		this.amountRange = amountRange;
		return this;
	}

	/**
	 * Getter for amountRange
	 */
	public Currency getAmountRange() {
		return this.amountRange;
	}


	/**
	 * Setter for buyerEditable
	 */
	public Terms setBuyerEditable(String buyerEditable) {
		this.buyerEditable = buyerEditable;
		return this;
	}

	/**
	 * Getter for buyerEditable
	 */
	public String getBuyerEditable() {
		return this.buyerEditable;
	}
	
}
