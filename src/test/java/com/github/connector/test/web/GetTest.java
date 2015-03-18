/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.connector.test.web;

import org.junit.Before;
import org.junit.Test;

import static com.github.connector.test.web.request.TestRequestBuilders.api;
import static com.github.connector.test.web.response.HttpResultHandlers.print;

/**
 * Created by YongTang on 2015/3/17.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class GetTest {
    private WebTemplate webTemplate;

    @Before
    public void setUp() {
        WebTemplate webTemplate = WebTemplateBuilder.defaultConfig().build();
    }

    @Test
    public void testDemo() throws Exception {
        webTemplate = WebTemplateBuilder.defaultConfig().build();
        webTemplate.perform(api(TestAPI.class).postGrade("{\"test\":123}")).andDo(print());
    }
}
