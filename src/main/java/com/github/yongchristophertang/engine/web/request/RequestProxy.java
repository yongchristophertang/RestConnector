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

import com.github.yongchristophertang.engine.web.annotations.*;
import com.github.yongchristophertang.engine.web.http.HttpMethod;
import com.github.yongchristophertang.engine.web.http.MultipartBodyFormBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.yongchristophertang.engine.AssertUtils.notNull;

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
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Use the reflected constructor to initialize a {@link java.lang.invoke.MethodHandles.Lookup} in order to
     * disable the access check with method {@link java.lang.invoke.MethodHandles.Lookup#in} which prevents access to
     * "default" methods if the caller class, {@link RequestProxy} in this
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
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // if the method is a default method, process with the default implementation
        if (method.isDefault()) {
            return LOOKUP_CONSTRUCTOR.newInstance(method.getDeclaringClass(), MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, method.getDeclaringClass()).bindTo(proxy).invokeWithArguments(args);
        }

        Multimap<String, String> queryParams = ArrayListMultimap.create();
        Multimap<String, String> bodyParams = ArrayListMultimap.create();
        Map<String, String> pathParams = new HashMap<>();
        Multimap<String, String> fileParams = ArrayListMultimap.create();
        Map<String, String> headerParams = new HashMap<>();

        /* Handle the class level annotations: Host and Path */
        // The url will be the URL path for the request
        String url = getHost(method.getDeclaringClass().getAnnotation(Host.class));

        /* Handle the method level annotations: Path, HTTPMethod, Produce and Consume */
        url += getPath(method.getDeclaringClass().getAnnotation(Path.class)) +
                getPath(method.getAnnotation(Path.class));

         /* Fill out the api's description */
        String description = Optional.ofNullable(method.getAnnotation(Description.class)).map(Description::value).orElse(method.getName());

        // Handle Produce and Consume
        String contentType = Optional.ofNullable(method.getAnnotation(Produce.class)).map(Produce::value).orElse(null);
        String accept = Optional.ofNullable(method.getAnnotation(Consume.class)).map(Consume::value).orElse(null);

        // Handle HTTPMethod, only ONE HTTPMethod can be annotated with one method
        List<HTTPMethod> httpMethods = Arrays.asList(method.getAnnotations()).stream()
                .map(a -> a.annotationType().getAnnotation(HTTPMethod.class)).filter(m -> m != null).collect(
                        Collectors.toList());
        if (httpMethods.size() != 1) {
            throw new IllegalArgumentException("HTTPMethod annotation must be annotated once, no more and no less");
        }

        notNull(httpMethods.get(0).value(), "Http Method is not defined");
        HttpMethod httpMethod = HttpMethod.valueOf(httpMethods.get(0).value());

        /*
            Handle the field level annotations: PathParam, BodyParam, QueryParam and HeaderParam.
            Noted that only the first annotation will be processed.
         */
        Field[] fields = method.getDeclaringClass().getFields();
        try {
            for (Field f : fields) {
                Object value = f.get(null);
                if (value != null) {
                    Class<? extends Annotation> annotationType = f.getAnnotations()[0].annotationType();
                    if (annotationType == PathParam.class) {
                        PathParam a = f.getAnnotation(PathParam.class);
                        pathParams.put(a.value(), a.converter().newInstance().convert(value));
                    } else if (annotationType == BodyParam.class) {
                        BodyParam a = f.getAnnotation(BodyParam.class);
                        if (value instanceof Collection) {
                            bodyParams.putAll(a.value(), (Iterable<? extends String>)
                                ((Collection) value).stream()
                                    .map(v -> Try.of(() -> a.converter().newInstance().convert(v)).orElse(""))
                                    .collect(Collectors.toList()));
                        } else if (value instanceof Map) {
                            ((Map<String, Object>) value).entrySet().parallelStream().forEach(e -> bodyParams.put(e.getKey(), e.getValue().toString()));
                        } else {
                            bodyParams.put(a.value(), a.converter().newInstance().convert(value));
                        }
                    } else if (annotationType == QueryParam.class) {
                        QueryParam a = f.getAnnotation(QueryParam.class);
                        if (value instanceof Collection) {
                            queryParams.putAll(a.value(), (Iterable<? extends String>)
                                ((Collection) value).stream().map(v -> Try.of(() -> a.converter().newInstance().convert(v)).orElse(""))
                                    .collect(Collectors.toList()));
                        } else {
                            queryParams.put(a.value(), a.converter().newInstance().convert(value));
                        }
                    } else if (annotationType == HeaderParam.class) {
                        HeaderParam a = f.getAnnotation(HeaderParam.class);
                        headerParams.put(a.value(), a.converter().newInstance().convert(value));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot process filed level annotations");
        }
//        Stream.of(method.getDeclaringClass().getFields()).filter(f -> Try.of(() -> Optional.of(f.get(null)).isPresent()).orElse(false)).forEach(f ->
//                Optional.ofNullable(f.getAnnotations()[0]).map(Match.as(String.class)
//                        .whenType(PathParam.class).then(anno -> Try.of(() -> anno.converter().newInstance().convert(f.get(null)))
//                            .onSuccess(p -> pathParams.put(anno.value(), p)).orElse(""))
//                        .whenType(BodyParam.class).then(anno -> Try.of(() -> {
//                                Object object = f.get(null);
//                                if (object instanceof Collection) {
//                                    return ((Collection) object).stream().map(e -> Try.of(() -> anno.converter().newInstance().convert(e)).orElse("")).collect(Collectors.toList());
//                                } else {
//                                    return anno.converter().newInstance().convert(object);
//                                }
//                            }).onSuccess(p -> {
//                                if (p instanceof Collection) {
//                                    bodyParams.putAll(anno.value(), (Iterable<String>) p);
//                                } else {
//                                    bodyParams.put(anno.value(), String.valueOf(p));
//                                }
//                            }).orElse(""))
//                        .whenType(QueryParam.class).then(anno -> Try.of(() -> anno.converter().newInstance().convert(f.get(null)))
//                            .onSuccess(p -> queryParams.put(anno.value(), p)).orElse(""))
//                        .whenType(HeaderParam.class).then(anno -> Try.of(() -> anno.converter().newInstance().convert(f.get(null)))
//                            .onSuccess(p -> headerParams.put(anno.value(), p)).orElse(""))));

        /*
            Handle the method parameter level annotations: PathParam, BodyParam, QueryParam, HeaderParam and FileParam.
            Noted that only the first annotation will be processed.
        */
        Parameter[] parameters = method.getParameters();
        try {
           for (int i = 0; i < args.length; i++) {
               if (args[i] != null && parameters[i].getAnnotations() != null) {
                   Class<? extends Annotation> annotationType = parameters[i].getAnnotations()[0].annotationType();
                   if (annotationType == PathParam.class) {
                       PathParam a = parameters[i].getAnnotation(PathParam.class);
                       pathParams.put(a.value(), a.converter().newInstance().convert(args[i]));
                   } else if (annotationType == BodyParam.class) {
                       BodyParam a = parameters[i].getAnnotation(BodyParam.class);
                       if (args[i] instanceof Collection) {
                           bodyParams.putAll(a.value(),
                               (Iterable<? extends String>) ((Collection) args[i]).stream()
                                   .map(v -> Try.of(() -> a.converter().newInstance().convert(v)).orElse("")).collect(Collectors.toList()));
                       } else if (args[i] instanceof Map) {
                           ((Map<String, Object>) args[i]).entrySet().parallelStream().forEach(e -> bodyParams.put(e.getKey(), e.getValue().toString()));
                       } else {
                           bodyParams.put(a.value(), a.converter().newInstance().convert(args[i]));
                       }
                   } else if (annotationType == QueryParam.class) {
                       QueryParam a = parameters[i].getAnnotation(QueryParam.class);
                       if (args[i] instanceof Collection) {
                           queryParams.putAll(a.value(),
                               (Iterable<? extends String>) ((Collection) args[i]).stream()
                                   .map(v -> Try.of(() -> a.converter().newInstance().convert(v)).orElse("")).collect(Collectors.toList()));
                       } else if (args[i] instanceof Map) {
                           ((Map<String, Object>) args[i]).entrySet().parallelStream().forEach(e -> queryParams.put(e.getKey(), e.getValue().toString()));
                       } else {
                           queryParams.put(a.value(), a.converter().newInstance().convert(args[i]));
                       }
                   } else if (annotationType == HeaderParam.class) {
                       HeaderParam a = parameters[i].getAnnotation(HeaderParam.class);
                       headerParams.put(a.value(), a.converter().newInstance().convert(args[i]));
                   }
               }
           }
        } catch (Exception e) {
            LOGGER.error("Cannot process parameter level annotations");
        }


//        Stream.range(0, parameters.length).filter(
//            i -> args[i] != null & parameters[i].getAnnotations() != null).map(
//            i -> new Tuple2<>(parameters[i].getAnnotations()[0], args[i])).forEach(t -> Option.of(t._1).map(Match.as(String.class)
//                .whenType(PathParam.class).then(p -> Try.of(() -> p.converter().newInstance().convert(t._2))
//                    .onSuccess(v -> pathParams.put(p.value(), v)).orElse(""))
//                .whenType(BodyParam.class).then(p -> Try.of(() -> p.converter().newInstance().convert(t._2))
//                    .onSuccess(v -> bodyParams.put(p.value(), v)).orElse(""))
//                .whenType(QueryParam.class).then(p -> Try.of(() -> p.converter().newInstance().convert(t._2))
//                    .onSuccess(v -> queryParams.put(p.value(), v)).orElse(""))
//                .whenType(HeaderParam.class).then(p -> Try.of(() -> p.converter().newInstance().convert(t._2))
//                    .onSuccess(v -> headerParams.put(p.value(), v)).orElse(""))
//                .whenType(FileParam.class).then(p -> String.valueOf(fileParams.put(p.value(), t._2.toString()))
//            )));

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
                        // if use a raw string to set up body, then we only add the first value, igonor the further
                        builders.body(bodyParams.get(key).iterator().next());
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
                new RequestConstructor(new HttpRequestBuilders(httpMethod.getHttpRequest(), url, description)).build() :
                new MultipartConstructor(new HttpMultipartRequestBuilders(httpMethod.getHttpRequest(), url, description)).build();
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
