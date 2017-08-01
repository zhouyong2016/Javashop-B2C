package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.SDKUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.SSLUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.sdk.info.SDKVersionImpl;

public class Event  extends PayPalResource {
	
	private static final Logger log = LoggerFactory.getLogger(Event.class);

	/**
	 * Identifier of the Webhooks event resource.
	 */
	private String id;

	/**
	 * Time the resource was created.
	 */
	private String createTime;

	/**
	 * Name of the resource contained in resource element.
	 */
	private String resourceType;

	/**
	 * Name of the event type that occurred on resource, identified by data_resource element, to trigger the Webhooks event.
	 */
	private String eventType;

	/**
	 * A summary description of the event. E.g. A successful payment authorization was created for $$
	 */
	private String summary;

	/**
	 * This contains the resource that is identified by resource_type element.
	 */
	private Object resource;

	/**
	 * Hateoas links.
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public Event() {
	}


	/**
	 * Setter for id
	 */
	public Event setId(String id) {
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
	 * Setter for createTime
	 */
	public Event setCreateTime(String createTime) {
		this.createTime = createTime;
		return this;
	}

	/**
	 * Getter for createTime
	 */
	public String getCreateTime() {
		return this.createTime;
	}


	/**
	 * Setter for resourceType
	 */
	public Event setResourceType(String resourceType) {
		this.resourceType = resourceType;
		return this;
	}

	/**
	 * Getter for resourceType
	 */
	public String getResourceType() {
		return this.resourceType;
	}


	/**
	 * Setter for eventType
	 */
	public Event setEventType(String eventType) {
		this.eventType = eventType;
		return this;
	}

	/**
	 * Getter for eventType
	 */
	public String getEventType() {
		return this.eventType;
	}


	/**
	 * Setter for summary
	 */
	public Event setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	/**
	 * Getter for summary
	 */
	public String getSummary() {
		return this.summary;
	}


	/**
	 * Setter for resource
	 */
	public Event setResource(Object resource) {
		this.resource = resource;
		return this;
	}

	/**
	 * Getter for resource
	 */
	public Object getResource() {
		return this.resource;
	}


	/**
	 * Setter for links
	 */
	public Event setLinks(List<Links> links) {
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
	 * Retrieves the Webhooks event resource identified by event_id. Can be used to retrieve the payload for an event.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param eventId
	 *            String
	 * @return Event
	 * @throws PayPalRESTException
	 */
	public static Event get(String accessToken, String eventId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return get(apiContext, eventId);
	}

	/**
	 * Retrieves the Webhooks event resource identified by event_id. Can be used to retrieve the payload for an event.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param eventId
	 *            String
	 * @return Event
	 * @throws PayPalRESTException
	 */
	public static Event get(APIContext apiContext, String eventId) throws PayPalRESTException {
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
		if (eventId == null) {
			throw new IllegalArgumentException("eventId cannot be null");
		}
		Object[] parameters = new Object[] {eventId};
		String pattern = "v1/notifications/webhooks-events/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, Event.class);
	}


	/**
	 * Resends the Webhooks event resource identified by event_id.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return Event
	 * @throws PayPalRESTException
	 */
	public Event resend(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return resend(apiContext);
	}

	/**
	 * Resends the Webhooks event resource identified by event_id.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return Event
	 * @throws PayPalRESTException
	 */
	public Event resend(APIContext apiContext) throws PayPalRESTException {
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
		if (this.getId() == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/notifications/webhooks-events/{0}/resend";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, Event.class);
	}


	/**
	 * Retrieves the list of Webhooks events resources for the application associated with token. The developers can use it to see list of past webhooks events.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return EventList
	 * @throws PayPalRESTException
	 */
	public static EventList list(String accessToken, String queryParams) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return list(apiContext, queryParams);
	}

	/**
	 * Retrieves the list of Webhooks events resources for the application associated with token. The developers can use it to see list of past webhooks events.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return EventList
	 * @throws PayPalRESTException
	 */
	public static EventList list(APIContext apiContext, String queryParams) throws PayPalRESTException {
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
		String resourcePath = "v1/notifications/webhooks-events" + queryParams;
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, EventList.class);
	}
	
	/**
	 * Validates received event received from PayPal to webhook endpoint set for particular webhook Id with PayPal trust source, to verify Data and Certificate integrity.
	 * It validates both certificate chain, as well as data integrity. 
	 * 
	 * @param apiContext APIContext object
	 * @param headers Map of Headers received in the event, from request
	 * @param requestBody Request body received in the provided webhook
	 * @return true if valid, false otherwise
	 * @throws PayPalRESTException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	public static boolean validateReceivedEvent(APIContext apiContext, Map<String, String> headers, String requestBody) throws PayPalRESTException, InvalidKeyException, NoSuchAlgorithmException, SignatureException  {

		if (headers == null) {
			throw new PayPalRESTException("Headers cannot be null");
		}

		Map<String, String> cmap = new HashMap<String, String>();
		Boolean isChainValid = false, isDataValid = false;
		Collection<X509Certificate> trustCerts, clientCerts;

		// Load the configurations from all possible sources
		cmap = getConfigurations(apiContext);

		// Fetch Certificate Locations
		String clientCertificateLocation = SDKUtil.validateAndGet(headers, Constants.PAYPAL_HEADER_CERT_URL);
		String trustCertificateLocation = SDKUtil.validateAndGet(cmap, Constants.PAYPAL_TRUST_CERT_URL);

		// Load certificates
		clientCerts = SSLUtil.getCertificateFromStream(SSLUtil.downloadCertificateFromPath(clientCertificateLocation));
		trustCerts = SSLUtil.getCertificateFromStream(Event.class.getClassLoader().getResourceAsStream(trustCertificateLocation));

		// Check if Chain Valid
		isChainValid = SSLUtil.validateCertificateChain(clientCerts, trustCerts, SDKUtil.validateAndGet(cmap, Constants.PAYPAL_WEBHOOK_CERTIFICATE_AUTHTYPE));

		log.debug("Is Chain Valid: " + isChainValid);
		if (isChainValid) {
			// If Chain Valid, check for data signature valid
			// Lets check for data now
			String webhookId = SDKUtil.validateAndGet(cmap, Constants.PAYPAL_WEBHOOK_ID);
			String actualSignatureEncoded = SDKUtil.validateAndGet(headers, Constants.PAYPAL_HEADER_TRANSMISSION_SIG);
			String authAlgo = SDKUtil.validateAndGet(headers, Constants.PAYPAL_HEADER_AUTH_ALGO);
			String transmissionId = SDKUtil.validateAndGet(headers, Constants.PAYPAL_HEADER_TRANSMISSION_ID);
			String transmissionTime = SDKUtil.validateAndGet(headers, Constants.PAYPAL_HEADER_TRANSMISSION_TIME);
			String expectedSignature = String.format("%s|%s|%s|%s", transmissionId, transmissionTime, webhookId, SSLUtil.crc32(requestBody));

			// Validate Data
			isDataValid = SSLUtil.validateData(clientCerts, authAlgo, actualSignatureEncoded, expectedSignature, requestBody, webhookId);

			log.debug("Is Data Valid: " + isDataValid);
			// Return true if both data and chain valid
			return isDataValid;
		}

		return false;

	}

	/**
	 * Returns configurations by merging apiContext configurations in Map format
	 * 
	 * @param apiContext
	 * @return Map of configurations to be used for particular request
	 */
	private static Map<String, String> getConfigurations(APIContext apiContext) {

		Map<String, String> cmap = new HashMap<String, String>();
		if (apiContext != null) {
			if (apiContext.getConfigurationMap() == null) {
				apiContext.setConfigurationMap(new HashMap<String, String>());
			}
			cmap = SDKUtil.combineDefaultMap(apiContext.getConfigurationMap());
			cmap = SDKUtil.combineMap(cmap, PayPalResource.getConfigurations());
		} else {
			cmap = SDKUtil.combineDefaultMap(PayPalResource.getConfigurations());
		}
		return cmap;
	}


}
