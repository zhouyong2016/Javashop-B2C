package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.ArrayList;
import java.util.List;

public class Transaction extends TransactionBase {

	/**
	 * Additional transactions for complex payment scenarios.
	 */
	private List<Transaction> transactions;

	/**
	 * Default Constructor
	 */
	public Transaction() {
		transactions = new ArrayList<Transaction>();
	}


	/**
	 * Setter for transactions
	 */
	public Transaction setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
		return this;
	}

	/**
	 * Getter for transactions
	 */
	public List<Transaction> getTransactions() {
		return this.transactions;
	}


}
