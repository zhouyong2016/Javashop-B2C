package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class OverrideChargeModel  extends PayPalModel {

	/**
	 * ID of charge model.
	 */
	private String chargeId;

	/**
	 * Updated Amount to be associated with this charge model.
	 */
	private Currency amount;

	/**
	 * Default Constructor
	 */
	public OverrideChargeModel() {
	}

	/**
	 * Parameterized Constructor
	 */
	public OverrideChargeModel(String chargeId, Currency amount) {
		this.chargeId = chargeId;
		this.amount = amount;
	}


	/**
	 * Setter for chargeId
	 */
	public OverrideChargeModel setChargeId(String chargeId) {
		this.chargeId = chargeId;
		return this;
	}

	/**
	 * Getter for chargeId
	 */
	public String getChargeId() {
		return this.chargeId;
	}


	/**
	 * Setter for amount
	 */
	public OverrideChargeModel setAmount(Currency amount) {
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
