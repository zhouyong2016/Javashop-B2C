package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class ChargeModels  extends PayPalModel {

	/**
	 * Identifier of the charge model. 128 characters max.
	 */
	private String id;

	/**
	 * Type of charge model. Allowed values: `SHIPPING`, `TAX`.
	 */
	private String type;

	/**
	 * Specific amount for this charge model.
	 */
	private Currency amount;

	/**
	 * Default Constructor
	 */
	public ChargeModels() {
	}

	/**
	 * Parameterized Constructor
	 */
	public ChargeModels(String type, Currency amount) {
		this.type = type;
		this.amount = amount;
	}


	/**
	 * Setter for id
	 */
	public ChargeModels setId(String id) {
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
	public ChargeModels setType(String type) {
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
	 * Setter for amount
	 */
	public ChargeModels setAmount(Currency amount) {
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
