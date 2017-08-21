package com.enation.app.shop.component.payment.plugin.paypal.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.enation.app.shop.component.payment.plugin.paypal.base.credential.CertificateCredential;
import com.enation.app.shop.component.payment.plugin.paypal.base.credential.ICredential;
import com.enation.app.shop.component.payment.plugin.paypal.base.credential.SignatureCredential;
import com.enation.app.shop.component.payment.plugin.paypal.base.credential.SubjectAuthorization;
import com.enation.app.shop.component.payment.plugin.paypal.base.credential.ThirdPartyAuthorization;
import com.enation.app.shop.component.payment.plugin.paypal.base.exception.InvalidCredentialException;
import com.enation.app.shop.component.payment.plugin.paypal.base.exception.MissingCredentialException;

import java.util.Set;

/**
 * <code>CredentialManager</code> acts as a factory for loading
 * {@link ICredential} credential configured in application properties
 * 
 */
public final class CredentialManager {

	/*
	 * Map used for to override ConfigManager configurations
	 */
	private Map<String, String> configurationMap = null;

	/**
	 * Credential Manager
	 * 
	 * @param configurationMap
	 *            {@link Map}
	 */
	public CredentialManager(Map<String, String> configurationMap) {
		if (configurationMap == null) {
			throw new IllegalArgumentException(
					"ConfigurationMap cannot be null");
		}
		this.configurationMap = configurationMap;
	}

	public ICredential getCredentialObject(String userId)
			throws MissingCredentialException, InvalidCredentialException {
		ICredential credential = null;
		if (getAccounts(configurationMap).size() == 0) {
			throw new MissingCredentialException(
					"No API accounts have been configured in application properties");
		}
		String prefix = Constants.ACCOUNT_PREFIX;
		Map<String, String> credMap = getValuesByCategory(configurationMap,
				prefix);
		if (userId != null && userId.trim().length() != 0) {
			for (Entry<String, String> entry : credMap.entrySet()) {
				if (entry.getKey().endsWith(
						Constants.CREDENTIAL_USERNAME_SUFFIX)
						&& entry.getValue().equalsIgnoreCase(userId)) {
					String acctKey = entry.getKey().substring(0,
							entry.getKey().indexOf('.'));
					credential = returnCredential(credMap, acctKey);
				}
			}
			if (credential == null) {
				throw new MissingCredentialException(
						"Account for the username does not exists in the properties file");
			}
		} else {
			int index = 1;
			String userName = (String) credMap.get(prefix + index
					+ Constants.CREDENTIAL_USERNAME_SUFFIX);
			if (userName != null && userName.trim().length() != 0) {
				credential = returnCredential(credMap, prefix + index);
			} else {
				throw new MissingCredentialException(
						"Associate valid account for index 1");
			}
		}
		return credential;
	}

	private Set<String> getAccounts(Map<String, String> configurationMap) {
		String key = Constants.EMPTY_STRING;
		Set<String> set = new HashSet<String>();
		for (Object obj : configurationMap.keySet()) {
			key = (String) obj;
			if (key.contains("acct")) {
				int pos = key.indexOf('.');
				String acct = key.substring(0, pos);
				set.add(acct);
			}
		}
		return set;
	}

	private Map<String, String> getValuesByCategory(
			Map<String, String> configurationMap, String category) {
		String key = Constants.EMPTY_STRING;
		HashMap<String, String> map = new HashMap<String, String>();
		for (Object obj : configurationMap.keySet()) {
			key = (String) obj;
			if (key.contains(category)) {
				map.put(key, configurationMap.get(key));
			}
		}
		return map;
	}

	private ICredential returnCredential(Map<String, String> credMap,
			String acctKey) throws InvalidCredentialException {
		ICredential credential = null;
		String userName = (String) credMap.get(acctKey
				+ Constants.CREDENTIAL_USERNAME_SUFFIX);
		String password = (String) credMap.get(acctKey
				+ Constants.CREDENTIAL_PASSWORD_SUFFIX);
		String appId = (String) credMap.get(acctKey
				+ Constants.CREDENTIAL_APPLICATIONID_SUFFIX);
		String subject = (String) credMap.get(acctKey
				+ Constants.CREDENTIAL_SUBJECT_SUFFIX);
		if (credMap.get(acctKey + Constants.CREDENTIAL_SIGNATURE_SUFFIX) != null) {
			String signature = (String) credMap.get(acctKey
					+ Constants.CREDENTIAL_SIGNATURE_SUFFIX);
			credential = new SignatureCredential(userName, password, signature);
			((SignatureCredential) credential).setApplicationId(appId);
			if (subject != null && subject.trim().length() > 0) {
				ThirdPartyAuthorization thirdPartyAuthorization = new SubjectAuthorization(
						subject);
				((SignatureCredential) credential)
						.setThirdPartyAuthorization(thirdPartyAuthorization);
			}
		} else if (credMap.get(acctKey + Constants.CREDENTIAL_CERTPATH_SUFFIX) != null) {
			String certPath = (String) credMap.get(acctKey
					+ Constants.CREDENTIAL_CERTPATH_SUFFIX);
			String certKey = (String) credMap.get(acctKey
					+ Constants.CREDENTIAL_CERTKEY_SUFFIX);
			credential = new CertificateCredential(userName, password,
					certPath, certKey);
			((CertificateCredential) credential).setApplicationId(appId);
			if (subject != null && subject.trim().length() > 0) {
				ThirdPartyAuthorization thirdPartyAuthorization = new SubjectAuthorization(
						subject);
				((CertificateCredential) credential)
						.setThirdPartyAuthorization(thirdPartyAuthorization);
			}
		} else {
			throw new InvalidCredentialException(
					"The account does not have a valid credential type(signature/certificate)");
		}
		return credential;

	}
}
