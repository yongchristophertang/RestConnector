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

package com.github.connector.test.web.http;

import com.github.connector.test.AssertUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.util.Optional;

/**
 * Builder to construct a multipart entity
 *
 * @author Yong Tang
 * @since 0.4
 */
public class MultipartBodyFormBuilder implements FormBuilder {
    private MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    private MultipartBodyFormBuilder() {
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    public static MultipartBodyFormBuilder create() {
        return new MultipartBodyFormBuilder();
    }

    /**
     * Add a request parameter to body in {@link com.github.connector.test.web.request.HttpRequestBuilders}
     *
     * @param name  parameter name
     * @param value parameter value
     * @return {@link MultipartBodyFormBuilder}
     */
    public MultipartBodyFormBuilder param(String name, String value) {
        AssertUtils.notNull(name, "Parameter name must not be null.");
        Optional.ofNullable(value).ifPresent(v -> builder.addTextBody(name, v));
        return this;
    }

    /**
     * Add an attaching file in body of {@link com.github.connector.test.web.request.HttpRequestBuilders}
     *
     * @param name     attached file name
     * @param filePath local file path
     * @return {@link MultipartBodyFormBuilder}
     */
    public MultipartBodyFormBuilder file(String name, String filePath) {
        AssertUtils.notNull(name, "File name must not be null.");
        Optional.ofNullable(filePath).ifPresent(f -> builder.addBinaryBody(name, new File(f)));
        return this;
    }

    /**
     * Add a bytes content in body of {@link com.github.connector.test.web.request.HttpRequestBuilders}
     *
     * @param name    content name
     * @param content bytes content
     * @return {@link MultipartBodyFormBuilder}
     */
    public MultipartBodyFormBuilder content(String name, byte[] content) {
        builder.addBinaryBody(name, content);
        return this;
    }

    /**
     * Create a {@link com.github.connector.test.web.http.BodyForm} instance
     *
     * @return {@link com.github.connector.test.web.http.BodyForm}
     */
    @Override
    public BodyForm buildBody() {
        return builder::build;
    }
}
