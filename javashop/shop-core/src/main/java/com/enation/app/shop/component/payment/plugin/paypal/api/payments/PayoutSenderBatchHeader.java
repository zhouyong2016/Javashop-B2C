package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class PayoutSenderBatchHeader extends PayPalModel  {

	/**
	 * Sender-created ID for tracking the batch payout in an accounting system. 30 characters max.
	 */
	private String senderBatchId;

	/**
	 * The subject line text for the email that PayPal sends when a payout item is completed. (The subject line is the same for all recipients.) Maximum of 255 single-byte alphanumeric characters.
	 */
	private String emailSubject;

	/**
	 * The type of ID for a payment receiver. If this field is provided, the payout items without a `recipient_type` will use the provided value. If this field is not provided, each payout item must include a value for the `recipient_type`. 
	 */
	private String recipientType;

	/**
	 * Default Constructor
	 */
	public PayoutSenderBatchHeader() {
	}


	/**
	 * Setter for senderBatchId
	 */
	public PayoutSenderBatchHeader setSenderBatchId(String senderBatchId) {
		this.senderBatchId = senderBatchId;
		return this;
	}

	/**
	 * Getter for senderBatchId
	 */
	public String getSenderBatchId() {
		return this.senderBatchId;
	}


	/**
	 * Setter for emailSubject
	 */
	public PayoutSenderBatchHeader setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
		return this;
	}

	/**
	 * Getter for emailSubject
	 */
	public String getEmailSubject() {
		return this.emailSubject;
	}


	/**
	 * Setter for recipientType
	 */
	public PayoutSenderBatchHeader setRecipientType(String recipientType) {
		this.recipientType = recipientType;
		return this;
	}

	/**
	 * Getter for recipientType
	 */
	public String getRecipientType() {
		return this.recipientType;
	}
	
}
