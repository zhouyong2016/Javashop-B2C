package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class AgreementDetails  extends PayPalModel {

	/**
	 * The outstanding balance for this agreement.
	 */
	private Currency outstandingBalance;

	/**
	 * Number of cycles remaining for this agreement.
	 */
	private String cyclesRemaining;

	/**
	 * Number of cycles completed for this agreement.
	 */
	private String cyclesCompleted;

	/**
	 * The next billing date for this agreement, represented as 2014-02-19T10:00:00Z format.
	 */
	private String nextBillingDate;

	/**
	 * Last payment date for this agreement, represented as 2014-06-09T09:42:31Z format.
	 */
	private String lastPaymentDate;

	/**
	 * Last payment amount for this agreement.
	 */
	private Currency lastPaymentAmount;

	/**
	 * Last payment date for this agreement, represented as 2015-02-19T10:00:00Z format.
	 */
	private String finalPaymentDate;

	/**
	 * Total number of failed payments for this agreement.
	 */
	private String failedPaymentCount;

	/**
	 * Default Constructor
	 */
	public AgreementDetails() {
	}


	/**
	 * Setter for outstandingBalance
	 */
	public AgreementDetails setOutstandingBalance(Currency outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
		return this;
	}

	/**
	 * Getter for outstandingBalance
	 */
	public Currency getOutstandingBalance() {
		return this.outstandingBalance;
	}


	/**
	 * Setter for cyclesRemaining
	 */
	public AgreementDetails setCyclesRemaining(String cyclesRemaining) {
		this.cyclesRemaining = cyclesRemaining;
		return this;
	}

	/**
	 * Getter for cyclesRemaining
	 */
	public String getCyclesRemaining() {
		return this.cyclesRemaining;
	}


	/**
	 * Setter for cyclesCompleted
	 */
	public AgreementDetails setCyclesCompleted(String cyclesCompleted) {
		this.cyclesCompleted = cyclesCompleted;
		return this;
	}

	/**
	 * Getter for cyclesCompleted
	 */
	public String getCyclesCompleted() {
		return this.cyclesCompleted;
	}


	/**
	 * Setter for nextBillingDate
	 */
	public AgreementDetails setNextBillingDate(String nextBillingDate) {
		this.nextBillingDate = nextBillingDate;
		return this;
	}

	/**
	 * Getter for nextBillingDate
	 */
	public String getNextBillingDate() {
		return this.nextBillingDate;
	}


	/**
	 * Setter for lastPaymentDate
	 */
	public AgreementDetails setLastPaymentDate(String lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
		return this;
	}

	/**
	 * Getter for lastPaymentDate
	 */
	public String getLastPaymentDate() {
		return this.lastPaymentDate;
	}


	/**
	 * Setter for lastPaymentAmount
	 */
	public AgreementDetails setLastPaymentAmount(Currency lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
		return this;
	}

	/**
	 * Getter for lastPaymentAmount
	 */
	public Currency getLastPaymentAmount() {
		return this.lastPaymentAmount;
	}


	/**
	 * Setter for finalPaymentDate
	 */
	public AgreementDetails setFinalPaymentDate(String finalPaymentDate) {
		this.finalPaymentDate = finalPaymentDate;
		return this;
	}

	/**
	 * Getter for finalPaymentDate
	 */
	public String getFinalPaymentDate() {
		return this.finalPaymentDate;
	}


	/**
	 * Setter for failedPaymentCount
	 */
	public AgreementDetails setFailedPaymentCount(String failedPaymentCount) {
		this.failedPaymentCount = failedPaymentCount;
		return this;
	}

	/**
	 * Getter for failedPaymentCount
	 */
	public String getFailedPaymentCount() {
		return this.failedPaymentCount;
	}
}
