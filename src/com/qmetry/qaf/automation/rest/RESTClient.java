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

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.qmetry.qaf.automation.util.StringUtil;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * @author amit.bhoraniya
 */
public class RESTClient {
	// move to rest test-base
	public static void request(RestRequestBean bean) {

		WebResource resource = new RestTestBase().getWebResource(bean.getBaseUrl(), bean.getEndPoint());

		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

		for (Entry<String, Object> entry : bean.getQueryParameters().entrySet()) {
			queryParams.add(entry.getKey(), entry.getValue().toString());
		}
		resource = resource.queryParams(queryParams);

		Builder builder = resource.getRequestBuilder();

		if (bean.getHeaders().containsKey("Accept"))
			builder.accept((String) bean.getHeaders().get("Accept"));

		for (Entry<String, Object> header : bean.getHeaders().entrySet()) {
			builder.header(header.getKey(), header.getValue());
		}

		if (StringUtil.isNotBlank(bean.getBody())) {
			// if body then post only body
			builder.method(bean.getMethod(), ClientResponse.class, bean.getBody());
		} else if (isMultiPart(bean.getFormParameters())) {
			// if contains file then upload as multipart
			FormDataMultiPart multiPart = new FormDataMultiPart();
			for (Entry<String, Object> entry : bean.getFormParameters().entrySet()) {
				String value = String.valueOf(entry.getValue());
				if (value.startsWith("file:")) {
					multiPart.bodyPart(new FileDataBodyPart(entry.getKey(), new File(value.split(":", 2)[1])));
				} else {
					multiPart.field(entry.getKey(), value);
				}
			}
			builder.type(MediaType.MULTIPART_FORM_DATA).method(bean.getMethod(), ClientResponse.class, multiPart);
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

	private static boolean isMultiPart(Map<String, Object> formParameters) {
		for (Entry<String, Object> params : formParameters.entrySet()) {
			String value = String.valueOf(params.getValue()).trim();
			if (value.startsWith("file:"))
				return true;
		}
		return false;
	}

}
