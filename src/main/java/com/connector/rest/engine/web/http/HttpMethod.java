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

package com.connector.rest.engine.web.http;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.*;

/**
 * Enumeration of supported Http request methods
 *
 * @author YongTang
 * @since 0.4
 */
public enum HttpMethod {

    GET(new HttpGet()), POST(new HttpPost()), PUT(new HttpPut()), DELETE(new HttpDelete()), OPTIONS(new HttpOptions()),
    HEAD(new HttpHead()), PATCH(new HttpPatch()), TRACE(new HttpTrace());

    private HttpRequestBase requestBase;

    private HttpMethod(HttpRequestBase request) {
        this.requestBase = request;
    }

    public HttpRequest getHttpRequest() {
        try {
            return (HttpRequest) requestBase.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
