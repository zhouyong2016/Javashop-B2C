package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * InvalidResponseException used to denote errors in response data
 * 
 */
public class InvalidResponseDataException extends BaseException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7489562847530985773L;

	public InvalidResponseDataException(String msg) {
		super(msg);
	}

	public InvalidResponseDataException(String msg, Throwable exception) {
		super(msg, exception);
	}

}
