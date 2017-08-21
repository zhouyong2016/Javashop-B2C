package com.enation.app.shop.component.payment.plugin.paypal.api.openidconnect;

import java.util.HashMap;

import com.enation.app.shop.component.payment.plugin.paypal.base.Constants;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.HttpMethod;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.RESTUtil;

/**
 * Class Userinfo
 *
 */
public class Userinfo extends PayPalResource{

	/**
	 * Subject - Identifier for the End-User at the Issuer.
	 */
	private String userId;
	
	/**
	 * Subject - Identifier for the End-User at the Issuer.
	 */
	private String sub;
	
	/**
	 * End-User's full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences.
	 */
	private String name;
	
	/**
	 * Given name(s) or first name(s) of the End-User
	 */
	private String givenName;
	
	/**
	 * Surname(s) or last name(s) of the End-User.
	 */
	private String familyName;
	
	/**
	 * Middle name(s) of the End-User.
	 */
	private String middleName;
	
	/**
	 * URL of the End-User's profile picture.
	 */
	private String picture;
	
	/**
	 * End-User's preferred e-mail address.
	 */
	private String email;
	
	/**
	 * True if the End-User's e-mail address has been verified; otherwise false.
	 */
	private Boolean emailVerified;
	
	/**
	 * End-User's gender.
	 */
	private String gender;
	
	/**
	 * End-User's birthday, represented as an YYYY-MM-DD format. They year MAY be 0000, indicating it is omited. To represent only the year, YYYY format would be used.
	 */
	private String birthdate;
	
	/**
	 * Time zone database representing the End-User's time zone
	 */
	private String zoneinfo;
	
	/**
	 * End-User's locale.
	 */
	private String locale;
	
	/**
	 * End-User's preferred telephone number.
	 */
	private String phoneNumber;
	
	/**
	 * End-User's preferred address.
	 */
	private Address address;
	
	/**
	 * Verified account status.
	 */
	private Boolean verifiedAccount;
	
	/**
	 * Account type.
	 */
	private String accountType;
	
	/**
	 * Account holder age range.
	 */
	private String ageRange;
	
	/**
	 * Account payer identifier.
	 */
	private String payerId;

	/**
	 * Default Constructor
	 */
	public Userinfo() {
	}

	/**
	 * Setter for userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
 	}
 	
 	/**
	 * Getter for userId
	 */
	public String getUserId() {
		return this.userId;
	}
	
	/**
	 * Setter for sub
	 */
	public void setSub(String sub) {
		this.sub = sub;
 	}
 	
 	/**
	 * Getter for sub
	 */
	public String getSub() {
		return this.sub;
	}
	
	/**
	 * Setter for name
	 */
	public void setName(String name) {
		this.name = name;
 	}
 	
 	/**
	 * Getter for name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Setter for givenName
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
 	}
 	
 	/**
	 * Getter for givenName
	 */
	public String getGivenName() {
		return this.givenName;
	}
	
	/**
	 * Setter for familyName
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
 	}
 	
 	/**
	 * Getter for familyName
	 */
	public String getFamilyName() {
		return this.familyName;
	}
	
	/**
	 * Setter for middleName
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
 	}
 	
 	/**
	 * Getter for middleName
	 */
	public String getMiddleName() {
		return this.middleName;
	}
	
	/**
	 * Setter for picture
	 */
	public void setPicture(String picture) {
		this.picture = picture;
 	}
 	
 	/**
	 * Getter for picture
	 */
	public String getPicture() {
		return this.picture;
	}
	
	/**
	 * Setter for email
	 */
	public void setEmail(String email) {
		this.email = email;
 	}
 	
 	/**
	 * Getter for email
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Setter for emailVerified
	 */
	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
 	}
 	
 	/**
	 * Getter for emailVerified
	 */
	public Boolean getEmailVerified() {
		return this.emailVerified;
	}
	
	/**
	 * Setter for gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
 	}
 	
 	/**
	 * Getter for gender
	 */
	public String getGender() {
		return this.gender;
	}
	
	/**
	 * Setter for birthdate
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
 	}
 	
 	/**
	 * Getter for birthdate
	 */
	public String getBirthdate() {
		return this.birthdate;
	}
	
	/**
	 * Setter for zoneinfo
	 */
	public void setZoneinfo(String zoneinfo) {
		this.zoneinfo = zoneinfo;
 	}
 	
 	/**
	 * Getter for zoneinfo
	 */
	public String getZoneinfo() {
		return this.zoneinfo;
	}
	
	/**
	 * Setter for locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
 	}
 	
 	/**
	 * Getter for locale
	 */
	public String getLocale() {
		return this.locale;
	}
	
	/**
	 * Setter for phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
 	}
 	
 	/**
	 * Getter for phoneNumber
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	/**
	 * Setter for address
	 */
	public void setAddress(Address address) {
		this.address = address;
 	}
 	
 	/**
	 * Getter for address
	 */
	public Address getAddress() {
		return this.address;
	}
	
	/**
	 * Setter for verifiedAccount
	 */
	public void setVerifiedAccount(Boolean verifiedAccount) {
		this.verifiedAccount = verifiedAccount;
 	}
 	
 	/**
	 * Getter for verifiedAccount
	 */
	public Boolean getVerifiedAccount() {
		return this.verifiedAccount;
	}
	
	/**
	 * Setter for accountType
	 * @param accountType 
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
 	}
 	
 	/**
	 * Getter for accountType
	 */
	public String getAccountType() {
		return this.accountType;
	}
	
	/**
	 * Setter for ageRange
	 */
	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
 	}
 	
 	/**
	 * Getter for ageRange
	 */
	public String getAgeRange() {
		return this.ageRange;
	}
	
	/**
	 * Setter for payerId
	 */
	public void setPayerId(String payerId) {
		this.payerId = payerId;
 	}
 	
 	/**
	 * Getter for payerId
	 */
	public String getPayerId() {
		return this.payerId;
	}
	
	
	/**
	 * Returns user details
	 * 
	 * @param accessToken
	 *            access token
	 * @return Userinfo
	 * @throws PayPalRESTException
	 */
	public static Userinfo getUserinfo(String accessToken)
			throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return getUserinfo(apiContext);
	}

	/**
	 * Returns user details
	 * 
	 * @param apiContext
	 *            {@link APIContext} to be used for the call.
	 * @return Userinfo
	 * @throws PayPalRESTException
	 */
	public static Userinfo getUserinfo(APIContext apiContext) throws PayPalRESTException {
		String resourcePath = "v1/identity/openidconnect/userinfo?schema=openid";
		String payLoad = "";
		String accessToken = apiContext.getAccessToken();
		HashMap<String, String> httpHeaders = new HashMap<String, String>();
		if (!accessToken.startsWith("Bearer ")) {
			accessToken = "Bearer " + accessToken;
		}
		httpHeaders.put(Constants.AUTHORIZATION_HEADER, accessToken);
		apiContext.setHTTPHeaders(httpHeaders);
		return configureAndExecute(apiContext, HttpMethod.GET,
				resourcePath, payLoad, Userinfo.class);
	}

}