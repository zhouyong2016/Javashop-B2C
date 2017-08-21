package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class Details  extends PayPalModel {

	/**
	 * Amount being charged for shipping.
	 */
	private String shipping;

	/**
	 * Sub-total (amount) of items being paid for.
	 */
	private String subtotal;

	/**
	 * Amount being charged as tax.
	 */
	private String tax;

	/**
	 * Fee charged by PayPal. In case of a refund, this is the fee amount refunded to the original receipient of the payment.
	 */
	private String fee;

	/**
	 * Amount being charged as handling fee.
	 */
	private String handlingFee;

	/**
	 * Amount being charged as gift wrap fee.
	 */
	private String giftWrap;

	/**
	 * Amount being charged for the insurance fee.
	 */
	private String insurance;
	
	/**
	 * Amount being discounted for the shipping fee.
	 */
	private String shippingDiscount;

	/**
	 * Default Constructor
	 */
	public Details() {
	}


	/**
	 * Setter for shipping
	 */
	public Details setShipping(String shipping) {
		this.shipping = shipping;
		return this;
	}

	/**
	 * Getter for shipping
	 */
	public String getShipping() {
		return this.shipping;
	}


	/**
	 * Setter for subtotal
	 */
	public Details setSubtotal(String subtotal) {
		this.subtotal = subtotal;
		return this;
	}

	/**
	 * Getter for subtotal
	 */
	public String getSubtotal() {
		return this.subtotal;
	}


	/**
	 * Setter for tax
	 */
	public Details setTax(String tax) {
		this.tax = tax;
		return this;
	}

	/**
	 * Getter for tax
	 */
	public String getTax() {
		return this.tax;
	}


	/**
	 * Setter for fee
	 */
	public Details setFee(String fee) {
		this.fee = fee;
		return this;
	}

	/**
	 * Getter for fee
	 */
	public String getFee() {
		return this.fee;
	}


	/**
	 * Setter for handlingFee
	 */
	public Details setHandlingFee(String handlingFee) {
		this.handlingFee = handlingFee;
		return this;
	}

	/**
	 * Getter for handlingFee
	 */
	public String getHandlingFee() {
		return this.handlingFee;
	}


	/**
	 * Setter for giftWrap
	 */
	public Details setGiftWrap(String giftWrap) {
		this.giftWrap = giftWrap;
		return this;
	}

	/**
	 * Getter for giftWrap
	 */
	public String getGiftWrap() {
		return this.giftWrap;
	}

	/**
	 * Getter for insurance
	 */
	public String getInsurance() {
		return insurance;
	}

	/**
	 * Setter for insurance
	 */
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	/**
	 * Getter for shipping discount
	 */
	public String getShippingDiscount() {
		return shippingDiscount;
	}

	/**
	 * Setter for shipping discount
	 */
	public void setShippingDiscount(String shippingDiscount) {
		this.shippingDiscount = shippingDiscount;
	}

}
