package com.enation.app.shop.component.payment.plugin.paypal.base;

import com.enation.app.shop.component.payment.plugin.paypal.base.credential.ICredential;

/**
 * A Strategy pattern to retrieve {@link ICredential} as any conceivable
 * datatype as required by the application
 * 
 * @param <T>
 *            Return data type
 * @param <E>
 *            Operated {@link ICredential}
 */
public interface AuthenticationStrategy<T, E extends ICredential> {

	/**
	 * Generates Headers {@link ICredential} as any type as chosen by the
	 * implementation
	 * 
	 * @param e
	 *            {@link ICredential} instance
	 * @return T
	 * @throws Exception
	 */
	T generateHeaderStrategy(E e) throws Exception;

}
