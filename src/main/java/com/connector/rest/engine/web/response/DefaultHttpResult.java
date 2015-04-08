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

package com.connector.rest.engine.web.response;

import com.connector.rest.engine.web.HttpResult;
import com.google.common.annotations.VisibleForTesting;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * A default implementation for {@link com.connector.rest.engine.web.HttpResult}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class DefaultHttpResult implements HttpResult {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final long time;

    private String response;

    /**
     * Build an instance of {@link com.connector.rest.engine.web.HttpResult} from an {@link org.apache.http
     * .HttpRequest}, an {@link org.apache.http.HttpResponse} and an execution time.
     */
    public DefaultHttpResult(HttpRequest httpRequest, HttpResponse httpResponse, long time) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.time = time;
    }

    /**
     * Build an instance of {@link com.connector.rest.engine.web.HttpResult} from an existing one.
     */
    public DefaultHttpResult(HttpResult httpResult) {
        this.httpRequest = httpResult.getHttpRequest();
        this.httpResponse = httpResult.getHttpResponse();
        this.time = httpResult.getCostTime();
    }

    /**
     * Only for test
     */
    @VisibleForTesting
    DefaultHttpResult() {
        httpRequest = null;
        httpResponse = null;
        time = 0;
    }

    @Override
    public long getCostTime() {
        return time;
    }

    @Override
    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    @Override
    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(String response) {
        this.response = response;
    }

    @Override
    public String getResponseStringContent() throws IOException {
        if (response == null) {
            response = EntityUtils.toString(httpResponse.getEntity());
        }
        return response;
    }
}
