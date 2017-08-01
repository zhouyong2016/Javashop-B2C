package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect.CreateFromAuthorizationCodeParameters;
import com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect.CreateFromRefreshTokenParameters;
import com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect.Tokeninfo;
import com.enation.app.shop.component.payment.plugin.paypal.base.ClientCredentials;
import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;

public class FuturePayment extends Payment {

	
	private Properties getCredential() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileReader(new File(".",
				"src/main/resources/sdk_config.properties")));
		return properties;
	}
	
	/**
	 * Creates a future payment using either authorization code or refresh token with correlation ID. <br>
	 * https://developer.paypal.com/webapps/developer/docs/integration/mobile/make-future-payment/
	 * 
	 * @param authorizationCode	an authorization code
	 * @param accessToken		an access token
	 * @param correlationId		paypal application correlation ID
	 * @return	a <code>Payment</code> object
	 * @throws PayPalRESTException
	 * @throws IOException thrown when config file cannot be read properly
	 * @throws FileNotFoundException thrown when config file does not exist
	 */
	public Payment create(String accessToken, String correlationId) throws PayPalRESTException, FileNotFoundException, IOException {
		if (correlationId == null || correlationId.equals("")) {
			throw new IllegalArgumentException("correlation ID cannot be null or empty");
		}
		
		APIContext apiContext = new APIContext(accessToken);
		apiContext.setHTTPHeaders(new HashMap<String, String>());
		apiContext.getHTTPHeaders().put("PAYPAL-CLIENT-METADATA-ID", correlationId);
		return this.create(apiContext);
	}
	
	public Tokeninfo getTokeninfo(CreateFromAuthorizationCodeParameters params) throws PayPalRESTException {
		Map<String, String> configurationMap = new HashMap<String, String>();
	    configurationMap.put(Constants.CLIENT_ID, params.getClientID());
	    configurationMap.put(Constants.CLIENT_SECRET, params.getClientSecret());
	    configurationMap.put("response_type", "token");
		APIContext apiContext = new APIContext();
		apiContext.setConfigurationMap(configurationMap);
	    params.setRedirectURI("urn:ietf:wg:oauth:2.0:oob");
		Tokeninfo info = Tokeninfo.createFromAuthorizationCodeForFpp(apiContext, params);
		return info;
	}
	
	public Tokeninfo getTokeninfo(CreateFromRefreshTokenParameters params, Tokeninfo info) throws PayPalRESTException {
		Map<String, String> configurationMap = new HashMap<String, String>();
		APIContext apiContext = new APIContext();
		apiContext.setConfigurationMap(configurationMap);
		info = info.createFromRefreshToken(apiContext, params);
		return info;
	}
	
	public ClientCredentials getClientCredential() throws FileNotFoundException, IOException {
		ClientCredentials credentials = new ClientCredentials();
		Properties properties = getCredential();
		credentials.setClientID(properties.getProperty(Constants.CLIENT_ID));
		credentials.setClientSecret(properties.getProperty(Constants.CLIENT_SECRET));
		return credentials;
	}
}
