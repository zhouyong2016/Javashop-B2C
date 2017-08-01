package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

import java.util.HashMap;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;
import com.enation.app.shop.component.payment.plugin.paypal.base.sdk.info.SDKVersionImpl;

public class PayoutItem extends PayPalResource {

	/**
	 * The type of identification for the payment receiver. If this field is
	 * provided, the payout items without a `recipient_type` will use the
	 * provided value. If this field is not provided, each payout item must
	 * include a value for the `recipient_type`.
	 */
	private String recipientType;

	/**
	 * The amount of money to pay a receiver.
	 */
	private Currency amount;

	/**
	 * Note for notifications. The note is provided by the payment sender. This
	 * note can be any string. 4000 characters max.
	 */
	private String note;

	/**
	 * The receiver of the payment. In a call response, the format of this value
	 * corresponds to the `recipient_type` specified in the request. 127
	 * characters max.
	 */
	private String receiver;

	/**
	 * A sender-specific ID number, used in an accounting system for tracking
	 * purposes. 30 characters max.
	 */
	private String senderItemId;
	
	/**
	 * Default Constructor
	 */
	public PayoutItem() {
	}

	/**
	 * Parameterized Constructor
	 */
	public PayoutItem(Currency amount, String receiver) {
		this.amount = amount;
		this.receiver = receiver;
	}

	/**
	 * Setter for recipientType
	 */
	public PayoutItem setRecipientType(String recipientType) {
		this.recipientType = recipientType;
		return this;
	}

	/**
	 * Getter for recipientType
	 */
	public String getRecipientType() {
		return this.recipientType;
	}

	/**
	 * Setter for amount
	 */
	public PayoutItem setAmount(Currency amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * Getter for amount
	 */
	public Currency getAmount() {
		return this.amount;
	}

	/**
	 * Setter for note
	 */
	public PayoutItem setNote(String note) {
		this.note = note;
		return this;
	}

	/**
	 * Getter for note
	 */
	public String getNote() {
		return this.note;
	}

	/**
	 * Setter for receiver
	 */
	public PayoutItem setReceiver(String receiver) {
		this.receiver = receiver;
		return this;
	}

	/**
	 * Getter for receiver
	 */
	public String getReceiver() {
		return this.receiver;
	}

	/**
	 * Setter for senderItemId
	 */
	public PayoutItem setSenderItemId(String senderItemId) {
		this.senderItemId = senderItemId;
		return this;
	}

	/**
	 * Getter for senderItemId
	 */
	public String getSenderItemId() {
		return this.senderItemId;
	}

	/**
	 * Obtain the status of a payout item by passing the item ID to the request
	 * URI.
	 * 
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param payoutItemId
	 *            String
	 * @return PayoutItemDetails
	 * @throws PayPalRESTException
	 */
	public static PayoutItemDetails get(String accessToken, String payoutItemId)
			throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return get(apiContext, payoutItemId);
	}

	/**
	 * Obtain the status of a payout item by passing the item ID to the request
	 * URI.
	 * 
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param payoutItemId
	 *            String
	 * @return PayoutItemDetails
	 * @throws PayPalRESTException
	 */
	public static PayoutItemDetails get(APIContext apiContext,
			String payoutItemId) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null
				|| apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException(
					"AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER,
				Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());
		if (payoutItemId == null) {
			throw new IllegalArgumentException("payoutItemId cannot be null");
		}
		Object[] parameters = new Object[] { payoutItemId };
		String pattern = "v1/payments/payouts-item/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET,
				resourcePath, payLoad, PayoutItemDetails.class);
	}

	/**
	 * Cancels the unclaimed payment using the items id passed in the request
	 * URI. If an unclaimed item is not claimed within 30 days, the funds will
	 * be automatically returned to the sender. This call can be used to cancel
	 * the unclaimed item prior to the automatic 30-day return.
	 * 
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param payoutItemId
	 *            String
	 * @return PayoutItemDetails
	 * @throws PayPalRESTException
	 */
	public static PayoutItemDetails cancel(String accessToken,
			String payoutItemId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return cancel(apiContext, payoutItemId);
	}

	/**
	 * Cancels the unclaimed payment using the items id passed in the request
	 * URI. If an unclaimed item is not claimed within 30 days, the funds will
	 * be automatically returned to the sender. This call can be used to cancel
	 * the unclaimed item prior to the automatic 30-day return.
	 * 
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param payoutItemId
	 *            String
	 * @return PayoutItemDetails
	 * @throws PayPalRESTException
	 */
	public static PayoutItemDetails cancel(APIContext apiContext,
			String payoutItemId) throws PayPalRESTException {
		if (apiContext == null) {
			throw new IllegalArgumentException("APIContext cannot be null");
		}
		if (apiContext.getAccessToken() == null
				|| apiContext.getAccessToken().trim().length() <= 0) {
			throw new IllegalArgumentException(
					"AccessToken cannot be null or empty");
		}
		if (apiContext.getHTTPHeaders() == null) {
			apiContext.setHTTPHeaders(new HashMap<String, String>());
		}
		apiContext.getHTTPHeaders().put(Constants.HTTP_CONTENT_TYPE_HEADER,
				Constants.HTTP_CONTENT_TYPE_JSON);
		apiContext.setSdkVersion(new SDKVersionImpl());
		if (payoutItemId == null) {
			throw new IllegalArgumentException("payoutItemId cannot be null");
		}
		Object[] parameters = new Object[] { payoutItemId };
		String pattern = "v1/payments/payouts-item/{0}/cancel";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.POST,
				resourcePath, payLoad, PayoutItemDetails.class);
	}
	
}
