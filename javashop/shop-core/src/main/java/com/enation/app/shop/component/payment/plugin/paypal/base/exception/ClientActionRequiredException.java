package com.enation.app.shop.component.payment.plugin.paypal.base.exception;

/**
 * ClientActionRequiredException, encapsulates instances where client has to
 * take actions based or errors in API call.
 * 
 */
public class ClientActionRequiredException extends HttpErrorException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -15345584654755445L;

	public ClientActionRequiredException(String message) {
		super(message);
	}

	public ClientActionRequiredException(String message, Throwable exception) {
		super(message, exception);
	}

	public ClientActionRequiredException(int responsecode, String errorResponse, String msg, Throwable exception) {
		super(responsecode, errorResponse, msg, exception);
	}
	
	public String toString() {
		return "HTTP response code: " + this.getResponsecode() + "\n"
				+ "error message: " + this.getErrorResponse();
	}
}
