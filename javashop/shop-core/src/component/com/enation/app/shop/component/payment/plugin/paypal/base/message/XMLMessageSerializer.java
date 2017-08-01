package com.enation.app.shop.component.payment.plugin.paypal.base.message;

/**
 * Serializer for XML
 * 
 * @author kjayakumar
 * 
 */
public interface XMLMessageSerializer {

	/**
	 * Serialize the object as XML with namespaces
	 * 
	 * @return Serialized object as XML String
	 */
	String toXMLString();

}
