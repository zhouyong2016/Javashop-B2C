package com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;

public class Address extends PayPalModel {

	/**
	 * Full street address component, which may include house number, street name.
	 */
	private String streetAddress;
	
	/**
	 * City or locality component.
	 */
	private String locality;
	
	/**
	 * State, province, prefecture or region component.
	 */
	private String region;
	
	/**
	 * Zip code or postal code component.
	 */
	private String postalCode;
	
	/**
	 * Country name component.
	 */
	private String country;
	
	/**
	 * Default Constructor
	 */
	public Address() {
	}

	/**
	 * Setter for streetAddress
	 * @param streetAddress 
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
 	}
 	
 	/**
	 * Getter for streetAddress
	 */
	public String getStreetAddress() {
		return this.streetAddress;
	}
	
	/**
	 * Setter for locality
	 */
	public void setLocality(String locality) {
		this.locality = locality;
 	}
 	
 	/**
	 * Getter for locality
	 */
	public String getLocality() {
		return this.locality;
	}
	
	/**
	 * Setter for region
	 */
	public void setRegion(String region) {
		this.region = region;
 	}
 	
 	/**
	 * Getter for region
	 */
	public String getRegion() {
		return this.region;
	}
	
	/**
	 * Setter for postalCode
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
 	}
 	
 	/**
	 * Getter for postalCode
	 */
	public String getPostalCode() {
		return this.postalCode;
	}
	
	/**
	 * Setter for country
	 */
	public void setCountry(String country) {
		this.country = country;
 	}
 	
 	/**
	 * Getter for country
	 */
	public String getCountry() {
		return this.country;
	}

}