package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class InvoicingNotification  extends PayPalModel {

	/**
	 * Subject of the notification.
	 */
	private String subject;

	/**
	 * Note to the payer.
	 */
	private String note;

	/**
	 * A flag indicating whether a copy of the email has to be sent to the merchant.
	 */
	private Boolean sendToMerchant;

	/**
	 * Default Constructor
	 */
	public InvoicingNotification() {
	}


	/**
	 * Setter for subject
	 */
	public InvoicingNotification setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	/**
	 * Getter for subject
	 */
	public String getSubject() {
		return this.subject;
	}


	/**
	 * Setter for note
	 */
	public InvoicingNotification setNote(String note) {
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
	 * Setter for sendToMerchant
	 */
	public InvoicingNotification setSendToMerchant(Boolean sendToMerchant) {
		this.sendToMerchant = sendToMerchant;
		return this;
	}

	/**
	 * Getter for sendToMerchant
	 */
	public Boolean getSendToMerchant() {
		return this.sendToMerchant;
	}


}
