package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.List;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class PayoutBatch extends PayPalModel  {

	/**
	 * A batch header that includes the generated batch status.
	 */
	private PayoutBatchHeader batchHeader;

	/**
	 * Array of the items in a batch payout.
	 */
	private List<PayoutItemDetails> items;

	/**
	 * 
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public PayoutBatch() {
	}

	/**
	 * Parameterized Constructor
	 */
	public PayoutBatch(PayoutBatchHeader batchHeader, List<PayoutItemDetails> items) {
		this.batchHeader = batchHeader;
		this.items = items;
	}


	/**
	 * Setter for batchHeader
	 */
	public PayoutBatch setBatchHeader(PayoutBatchHeader batchHeader) {
		this.batchHeader = batchHeader;
		return this;
	}

	/**
	 * Getter for batchHeader
	 */
	public PayoutBatchHeader getBatchHeader() {
		return this.batchHeader;
	}


	/**
	 * Setter for items
	 */
	public PayoutBatch setItems(List<PayoutItemDetails> items) {
		this.items = items;
		return this;
	}

	/**
	 * Getter for items
	 */
	public List<PayoutItemDetails> getItems() {
		return this.items;
	}
	
	/**
	 * Setter for links
	 */
	public PayoutBatch setLinks(List<Links> links) {
		this.links = links;
		return this;
	}

	/**
	 * Getter for links
	 */
	public List<Links> getLinks() {
		return this.links;
	}
}
