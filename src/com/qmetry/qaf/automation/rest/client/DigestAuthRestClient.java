package com.qmetry.qaf.automation.rest.client;

import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.ws.rest.DefaultRestClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;

public class DigestAuthRestClient extends DefaultRestClient {

	public static final String REST_CLIENT_DIGEST_AUTH_USER = "rest.client.digest.auth.username";
	public static final String REST_CLIENT_DIGEST_AUTH_PASSWORD = "rest.client.digest.auth.password";

	@Override
	protected Client createClient() {
		Client client = super.createClient();
		client.addFilter(
				new HTTPDigestAuthFilter(ConfigurationManager.getBundle().getString(REST_CLIENT_DIGEST_AUTH_USER, ""),
						ConfigurationManager.getBundle().getString(REST_CLIENT_DIGEST_AUTH_PASSWORD, "")));
		return client;
	}
}
