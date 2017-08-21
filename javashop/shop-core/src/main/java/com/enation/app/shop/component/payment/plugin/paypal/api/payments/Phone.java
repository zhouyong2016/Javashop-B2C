package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class Phone  extends PayPalModel {

	/**
	 * Country code (from in E.164 format)
	 */
	private String countryCode;

	/**
	 * In-country phone number (from in E.164 format)
	 */
	private String nationalNumber;

	/**
	 * Phone extension
	 */
	private String extension;

	/**
	 * Default Constructor
	 */
	public Phone() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Phone(String countryCode, String nationalNumber) {
		this.countryCode = countryCode;
		this.nationalNumber = nationalNumber;
	}


	/**
	 * Setter for countryCode
	 */
	public Phone setCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	/**
	 * Getter for countryCode
	 */
	public String getCountryCode() {
		return this.countryCode;
	}


	/**
	 * Setter for nationalNumber
	 */
	public Phone setNationalNumber(String nationalNumber) {
		this.nationalNumber = nationalNumber;
		return this;
	}

	/**
	 * Getter for nationalNumber
	 */
	public String getNationalNumber() {
		return this.nationalNumber;
	}


	/**
	 * Setter for extension
	 */
	public Phone setExtension(String extension) {
		this.extension = extension;
		return this;
	}

	/**
	 * Getter for extension
	 */
	public String getExtension() {
		return this.extension;
	}


}
