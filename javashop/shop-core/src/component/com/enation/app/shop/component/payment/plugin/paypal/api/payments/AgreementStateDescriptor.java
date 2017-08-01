package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class AgreementStateDescriptor  extends PayPalModel {

	/**
	 * Reason for changing the state of the agreement.
	 */
	private String note;

	/**
	 * The amount and currency of the agreement.
	 */
	private Currency amount;

	/**
	 * Default Constructor
	 */
	public AgreementStateDescriptor() {
	}

	/**
	 * Parameterized Constructor
	 */
	public AgreementStateDescriptor(Currency amount) {
		this.amount = amount;
	}


	/**
	 * Setter for note
	 */
	public AgreementStateDescriptor setNote(String note) {
		this.note = note;
		return this;
	}

	/**
	 * Getter for note
	 */
	public String getNote() {
		return this.note;
	}


	/**
	 * Setter for amount
	 */
	public AgreementStateDescriptor setAmount(Currency amount) {
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
