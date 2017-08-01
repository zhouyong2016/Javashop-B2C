package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * BaseException for SDK
 */
public class BaseException extends Exception {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5345825923487658213L;

	public BaseException(String msg) {
		super(msg);
	}

	public BaseException(String msg, Throwable exception) {
		super(msg, exception);
	}

}
