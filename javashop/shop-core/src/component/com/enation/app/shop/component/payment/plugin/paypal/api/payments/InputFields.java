package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class InputFields  extends PayPalModel {

	/**
	 * Enables the buyer to enter a note to the merchant on the PayPal page during checkout.
	 */
	private Boolean allowNote;

	/**
	 * Determines whether or not PayPal displays shipping address fields on the experience pages. Allowed values: `0`, `1`, or `2`. When set to `0`, PayPal displays the shipping address on the PayPal pages. When set to `1`, PayPal does not display shipping address fields whatsoever. When set to `2`, if you do not pass the shipping address, PayPal obtains it from the buyer's account profile. For digital goods, this field is required, and you must set it to `1`. 
	 */
	private int noShipping;

	/**
	 * Determines if the PayPal pages should display the shipping address supplied in this call, rather than the shipping address on file with PayPal for this buyer. Displaying the address on file does not allow the buyer to edit the address. Allowed values: `0` or `1`. When set to `0`, the PayPal pages should display the address on file. When set to `1`, the PayPal pages should display the addresses supplied in this call instead of the address from the buyer's PayPal account.
	 */
	private int addressOverride;

	/**
	 * Default Constructor
	 */
	public InputFields() {
	}


	/**
	 * Setter for allowNote
	 */
	public InputFields setAllowNote(Boolean allowNote) {
		this.allowNote = allowNote;
		return this;
	}

	/**
	 * Getter for allowNote
	 */
	public Boolean getAllowNote() {
		return this.allowNote;
	}


	/**
	 * Setter for noShipping
	 */
	public InputFields setNoShipping(int noShipping) {
		this.noShipping = noShipping;
		return this;
	}

	/**
	 * Getter for noShipping
	 */
	public int getNoShipping() {
		return this.noShipping;
	}


	/**
	 * Setter for addressOverride
	 */
	public InputFields setAddressOverride(int addressOverride) {
		this.addressOverride = addressOverride;
		return this;
	}

	/**
	 * Getter for addressOverride
	 */
	public int getAddressOverride() {
		return this.addressOverride;
	}
	
}
