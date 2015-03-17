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


import com.github.connector.test.web.annotations.*;
import com.github.connector.test.web.http.BodyFormBuilder;
import com.github.connector.test.web.http.HttpMethod;
import org.apache.http.client.methods.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;

import static com.github.connector.test.AssertUtils.notNull;
import static com.github.connector.test.AssertUtils.stringNotBlank;

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
     * Create a {@link HttpRequestBuilders} for a request with the given HTTP method.
     *
     * @param httpMethod   the HTTP method except for PATCH
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpRequestBuilders request(HttpMethod httpMethod, String urlTemplate, Object... urlVariables) {
        return new HttpRequestBuilders(httpMethod.getHttpRequest(), urlTemplate, urlVariables);
    }

    /**
     * Create a {@link com.github.connector.test.web.request.HttpMultipartRequestBuilders} for a request with the
     * given HTTP method.
     *
     * @param httpMethod   the HTTP method except for PATCH
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    public static HttpMultipartRequestBuilders multipartRequest(HttpMethod httpMethod, String urlTemplate,
            Object... urlVariables) {
        return new HttpMultipartRequestBuilders(httpMethod.getHttpRequest(), urlTemplate, urlVariables);
    }

    @SuppressWarnings("unchecked")
    public static <T> T api(Class<T> clientIface) {
        return (T) Proxy.newProxyInstance(TestRequestBuilders.class.getClassLoader(), new Class<?>[]{clientIface},
                new ApiProxy());
    }

    private static class ApiProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Host host = method.getDeclaringClass().getAnnotation(Host.class);
            notNull(host, "Http Host must not be null.");
            stringNotBlank(host.value(), "Http Host must not be blank.");

            String url = host.value();
            Path path = method.getDeclaringClass().getAnnotation(Path.class);
            if (path != null) {
                url += path.value();
            }

            path = method.getAnnotation(Path.class);
            if (path != null) {
                url += path.value();
            }

            com.github.connector.test.web.annotations.HttpMethod httpMethod = null;
            for (Annotation annotation : method.getAnnotations()) {
                httpMethod = annotation.annotationType().getAnnotation(
                        com.github.connector.test.web.annotations.HttpMethod.class);
            }
            notNull(httpMethod, "Http method must not be null.");


            HttpMethod hm = HttpMethod.valueOf(httpMethod.value());
            HttpRequestBuilders builders = TestRequestBuilders.request(hm, url);

            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                if (p.isAnnotationPresent(PathParam.class)) {
                    builders.path(p.getAnnotation(PathParam.class).value(), args[i].toString());
                } else if (p.isAnnotationPresent(BodyParam.class)) {
                    builders.body(BodyFormBuilder.multipart()
                            .param(p.getAnnotation(BodyParam.class).value(), args[i].toString()));
                } else if (p.isAnnotationPresent(FileParam.class)) {
                    builders.body(BodyFormBuilder.multipart()
                            .file(p.getAnnotation(FileParam.class).value(), args[i].toString()));
                } else if (p.isAnnotationPresent(QueryParam.class)) {
                    builders.param(p.getAnnotation(QueryParam.class).value(), args[i].toString());
                }
            }
            return builders;
        }
    }

}
