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

import org.testng.annotations.Test;

import static com.connector.rest.engine.web.request.TestRequestBuilders.api;
import static com.connector.rest.engine.web.response.HttpResultHandlers.print;
import static com.connector.rest.engine.web.response.HttpResultMatchers.content;
import static com.connector.rest.engine.web.response.HttpResultMatchers.jsonPath;
import static com.connector.rest.engine.web.response.HttpResultTransformers.json;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by YongTang on 2015/3/19.
 *
 * @author Yong Tang
 * @since 0.4
 */
//@Guice(moduleFactory = TestNGModuleFactory.class)
//@Mongo(host = "172.16.79.20", port = 30017, database = "categories")
public class EpdResDemoTest {
//    @Inject
//    private MongoTemplate mongoTemplate;

    @Test
    public void test() throws Exception {
        WebTemplate webTemplate = WebTemplateBuilder.defaultConfig().build();
        Object object = webTemplate.perform(api(TestAPI.class).getGrades("1234", 456)).andDo(print())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(jsonPath("$.code", is("10004040"))).andTransform(json().object(Object.class));
//        System.out.println(mongoTemplate);
    }

    @Test
    public void testDefaultMethod() throws Exception {
        WebTemplate webTemplate = WebTemplateBuilder.defaultConfig().build();
        webTemplate.perform(api(TestAPI.class).testDefaultMethod()).andDo(print());
    }
}
