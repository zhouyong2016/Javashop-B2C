package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.List;

public class TransactionBase extends CartBase {

	/**
	 * List of financial transactions (Sale, Authorization, Capture, Refund) related to the payment.
	 */
	private List<RelatedResources> relatedResources;

	/**
	 * Identifier to the purchase unit corresponding to this sale transaction.
	 */
	private String purchaseUnitReferenceId;


	/**
	 * Default Constructor
	 */
	public TransactionBase() {
	}


	/**
	 * Setter for relatedResources
	 */
	public TransactionBase setRelatedResources(List<RelatedResources> relatedResources) {
		this.relatedResources = relatedResources;
		return this;
	}

	/**
	 * Getter for relatedResources
	 */
	public List<RelatedResources> getRelatedResources() {
		return this.relatedResources;
	}
	
	public String getPurchaseUnitReferenceId() {
		return purchaseUnitReferenceId;
	}


	public TransactionBase setPurchaseUnitReferenceId(String purchaseUnitReferenceId) {
		this.purchaseUnitReferenceId = purchaseUnitReferenceId;
		return this;
	}


}
