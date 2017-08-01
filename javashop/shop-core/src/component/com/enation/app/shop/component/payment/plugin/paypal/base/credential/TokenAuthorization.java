package com.enation.app.shop.component.payment.plugin.paypal.base.credential;

/**
 * TokenAuthorization encapsulates third party token authorization. Used for
 * MERCHANT or PLATFORM APIs
 */
public class TokenAuthorization implements ThirdPartyAuthorization {

	/**
	 * Access token
	 */
	private String accessToken;

	/**
	 * Token secret
	 */
	private String tokenSecret;

	/**
	 * Token based third party authorization used in MERCHANT or PLATFORM APIs
	 * 
	 * @param accessToken
	 *            Access Token
	 * @param tokenSecret
	 *            Token Secret
	 */
	public TokenAuthorization(String accessToken, String tokenSecret) {
		super();
		if (accessToken == null || accessToken.trim().length() == 0
				|| tokenSecret == null || tokenSecret.trim().length() == 0) {
			throw new IllegalArgumentException(
					"TokenAuthorization arguments cannot be empty or null");
		}
		this.accessToken = accessToken;
		this.tokenSecret = tokenSecret;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return the tokenSecret
	 */
	public String getTokenSecret() {
		return tokenSecret;
	}

}
