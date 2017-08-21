package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.JSONFormatter;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.OAuthTokenCredential;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.sdk.info.SDKVersionImpl;

public class WebProfile  {

	/**
	 * Unique ID of the web experience profile.
	 */
	private String id;

	/**
	 * Name of the web experience profile. Unique only among the profiles for a given merchant.
	 */
	private String name;

	/**
	 * Parameters for flow configuration.
	 */
	private FlowConfig flowConfig;

	/**
	 * Parameters for input fields customization.
	 */
	private InputFields inputFields;

	/**
	 * Parameters for style and presentation.
	 */
	private Presentation presentation;

	/**
	 * Returns the last request sent to the Service
	 *
	 * @return Last request sent to the server
	 */
	public static String getLastRequest() {
		return PayPalResource.getLastRequest();
	}

	/**
	 * Returns the last response returned by the Service
	 *
	 * @return Last response got from the Service
	 */
	public static String getLastResponse() {
		return PayPalResource.getLastResponse();
	}

	/**
	 * Initialize using InputStream(of a Properties file)
	 *
	 * @param is
	 *            InputStream
	 * @throws PayPalRESTException
	 * @return OAuthTokenCredential instance using client ID and client secret loaded from configuration.
	 */
	public static OAuthTokenCredential initConfig(InputStream is) throws PayPalRESTException {
		return PayPalResource.initConfig(is);
	}

	/**
	 * Initialize using a File(Properties file)
	 *
	 * @param file
	 *            File object of a properties entity
	 * @throws PayPalRESTException
	 * @return OAuthTokenCredential instance using client ID and client secret loaded from configuration.
	 */
	public static OAuthTokenCredential initConfig(File file) throws PayPalRESTException {
		return PayPalResource.initConfig(file);
	}

	/**
	 * Initialize using Properties
	 *
	 * @param properties
	 *            Properties object
	 * @return OAuthTokenCredential instance using client ID and client secret loaded from configuration.
	 */
	public static OAuthTokenCredential initConfig(Properties properties) {
		return PayPalResource.initConfig(properties);
	}
	/**
	 * Default Constructor
	 */
	public WebProfile() {
	}

	/**
	 * Parameterized Constructor
	 */
	public WebProfile(String name) {
		this.name = name;
	}


	/**
	 * Setter for id
	 */
	public WebProfile setId(String id) {
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
	 * Setter for name
	 */
	public WebProfile setName(String name) {
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
	 * Setter for flowConfig
	 */
	public WebProfile setFlowConfig(FlowConfig flowConfig) {
		this.flowConfig = flowConfig;
		return this;
	}

	/**
	 * Getter for flowConfig
	 */
	public FlowConfig getFlowConfig() {
		return this.flowConfig;
	}


	/**
	 * Setter for inputFields
	 */
	public WebProfile setInputFields(InputFields inputFields) {
		this.inputFields = inputFields;
		return this;
	}

	/**
	 * Getter for inputFields
	 */
	public InputFields getInputFields() {
		return this.inputFields;
	}


	/**
	 * Setter for presentation
	 */
	public WebProfile setPresentation(Presentation presentation) {
		this.presentation = presentation;
		return this;
	}

	/**
	 * Getter for presentation
	 */
	public Presentation getPresentation() {
		return this.presentation;
	}


	/**
	 * Create a web experience profile by passing the name of the profile and other profile details in the request JSON to the request URI.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return CreateProfileResponse
	 * @throws PayPalRESTException
	 */
	public CreateProfileResponse create(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return create(apiContext);
	}

	/**
	 * Create a web experience profile by passing the name of the profile and other profile details in the request JSON to the request URI.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return CreateProfileResponse
	 * @throws PayPalRESTException
	 */
	public CreateProfileResponse create(APIContext apiContext) throws PayPalRESTException {
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
		String resourcePath = "v1/payment-experience/web-profiles";
		String payLoad = this.toJSON();
		return PayPalResource.configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, CreateProfileResponse.class);
	}


	/**
	 * Update a web experience profile by passing the ID of the profile to the request URI. In addition, pass the profile details in the request JSON. If your request does not include values for all profile detail fields, the previously set values for the omitted fields are removed by this operation.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return 
	 * @throws PayPalRESTException
	 */
	public void update(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		update(apiContext);
		return;
	}

	/**
	 * Update a web experience profile by passing the ID of the profile to the request URI. In addition, pass the profile details in the request JSON. If your request does not include values for all profile detail fields, the previously set values for the omitted fields are removed by this operation.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return 
	 * @throws PayPalRESTException
	 */
	public void update(APIContext apiContext) throws PayPalRESTException {
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
		String pattern = "v1/payment-experience/web-profiles/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = this.toJSON();
		PayPalResource.configureAndExecute(apiContext, HttpMethod.PUT, resourcePath, payLoad, null);
		return;
	}


	/**
	 * Partially update an existing web experience profile by passing the ID of the profile to the request URI. In addition, pass a patch object in the request JSON that specifies the operation to perform, path of the profile location to update, and a new value if needed to complete the operation.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param patchRequest
	 *            PatchRequest
	 * @return 
	 * @throws PayPalRESTException
	 */
	public void partialUpdate(String accessToken, PatchRequest patchRequest) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		partialUpdate(apiContext, patchRequest);
		return;
	}

