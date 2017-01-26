/*******************************************************************************
  QMetry Automation Framework provides a powerful and versatile platform to author 
  Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven approach
                 
  Copyright 2016 Infostretch Corporation
 
  This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 
  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
  OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 
  You should have received a copy of the GNU General Public License along with this program in the name of LICENSE.txt in the root folder of the distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 
  See the NOTICE.TXT file in root folder of this source files distribution 
  for additional information regarding copyright ownership and licenses
  of other open source software / files used by QMetry Automation Framework.
 
  For any inquiry or need additional information, please contact support-qaf@infostretch.com
 *******************************************************************************/

package com.qmetry.qaf.automation.step;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;
import static com.qmetry.qaf.automation.util.Validator.assertThat;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.hamcrest.Matchers;

import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * com.qmetry.qaf.automation.step.CommonStep.java
 * 
 * @author chirag
 */
public final class WsStep {

	/**
	 * sets the service end point URL
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * service endpoint is 'http://feeds.feedburner.com/InfostretchMobileAndQaBlog'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param endpoint
	 *            : {0} : The URL to be set as end point
	 */
	@QAFTestStep(description = "service endpoint is {endpoint}")
	public static void setServiceEndPoint(String endpoint) {
		getBundle().setProperty("ws.endurl", endpoint);
	}

	/**
	 * This method request for resource to the web service by passing the
	 * required parameters.
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * user request for resource 'resource' with 'params'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param resource
	 *            : {0} : resource String
	 * @param params
	 *            : {1} : parameters
	 */
	@QAFTestStep(stepName = "requestForResourceWithParams", description = "user request for resource {resource} with {params}")
	public static void requestForResource(String resource, Map<String, String> params) {
		requestFor(resource, params);
	}

	private static void requestFor(String resource, Map<String, String> params) {
		WebResource webResource = new RestTestBase().getWebResource(
				getBundle().getString("ws.endurl",
						ApplicationProperties.SELENIUM_BASE_URL.getStringVal()),
				resource);
		if (null != params && !params.isEmpty()) {
			MultivaluedMap<String, String> mparams = new MultivaluedMapImpl();

			for (String key : params.keySet()) {
				mparams.add(key, params.get(key));
			}
			webResource = webResource.queryParams(mparams);
		}
		webResource.get(ClientResponse.class);
	}

	/**
	 * This method request for resource to the web service.
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * user request for resource 'Resource String'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param resource
	 *            : {0} : resource String
	 */
	@QAFTestStep(description = "user request for resource {resource}")
	public static void requestForResource(String resource) {
		requestFor(resource, null);
	}

	/**
	 * This method post the content through the web service.
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * user post 'postContent' for resource 'resource'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param content
	 *            : {0} : content to be posted to service end point
	 * @param resource
	 *            : {1} : resource string
	 */
	@QAFTestStep(description = "user post {content} for resource {resource}")
	public static void postContent(String content, String resource) {
		new RestTestBase().getWebResource(getBundle().getString("ws.endurl"), resource)
				.post(content);
	}

	/**
	 * This method check for the response status of web service
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * response should have status 'ResponceStatus'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param status
	 *            : {0} : Status String for Exampe: OK, CREATED
	 * @see Status
	 */
	@QAFTestStep(description = "response should have status {status}")
	public static void response_should_have_status(String status) {
		assertThat("Response Status", new RestTestBase().getResponse().getStatus().name(),
				Matchers.equalToIgnoringCase(status));
	}

	/**
	 * This method check for the response status of web service
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * response should have status 'ResponceStatus'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param status
	 *            : {0} : Status code, for example 200, 301
	 * @see Status
	 */
	@QAFTestStep(description = "response should have status code {statusCode}")
	public static void response_should_have_statuscode(int statusCode) {
		assertThat("Response Status",
				new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.equalTo(statusCode));
	}

	/**
	 * This method check given Xpath is there in response status of web service
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * response should have xpath 'Xpath String'<br/>
	 * </code>
	 * <p>
	 * KWD
	 * </p>
	 * 
	 * @param xpath
	 *            : {0} : xpath string to be verified in respose
	 */
	@QAFTestStep(description = "response should have xpath {xpath}")
	public static void response_should_have_xpath(String xpath) {
		assertThat(the(new RestTestBase().getResponse().getMessageBody()),
				hasXPath(xpath));
	}
}
