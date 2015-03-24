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

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The main class to build {@link WebTemplate} with different configurations
 *
 * @author Yong Tang
 * @since 0.4
 */
public class WebTemplateBuilder implements TemplateBuilder {
    private RequestConfig.Builder builder;
    private List<ResultMatcher> resultMatchers = new ArrayList<>();
    private List<ResultHandler> resultHandlers = new ArrayList<>();

    /**
     * Accessed via {@link WebTemplateBuilder}
     */
    private WebTemplateBuilder(RequestConfig.Builder builder) {
        this.builder = builder;
    }

    /**
     * Build a webTemplate with custom config
     *
     * @return {@link WebTemplateBuilder}
     */
    public static WebTemplateBuilder customConfig() {
        return new WebTemplateBuilder(RequestConfig.custom());
    }

    /**
     * Build a {@link WebTemplate} with default settings.
     *
     * @return {@link TemplateBuilder}
     */
    public static TemplateBuilder defaultConfig() {
        return customConfig();
    }

    /**
     * Set http connection time-out
     *
     * @param timeOut timeOut length in milliseconds
     */
    public WebTemplateBuilder connectionTimeOut(int timeOut) {
        builder.setConnectTimeout(timeOut);
        return this;
    }

    /**
     * Set cookie policy to {@link org.apache.http.client.config.CookieSpecs#BEST_MATCH}
     */
    public WebTemplateBuilder setCookie() {
        builder.setCookieSpec(CookieSpecs.BEST_MATCH);
        return this;
    }

    /**
     * Set cookie policy to {@link org.apache.http.client.config.CookieSpecs#IGNORE_COOKIES}
     */
    public WebTemplateBuilder noCookie() {
        builder.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
        return this;
    }

    /**
     * Set global default {@link ResultMatcher} for built {@link WebTemplate}.
     */
    public WebTemplateBuilder alwaysExpect(ResultMatcher resultMatcher) {
        Objects.requireNonNull(resultMatcher, "resultMatcher must not be null");
        resultMatchers.add(resultMatcher);
        return this;
    }

    /**
     * Set global default {@link ResultHandler} for built {@link WebTemplate}.
     */
    public WebTemplateBuilder alwaysDo(ResultHandler resultHandler) {
        Objects.requireNonNull(resultHandler, "resultHandler must not be null");
        resultHandlers.add(resultHandler);
        return this;
    }

    /**
     * Build a {@link WebTemplate} with custom config builder
     */
    @Override
    public WebTemplate build() {
        WebTemplate webTemplate = new WebTemplate(builder.build());
        webTemplate.setDefaultResultHandlers(resultHandlers);
        webTemplate.setDefaultResultMatchers(resultMatchers);
        return webTemplate;
    }
}
