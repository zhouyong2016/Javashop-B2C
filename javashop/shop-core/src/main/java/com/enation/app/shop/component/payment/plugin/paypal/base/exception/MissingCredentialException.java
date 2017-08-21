package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * MissingCredentialException is throw when the credential used are wrongly
 * configured or not found in the application properties
 * 
 */
public class MissingCredentialException extends BaseException {

	/**
	 * Serial versio UID
	 */
	private static final long serialVersionUID = -2345825926387658303L;

	public MissingCredentialException(String message) {
		super(message);
	}

	public MissingCredentialException(String message, Throwable exception) {
		super(message, exception);
	}

}
