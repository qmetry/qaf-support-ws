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

package com.qmetry.qaf.automation.step;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;
import static com.qmetry.qaf.automation.util.Validator.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.hamcrest.Matchers;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.qmetry.qaf.automation.core.AutomationError;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.core.MessageTypes;
import com.qmetry.qaf.automation.rest.RestRequestBean;
import com.qmetry.qaf.automation.rest.WSCRepositoryConstants;
import com.qmetry.qaf.automation.util.JSONUtil;
import com.qmetry.qaf.automation.util.Reporter;
import com.qmetry.qaf.automation.util.StringUtil;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * com.qmetry.qaf.automation.step.CommonStep.java
 * 
 * @author chirag
 */
public final class WsStep {

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
	public static void responseShouldHaveStatus(String status) {
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
	public static void responseShouldHaveStatusCode(int statusCode) {
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
	public static void responseShouldHaveXpath(String xpath) {
		assertThat(the(new RestTestBase().getResponse().getMessageBody()),
				hasXPath(xpath));
	}

	/**
	 * This method request for the given parameters
	 * 
	 * @param request
	 *            key or map
	 */
	@QAFTestStep(description = "user requests {0}")
	public static void userRequests(Object request) {
		RestRequestBean bean = new RestRequestBean();
		if (request instanceof String) {
			request = JSONUtil.toMap(getBundle().getString(String.valueOf(request),
					String.valueOf(request)));
		}
		try {
			Gson gson = new Gson();
			String json = gson.toJson(request);
			json = getBundle().getSubstitutor().replace(json);
			bean = gson.fromJson(json, RestRequestBean.class);
		} catch (Exception e) {
			throw new AutomationError("Unable to populate request bean", e);
		}
		request(bean);
	}

	/**
	 * This method request for given parameters with given dataset
	 * 
	 * @param request
	 *            key or map
	 * @param data
	 *            data set of key value pair
	 */
	@QAFTestStep(description = "user requests {request} with data {data}", stepName = "userRequestsWithData")
	public static void userRequests(Object request, Map<String, Object> data) {
		RestRequestBean bean = new RestRequestBean();
		if (request instanceof String) {
			request = JSONUtil.toMap(getBundle().getString(String.valueOf(request),
					String.valueOf(request)));
		}
		try {
			Gson gson = new Gson();
			String json = gson.toJson(request);
			json = getBundle().getSubstitutor().replace(json);
			json = StrSubstitutor.replace(json, data);
			bean = gson.fromJson(json, RestRequestBean.class);
		} catch (Exception e) {
			throw new AutomationError("Unable to populate request bean", e);
		}
		request(bean);
	}

	/**
	 * This method check given header is there in response of web service
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * response should have header 'Content-Type'<br/>
	 * </code> <br/>
	 * 
	 * @param xpath
	 *            : header to be verified in respose
	 */
	@QAFTestStep(description = "response should have header {0}")
	public static void responseShouldHaveHeader(String header) {
		assertThat(new RestTestBase().getResponse().getHeaders(),
				Matchers.hasKey(header));
	}

	/**
	 * This method check given header with specific value present in response of
	 * web
	 * service
	 * <p>
	 * Example:
	 * <p>
	 * BDD
	 * </p>
	 * <code>
	 * response should have header 'Content-Type' with value 'application/json'<br/>
	 * </code> <br/>
	 * 
	 * @param header
	 *            : header to be present in respose
	 * @param value
	 *            : value to be verified for header
	 */
	@QAFTestStep(description = "response should have header {0} with value {1}")
	public static void responseShouldHaveHeaderWithValue(String header, String value) {
		assertThat(new RestTestBase().getResponse().getHeaders(),
				hasEntry(equalTo(header), Matchers.hasItem(value)));
	}

	/**
	 * This method check given jsonpath is there in response of web service
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should have key 'user.username' with value 'admin'
	 * </code>
	 * <p />
	 * 
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should have {jsonpath}")
	public static void responseShouldHaveJsonPath(String path) {
		if (!path.startsWith("$"))
			path = "$." + path;
		assertThat("Response Body has " + path,
				hasJsonPath(new RestTestBase().getResponse().getMessageBody(), path),
				Matchers.equalTo(true));
	}

	/**
	 * This method check given jsonpath is not in response of web service
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should not have 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should not have {jsonpath}")
	public static void responseShouldNotHaveJsonPath(String path) {
		if (!path.startsWith("$"))
			path = "$." + path;
		assertThat("Response Body has not " + path,
				hasJsonPath(new RestTestBase().getResponse().getMessageBody(), path),
				Matchers.equalTo(false));
	}

	/**
	 * This method validates value for given jsonpath
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should have 'admin' at 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param expectedValue
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should have {expectedvalue} at {jsonpath}")
	public static void responseShouldHaveKeyWithValue(Object expectedValue, String path) {
		if (!path.startsWith("$"))
			path = "$." + path;
		Object actual =
				JsonPath.read(new RestTestBase().getResponse().getMessageBody(), path);
		if (Number.class.isAssignableFrom(actual.getClass())) {
			assertThat(new BigDecimal(String.valueOf(actual)),
					Matchers.equalTo(new BigDecimal(String.valueOf(expectedValue))));
		} else {
			assertThat(actual, Matchers.equalTo((Object) expectedValue));
		}
	}

	/**
	 * This method store value of given json path to
	 * {@link ConfigurationManager}
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * store response body 'user.username' to 'loginuser'
	 * </code>
	 * <p />
	 * 
	 * @param path
	 *            jsonpath
	 * @param variable
	 *            variable that can be use later
	 */
	@QAFTestStep(description = "store response body {0} (in)to {1}")
	public static void storeResponseBodyto(String path, String variable) {
		if (!path.startsWith("$"))
			path = "$." + path;
		Object value =
				JsonPath.read(new RestTestBase().getResponse().getMessageBody(), path);
		getBundle().setProperty(variable, value);
	}

	/**
	 * This method validates value of jsonpath contains expected value or not
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should have value contains 'admin' at 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should have value contains {expectedvalue} at {jsonpath}")
	public static void responseShouldHaveKeyAndValueContains(String value, String path) {
		if (!path.startsWith("$"))
			path = "$." + path;
		Object actual =
				JsonPath.read(new RestTestBase().getResponse().getMessageBody(), path);
		assertThat(String.valueOf(actual), Matchers.containsString(value));
	}

	/**
	 * This method store value of given header to {@link ConfigurationManager}
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * store response header 'X-Auth-token' to 'token'
	 * </code>
	 * <p />
	 * 
	 * @param header
	 *            header
	 * @param property
	 *            variable that can be use later
	 */
	@QAFTestStep(description = "store response header {0} (in)to {1}")
	public static void storeResponseHeaderTo(String header, String property) {
		getBundle().setProperty(property,
				new RestTestBase().getResponse().getHeaders().get(header));
	}

	/**
	 * This method validates that value at jsonpath should be less than
	 * expectedvalue
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should be less than 500 at 'student.totalmarks'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should be less than {expectedvalue} at {jsonpath}")
	public static void responseShouldLessThan(double expectedValue, String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(Double.parseDouble(String.valueOf(actual)),
				Matchers.lessThan(expectedValue));
	}

	/**
	 * This method validates that value at jsonpath should be less than or equal
	 * to
	 * expectedvalue
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should be less than or equals to 500 at 'student.totalmarks'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should be less than or equals to {expectedvalue} at {jsonpath}")
	public static void responseShouldLessThanOrEqualsTo(double expectedValue,
			String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(Double.parseDouble(String.valueOf(actual)),
				Matchers.lessThanOrEqualTo(expectedValue));
	}

	/**
	 * This method validates that value at jsonpath should be greater than
	 * expectedvalue
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should be greater than 500 at 'student.totalmarks'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should be greater than {expectedvalue} at {jsonpath}")
	public static void responseShouldGreaterThan(double expectedValue, String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(Double.parseDouble(String.valueOf(actual)),
				Matchers.greaterThan(expectedValue));
	}

	/**
	 * This method validates that value at jsonpath should be greater than or
	 * equal
	 * to expectedvalue
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should be greater than or equals to 500 at 'student.totalmarks'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should be greater than or equals to {expectedvalue} at {jsonpath}")
	public static void responseShouldGreaterThanOrEqualsTo(double expectedValue,
			String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(Double.parseDouble(String.valueOf(actual)),
				Matchers.greaterThanOrEqualTo(expectedValue));
	}

	/**
	 * This method validates value at jsonpath equals to expected value with
	 * ignoring case or not
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should have value ignoring case 'admin' at 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should have value ignoring case {expectedvalue} at {jsonpath}")
	public static void responseShouldHaveValueIgnoringCase(String expectedValue,
			String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(String.valueOf(actual), Matchers.equalToIgnoringCase(expectedValue));
	}

	/**
	 * This method validates value at jsonpath contains expected value with
	 * ignoring
	 * case or not
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should have value contains ignoring case 'admin' at 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should have value contains ignoring case {expectedvalue} at {jsonpath}")
	public static void responseShouldHaveValueContainsIgnoringCase(String expectedValue,
			String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(String.valueOf(actual).toUpperCase(),
				Matchers.containsString(expectedValue.toUpperCase()));
	}

	/**
	 * This method validates value of jsonpath matches with regEx value or not
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should have value matches with '[a-z]*' at 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should have value matches with {regEx} at {jsonpath}")
	public static void responseShouldHaveValueMatchesWith(String regEx, String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(String.valueOf(actual).matches(regEx), Matchers.equalTo(true));
	}

	/**
	 * This method validates value of jsonpath is not equal to expected value
	 * <p>
	 * Example:
	 * </p>
	 * <code>
	 * response should not have value 'admin' at 'user.username'
	 * </code>
	 * <p />
	 * 
	 * @param value
	 *            : expected value
	 * @param path
	 *            : jsonpath
	 */
	@QAFTestStep(description = "response should not have value {expectedvalue} at {jsonpath}")
	public static void responseShouldNotHaveValue(Object expectedValue, String path) {
		Object actual = JsonPath.read(new RestTestBase().getResponse().getMessageBody(),
				getPath(path));
		assertThat(actual, Matchers.not(expectedValue));
	}

	@QAFTestStep(description = "verify response schema for {0}")
	public static boolean verifyResponseSchema(String requestKey) {
		ProcessingReport result = null;
		try {
			com.fasterxml.jackson.databind.JsonNode responseNode = JsonLoader
					.fromString(new RestTestBase().getResponse().getMessageBody());
			Map<String, Object> map = JSONUtil.toMap(
					ConfigurationManager.getBundle().getString(requestKey, requestKey));
			Object responseSchema = map.get(WSCRepositoryConstants.RESPONSE_SCHEMA);
			if (responseSchema instanceof Map) {
				responseSchema = new Gson().toJson(responseSchema);
			} else {
				File file = new File(responseSchema.toString());
				if (file.exists())
					responseSchema = FileUtils.readFileToString(file, "UTF-8");
			}
			com.fasterxml.jackson.databind.JsonNode schemaNode =
					JsonLoader.fromString(String.valueOf(responseSchema));
			JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
			result = schema.validate(responseNode);
			if (!result.isSuccess()) {
				for (ProcessingMessage processingMessage : result) {
					Reporter.log(processingMessage.getMessage(), MessageTypes.Fail);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProcessingException e) {
			e.printStackTrace();
		}
		return result != null && result.isSuccess();
	}

	// move to rest test-base
	public static void request(RestRequestBean bean) {

		WebResource resource =
				new RestTestBase().getWebResource(bean.getBaseUrl(), bean.getEndPoint());

		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

		for (Entry<String, Object> entry : bean.getQueryParameters().entrySet()) {
			queryParams.add(entry.getKey(), entry.getValue().toString());
		}
		resource = resource.queryParams(queryParams);

		Builder builder = resource.getRequestBuilder();

		for (Entry<String, Object> header : bean.getHeaders().entrySet()) {
			if (header.getKey().equalsIgnoreCase("Accept")) {
				builder.accept((String) header.getValue());
			}
			builder.header(header.getKey(), header.getValue());
		}

		if (StringUtil.isNotBlank(String.valueOf(bean.getBody()))) {
			// if body then post only body
			builder.method(bean.getMethod(), ClientResponse.class,
					String.valueOf(bean.getBody()));
		} else if (isFileUpload(bean.getFormParameters())) {
			String fileName = "";
			// if contains file then upload as multipart/octet-stream as per
			// Content-Type header
			FormDataMultiPart multiPart = new FormDataMultiPart();
			for (Entry<String, Object> entry : bean.getFormParameters().entrySet()) {
				String value = String.valueOf(entry.getValue());
				if (value.startsWith("file:")) {
					fileName = value.split("file:", 2)[1];
					multiPart.bodyPart(
							new FileDataBodyPart(entry.getKey(), new File(fileName)));
				} else {
					multiPart.field(entry.getKey(), value);
				}
			}
			if (bean.getHeaders().containsValue(MediaType.APPLICATION_OCTET_STREAM)) {
				Path path = Paths.get(new File(fileName).getAbsolutePath());
				try {
					builder.type(MediaType.APPLICATION_OCTET_STREAM).method(
							bean.getMethod(), ClientResponse.class,
							Files.readAllBytes(path));
				} catch (UniformInterfaceException e) {
					e.printStackTrace();
				} catch (ClientHandlerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				builder.type(MediaType.MULTIPART_FORM_DATA).method(bean.getMethod(),
						ClientResponse.class, multiPart);
			}
		} else {
			// does not contain files
			MultivaluedMap<String, String> formParam = new MultivaluedMapImpl();
			for (Entry<String, Object> entry : bean.getFormParameters().entrySet()) {
				formParam.add(entry.getKey(), String.valueOf(entry.getValue()));
			}
			if (formParam.isEmpty()) {
				builder.method(bean.getMethod(), ClientResponse.class);
			} else {
				builder.method(bean.getMethod(), ClientResponse.class, formParam);
			}

		}

	}

	/**
	 * @param json
	 * @param path
	 * @return
	 */
	private static boolean hasJsonPath(String json, String path) {
		try {
			JsonPath.read(json, path);
		} catch (Exception exception) {
			return false;
		}
		return true;
	}

	private static boolean isFileUpload(Map<String, Object> formParameters) {
		for (Entry<String, Object> params : formParameters.entrySet()) {
			String value = String.valueOf(params.getValue()).trim();
			if (value.startsWith("file:"))
				return true;
		}
		return false;
	}

	/**
	 * @param jsonpath
	 * @return
	 */
	private static String getPath(String jsonpath) {
		if (!jsonpath.startsWith("$"))
			jsonpath = "$." + jsonpath;
		return jsonpath;
	}

}
