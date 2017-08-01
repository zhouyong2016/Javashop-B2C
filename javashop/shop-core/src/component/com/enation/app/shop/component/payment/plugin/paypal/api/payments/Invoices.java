package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.ArrayList;
import java.util.List;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class Invoices  extends PayPalModel {

	/**
	 * 
	 */
	private int totalCount;

	/**
	 * List of invoices belonging to a merchant.
	 */
	private List<Invoice> invoices;

	/**
	 * Default Constructor
	 */
	public Invoices() {
		invoices = new ArrayList<Invoice>();
	}


	/**
	 * Setter for totalCount
	 */
	public Invoices setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		return this;
	}

	/**
	 * Getter for totalCount
	 */
	public int getTotalCount() {
		return this.totalCount;
	}


	/**
	 * Setter for invoices
	 */
	public Invoices setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
		return this;
	}

	/**
	 * Getter for invoices
	 */
	public List<Invoice> getInvoices() {
		return this.invoices;
	}


}
