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

package com.github.connector.engine.web;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Provides access to the result of an executed request.
 *
 * @author Yong Tang
 * @since 0.4
 */
public interface HttpResult {
    /**
     * Return the cost time of performed http request.
     *
     * @return cost time in milliseconds
     */
    public long getCostTime();

    /**
     * Return the performed http request.
     */
    public HttpRequest getHttpRequest();

    /**
     * Return the returned http response.
     */
    public HttpResponse getHttpResponse();

    /**
     * Return the response content body as string.
     * <strong>Always access to body content using this method, because the access from HttpResponse can only consume
     * ONCE</strong>.
     */
    public String getResponseStringContent() throws IOException;
}
