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
package com.qmetry.qaf.automation.rest;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.apache.commons.lang.text.StrSubstitutor;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.annotations.SerializedName;
import com.qmetry.qaf.automation.core.AutomationError;
import com.qmetry.qaf.automation.data.BaseDataBean;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.util.FileUtil;
import com.qmetry.qaf.automation.util.StringMatcher;
import com.qmetry.qaf.automation.util.StringUtil;

/**
 * @author chirag.jayswal
 */
public class RestRequestBean extends BaseDataBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 423394605353099602L;

	private String method = "GET";

	private String baseUrl = "";

	private String endPoint = "";

	private Map<String, Object> headers = new HashMap<String, Object>();

	private String[] accept = {};

	private String schema = "";

	private String body = "";

	@SerializedName(value = WSCRepositoryConstants.QUERY_PARAMETERS)
	private Map<String, Object> queryParameters = new HashMap<String, Object>();

	@SerializedName(value = WSCRepositoryConstants.FORM_PARAMETERS)
	private Map<String, Object> formParameters = new HashMap<String, Object>();

	private Map<String, Object> parameters = new HashMap<String, Object>();

	private String reference = "";

	// private final transient Gson gson = getGson();

	public String getBaseUrl() {
		return StringUtil.isNotBlank(baseUrl) ? baseUrl : ApplicationProperties.SELENIUM_BASE_URL.getStringVal("");
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, Object> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(Map<String, Object> parameters) {
		this.queryParameters = parameters;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endpoint) {
		this.endPoint = endpoint;
	}

	public String[] getAccept() {
		return accept;
	}

	public void setAccept(String[] accept) {
		this.accept = accept;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Map<String, Object> getFormParameters() {
		return formParameters;
	}

	public void setFormParameters(Map<String, Object> formParameters) {
		this.formParameters = formParameters;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Priority for resolver is:
	 * <ol>
	 * <li>data provided in argument
	 * <li>parameter in request call
	 * <li>parameter in request reference
	 * <li>configuration property
	 * </ol>
	 * 
	 * @param data
	 */
	public void resolveParameters(Map<String, Object> data) {
		JSONObject j = new JSONObject(this);
		j.remove("reference");
		String source = resolveParameters(j.toString(), data);
		source = resolveParameters(j.toString(), getParameters());

		fillFromJsonString(source);

		if (StringUtil.isNotBlank(body)) {
			// is it points to file?
			if (StringMatcher.startsWithIgnoringCase("file:").match(body)) {
				String file = body.split(":", 2)[1];
				try {
					body = FileUtil.readFileToString(new File(file), StandardCharsets.UTF_8);
					body = resolveParameters(body, data);
				} catch (IOException e) {
					throw new AutomationError("Unable to read file: " + file, e);
				}
			}
		}
	}

	private String resolveParameters(String source, Map<String, Object> data) {
		if (null != data && !data.isEmpty()) {
			source = StrSubstitutor.replace(source, data);
		}
		source = StrSubstitutor.replace(source, parameters);
		source = getBundle().getSubstitutor().replace(source);

		return source;
	}

	@Override
	public void fillData(Object obj) {
		try {
			boolean isString = (obj instanceof String);
			if (isString && getBundle().containsKey((String) obj)) {
				fillFromConfig((String) obj);

			} else {
				String jsonStr = (isString ? (String) obj : new JSONObject(obj).toString());
				fillFromJsonString(jsonStr);
			}
		} catch (Exception e) {
			throw new AutomationError("Unable to populate request from" + obj, e);
		}
	}

	@Override
	public void fillFromConfig(String reqkey) {
		Node node = getBundle().configurationAt(reqkey).getRoot();
		if (!node.hasChildren()) {
			fillFromJsonString(getBundle().getString(reqkey));
		} else {
			Configuration config = getBundle().subset(reqkey);
			Iterator<?> keys = config.getKeys();
			Map<String, String> map = new HashMap<String, String>();
			while (keys.hasNext()) {
				String dataKey = String.valueOf(keys.next());
				String value = config.getString(dataKey);
				map.put(dataKey, value);
			}
			fillData(map);
		}
	}

	@Override
	public void fillData(Map<String, String> map) {
		if (map.containsKey(WSCRepositoryConstants.REFERENCE)) {
			fillFromConfig(map.get(WSCRepositoryConstants.REFERENCE));
		}

		updateKey(map, WSCRepositoryConstants.FORM_PARAMETERS, "formParameters");
		updateKey(map, WSCRepositoryConstants.QUERY_PARAMETERS, "queryParameters");

		super.fillData(map);
	}

	/**
	 * fill bean from json data.
	 * 
	 * @param jsonstr
	 */
	public void fillFromJsonString(String jsonstr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonstr);
			String[] keys = JSONObject.getNames(jsonObject);
			Map<String, String> map = new HashMap<String, String>();
			for (String key : keys) {
				try {
					map.put(key, jsonObject.getJSONObject(key).toString());
				} catch (Exception e) {
					map.put(key, jsonObject.get(key).toString());
				}
			}
			fillData(map);
		} catch (JSONException e) {
			throw new AutomationError(jsonstr +" is not valid Json", e);
		}

	}

	public void setFormParameters(String val) {
		setMap(val, formParameters);
	}

	public void setQueryParameters(String val) {
		setMap(val, queryParameters);
	}

	public void setHeaders(String val) {
		setMap(val, headers);
	}

	public void setParameters(String val) {
		setMap(val, parameters);
	}

	private void setMap(String val, Map<String, Object> map) {
		if (StringUtil.isNotBlank(val)) {
			JSONObject jsonObject = new JSONObject(val);
			map.putAll(jsonObject.toMap());
		}
	}

	private void updateKey(Map<String, String> map, String oldKey, String newKey) {
		if (map.containsKey(oldKey)) {
			String value = map.remove(oldKey);
			map.put(newKey, value);
		}
	}

	public static void main(String[] args) {
		getBundle().setProperty("env.baseurl", "http://localhost:8080");
		getBundle().setProperty("get.sample.ref",
				"{'headers':{},'endPoint':'/myservice-endpoint','baseUrl':'${env.baseurl}','method':'POST','query-parameters':{'param1':'${val1}','param2':'${val2}'},'form-parameters':{'a':'b','i':10},'body':'','parameters':{'val1':'abc','val2':'xyz'}}");

		getBundle().setProperty("get.sample.call",
				"{'reference':'get.sample.ref','parameters':{'val1':'abc123','val3':'xyz123'}}");
		RestRequestBean r = new RestRequestBean();
		 r.fillData("get.sample.call");
		
		 System.out.println(r);
		
		 r.resolveParameters(null);
		 System.out.println(r);
	}
}
