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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
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
    /**
     * Use the reflected constructor to initialize a {@link java.lang.invoke.MethodHandles.Lookup} in order to
     * disable the access check with method {@link java.lang.invoke.MethodHandles.Lookup#in} which prevents access to
     * "default" methods if the caller class, {@link com.connector.rest.engine.web.request.RequestProxy} in this
     * case, and the {@code requestLookupClass} do not reside in the same package.
     */
    private static final Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR;

    static {
        try {
            LOOKUP_CONSTRUCTOR = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            if (!LOOKUP_CONSTRUCTOR.isAccessible()) {
                LOOKUP_CONSTRUCTOR.setAccessible(true);
            }
        } catch (NoSuchMethodException exp) {
            throw new IllegalStateException(exp);
        }
    }

    /**
     * Disable direct initialization
     */
    private RequestProxy() {
    }

    /**
     * Get the singleton instance of {@link RequestProxy}.
     */
    public static RequestProxy getInstance() {
        return INSTANCE;
    }

    // TODO: too messy, need to refactor data collection part
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // if the method is a default method, process with the default implementation
        if (method.isDefault()) {
            return LOOKUP_CONSTRUCTOR.newInstance(method.getDeclaringClass(), MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, method.getDeclaringClass()).bindTo(proxy).invokeWithArguments(args);
        }

        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> bodyParams = new HashMap<>();
        Map<String, String> pathParams = new HashMap<>();
        Multimap<String, String> fileParams = ArrayListMultimap.create();
        Map<String, String> headerParams = new HashMap<>();
        String contentType;
        String accept;

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
                    StringConverter converter = f.getAnnotation(PathParam.class).converter().newInstance();
                    pathParams.put(f.getAnnotation(PathParam.class).value(), converter.convert(f.get(null)));
                } else if (f.isAnnotationPresent(BodyParam.class)) {
                    StringConverter converter = f.getAnnotation(BodyParam.class).converter().newInstance();
                    bodyParams.put(f.getAnnotation(BodyParam.class).value(), converter.convert(f.get(null)));
                } else if (f.isAnnotationPresent(QueryParam.class)) {
                    StringConverter converter = f.getAnnotation(QueryParam.class).converter().newInstance();
                    queryParams.put(f.getAnnotation(QueryParam.class).value(), converter.convert(f.get(null)));
                } else if (f.isAnnotationPresent(HeaderParam.class)) {
                    StringConverter converter = f.getAnnotation(HeaderParam.class).converter().newInstance();
                    headerParams.put(f.getAnnotation(HeaderParam.class).value(), converter.convert(f.get(null)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Produce produce = method.getAnnotation(Produce.class);
        contentType = produce != null ? produce.value() : null;

        Consume consume = method.getAnnotation(Consume.class);
        accept = consume != null ? consume.value() : null;

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
            try {
                if (p.isAnnotationPresent(PathParam.class)) {
                    StringConverter converter = p.getAnnotation(PathParam.class).converter().newInstance();
                    pathParams.put(p.getAnnotation(PathParam.class).value(), converter.convert(args[i]));
                } else if (p.isAnnotationPresent(BodyParam.class)) {
                    StringConverter converter = p.getAnnotation(BodyParam.class).converter().newInstance();
                    bodyParams.put(p.getAnnotation(BodyParam.class).value(), converter.convert(args[i]));
                } else if (p.isAnnotationPresent(FileParam.class)) {
                    fileParams.put(p.getAnnotation(FileParam.class).value(), args[i].toString());
                } else if (p.isAnnotationPresent(QueryParam.class)) {
                    StringConverter converter = p.getAnnotation(QueryParam.class).converter().newInstance();
                    queryParams.put(p.getAnnotation(QueryParam.class).value(), converter.convert(args[i]));
                } else if (p.isAnnotationPresent(HeaderParam.class)) {
                    StringConverter converter = p.getAnnotation(HeaderParam.class).converter().newInstance();
                    headerParams.put(p.getAnnotation(HeaderParam.class).value(), converter.convert(args[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        /**
         * Local inner class for creating a {@link HttpRequestBuilders} or {@link HttpMultipartRequestBuilders}.
         * This class provides a {@code build} method to construct the target {@code builders}.
         */
        abstract class RequestBuilderConstructor {
            HttpRequestBuilders builders;

            /**
             * Build path parameters.
             */
            void pathBuild() {
                pathParams.keySet().stream().forEach(key -> builders.path(key, pathParams.get(key)));
            }

            /**
             * Build query string parameters.
             */
            void queryBuild() {
                queryParams.keySet().stream().forEach(key -> builders.param(key, queryParams.get(key)));
            }

            /**
             * Build ordinary header parameters.
             */
            void headerBuild() {
                headerParams.keySet().stream().forEach(key -> builders.header(key, headerParams.get(key)));
            }

            /**
             * Build a special header parameter, ContentType.
             * The method behaviour varies with different request types..
             */
            abstract void contentTypeBuild();

            /**
             * Build a special header parameter, Accept.
             */
            void acceptBuild() {
                Optional.ofNullable(accept).ifPresent(builders::accept);
            }

            /**
             * Build body form parameters.
             * The method behaviour varies with different request types.
             */
            abstract void bodyBuild();

            /**
             * Attach files with requests.
             * This method is only valid for multipart request type.
             */
            abstract void fileBuild();

            /**
             * Post processors for returning {@code builders}
             */
            abstract void postProcessor();

            /**
             * Return a {@link HttpRequestBuilders}.
             */
            final HttpRequestBuilders build() {
                pathBuild();
                queryBuild();
                headerBuild();
                contentTypeBuild();
                acceptBuild();
                bodyBuild();
                fileBuild();
                postProcessor();
                return builders;
            }
        }

        /**
         * Local inner class for creating a {@link HttpMultipartRequestBuilders}.
         */
        class MultipartConstructor extends RequestBuilderConstructor {
            MultipartBodyFormBuilder multipartBodyFormBuilder = MultipartBodyFormBuilder.create();

            MultipartConstructor(HttpMultipartRequestBuilders builders) {
                this.builders = builders;
            }

            @Override
            void contentTypeBuild() {

            }

            @Override
            void bodyBuild() {
                bodyParams.keySet().stream().forEach(key -> multipartBodyFormBuilder.param(key, bodyParams.get(key)));
            }

            @Override
            void fileBuild() {
                fileParams.keySet().stream().forEach(key -> fileParams.get(key).stream().forEach(
                        value -> {
                            if (value.contains(",")) {
                                for (String file : value.split(",")) {
                                    multipartBodyFormBuilder.file(key, file);
                                }
                            } else if (!value.isEmpty()) {
                                multipartBodyFormBuilder.file(key, value);
                            }
                        }));
            }

            @Override
            void postProcessor() {
                HttpMultipartRequestBuilders.class.cast(builders).body(multipartBodyFormBuilder);
            }
        }

        /**
         * Local inner class for creating a {@link HttpRequestBuilders}.
         */
        class RequestConstructor extends RequestBuilderConstructor {

            RequestConstructor(HttpRequestBuilders builders) {
                this.builders = builders;
            }

            @Override
            void contentTypeBuild() {
                Optional.ofNullable(contentType).ifPresent(builders::contentType);
            }

            @Override
            void bodyBuild() {
                bodyParams.keySet().stream().forEach(key -> {
                    if (key.equals("")) {
                        builders.body(bodyParams.get(key));
                    } else {
                        builders.body(key, bodyParams.get(key));
                    }
                });
            }

            @Override
            void fileBuild() {

            }

            @Override
            void postProcessor() {

            }
        }

        return fileParams.isEmpty() ?
                new RequestConstructor(new HttpRequestBuilders(httpMethod.getHttpRequest(), url)).build() :
                new MultipartConstructor(new HttpMultipartRequestBuilders(httpMethod.getHttpRequest(), url)).build();
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
        if (path == null) {
            return "";
        }
        String p = path.value();
        return removeTrailingSlash(p.length() > 0 ? (p.startsWith("/") ? p : "/" + p) : "");
    }

    private String removeTrailingSlash(String path) {
        return path.endsWith("/") ? removeTrailingSlash(path.substring(0, path.length() - 1)) : path;
    }
}
