package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * SSLConfigurationException is thrown for error caused during SSL connection
 * 
 */
public class SSLConfigurationException extends BaseException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -2345834567387658303L;

	public SSLConfigurationException(String message) {
		super(message);
	}

	public SSLConfigurationException(String message, Throwable exception) {
		super(message, exception);
	}
}
