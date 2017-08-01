package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.JSONFormatter;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class Billing extends PayPalModel {

	/**
	 * Identifier of the instrument in PayPal Wallet
	 */
	private String billingAgreementId;

	/**
	 * Default Constructor
	 */
	public Billing() {
	}


	/**
	 * Setter for billingAgreementId
	 */
	public Billing setBillingAgreementId(String billingAgreementId) {
		this.billingAgreementId = billingAgreementId;
		return this;
	}

	/**
	 * Getter for billingAgreementId
	 */
	public String getBillingAgreementId() {
		return this.billingAgreementId;
	}

}
