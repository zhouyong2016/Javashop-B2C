package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class ErrorDetails  extends PayPalModel {

	/**
	 * Name of the field that caused the error.
	 */
	private String field;

	/**
	 * Reason for the error.
	 */
	private String issue;

	/**
	 * Reference ID of the purchase_unit associated with this error
	 */
	private String purchaseUnitReferenceId;
	
	/**
	 * PayPal internal error code.
	 */
	private String code;
	

	/**
	 * Default Constructor
	 */
	public ErrorDetails() {
	}

	/**
	 * Parameterized Constructor
	 */
	public ErrorDetails(String field, String issue) {
		this.field = field;
		this.issue = issue;
	}


	/**
	 * Setter for field
	 */
	public ErrorDetails setField(String field) {
		this.field = field;
		return this;
	}

	/**
	 * Getter for field
	 */
	public String getField() {
		return this.field;
	}


	/**
	 * Setter for issue
	 */
	public ErrorDetails setIssue(String issue) {
		this.issue = issue;
		return this;
	}

	/**
	 * Getter for issue
	 */
	public String getIssue() {
		return this.issue;
	}


	public String getPurchaseUnitReferenceId() {
		return purchaseUnitReferenceId;
	}

	public ErrorDetails setPurchaseUnitReferenceId(String purchaseUnitReferenceId) {
		this.purchaseUnitReferenceId = purchaseUnitReferenceId;
		return this;
	}

	public String getCode() {
		return code;
	}

	public ErrorDetails setCode(String code) {
		this.code = code;
		return this;
	}
	
}
