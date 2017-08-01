package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * InvalidCredentialException used to denote errors in credentials used in API
 * call
 * 
 */
public class InvalidCredentialException extends BaseException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -4321565982347658546L;

	public InvalidCredentialException(String msg) {
		super(msg);
	}

	public InvalidCredentialException(String msg, Throwable exception) {
		super(msg, exception);
	}
}
