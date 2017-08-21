package com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.OAuthTokenCredential;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;

/**
 * Class Tokeninfo
 *
 */
public class Tokeninfo extends PayPalResource {

	/**
	 * OPTIONAL, if identical to the scope requested by the client; otherwise,
	 * REQUIRED.
	 */
	private String scope;

	/**
	 * The access token issued by the authorization server.
	 */
	private String accessToken;

	/**
	 * The refresh token, which can be used to obtain new access tokens using
	 * the same authorization grant as described in OAuth2.0 RFC6749 in Section
	 * 6.
	 */
	private String refreshToken;

	/**
	 * The type of the token issued as described in OAuth2.0 RFC6749 (Section
	 * 7.1). Value is case insensitive.
	 */
	private String tokenType;

	/**
	 * The lifetime in seconds of the access token.
	 */
	private Integer expiresIn;

	/**
	 * APP ID associated with this token
	 */
	private String appId;
	
	/**
	 * Default Constructor
	 */
	public Tokeninfo() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Tokeninfo(String accessToken, String tokenType, Integer expiresIn) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
	}

	/**
	 * Setter for scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Getter for scope
	 */
	public String getScope() {
		return this.scope;
	}

	/**
	 * Setter for accessToken
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Getter for just accessToken without type (e.g., "EEwJ6tF9x5WCIZDYzyZGaz6Khbw7raYRIBV_WxVvgmsG")
	 */
	public String getAccessToken() {
		return this.accessToken;
	}
	
	/**
	 * Getter for accessToken with token type (e.g., "Bearer: EEwJ6tF9x5WCIZDYzyZGaz6Khbw7raYRIBV_WxVvgmsG")
	 */
	public String getAccessTokenWithType() {
		return this.tokenType + " " + this.accessToken;
	}

	/**
	 * Setter for refreshToken
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Getter for refreshToken
	 */
	public String getRefreshToken() {
		return this.refreshToken;
	}

	/**
	 * Setter for tokenType
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * Getter for tokenType
	 */
	public String getTokenType() {
		return this.tokenType;
	}

	/**
	 * Setter for expiresIn
	 */
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * Getter for expiresIn
	 */
	public Integer getExpiresIn() {
		return this.expiresIn;
	}

	/**
	 * Setter for appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * Getter for appId
	 */
	public String getAppId() {
		return this.appId;
	}

	/**
	 * Creates an Access Token from an Authorization Code.
	 * 
	 * @param createFromAuthorizationCodeParameters
	 *            Query parameters used for API call
	 * @return Tokeninfo
	 * @throws PayPalRESTException
	 */
	public static Tokeninfo createFromAuthorizationCode(
			CreateFromAuthorizationCodeParameters createFromAuthorizationCodeParameters)
			throws PayPalRESTException {
		return createFromAuthorizationCode(null,
				createFromAuthorizationCodeParameters);
	}

	/**
	 * Creates an Access Token from an Authorization Code.
	 * 
	 * @param apiContext
	 *            {@link APIContext} to be used for the call.
	 * @param createFromAuthorizationCodeParameters
	 *            Query parameters used for API call
	 * @return Tokeninfo
	 * @throws PayPalRESTException
	 */
	public static Tokeninfo createFromAuthorizationCode(
			APIContext apiContext,
			CreateFromAuthorizationCodeParameters createFromAuthorizationCodeParameters)
			throws PayPalRESTException {
		String pattern = "v1/identity/openidconnect/tokenservice?grant_type={0}&code={1}&redirect_uri={2}";
		Object[] parameters = new Object[] { createFromAuthorizationCodeParameters };
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		return createFromAuthorizationCodeParameters(apiContext, createFromAuthorizationCodeParameters, resourcePath);
	}

	/**
	 * Creates an Access and a Refresh Tokens from an Authorization Code for future payment.
	 * 
	 * @param apiContext
	 *            {@link APIContext} to be used for the call.
	 * @param createFromAuthorizationCodeParameters
	 *            Query parameters used for API call
	 * @return Tokeninfo
	 * @throws PayPalRESTException
	 */
	public static Tokeninfo createFromAuthorizationCodeForFpp(
			APIContext apiContext,
			CreateFromAuthorizationCodeParameters createFromAuthorizationCodeParameters)
			throws PayPalRESTException {
		String pattern = "v1/oauth2/token?grant_type=authorization_code&response_type=token&redirect_uri=urn:ietf:wg:oauth:2.0:oob&code={0}";
		Object[] parameters = new Object[] { createFromAuthorizationCodeParameters.getContainerMap().get("code") };
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		return createFromAuthorizationCodeParameters(apiContext, createFromAuthorizationCodeParameters, resourcePath);
	}
	
	private static Tokeninfo createFromAuthorizationCodeParameters(
			APIContext apiContext,
			CreateFromAuthorizationCodeParameters createFromAuthorizationCodeParameters,
			String resourcePath)
			throws PayPalRESTException {
		String payLoad = resourcePath.substring(resourcePath.indexOf('?') + 1);
		resourcePath = resourcePath.substring(0, resourcePath.indexOf('?'));
		Map<String, String> headersMap = new HashMap<String, String>();
		if (apiContext == null) {
			apiContext = new APIContext();
		}
		apiContext.setMaskRequestId(true);
		if (createFromAuthorizationCodeParameters.getClientID() == null
				|| createFromAuthorizationCodeParameters.getClientID().trim()
						.length() <= 0
				|| createFromAuthorizationCodeParameters.getClientSecret() == null
				|| createFromAuthorizationCodeParameters.getClientSecret()
						.trim().length() <= 0) {
			throw new PayPalRESTException(
					"ClientID and ClientSecret not set in CreateFromAuthorizationCodeParameters");
		} else {
			OAuthTokenCredential oauthTokenCredential = new OAuthTokenCredential(
					createFromAuthorizationCodeParameters.getClientID(),
					createFromAuthorizationCodeParameters.getClientSecret(),
					apiContext.getConfigurationMap());
			String authorizationHeader = oauthTokenCredential
					.getAuthorizationHeader();
			headersMap.put(Constants.AUTHORIZATION_HEADER, authorizationHeader);
		}
		headersMap.put(Constants.HTTP_CONTENT_TYPE_HEADER,
				Constants.HTTP_CONFIG_DEFAULT_CONTENT_TYPE);
		headersMap.put(Constants.HTTP_ACCEPT_HEADER,
				Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setHTTPHeaders(headersMap);
		return configureAndExecute(apiContext, HttpMethod.POST,
				resourcePath, payLoad, Tokeninfo.class);
	}
	
	/**
	 * Creates an Access Token from an Refresh Token.
	 * 
	 * @param createFromRefreshTokenParameters
	 *            Query parameters used for API call
	 * @return Tokeninfo
	 * @throws PayPalRESTException
	 */
	public Tokeninfo createFromRefreshToken(
			CreateFromRefreshTokenParameters createFromRefreshTokenParameters)
			throws PayPalRESTException {
		return createFromRefreshToken(null, createFromRefreshTokenParameters);
	}

	/**
	 * Creates an Access Token from an Refresh Token.
	 * 
	 * @param apiContext
	 *            {@link APIContext} to be used for the call.
	 * @param createFromRefreshTokenParameters
	 *            Query parameters used for API call
	 * @return Tokeninfo
	 * @throws PayPalRESTException
	 */
	public Tokeninfo createFromRefreshToken(APIContext apiContext,
			CreateFromRefreshTokenParameters createFromRefreshTokenParameters)
			throws PayPalRESTException {
		String pattern = "v1/identity/openidconnect/tokenservice?grant_type={0}&refresh_token={1}&scope={2}&client_id={3}&client_secret={4}";
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.putAll(createFromRefreshTokenParameters.getContainerMap());
		try {
			paramsMap.put("refresh_token", URLEncoder.encode(getRefreshToken(),
					Constants.ENCODING_FORMAT));
		} catch (UnsupportedEncodingException ex) {
			// Ignore
		}
		Object[] parameters = new Object[] { paramsMap };
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = resourcePath.substring(resourcePath.indexOf('?') + 1);
		resourcePath = resourcePath.substring(0, resourcePath.indexOf('?'));
		if (apiContext == null) {
			apiContext = new APIContext();
		}
		apiContext.setMaskRequestId(true);
		Map<String, String> headersMap = new HashMap<String, String>();
		if (createFromRefreshTokenParameters.getClientID() == null
				|| createFromRefreshTokenParameters.getClientID().trim()
						.length() <= 0
				|| createFromRefreshTokenParameters.getClientSecret() == null
				|| createFromRefreshTokenParameters.getClientSecret()
						.trim().length() <= 0) {
			throw new PayPalRESTException(
					"ClientID and ClientSecret not set in CreateFromRefreshTokenParameters");
		} else {
			OAuthTokenCredential oauthTokenCredential = new OAuthTokenCredential(
					createFromRefreshTokenParameters.getClientID(),
					createFromRefreshTokenParameters.getClientSecret(),
					apiContext.getConfigurationMap());
			String authorizationHeader = oauthTokenCredential
					.getAuthorizationHeader();
			headersMap.put(Constants.AUTHORIZATION_HEADER, authorizationHeader);
		}
		headersMap.put(Constants.HTTP_CONTENT_TYPE_HEADER,
				Constants.HTTP_CONFIG_DEFAULT_CONTENT_TYPE);
		
		apiContext.setHTTPHeaders(headersMap);
		return configureAndExecute(apiContext, HttpMethod.POST,
				resourcePath, payLoad, Tokeninfo.class);
	}

	/**
	 * Creates an Access Token from an Refresh Token for future payment.
	 * 
	 * @param apiContext
	 *            {@link APIContext} to be used for the call.
	 * @return Tokeninfo
	 * @throws PayPalRESTException
	 */
	public Tokeninfo createFromRefreshTokenForFpp(APIContext apiContext)
			throws PayPalRESTException {
		if (getRefreshToken() == null || getRefreshToken().equals("")) {
			throw new PayPalRESTException("refresh token is empty");
		}
		String pattern = "v1/oauth2/token?refresh_token={0}&grant_type=refresh_token";
		Map<String, String> paramsMap = new HashMap<String, String>();
		try {
			paramsMap.put("refresh_token", URLEncoder.encode(getRefreshToken(),
					Constants.ENCODING_FORMAT));
		} catch (UnsupportedEncodingException ex) {
			// Ignore
		}
		Object[] parameters = new Object[] { paramsMap };
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = resourcePath.substring(resourcePath.indexOf('?') + 1);
		resourcePath = resourcePath.substring(0, resourcePath.indexOf('?'));
		if (apiContext == null) {
			apiContext = new APIContext();
		}
		apiContext.setMaskRequestId(true);
		Map<String, String> headersMap = new HashMap<String, String>();
		headersMap.put(Constants.HTTP_CONTENT_TYPE_HEADER,
				Constants.HTTP_CONFIG_DEFAULT_CONTENT_TYPE);
		
		apiContext.setHTTPHeaders(headersMap);
		return configureAndExecute(apiContext, HttpMethod.POST,
				resourcePath, payLoad, Tokeninfo.class);
	}
}