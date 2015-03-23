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

package com.github.connector.engine.web.request;

import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.net.URISyntaxException;
import java.util.*;

import static com.github.connector.engine.AssertUtils.*;

/**
 * Default builder for {@link HttpRequestBase} required as input to
 * perform request in {@link com.github.connector.engine.web.WebTemplate}.
 * <p/>
 * <p>Application tests will typically access this builder through the static
 * factory methods in {@link com.github.connector.engine.web.request.TestRequestBuilders}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class HttpRequestBuilders implements RequestBuilder {

    private final List<Header> headers = new ArrayList<>();
    private final List<NameValuePair> parameters = new ArrayList<>();
    private final List<Cookie> cookies = new ArrayList<>();

    private final List<RequestPostProcessor> postProcessors = new ArrayList<>();
    private HttpRequest httpRequest;
    private String uriTemplate;
    private byte[] content;
    private Locale locale;
    private String characterEncoding;

    /**
     * Package private constructor. To get an instance, use static factory
     * methods in {@link com.github.connector.engine.web.request.TestRequestBuilders}.
     * <p/>
     * <p>Although this class cannot be extended, additional ways to initialize
     * the {@code MockHttpServletRequest} can be plugged in via
     * {@link #with(RequestPostProcessor)}.
     *
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    HttpRequestBuilders(HttpRequest httpRequest, String urlTemplate, Object... urlVariables) {

        Objects.requireNonNull(urlTemplate, "uriTemplate is required");
        Objects.requireNonNull(httpRequest, "httpRequest is required");

        this.httpRequest = httpRequest;
        expandURLTemplate(urlTemplate, urlVariables);
        this.uriTemplate = urlTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest buildRequest() throws URISyntaxException {
        URIBuilder builder = new URIBuilder(uriTemplate);
        builder.addParameters(parameters);
        Header[] heads = new Header[headers.size()];
        httpRequest.setHeaders(headers.toArray(heads));
        ((HttpRequestBase) httpRequest).setURI(builder.build());
        postProcessors.stream().forEach(p -> httpRequest = p.postProcessRequest(httpRequest));
        if (content != null && content.length > 0) {
            ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(new ByteArrayEntity(content));
        }
        return httpRequest;
    }

    /**
     * Add a request parameter to the {@link com.github.connector.engine.web.request.HttpRequestBuilders}.
     * If called more than once, the new values are added.
     *
     * @param name  the parameter name
     * @param value the parameter value
     */
    public HttpRequestBuilders param(String name, String value) {
        Objects.requireNonNull(name, "parameter name must not be null");
        Optional.ofNullable(value).ifPresent(v -> parameters.add(new BasicNameValuePair(name, v)));
        return this;
    }

    /**
     * Replace a section of path in the uriTemplate
     *
     * @param name  replacement expression
     * @param value replacement value
     */
    public HttpRequestBuilders path(String name, String value) {
        Objects.requireNonNull(name, "path expression must not be null");
        Objects.requireNonNull(value, "path replacement must not be null");

        uriTemplate = uriTemplate.replaceAll("\\$\\{" + name + "\\}", value);
        return this;
    }

    /**
     * Add a header to the request. Values are always added.
     *
     * @param name   the header name
     * @param values one or more header values
     */
    public HttpRequestBuilders header(String name, Object... values) {
        Objects.requireNonNull(name, "header name must not be null");
        Arrays.asList(values).stream().filter(Objects::nonNull).forEach(
                value -> headers.add(new BasicHeader(name, value.toString())));
        return this;
    }

    /**
     * Set the 'Content-Type' header of the request.
     *
     * @param contentType the content type
     */
    public HttpRequestBuilders contentType(String contentType) {
        Objects.requireNonNull(contentType, "contentType must not be null");
        this.headers.add(new BasicHeader("Content-Type", contentType));
        return this;
    }

    /**
     * Set the 'Accept' header to the given media type(s).
     *
     * @param mediaTypes one or more media types
     */
    public HttpRequestBuilders accept(String... mediaTypes) {
        arrayNotEmpty(mediaTypes, "mediaTypes must not be null");
        this.headers.add(new BasicHeader("Accept",
                Lists.newArrayList(mediaTypes).stream().reduce((s1, s2) -> (s1 + ";" + s2)).orElse("")));
        return this;
    }

    /**
     * Set the request body.
     *
     * @param content the body content
     */
    public HttpRequestBuilders body(byte[] content) {
        this.content = content;
        return this;
    }

    /**
     * Set the request body.
     *
     * @param content the body content
     */
    public HttpRequestBuilders body(String content) {
        return body(content.getBytes());
    }

    /**
     * Set the request body form.
     *
     * @param param the body form parameter
     * @param value the body form value
     */
    public HttpRequestBuilders body(String param, String value) {
        notNull(param, "Parameter must not be null");
        stringNotBlank(value, "Value must not be blank");

        if (content != null && content.length > 0) {
            content = (new String(content) + "&" + param + "=" + value).getBytes();
        } else {
            if (param.equals("")) {
                return body(value.getBytes());
            } else {
                content = (param + "=" + value).getBytes();
            }
        }
        return this;
    }

    /**
     * Add the given cookies to the request. Cookies are always added.
     *
     * @param cookies the cookies to add
     */
    public HttpRequestBuilders cookie(Cookie... cookies) {
        Objects.requireNonNull(cookies, "cookies must not be null");
        arrayNotEmpty(cookies, "cookies must not be empty");
        this.cookies.addAll(Arrays.asList(cookies));
        return this;
    }

    /**
     * Set the locale of the request.
     *
     * @param locale the locale
     */
    public HttpRequestBuilders locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Set the character encoding of the request.
     *
     * @param encoding the character encoding
     */
    public HttpRequestBuilders characterEncoding(String encoding) {
        this.characterEncoding = encoding;
        return this;
    }


    /**
     * An extension point for further initialization of {@link org.apache.http.client.methods.HttpRequestBase}
     * in ways not built directly into the {@code MockHttpServletRequestBuilder}.
     * Implementation of this interface can have builder-style methods themselves
     * and be made accessible through static factory methods.
     *
     * @param postProcessor a post-processor to add
     */
    public HttpRequestBuilders with(RequestPostProcessor postProcessor) {
        Objects.requireNonNull(postProcessor, "postProcessor is required");
        this.postProcessors.add(postProcessor);
        return this;
    }

    private String expandURLTemplate(String urlTemplate, Object... urlVars) {
        if (urlVars == null || urlVars.length == 0) {
            return urlTemplate;
        }

        if (urlVars.length == 1) {
            return urlTemplate + "/" + urlVars[0].toString();
        } else {
            return Arrays.asList(urlVars).stream()
                    .reduce(urlTemplate, (v1, v2) -> "/" + v1.toString() + "/" + v2.toString()).toString();
        }
    }

}
