package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class Tax  extends PayPalModel {

	/**
	 * Identifier of the resource.
	 */
	private String id;

	/**
	 * Name of the tax. 10 characters max.
	 */
	private String name;

	/**
	 * Rate of the specified tax. Range of 0.001 to 99.999.
	 */
	private float percent;

	/**
	 * Tax in the form of money. Cannot be specified in a request.
	 */
	private Currency amount;

	/**
	 * Default Constructor
	 */
	public Tax() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Tax(String name, float percent) {
		this.name = name;
		this.percent = percent;
	}


	/**
	 * Setter for id
	 */
	public Tax setId(String id) {
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
	 * Setter for name
	 */
	public Tax setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Getter for name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Setter for percent
	 */
	public Tax setPercent(float percent) {
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
	public Tax setAmount(Currency amount) {
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
