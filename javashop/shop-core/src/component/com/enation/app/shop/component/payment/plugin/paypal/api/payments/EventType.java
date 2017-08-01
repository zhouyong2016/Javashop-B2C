package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.HashMap;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.sdk.info.SDKVersionImpl;

public class EventType  extends PayPalResource {

	/**
	 * Unique event-type name.
	 */
	private String name;

	/**
	 * Human readable description of the event-type
	 */
	private String description;
	
	/**
	 * Default Constructor
	 */
	public EventType() {
	}

	/**
	 * Parameterized Constructor
	 */
	public EventType(String name) {
		this.name = name;
	}


	/**
	 * Setter for name
	 */
	public EventType setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Getter for name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Setter for description
	 */
	public EventType setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Getter for description
	 */
	public String getDescription() {
		return this.description;
	}


	/**
	 * Retrieves the list of events-types subscribed by the given Webhook.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param webhookId
	 *            String
	 * @return EventTypeList
	 * @throws PayPalRESTException
	 */
	public static EventTypeList subscribedEventTypes(String accessToken, String webhookId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return subscribedEventTypes(apiContext, webhookId);
	}

	/**
	 * Retrieves the list of events-types subscribed by the given Webhook.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param webhookId
	 *            String
	 * @return EventTypeList
	 * @throws PayPalRESTException
	 */
	public static EventTypeList subscribedEventTypes(APIContext apiContext, String webhookId) throws PayPalRESTException {
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
		if (webhookId == null) {
			throw new IllegalArgumentException("webhookId cannot be null");
		}
		Object[] parameters = new Object[] {webhookId};
		String pattern = "v1/notifications/webhooks/{0}/event-types";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		EventTypeList eventTypeList =  configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, EventTypeList.class);
		if (eventTypeList == null) {
			eventTypeList = new EventTypeList();
		}
		
		return eventTypeList;
	}


	/**
	 * Retrieves the master list of available Webhooks events-types resources for any webhook to subscribe to.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return EventTypeList
	 * @throws PayPalRESTException
	 */
	public static EventTypeList availableEventTypes(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return availableEventTypes(apiContext);
	}

	/**
	 * Retrieves the master list of available Webhooks events-types resources for any webhook to subscribe to.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return EventTypeList
	 * @throws PayPalRESTException
	 */
	public static EventTypeList availableEventTypes(APIContext apiContext) throws PayPalRESTException {
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
		String resourcePath = "v1/notifications/webhooks-event-types";
		String payLoad = "";
		EventTypeList eventTypeList = configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, EventTypeList.class);
//		if (eventTypeList == null) {
//			eventTypeList = new EventTypeList();
//		}
		
		return eventTypeList;
	}


}
