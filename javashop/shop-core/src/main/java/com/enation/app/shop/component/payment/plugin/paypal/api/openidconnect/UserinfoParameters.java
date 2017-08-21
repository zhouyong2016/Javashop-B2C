package com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Class UserinfoParameters
 *
 */
public class UserinfoParameters {

	/**
	 * Schema
	 */
	private static final String SCHEMA = "schema";

	/**
	 * Access Token
	 */
	private static final String ACCESSTOKEN = "access_token";

	// Map backing QueryParameters intended to processed
	// by SDK library 'RESTUtil'
	private Map<String, String> containerMap;

	/**
	 * 
	 */
	public UserinfoParameters() {
		containerMap = new HashMap<String, String>();
		containerMap.put(SCHEMA, "openid");
	}

	/**
	 * @return the containerMap
	 */
	public Map<String, String> getContainerMap() {
		return containerMap;
	}

	/**
	 * Set the accessToken
	 * 
	 * @param accessToken
	 */
	public void setAccessToken(String accessToken) {
		try {
			containerMap.put(ACCESSTOKEN, URLEncoder.encode(accessToken, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
	}

}
