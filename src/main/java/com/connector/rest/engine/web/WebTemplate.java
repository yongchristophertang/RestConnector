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

package com.connector.rest.engine.web;

import com.connector.rest.engine.web.request.RequestBuilder;
import com.connector.rest.engine.web.response.DefaultHttpResult;
import com.connector.rest.engine.web.response.DefaultResultActions;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * <strong>Main entry point for Http engine support.</strong>
 * <p/>
 * <p>Below is an example:
 * <p/>
 * <pre>
 *
 * WebTemplate webTemplate = WebTemplateBuilder.defaultConfig().build();
 *
 * webTemplate.perform(get(http://localhost:8080).param("test", "test")).andDo(print())
 *     .andExpect(content().contentType("application/json;charset=utf-8"))
 *     .andExpect(jsonPath("$.code", is("10004040"))).andTransform(json().parse("$.code")).andDo(print());
 *
 * </pre>
 *
 * @author Yong Tang
 * @since 0.4
 */
public final class WebTemplate {
    private HttpClient httpClient;
    private List<ResultMatcher> defaultResultMatchers = new ArrayList<>();
    private List<ResultHandler> defaultResultHandlers = new ArrayList<>();

    /**
     * Access via {@link WebTemplateBuilder#build}
     */
    WebTemplate(RequestConfig config) {
        httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
    }

    public ResultActions perform(RequestBuilder builder) throws Exception {
        HttpUriRequest httpRequest = (HttpUriRequest) builder.buildRequest();

        long before = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        long after = System.currentTimeMillis();
        HttpResult httpResult = new DefaultHttpResult(httpRequest, httpResponse, after - before);

        applyDefaultResultMatchersAndHandlers(httpResult);
        return new DefaultResultActions(httpResult);
    }

    private void applyDefaultResultMatchersAndHandlers(HttpResult httpResult) throws Exception {
        for (ResultMatcher matcher : defaultResultMatchers) {
            matcher.match(httpResult);
        }

        for (ResultHandler handler : defaultResultHandlers) {
            handler.handle(httpResult);
        }
    }

    /**
     * Expectations to assert after every performed request.
     *
     * @see WebTemplateBuilder#alwaysExpect
     */
    void setDefaultResultMatchers(List<ResultMatcher> resultMatchers) {
        Assert.notNull(resultMatchers, "resultMatchers is required");
        this.defaultResultMatchers = resultMatchers;
    }

    /**
     * General actions to apply after every performed request.
     *
     * @see WebTemplateBuilder#alwaysDo
     */
    void setDefaultResultHandlers(List<ResultHandler> resultHandlers) {
        Assert.notNull(resultHandlers, "resultHandlers is required");
        this.defaultResultHandlers = resultHandlers;
    }
}
