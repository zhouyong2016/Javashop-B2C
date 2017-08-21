package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * HttpErrorException denotes errors that occur in HTTP call
 * 
 */
public class HttpErrorException extends BaseException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -4312358746964758546L;

	private int responsecode;
	private String errorResponse;

	public HttpErrorException(String msg) {
		super(msg);
	}

	public HttpErrorException(String msg, Throwable exception) {
		super(msg, exception);
	}

	public HttpErrorException(int responsecode, String errorResponse, String msg, Throwable exception) {
		super(msg, exception);
		this.responsecode = responsecode;
		this.errorResponse = errorResponse;
	}

	public int getResponsecode() {
		return responsecode;
	}

	public String getErrorResponse() {
		return errorResponse;
	}
}
