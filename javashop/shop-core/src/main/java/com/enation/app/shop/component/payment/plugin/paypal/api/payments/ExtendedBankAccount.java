package com.enation.app.shop.component.payment.plugin.paypal.api.payments;


public class ExtendedBankAccount extends BankAccount {

	/**
	 * Identifier of the direct debit mandate to validate. Currently supported only for EU bank accounts(SEPA).
	 */
	private String mandateReferenceNumber;

	/**
	 * Default Constructor
	 */
	public ExtendedBankAccount() {
	}


	/**
	 * Setter for mandateReferenceNumber
	 */
	public ExtendedBankAccount setMandateReferenceNumber(String mandateReferenceNumber) {
		this.mandateReferenceNumber = mandateReferenceNumber;
		return this;
	}

	/**
	 * Getter for mandateReferenceNumber
	 */
	public String getMandateReferenceNumber() {
		return this.mandateReferenceNumber;
	}


}
