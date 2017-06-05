package com.qmetry.qaf.automation.rest.auth.oauth;

import org.apache.commons.codec.binary.Base64;

public class Base64Encoded {

	public static void main(String...strings){
		
		String clientId = "SuperOAuthArticle";
		String clientSecret = "YourFeedbackIsAppreciated";
		System.out.println(encodeCredentials(clientId, clientSecret));
		
	}
	
	public static String encodeCredentials(String username, String password) {
		String cred = username + ":" + password;
		String encodedValue = null;
		byte[] encodedBytes = Base64.encodeBase64(cred.getBytes());
		encodedValue = new String(encodedBytes);
		System.out.println("encodedBytes " + new String(encodedBytes));

		byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
		System.out.println("decodedBytes " + new String(decodedBytes));

		return encodedValue;

	}

}
