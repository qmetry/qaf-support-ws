/*******************************************************************************
 * QMetry Automation Framework provides a powerful and versatile platform to
 * author
 * Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven
 * approach
 * Copyright 2016 Infostretch Corporation
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 * You should have received a copy of the GNU General Public License along with
 * this program in the name of LICENSE.txt in the root folder of the
 * distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 * See the NOTICE.TXT file in root folder of this source files distribution
 * for additional information regarding copyright ownership and licenses
 * of other open source software / files used by QMetry Automation Framework.
 * For any inquiry or need additional information, please contact
 * support-qaf@infostretch.com
 *******************************************************************************/

package com.qmetry.qaf.automation.rest.client;

import java.util.Arrays;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.qmetry.qaf.automation.ws.rest.RestClientFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;

/**
 * Jersey client to support NTLM authentication. This class uses following properties:
 *<ul>
 *<li><code>ntlm.user </code>- The user name. This should not include the domain to authenticate with. For example: "user" is correct whereas "DOMAIN\\user" is not.
 *<li><code>ntlm.password </code>- The password
 *<li><code>ntlm.workstation </code>- workstation (default is blank) The workstation the authentication request is originating from. Essentially, the computer name for this machine.
 *<li><code>ntlm.domain</code>- domain The domain to authenticate within (default is blank).
 *</ul>
 *
 * For NTLM authentication, register this class using <code>rest.client.impl</code> property as below:
 * <p>
 * <code>rest.client.impl=com.qmetry.qaf.automation.rest.client.NTLMAuthClient</code>
 * 
 * @author Chirag Jayswal
 * @since 2.1.12
 */
public class NTLMAuthClient extends RestClientFactory {

	private static final String USERNAME = getBundle().getString("ntlm.user");
	private static final String PASSWORD = getBundle().getString("ntlm.password");
	private static final String WORKSTATION = getBundle().getString("ntlm.workstation","");
	private static final String DOMAIN = getBundle().getString("ntlm.domain","");

	@Override
	protected Client createClient() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000)
				.setConnectionRequestTimeout(30000)
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
				.build();
		
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new NTCredentials(USERNAME, PASSWORD, WORKSTATION, DOMAIN));
		
		CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(cm)
				.setDefaultCredentialsProvider(credentialsProvider)
				.setDefaultRequestConfig(requestConfig)
				.build();
		
		ApacheHttpClient4Handler root = new ApacheHttpClient4Handler(closeableHttpClient, new BasicCookieStore(), false);
		Client client = new Client(root);
		return client;
	}

}
