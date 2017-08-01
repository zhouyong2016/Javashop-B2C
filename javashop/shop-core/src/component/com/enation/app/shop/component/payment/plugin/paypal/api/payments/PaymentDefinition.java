package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.List;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class PaymentDefinition  extends PayPalModel {

	/**
	 * Identifier of the payment_definition. 128 characters max.
	 */
	private String id;

	/**
	 * Name of the payment definition. 128 characters max.
	 */
	private String name;

	/**
	 * Type of the payment definition. Allowed values: `TRIAL`, `REGULAR`.
	 */
	private String type;

	/**
	 * How frequently the customer should be charged.
	 */
	private String frequencyInterval;

	/**
	 * Frequency of the payment definition offered. Allowed values: `WEEK`, `DAY`, `YEAR`, `MONTH`.
	 */
	private String frequency;

	/**
	 * Number of cycles in this payment definition.
	 */
	private String cycles;

	/**
	 * Amount that will be charged at the end of each cycle for this payment definition.
	 */
	private Currency amount;

	/**
	 * Array of charge_models for this payment definition.
	 */
	private List<ChargeModels> chargeModels;

	/**
	 * Default Constructor
	 */
	public PaymentDefinition() {
	}

	/**
	 * Parameterized Constructor
	 */
	public PaymentDefinition(String name, String type, String frequencyInterval, String frequency, String cycles, Currency amount) {
		this.name = name;
		this.type = type;
		this.frequencyInterval = frequencyInterval;
		this.frequency = frequency;
		this.cycles = cycles;
		this.amount = amount;
	}


	/**
	 * Setter for id
	 */
	public PaymentDefinition setId(String id) {
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
	public PaymentDefinition setName(String name) {
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
	 * Setter for type
	 */
	public PaymentDefinition setType(String type) {
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
	 * Setter for frequencyInterval
	 */
	public PaymentDefinition setFrequencyInterval(String frequencyInterval) {
		this.frequencyInterval = frequencyInterval;
		return this;
	}

	/**
	 * Getter for frequencyInterval
	 */
	public String getFrequencyInterval() {
		return this.frequencyInterval;
	}


	/**
	 * Setter for frequency
	 */
	public PaymentDefinition setFrequency(String frequency) {
		this.frequency = frequency;
		return this;
	}

	/**
	 * Getter for frequency
	 */
	public String getFrequency() {
		return this.frequency;
	}


	/**
	 * Setter for cycles
	 */
	public PaymentDefinition setCycles(String cycles) {
		this.cycles = cycles;
		return this;
	}

	/**
	 * Getter for cycles
	 */
	public String getCycles() {
		return this.cycles;
	}


	/**
	 * Setter for amount
	 */
	public PaymentDefinition setAmount(Currency amount) {
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
	 * Setter for chargeModels
	 */
	public PaymentDefinition setChargeModels(List<ChargeModels> chargeModels) {
		this.chargeModels = chargeModels;
		return this;
	}

	/**
	 * Getter for chargeModels
	 */
	public List<ChargeModels> getChargeModels() {
		return this.chargeModels;
	}

}