	/**
	 * Partially update an existing web experience profile by passing the ID of the profile to the request URI. In addition, pass a patch object in the request JSON that specifies the operation to perform, path of the profile location to update, and a new value if needed to complete the operation.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param patchRequest
	 *            PatchRequest
	 * @return 
	 * @throws PayPalRESTException
	 */
	public void partialUpdate(APIContext apiContext, PatchRequest patchRequest) throws PayPalRESTException {
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
		if (patchRequest == null) {
			throw new IllegalArgumentException("patchRequest cannot be null");
		}
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/payment-experience/web-profiles/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = patchRequest.toJSON();
		PayPalResource.configureAndExecute(apiContext, HttpMethod.PATCH, resourcePath, payLoad, null);
		return;
	}


	/**
	 * Retrieve the details of a particular web experience profile by passing the ID of the profile to the request URI.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param profileId
	 *            String
	 * @return WebProfile
	 * @throws PayPalRESTException
	 */
	public static WebProfile get(String accessToken, String profileId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return get(apiContext, profileId);
	}

	/**
	 * Retrieve the details of a particular web experience profile by passing the ID of the profile to the request URI.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param profileId
	 *            String
	 * @return WebProfile
	 * @throws PayPalRESTException
	 */
	public static WebProfile get(APIContext apiContext, String profileId) throws PayPalRESTException {
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
		if (profileId == null) {
			throw new IllegalArgumentException("profileId cannot be null");
		}
		Object[] parameters = new Object[] {profileId};
		String pattern = "v1/payment-experience/web-profiles/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return PayPalResource.configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, WebProfile.class);
	}


	/**
	 * Lists all web experience profiles that exist for a merchant (or subject).
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return WebProfileList
	 * @throws PayPalRESTException
	 */
	public static List<WebProfile> getList(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return getList(apiContext);
	}

	/**
	 * Lists all web experience profiles that exist for a merchant (or subject).
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return WebProfileList
	 * @throws PayPalRESTException
	 */
	public static List<WebProfile> getList(APIContext apiContext) throws PayPalRESTException {
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
		String resourcePath = "v1/payment-experience/web-profiles";
		String payLoad = "";
		List<WebProfile> webProfiles = PayPalResource.configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, WebProfileList.class);
//		if (webProfiles == null) {
//			webProfiles = new ArrayList<WebProfile>();
//		}
		
		return webProfiles;
	}


	/**
	 * Delete an existing web experience profile by passing the profile ID to the request URI.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return 
	 * @throws PayPalRESTException
	 */
	public void delete(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		delete(apiContext);
		return;
	}

	/**
	 * Delete an existing web experience profile by passing the profile ID to the request URI.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return 
	 * @throws PayPalRESTException
	 */
	public void delete(APIContext apiContext) throws PayPalRESTException {
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
			apiContext.setMaskRequestId(true);
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/payment-experience/web-profiles/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		PayPalResource.configureAndExecute(apiContext, HttpMethod.DELETE, resourcePath, payLoad, null);
		return;
	}

	/**
	 * Returns a JSON string corresponding to object state
	 *
	 * @return JSON representation
	 */
	public String toJSON() {
		return JSONFormatter.toJSON(this);
	}

	@Override
	public String toString() {
		return toJSON();
	}
}
