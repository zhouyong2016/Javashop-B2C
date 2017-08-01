package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class AgreementTransaction  extends PayPalModel {

	/**
	 * Id corresponding to this transaction.
	 */
	private String transactionId;

	/**
	 * State of the subscription at this time.
	 */
	private String status;

	/**
	 * Type of transaction, usually Recurring Payment.
	 */
	private String transactionType;

	/**
	 * Amount for this transaction.
	 */
	private Currency amount;

	/**
	 * Fee amount for this transaction.
	 */
	private Currency feeAmount;

	/**
	 * Net amount for this transaction.
	 */
	private Currency netAmount;

	/**
	 * Email id of payer.
	 */
	private String payerEmail;

	/**
	 * Business name of payer.
	 */
	private String payerName;

	/**
	 * @deprecated use timeUpdated instead.
	 * Time at which this transaction happened.
	 */
	private String timeUpdated;

	/**
	 * Time zone of time_updated field.
	 */
	private String timeZone;
	
	/**
	 * Time at which this transaction happened.
	 */
	private String timeStamp;

	/**
	 * Default Constructor
	 */
	public AgreementTransaction() {
	}

	/**
	 * Parameterized Constructor
	 */
	public AgreementTransaction(Currency amount, Currency feeAmount, Currency netAmount) {
		this.amount = amount;
		this.feeAmount = feeAmount;
		this.netAmount = netAmount;
	}


	/**
	 * Setter for transactionId
	 */
	public AgreementTransaction setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	/**
	 * Getter for transactionId
	 */
	public String getTransactionId() {
		return this.transactionId;
	}


	/**
	 * Setter for status
	 */
	public AgreementTransaction setStatus(String status) {
		this.status = status;
		return this;
	}

	/**
	 * Getter for status
	 */
	public String getStatus() {
		return this.status;
	}


	/**
	 * Setter for transactionType
	 */
	public AgreementTransaction setTransactionType(String transactionType) {
		this.transactionType = transactionType;
		return this;
	}

	/**
	 * Getter for transactionType
	 */
	public String getTransactionType() {
		return this.transactionType;
	}


	/**
	 * Setter for amount
	 */
	public AgreementTransaction setAmount(Currency amount) {
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
	 * Setter for feeAmount
	 */
	public AgreementTransaction setFeeAmount(Currency feeAmount) {
		this.feeAmount = feeAmount;
		return this;
	}

	/**
	 * Getter for feeAmount
	 */
	public Currency getFeeAmount() {
		return this.feeAmount;
	}


	/**
	 * Setter for netAmount
	 */
	public AgreementTransaction setNetAmount(Currency netAmount) {
		this.netAmount = netAmount;
		return this;
	}

	/**
	 * Getter for netAmount
	 */
	public Currency getNetAmount() {
		return this.netAmount;
	}


	/**
	 * Setter for payerEmail
	 */
	public AgreementTransaction setPayerEmail(String payerEmail) {
		this.payerEmail = payerEmail;
		return this;
	}

	/**
	 * Getter for payerEmail
	 */
	public String getPayerEmail() {
		return this.payerEmail;
	}


	/**
	 * Setter for payerName
	 */
	public AgreementTransaction setPayerName(String payerName) {
		this.payerName = payerName;
		return this;
	}

	/**
	 * Getter for payerName
	 */
	public String getPayerName() {
		return this.payerName;
	}


	/**
	 * @deprecated use setTimeStamp instead.
	 * Setter for timeUpdated
	 */
	public AgreementTransaction setTimeUpdated(String timeUpdated) {
		this.timeStamp = timeUpdated;
		return this;
	}

	/**
	 * @deprecated use getTimeStamp instead.
	 * Getter for timeUpdated
	 */
	public String getTimeUpdated() {
		return this.timeStamp;
	}


	/**
	 * Setter for timeZone
	 */
	public AgreementTransaction setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	/**
	 * Getter for timeZone
	 */
	public String getTimeZone() {
		return this.timeZone;
	}



	/**
	 * Setter for timeStamp
	 */
	public AgreementTransaction setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
		return this;
	}

	/**
	 * Getter for timeStamp
	 */
	public String getTimeStamp() {
		return this.timeStamp;
	}

}
