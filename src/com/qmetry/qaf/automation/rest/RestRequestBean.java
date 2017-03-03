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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.qmetry.qaf.automation.data.BaseDataBean;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.util.StringUtil;

/**
 * @author chirag.jayswal
 */
public class RestRequestBean extends BaseDataBean {

	private String method;
	private String baseUrl;
	private String endPoint;
	private Map<String, Object> headers = new HashMap<String, Object>();
	private String[] accept = {};
	private String schema;
	private String body;
	@SerializedName("query-parameters")
	private Map<String, Object> queryParameters = new HashMap<String, Object>();
	@SerializedName("form-parameters")
	private Map<String, Object> formParameters = new HashMap<String, Object>();

	public String getBaseUrl() {
		return StringUtil.isNotBlank(baseUrl) ? baseUrl
				: ApplicationProperties.SELENIUM_BASE_URL.getStringVal("");
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

}
