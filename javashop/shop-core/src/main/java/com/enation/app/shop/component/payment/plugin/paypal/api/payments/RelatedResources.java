package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class RelatedResources  extends PayPalModel {

	/**
	 * A sale transaction
	 */
	private Sale sale;

	/**
	 * An order transaction
	 */
	private Authorization authorization;

	/**
	 * A capture transaction
	 */
	private Capture capture;

	/**
	 * A refund transaction
	 */
	private Refund refund;
	
	/**
	 * An order transaction
	 */
	private Order order;

	/**
	 * Default Constructor
	 */
	public RelatedResources() {
	}


	/**
	 * Setter for sale
	 */
	public RelatedResources setSale(Sale sale) {
		this.sale = sale;
		return this;
	}

	/**
	 * Getter for sale
	 */
	public Sale getSale() {
		return this.sale;
	}


	/**
	 * Setter for authorization
	 */
	public RelatedResources setAuthorization(Authorization authorization) {
		this.authorization = authorization;
		return this;
	}

	/**
	 * Getter for authorization
	 */
	public Authorization getAuthorization() {
		return this.authorization;
	}


	/**
	 * Setter for capture
	 */
	public RelatedResources setCapture(Capture capture) {
		this.capture = capture;
		return this;
	}

	/**
	 * Getter for capture
	 */
	public Capture getCapture() {
		return this.capture;
	}


	/**
	 * Setter for refund
	 */
	public RelatedResources setRefund(Refund refund) {
		this.refund = refund;
		return this;
	}

	/**
	 * Getter for refund
	 */
	public Refund getRefund() {
		return this.refund;
	}
	
	/**
	 * Setter for order
	 */
	public RelatedResources setOrder(Order order) {
		this.order = order;
		return this;
	}

	/**
	 * Getter for order
	 */
	public Order getOrder() {
		return this.order;
	}
}
