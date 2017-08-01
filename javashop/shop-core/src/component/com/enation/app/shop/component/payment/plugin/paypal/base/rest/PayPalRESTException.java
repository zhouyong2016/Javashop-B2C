package com.enation.app.shop.component.payment.plugin.paypal.base.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Error;
import com.enation.app.shop.component.payment.plugin.paypal.base.exception.HttpErrorException;

/**
 * PayPalException handles all exceptions related to REST services
 */
public class PayPalRESTException extends Exception {


	private static final Logger log = LoggerFactory
			.getLogger(PayPalRESTException.class);
	
	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * If source is {@link HttpErrorException}, 
	 * exception's response code value is copied
	 */
	private int responsecode;
	
	/**
	 * If source is {@link HttpErrorException} and response code is 400,
	 * error response content is converted to {@link Error} object
	 */
	private Error details;
	
	public PayPalRESTException(String message) {
		super(message);
	}

	public PayPalRESTException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public PayPalRESTException(Throwable throwable) {
		super(throwable);
	}

	public int getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(int responsecode) {
		this.responsecode = responsecode;
	}

	public Error getDetails() {
		return details;
	}

	public void setDetails(Error details) {
		this.details = details;
	}
	
	/**
	 * Utility method that creates a {@link PayPalRESTException} object from {@link HttpErrorException}.
	 * if {@link HttpErrorException} contains 400 response code, error response is converted to {@link Error} object.
	 * 
	 * @param httpErrorException
	 * 				{@link HttpErrorException} thrown from API call		
	 * @return
	 */
	protected static PayPalRESTException createFromHttpErrorException(HttpErrorException httpErrorException){
		PayPalRESTException ppre = new PayPalRESTException(httpErrorException.getMessage(), httpErrorException);
		ppre.setResponsecode(httpErrorException.getResponsecode());
		if( httpErrorException.getResponsecode() >= 400 &&  httpErrorException.getErrorResponse()!=null){
			try{
				Error details = JSONFormatter.fromJSON(httpErrorException.getErrorResponse(), Error.class);	
				ppre.setDetails(details);
			} catch(Exception e){
				log.error("Exception thrown while parsing error response: " + httpErrorException.getErrorResponse() , e);
			}
		}
		return ppre;
	}
	
	public String toString() {
		return "response-code: " + this.responsecode + "\tdetails: " + this.details;
	}

}