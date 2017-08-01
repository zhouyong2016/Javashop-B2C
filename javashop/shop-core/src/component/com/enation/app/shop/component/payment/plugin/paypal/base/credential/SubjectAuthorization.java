package com.enation.app.shop.component.payment.plugin.paypal.base.credential;

/**
 * SubjectAuthorization encapsulates third party subject authorization. Subject
 * Authorization caters to MERCHANT APIs only
 * 
 */
public class SubjectAuthorization implements ThirdPartyAuthorization {

	/**
	 * Subject information
	 */
	private String subject;

	/**
	 * Subject based third party authorization used for MERCHANT APIs
	 * 
	 * @param subject
	 *            Subject string
	 */
	public SubjectAuthorization(String subject) {
		super();
		if (subject == null || subject.trim().length() == 0) {
			throw new IllegalArgumentException(
					"Subject is null or empty in SubjectAuthorization");
		}
		this.subject = subject;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

}
