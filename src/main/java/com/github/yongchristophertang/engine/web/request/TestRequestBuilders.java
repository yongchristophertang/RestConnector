/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.yongchristophertang.engine.web.request;


import com.github.yongchristophertang.engine.web.http.HttpMethod;
import org.apache.http.client.methods.*;

import java.lang.reflect.Proxy;

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
        return new HttpRequestBuilders(new HttpGet(), urlTemplate, "GET Request", urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a POST request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders post(String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(new HttpPost(), urlTemplate, "POST Request", urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a PUT request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders put(String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(new HttpPut(), urlTemplate, "PUT Request", urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a DELETE request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders delete(String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(new HttpDelete(), urlTemplate, "DELETE Request", urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a PATCH request.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders patch(String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(new HttpPatch(), urlTemplate, "PATCH Request", urlVariables);
    }

    /**
     * Create a {@link HttpRequestBuilders} for a request with the given HTTP method.
     *
     * @param httpMethod   the HTTP method
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(httpMethod.getHttpRequest(), urlTemplate, httpMethod.name(), urlVariables);
    }

    /**
     * Create a {@link HttpMultipartRequestBuilders} for a request with the
     * given HTTP method.
     *
     * @param httpMethod   the HTTP method
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpMultipartRequestBuilders multipartRequest(HttpMethod httpMethod, String urlTemplate,
            Object... urlVariables) {
        return new HttpMultipartRequestBuilders(httpMethod.getHttpRequest(), urlTemplate, httpMethod.name(), urlVariables);
    }

    /**
     * Create a {@code T} instance which is a proxy for the interface {@code clientIface}. Typically methods in
     * {@code clientIface} will return a {@link com.github.yongchristophertang.engine.web.request.RequestBuilder} or, more
     * concrete, {@link HttpRequestBuilders}.
     *
     * @param clientIface the API method definition interface
     * @param <T>         Inteface class
     * @return {@code T} instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T api(Class<T> clientIface) throws Exception {
        return (T) Proxy.newProxyInstance(TestRequestBuilders.class.getClassLoader(), new Class<?>[]{clientIface},
                RequestProxy.getInstance());
    }
}
