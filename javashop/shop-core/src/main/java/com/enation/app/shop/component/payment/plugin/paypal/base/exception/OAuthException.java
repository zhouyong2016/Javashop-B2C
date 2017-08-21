/*
 * Copyright 2005 PayPal, Inc. All Rights Reserved.
 */

package com.enation.app.shop.component.payment.plugin.paypal.base.exception;


public class OAuthException extends PayPalException{
    
    /**
     * Constructs a new exception with the specified detail message.
     */
    public OAuthException(String message)
    {
    	super(message);
    	this.message = message;
    }
    
    public OAuthException(String message, Throwable throwable) {
    	super(message, throwable);
    	this.message = message;
    }
    
    public String getError() {
    	return message;
    }
    
    private String message;
}
