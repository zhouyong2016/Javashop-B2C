package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.HashMap;
import java.util.List;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.sdk.info.SDKVersionImpl;

public class Webhook  extends PayPalResource {

	/**
	 * Identifier of the webhook resource.
	 */
	private String id;

	/**
	 * Webhook notification endpoint url.
	 */
	private String url;

	/**
	 * List of Webhooks event-types.
	 */
	private List<EventType> eventTypes;

	/**
	 * Hateoas Links.
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public Webhook() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Webhook(String url, List<EventType> eventTypes) {
		this.url = url;
		this.eventTypes = eventTypes;
	}


	/**
	 * Setter for id
	 */
	public Webhook setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Getter for id
	 */
	public String getId() {
		return this.id;
	}


	/**
	 * Setter for url
	 */
	public Webhook setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * Getter for url
	 */
	public String getUrl() {
		return this.url;
	}


	/**
	 * Setter for eventTypes
	 */
	public Webhook setEventTypes(List<EventType> eventTypes) {
		this.eventTypes = eventTypes;
		return this;
	}

	/**
	 * Getter for eventTypes
	 */
	public List<EventType> getEventTypes() {
		return this.eventTypes;
	}


	/**
	 * Setter for links
	 */
	public Webhook setLinks(List<Links> links) {
		this.links = links;
		return this;
	}

	/**
	 * Getter for links
	 */
	public List<Links> getLinks() {
		return this.links;
	}
	
	/**
	 * Creates the Webhook for the application associated with the access token.
	 * @param accessToken
	 *            Access Token used for the API call.	
	 * @param webhook
	 *            Webhook Request
	 * @return Webhook
	 * @throws PayPalRESTException
	 */
	public Webhook create(String accessToken, Webhook webhook) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return create(apiContext, webhook);
	}

	/**
	 * Creates the Webhook for the application associated with the access token.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.	 
	 * @param webhook
	 *            Webhook Request
	 * @return Webhook
	 * @throws PayPalRESTException
	 */
	public Webhook create(APIContext apiContext, Webhook webhook) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null || apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException("AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		if (webhook == null) {
			throw new IllegalArgumentException("webhook cannot be null");
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER, Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());

		Object[] parameters = new Object[] {};
		String pattern = "v1/notifications/webhooks";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = webhook.toJSON();
		return configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, Webhook.class);
	}
	
	/**
	 * Retrieves the Webhook identified by webhook_id for the application associated with access token.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param webhookId
	 *            Identifier of the webhook
	 * @return Webhook
	 * @throws PayPalRESTException
	 */
	public Webhook get(String accessToken, String webhookId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return get(apiContext, webhookId);
	}

	/**
	 * Retrieves the Webhook identified by webhook_id for the application associated with access token.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param webhookId
	 *            Identifier of the webhook
	 * @param patchRequest
	 *            patchRequest
	 * @return Webhook
	 * @throws PayPalRESTException
	 */
	public Webhook get(APIContext apiContext, String webhookId) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null || apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException("AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		if (webhookId == null) {
			throw new IllegalArgumentException("webhookId cannot be null");
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER, Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());

		Object[] parameters = new Object[] {webhookId};
		String pattern = "v1/notifications/webhooks/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, Webhook.class);
	}

	/**
	 * Updates the Webhook identified by webhook_id for the application associated with access token.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param webhookId
	 *            Identifier of the webhook
	 * @param patchRequest
	 *            patchRequest
	 * @return Webhook
	 * @throws PayPalRESTException
	 */
	public Webhook update(String accessToken, String webhookId, String patchRequest) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return update(apiContext, webhookId, patchRequest);
	}

	/**
	 * Updates the Webhook identified by webhook_id for the application associated with access token.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param webhookId
	 *            Identifier of the webhook
	 * @return Webhook
	 * @throws PayPalRESTException
	 */
	public Webhook update(APIContext apiContext, String webhookId, String patchRequest) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null || apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException("AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		if (webhookId == null) {
			throw new IllegalArgumentException("webhookId cannot be null");
		}
		if (patchRequest == null) {
			throw new IllegalArgumentException("patchRequest cannot be null");
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER, Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());

		Object[] parameters = new Object[] {webhookId};
		String pattern = "v1/notifications/webhooks/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = patchRequest;
		return configureAndExecute(apiContext, HttpMethod.PATCH, resourcePath, payLoad, Webhook.class);
	}
	
	/**
	 * Deletes the Webhook identified by webhook_id for the application associated with access token.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param webhookId
	 *            Identifier of the webhook
	 * @throws PayPalRESTException
	 */
	public void delete(String accessToken, String webhookId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		delete(apiContext, webhookId);
	}

	/**
	 * Deletes the Webhook identified by webhook_id for the application associated with access token.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param webhookId
	 *            Identifier of the webhook
	 * @throws PayPalRESTException
	 */
	public void delete(APIContext apiContext, String webhookId) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null || apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException("AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		if (webhookId == null) {
			throw new IllegalArgumentException("webhookId cannot be null");
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER, Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());

		Object[] parameters = new Object[] {webhookId};
		String pattern = "v1/notifications/webhooks/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		configureAndExecute(apiContext, HttpMethod.DELETE, resourcePath, payLoad, null);
	}
	

}