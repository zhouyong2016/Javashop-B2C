package com.enation.app.shop.component.payment.plugin.paypal.base;

import java.util.HashMap;
import java.util.Map;

import com.enation.app.shop.component.payment.plugin.paypal.base.credential.SignatureCredential;
import com.enation.app.shop.component.payment.plugin.paypal.base.credential.TokenAuthorization;
import com.enation.app.shop.component.payment.plugin.paypal.base.exception.OAuthException;

/**
 * AbstractSignatureHttpHeaderAuthStrategy is an abstract implementation for
 * signature based {@link ICredential} to be realized as HTTP headers
 */
public abstract class AbstractSignatureHttpHeaderAuthStrategy implements
		AuthenticationStrategy<Map<String, String>, SignatureCredential> {

	/**
	 * Endpoint url
	 */
	protected String endPointUrl;

	/**
	 * AbstractCertificateHttpHeaderAuthStrategy
	 * 
	 * @param endPointUrl
	 *            Endpoint URL
	 */
	public AbstractSignatureHttpHeaderAuthStrategy(String endPointUrl) {
		this.endPointUrl = endPointUrl;
	}

	/**
	 * Returns {@link CertificateCredential} as HTTP headers
	 */
	public Map<String, String> generateHeaderStrategy(SignatureCredential credential)
			throws OAuthException {
		Map<String, String> headers = null;
		if (credential.getThirdPartyAuthorization() instanceof TokenAuthorization) {
			headers = processTokenAuthorization(credential,
					(TokenAuthorization) credential
							.getThirdPartyAuthorization());

		} else {
			headers = new HashMap<String, String>();
			headers.put(Constants.PAYPAL_SECURITY_USERID_HEADER,
					credential.getUserName());
			headers.put(Constants.PAYPAL_SECURITY_PASSWORD_HEADER,
					credential.getPassword());
			headers.put(Constants.PAYPAL_SECURITY_SIGNATURE_HEADER,
					credential.getSignature());
		}
		return headers;
	}

	/**
	 * Process TokenAuthorization based on API format
	 * 
	 * @param credential
	 *            Instance of {@link SignatureCredential}
	 * @param tokenAuth
	 *            Instance of {@link TokenAuthorization}
	 * @return Map of HTTP headers
	 * @throws OAuthException
	 */
	protected abstract Map<String, String> processTokenAuthorization(
			SignatureCredential credential, TokenAuthorization tokenAuth)
			throws OAuthException;

}
