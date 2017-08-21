package com.enation.app.shop.component.payment.plugin.paypal.base.credential;

/**
 * <code>CertificateCredential</code> encapsulates certificate credential
 * information used by service authentication systems.
 * 
 */
public class CertificateCredential implements ICredential {

	/**
	 * Username credential
	 */
	private String userName;

	/**
	 * Password credential
	 */
	private String password;

	/**
	 * Application Id (Used by Platform APIs)
	 */
	private String applicationId;

	/**
	 * Certificate path
	 */
	private String certificatePath;

	/**
	 * Certificate key
	 */
	private String certificateKey;

	/**
	 * {@link ThirdPartyAuthorization} instance
	 */
	private ThirdPartyAuthorization thirdPartyAuthorization;

	/**
	 * Certificate Credential
	 * 
	 * @param userName
	 *            UserName
	 * @param password
	 *            Password
	 * @param certificatePath
	 *            Certificate file path
	 * @param certificateKey
	 *            Certificate private key
	 */
	public CertificateCredential(String userName, String password,
			String certificatePath, String certificateKey) {
		super();
		if (userName == null || userName.trim().length() == 0) {
			throw new IllegalArgumentException(
					"userName cannot be empty or null");
		}
		if (password == null || password.trim().length() == 0) {
			throw new IllegalArgumentException(
					"password cannot be empty or null");
		}
		if (certificatePath == null || certificatePath.trim().length() == 0) {
			throw new IllegalArgumentException(
					"certificatePath cannot be empty or null");
		}
		if (certificateKey == null || certificateKey.trim().length() == 0) {
			throw new IllegalArgumentException(
					"certificateKey cannot be empty or null");
		}
		this.userName = userName;
		this.password = password;
		this.certificatePath = certificatePath;
		this.certificateKey = certificateKey;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId
	 *            the applicationId to set, ignored for MERCHANT APIs
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the thirdPartyAuthorization
	 */
	public ThirdPartyAuthorization getThirdPartyAuthorization() {
		return thirdPartyAuthorization;
	}

	/**
	 * Sets any instance of {@link ThirdPartyAuthorization}. Can be an Instance
	 * of {@link TokenAuthorization} or {@link SubjectAuthorization}.
	 * {@link SubjectAuthorization} is used in MERCHANT APIs
	 * 
	 * @param thirdPartyAuthorization
	 *            the thirdPartyAuthorization to set
	 */
	public void setThirdPartyAuthorization(
			ThirdPartyAuthorization thirdPartyAuthorization) {
		this.thirdPartyAuthorization = thirdPartyAuthorization;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the certificatePath
	 */
	public String getCertificatePath() {
		return certificatePath;
	}

	/**
	 * @return the certificateKey
	 */
	public String getCertificateKey() {
		return certificateKey;
	}

}
