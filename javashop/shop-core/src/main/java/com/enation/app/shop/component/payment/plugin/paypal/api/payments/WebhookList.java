package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.sdk.info.SDKVersionImpl;

public class WebhookList  extends PayPalResource {

	/**
	 * A list of Webhooks
	 */
	private List<Webhook> webhooks;

	/**
	 * Default Constructor
	 */
	public WebhookList() {
		webhooks = new ArrayList<Webhook>();
	}


	/**
	 * Setter for webhooks
	 */
	public WebhookList setWebhooks(List<Webhook> webhooks) {
		this.webhooks = webhooks;
		return this;
	}

	/**
	 * Getter for webhooks
	 */
	public List<Webhook> getWebhooks() {
		return this.webhooks;
	}
	
	/**
	 * Retrieves all Webhooks for the application associated with access token.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return WebhookList
	 * @throws PayPalRESTException
	 */
	public WebhookList getAll(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return getAll(apiContext);
	}

	/**
	 * Retrieves all Webhooks for the application associated with access token.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return WebhookList
	 * @throws PayPalRESTException
	 */
	public WebhookList getAll(APIContext apiContext) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null || apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException("AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER, Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());

		Object[] parameters = new Object[] {};
		String pattern = "v1/notifications/webhooks/";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, WebhookList.class);
	}


}
