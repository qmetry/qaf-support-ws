package com.qmetry.qaf.automation.ws.client;

import com.qmetry.qaf.automation.ws.rest.DefaultRestClient;
import com.sun.jersey.api.client.Client;
import com.wealdtech.hawk.HawkClient;
import com.wealdtech.hawk.HawkCredentials;
import com.wealdtech.hawk.jersey.HawkAuthorizationFilter;

public class HawkAuthWsClient extends DefaultRestClient {

	public static final String REST_CLIENT_HAWK_KEY_ID = "rest.client.hawk.auth.keyId";
	public static final String REST_CLIENT_HAWK_KEY = "rest.client.hawk.auth.key";

	@Override
	protected Client createClient() {

		HawkCredentials hawkCredentials = new HawkCredentials.Builder()
				.keyId(REST_CLIENT_HAWK_KEY_ID).key(REST_CLIENT_HAWK_KEY)
				.algorithm(HawkCredentials.Algorithm.SHA256).build();

		HawkClient hawkClient =
				new HawkClient.Builder().credentials(hawkCredentials).build();

		Client client = super.createClient();
		client.addFilter(new HawkAuthorizationFilter(hawkClient));
		return client;
	}
}