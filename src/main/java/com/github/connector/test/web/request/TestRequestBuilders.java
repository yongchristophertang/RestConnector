/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.connector.test.web.request;


import com.github.connector.test.web.http.HttpMethod;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.methods.*;

/**
 * Static factory methods for {@link HttpRequestBuilders}s.
 *
 * @author Yong Tang
 * @since 0.4
 */
public abstract class TestRequestBuilders {

    private TestRequestBuilders() {
    }

    /**
     * Create a {@link HttpRequestBuilders} for a GET request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders get(String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(new HttpGet(), urlTemplate, urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a POST request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders post(String urlTemplate, Object... urlVariables) {
        return new HttpMultipartRequestBuilders(new HttpPost(), urlTemplate, urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a PUT request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders put(String urlTemplate, Object... urlVariables) {
        return new HttpMultipartRequestBuilders(new HttpPut(), urlTemplate, urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a DELETE request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders delete(String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(new HttpDelete(), urlTemplate, urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a PATCH request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders patch(String urlTemplate, Object... urlVariables) {
        return new HttpMultipartRequestBuilders(new HttpPatch(), urlTemplate, urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a request with the given HTTP method except for PATCH.
     *
     * @param httpMethod   the HTTP method except for PATCH
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables) {
        try {
            return new HttpRequestBuilders(httpMethod.getHttpRequest(), urlTemplate, urlVariables);
        } catch (MethodNotSupportedException e) {
            e.printStackTrace();
            // Except for PATCH method, this exception will never be invoked
            return null;
        }
    }

    /**
     * Create a {@link com.github.connector.test.web.request.HttpRequestBuilders} for a request with a predefined API.
     *
     * @param apiName the predefined API name
     * @param urlVars zero or more URL variables
     */
    public static HttpRequestBuilders api(String apiName, Object... urlVars) {

        // TODO: Give implementation
        return null;
    }

}
