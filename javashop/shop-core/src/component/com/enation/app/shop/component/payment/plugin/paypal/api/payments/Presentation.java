package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalModel;


public class Presentation  extends PayPalModel {

	/**
	 * A label that overrides the business name in the PayPal account on the PayPal pages. Character length and limitations: 127 single-byte alphanumeric characters.
	 */
	private String brandName;

	/**
	 * A URL to logo image. Allowed vaues: `.gif`, `.jpg`, or `.png`. Limit the image to 190 pixels wide by 60 pixels high. PayPal crops images that are larger. PayPal places your logo image at the top of the cart review area. PayPal recommends that you store the image on a secure (https) server. Otherwise, web browsers display a message that checkout pages contain non-secure items. Character length and limit: 127 single-byte alphanumeric characters.
	 */
	private String logoImage;

	/**
	 * Locale of pages displayed by PayPal payment experience. Allowed values: `AU`, `AT`, `BE`, `BR`, `CA`, `CH`, `CN`, `DE`, `ES`, `GB`, `FR`, `IT`, `NL`, `PL`, `PT`, `RU`, `US`. The following 5-character codes are also allowed for languages in specific countries: `da_DK`, `he_IL`, `id_ID`, `ja_JP`, `no_NO`, `pt_BR`, `ru_RU`, `sv_SE`, `th_TH`, `tr_TR`, `zh_CN`, `zh_HK`, `zh_TW`.
	 */
	private String localeCode;

	/**
	 * Default Constructor
	 */
	public Presentation() {
	}


	/**
	 * Setter for brandName
	 */
	public Presentation setBrandName(String brandName) {
		this.brandName = brandName;
		return this;
	}

	/**
	 * Getter for brandName
	 */
	public String getBrandName() {
		return this.brandName;
	}


	/**
	 * Setter for logoImage
	 */
	public Presentation setLogoImage(String logoImage) {
		this.logoImage = logoImage;
		return this;
	}

	/**
	 * Getter for logoImage
	 */
	public String getLogoImage() {
		return this.logoImage;
	}


	/**
	 * Setter for localeCode
	 */
	public Presentation setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
		return this;
	}

	/**
	 * Getter for localeCode
	 */
	public String getLocaleCode() {
		return this.localeCode;
	}
	
}
