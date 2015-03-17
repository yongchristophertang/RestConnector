/*
 * Copyright 2014-2015 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.connector.test.web.request;

import com.github.connector.test.web.annotations.Host;
import com.github.connector.test.web.annotations.Path;
import com.github.connector.test.web.annotations.QueryParam;
import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;

import static com.github.connector.test.AssertUtils.notNull;

/**
 * Request proxy singleton, which implements {@link java.lang.reflect.InvocationHandler} to handle forwarded method callings from the proxy class
 *
 * @author Yong Tang
 * @since 0.4
 */
public class RequestProxy implements InvocationHandler {
    private static final RequestProxy INSTANCE = new RequestProxy();

    private RequestProxy() {
    }

    public static RequestProxy getInstance() {
        return INSTANCE;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String url = getHost(method.getDeclaringClass().getAnnotation(Host.class));
        url += getPath(method.getDeclaringClass().getAnnotation(Path.class));

        Field[] fields = method.getDeclaringClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(QueryParam.class)) {

            }
        }
        return null;
    }

    private String getHost(Host host) throws Exception {
        notNull(host, "Host must not be null");
        String url;
        if (!Strings.isNullOrEmpty(host.value())) {
            url = host.value() + ":" + host.port();
        } else if (!Strings.isNullOrEmpty(host.location())) {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(host.location()));
            url = properties.getProperty("http.host") + ":" + properties.getProperty("http.port");
        } else {
            throw new IllegalArgumentException("Host is not defined");
        }

        return url;
    }

    private String getPath(Path path) {
        String p = path.value();
        return p.length() > 0 ? (p.startsWith("/") ? p : "/" + p) : "";
    }
}
