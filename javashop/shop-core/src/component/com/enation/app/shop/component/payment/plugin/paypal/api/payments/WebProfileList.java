package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.ArrayList;
import java.util.List;

public class WebProfileList  extends ArrayList<WebProfile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<WebProfile> webProfiles = null;
	
	/**
	 * Default Constructor
	 */
	public WebProfileList() {
		webProfiles = new ArrayList<WebProfile>();
	}


	/**
	 * Setter for items
	 */
	public List<WebProfile> setWebProfileList(List<WebProfile> webProfiles) {
		this.webProfiles = webProfiles;
		return this.webProfiles;
	}

	/**
	 * Getter for items
	 */
	public List<WebProfile> getWebProfileLists() {
		return this.webProfiles;
	}
}
