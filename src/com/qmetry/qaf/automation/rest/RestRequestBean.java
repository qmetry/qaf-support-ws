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

package com.qmetry.qaf.automation.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.qmetry.qaf.automation.data.BaseDataBean;
import com.qmetry.qaf.automation.util.JSONUtil;
import com.qmetry.qaf.automation.util.StringUtil;

/**
 * @author amit.bhoraniya
 *
 */
public class RestRequestBean extends BaseDataBean {

	private String method = "GET";
	private String uri = "";
	private String body = "";
	private Map<String, Collection<String>> headers = new HashMap<String, Collection<String>>();
	private Map<String, Collection<String>> parameters = new HashMap<String, Collection<String>>();

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, Collection<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Collection<String>> headers) {
		this.headers = headers;
	}

	public Map<String, Collection<String>> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Collection<String>> parameters) {
		this.parameters = parameters;
	}

	public void addHeader(String key, String value) {
		Collection<String> valuesList = headers.get(key);
		if (valuesList == null) {
			valuesList = new ArrayList<String>();
		}
		valuesList.add(value);
		headers.put(key, valuesList);
	}

	public void addParam(String key, String value) {
		Collection<String> valuesList = (Collection<String>) parameters.get(key);
		if (valuesList == null) {
			valuesList = new ArrayList<String>();
		}
		valuesList.add(value);
		parameters.put(key, valuesList);
	}

	@Override
	public void fillData(Map<String, String> request) {
		this.uri = request.get(RESTApiConstants.baseurl) + request.get(RESTApiConstants.endpoint);
		this.method = request.get(RESTApiConstants.method);
		addHeaders(getHeaders(request));
		if (StringUtil.isNotBlank(request.get(RESTApiConstants.body))) {
			this.body = request.get(RESTApiConstants.body);
		} else {
			addParams(getParams(request));
		}
	}

	public void addParams(Map<String, Object> params) {
		for (Entry<String, Object> entry : params.entrySet()) {
			addParam(entry.getKey(), String.valueOf(entry.getValue()));
		}
	}

	public void addHeaders(Map<String, Object> headers) {
		for (Entry<String, Object> entry : headers.entrySet()) {
			addHeader(entry.getKey(), String.valueOf(entry.getValue()));
		}
	}

	private Map<String, Object> getParams(Map<String, String> requset) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (requset.containsKey(RESTApiConstants.query_parameters)
				&& StringUtil.isNotBlank(requset.get(RESTApiConstants.query_parameters))) {
			String query_params = requset.get(RESTApiConstants.query_parameters);
			return getMap(query_params);
		}
		return params;
	}

	private Map<String, Object> getHeaders(Map<String, String> requset) {
		HashMap<String, Object> headers = new HashMap<String, Object>();
		if (requset.containsKey(RESTApiConstants.headers)
				&& StringUtil.isNotBlank(requset.get(RESTApiConstants.headers))) {
			return getMap(requset.get(RESTApiConstants.headers));
		}
		return headers;
	}

	private static Map<String, Object> getMap(String s) {
		if (JSONUtil.isValidGsonString(s)) {
			return JSONUtil.toMap(s);
		}
		//if not valid json string then it can be map to toString so parsing here
		Map<String, Object> map = new HashMap<String, Object>();
		if (s.startsWith("{")) {
			s = s.substring(1);
		}
		if (s.endsWith("}")) {
			s = s.substring(0, s.length() - 1);
		}
		String[] words = s.split(", ");
		for (String string : words) {
			if (string.contains("=")) {
				map.put(string.split("=")[0], string.split("=")[1]);
			}
		}
		return map;
	}
}
