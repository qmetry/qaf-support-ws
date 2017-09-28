package com.qmetry.qaf.automation.rest.client;

import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.rest.auth.oauth.OAuth2Details;
import com.qmetry.qaf.automation.rest.auth.oauth.OAuthConstants;
import com.qmetry.qaf.automation.rest.auth.oauth.OAuthUtils;
import com.qmetry.qaf.automation.util.StringUtil;
import com.qmetry.qaf.automation.ws.rest.DefaultRestClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

public class OAuthRestClient extends DefaultRestClient {
	
	@Override
	protected Client createClient() {
		Client client = super.createClient();
		client.addFilter(new ClientFilter() {

			public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
				cr.getHeaders().add(OAuthConstants.AUTHORIZATION, OAuthUtils
						.getAuthorizationHeaderForAccessToken(getAccessToken()));
				return getNext().handle(cr);
			}

			private String getAccessToken() {
				String accessToken = ConfigurationManager.getBundle()
						.getString(OAuthConstants.ACCESS_TOKEN);
				if (StringUtil.isEmpty(accessToken)) {
					// Generate the OAuthDetails bean from the config properties
					// file
					OAuth2Details oauthDetails = OAuthUtils
							.createOAuthDetails(ConfigurationManager.getBundle());

					System.out.println(oauthDetails);
					// Validate Input
					if (!OAuthUtils.isValidInput(oauthDetails)) {
						System.out.println(
								"Please provide valid config properties to continue.");
						System.exit(0);
					}

					// Generate new Access token
					accessToken = OAuthUtils.getAccessToken(oauthDetails);

					if (OAuthUtils.isValid(accessToken)) {
						ConfigurationManager.getBundle()
								.setProperty(OAuthConstants.ACCESS_TOKEN, accessToken);
						System.out.println(
								"Successfully generated Access token for client_credentials grant_type: "
										+ accessToken);
					} else {
						System.out.println(
								"Could not generate Access token for client_credentials grant_type");
					}
				}
				return accessToken;
			}
		});
		return client;
	}
}
