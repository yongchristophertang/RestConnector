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

import com.github.yongchristophertang.engine.web.http.BodyForm;
import com.github.yongchristophertang.engine.web.http.MultipartBodyFormBuilder;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Collection;
import java.util.Map;

import static com.github.yongchristophertang.engine.AssertUtils.notNull;

/**
 * Multipart builder to build {@link org.apache.http.client.methods.HttpRequestBase}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class HttpMultipartRequestBuilders extends HttpRequestBuilders {
    private MultipartBodyFormBuilder bodyBuilder = null;

    /**
     * Package private constructor. To get an instance, use static factory
     * methods in {@link TestRequestBuilders}.
     * <p/>
     * <p>Although this class cannot be extended, additional ways to initialize
     * the {@code MockHttpServletRequest} can be plugged in via
     * {@link #with(RequestPostProcessor)}.
     *
     * @param httpRequest  an HttpRequest object
     * @param urlTemplate  a URL template; the resulting URL will be encoded
     * @param urlVariables zero or more URL variables
     */
    HttpMultipartRequestBuilders(HttpRequest httpRequest, String urlTemplate, String description,
        Object... urlVariables) {
        super(httpRequest, urlTemplate, description, urlVariables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestBase buildRequest() throws Exception {
        HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) super.buildRequest();
        requestBase.setEntity(this.bodyBuilder.buildBody().getHttpEntity());
        return requestBase;
    }

    /**
     * Set the multipart body
     *
     * @param multipartBodyFormBuilder factory builder for {@link BodyForm}
     */
    public HttpRequestBuilders body(MultipartBodyFormBuilder multipartBodyFormBuilder) {
        this.bodyBuilder = multipartBodyFormBuilder;
        return this;
    }

    /**
     * Set the uploaded file
     *
     * @param name     updated file name
     * @param filePath local file path
     */
    public HttpMultipartRequestBuilders file(String name, String filePath) {
        if (bodyBuilder == null) {
            bodyBuilder = MultipartBodyFormBuilder.create();
        }

        bodyBuilder.file(name, filePath);
        return this;
    }

    /**
     * Set multipart body parameters
     *
     * @param param parameter name
     * @param values parameter values
     */
    public HttpMultipartRequestBuilders body(String param, Collection<String> values) {
        notNull(param, "Parameter must not be null");

        if (bodyBuilder == null) {
            bodyBuilder = MultipartBodyFormBuilder.create();
        }

        bodyBuilder.param(param, values);
        return this;
    }

    /**
     * Set multipart body parameter
     *
     * @param param the body form parameter
     * @param value the parameter value
     */
    public HttpMultipartRequestBuilders body(String param, String value) {
        notNull(param, "Parameter must not be null");

        if (bodyBuilder == null) {
            bodyBuilder = MultipartBodyFormBuilder.create();
        }

        bodyBuilder.param(param, value);
        return this;
    }

    /**
     * Set multipart body parameters
     *
     * @param params parameters in {@See Map}
     * @return
     */
    public HttpMultipartRequestBuilders body(Map<String, Object> params) {
        notNull(params, "Parameters must not be null");

        if (bodyBuilder == null) {
            bodyBuilder = MultipartBodyFormBuilder.create();
        }

        params.entrySet().forEach(e -> bodyBuilder.param(e.getKey(), e.getValue().toString()));
        return this;
    }
}
