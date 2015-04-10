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

package com.connector.rest.engine.web.request;

import com.connector.rest.engine.web.annotations.*;
import com.connector.rest.engine.web.http.HttpMethod;
import com.connector.rest.engine.web.http.MultipartBodyFormBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.connector.rest.engine.AssertUtils.notNull;

/**
 * Request proxy singleton, which implements {@link java.lang.reflect.InvocationHandler} to handle forwarded method
 * callings from the proxy class.
 * The class has no mutable states and can be viewed as a pure function, thus making it effectively thread safe.
 * Singleton pattern will not cause any troubles.
 *
 * @author Yong Tang
 * @since 0.4
 */
public final class RequestProxy implements InvocationHandler {
    private static final RequestProxy INSTANCE = new RequestProxy();

    private RequestProxy() {
    }

    /**
     * Get the singleton instance of {@link RequestProxy}.
     */
    public static RequestProxy getInstance() {
        return INSTANCE;
    }

    // TODO: too messy, need to refactor
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> bodyParams = new HashMap<>();
        Map<String, String> pathParams = new HashMap<>();
        Map<String, String> fileParams = new HashMap<>();
        Map<String, String> headerParams = new HashMap<>();
        String contentType = null;
        String accept = null;

        String url = getHost(method.getDeclaringClass().getAnnotation(Host.class));
        url += getPath(method.getDeclaringClass().getAnnotation(Path.class)) +
                getPath(method.getAnnotation(Path.class));

        Lists.newArrayList(method.getDeclaringClass().getFields()).stream().filter(f -> {
            try {
                return f.get(null) != null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }).forEach(f -> {
            try {
                if (f.isAnnotationPresent(PathParam.class)) {
                    pathParams.put(f.getAnnotation(PathParam.class).value(), String.valueOf(f.get(null)));
                } else if (f.isAnnotationPresent(BodyParam.class)) {
                    bodyParams.put(f.getAnnotation(BodyParam.class).value(), String.valueOf(f.get(null)));
                } else if (f.isAnnotationPresent(QueryParam.class)) {
                    queryParams.put(f.getAnnotation(QueryParam.class).value(), String.valueOf(f.get(null)));
                } else if (f.isAnnotationPresent(HeaderParam.class)) {
                    headerParams.put(f.getAnnotation(HeaderParam.class).value(), String.valueOf(f.get(null)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Produce produce = method.getAnnotation(Produce.class);
        if (produce != null) {
            contentType = produce.value();
        }

        Consume consume = method.getAnnotation(Consume.class);
        if (consume != null) {
            accept = consume.value();
        }

        List<HTTPMethod> httpMethods = Lists.newArrayList(method.getAnnotations()).stream()
                .map(a -> a.annotationType().getAnnotation(HTTPMethod.class)).filter(m -> m != null).collect(
                        Collectors.toList());
        if (httpMethods.size() != 1) {
            throw new IllegalArgumentException("HTTPMethod annotation must be annotated once, no more and no less");
        }

        notNull(httpMethods.get(0).value(), "Http Method is not defined");
        HttpMethod httpMethod = HttpMethod.valueOf(httpMethods.get(0).value());

        // insert parameters into url
        Parameter[] parameters = method.getParameters();
        IntStream.range(0, parameters.length).forEach(i -> {
            Parameter p = parameters[i];
            if (args[i] == null) {
                return;
            }
            if (p.isAnnotationPresent(PathParam.class)) {
                pathParams.put(p.getAnnotation(PathParam.class).value(), args[i].toString());
            } else if (p.isAnnotationPresent(BodyParam.class)) {
                bodyParams.put(p.getAnnotation(BodyParam.class).value(), args[i].toString());
            } else if (p.isAnnotationPresent(FileParam.class)) {
                fileParams.put(p.getAnnotation(FileParam.class).value(), args[i].toString());
            } else if (p.isAnnotationPresent(QueryParam.class)) {
                queryParams.put(p.getAnnotation(QueryParam.class).value(), args[i].toString());
            } else if (p.isAnnotationPresent(HeaderParam.class)) {
                headerParams.put(p.getAnnotation(HeaderParam.class).value(), args[i].toString());
            }
        });

        if (fileParams.isEmpty()) {
            HttpRequestBuilders builders = new HttpRequestBuilders(httpMethod.getHttpRequest(), url);
            pathParams.keySet().stream().forEach(key -> builders.path(key, pathParams.get(key)));
            queryParams.keySet().stream().forEach(key -> builders.param(key, queryParams.get(key)));
            bodyParams.keySet().stream().forEach(key -> builders.body(key, bodyParams.get(key)));
            headerParams.keySet().stream().forEach(key->builders.header(key, headerParams.get(key)));
            Optional.ofNullable(contentType).ifPresent(builders::contentType);
            Optional.ofNullable(accept).ifPresent(builders::accept);
            return builders;
        } else {
            HttpMultipartRequestBuilders builders = new HttpMultipartRequestBuilders(httpMethod.getHttpRequest(), url);
            pathParams.keySet().stream().forEach(key -> builders.path(key, pathParams.get(key)));
            queryParams.keySet().stream().forEach(key -> builders.param(key, queryParams.get(key)));
            headerParams.keySet().stream().forEach(key->builders.header(key, headerParams.get(key)));
            bodyParams.keySet().stream()
                    .forEach(key -> builders.body(MultipartBodyFormBuilder.create().param(key, bodyParams.get(key))));
            fileParams.keySet().stream().forEach(key -> builders.body(
                    MultipartBodyFormBuilder.create().file(key, bodyParams.get(key))));
            Optional.ofNullable(contentType).ifPresent(builders::contentType);
            Optional.ofNullable(accept).ifPresent(builders::accept);
            return builders;
        }
    }

    private String getHost(Host host) throws Exception {
        notNull(host, "Host must not be null");
        String url;
        if (!Strings.isNullOrEmpty(host.value())) {
            url = host.value() + ":" + host.port();
        } else if (!Strings.isNullOrEmpty(host.location())) {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream(host.location()));
            url = properties.getProperty("http.host") + ":" + properties.getProperty("http.port");
        } else {
            throw new IllegalArgumentException("Host is not defined");
        }

        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        return url;
    }

    private String getPath(Path path) {
        String p = path.value();
        return removeTrailingSlash(p.length() > 0 ? (p.startsWith("/") ? p : "/" + p) : "");
    }

    private String removeTrailingSlash(String path) {
        return path.endsWith("/") ? removeTrailingSlash(path.substring(0, path.length() - 1)) : path;
    }
}
