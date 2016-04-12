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

package com.github.yongchristophertang.engine.web;

import com.github.yongchristophertang.engine.web.request.TestRequestBuilders;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.github.yongchristophertang.engine.web.response.HttpResultHandlers.print;

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
        webTemplate = WebTemplateBuilder.defaultConfig().build();
    }

    @Test
    public void testDemo() throws Exception {
        webTemplate.perform(TestRequestBuilders.api(TestAPI.class).postGrade("{\"engine\":123}")).andDo(print());
    }

    @Test
    public void testDemo2() throws Exception {
        webTemplate.perform(TestRequestBuilders.api(TestAPI.class).uploadFile("name", "")).andDo(print());
    }

    @Test
    public void testDemo3() throws Exception {
        Tester tester = new Tester();
        tester.setId(100);
//        tester.setName("tester");
        Map<String, String> queries = new HashMap<>();
        queries.put("add1", "value1");
        queries.put("add2", "value2");
        webTemplate
            .perform(TestRequestBuilders.api(TestAPI.class).postTester("", Arrays.asList("1", "2", "3"), null, queries))
            .andDo(print());
    }

    @Test
    public void testDemo4() throws Exception {
        webTemplate.perform(TestRequestBuilders.api(TestAPI.class).inputBinStream().body(new byte[]{}));
    }

    @Test
    public void testDemo5() throws Exception {
        webTemplate.perform(TestRequestBuilders.uploadFile("http://test.cystorage.cycore.cn/v2/files/upload")
            .body("module", "panFilePrepareAdd").body("expires", "1460362301483").body("token", "q1AhfhihxKMtMAk40eQdwI6jCm4=")
            .body("userId", "2000000017000005331").body("action", "add").body("appKey", "rrt_ewp").file("file", "test-resource/test.txt")).andDo(print());
    }

    @Test
    public void testDemo6() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("module", "panFilePrepareAdd");
        params.put("expires", "1460366029660");
        params.put("token", "CuePqlgUacv4sqtsqgcYUGjSc7U=");
        params.put("userId", "2000000017000005331");
        params.put("action", "add");
        params.put("appKey", "rrt_ewp");
        webTemplate.perform(TestRequestBuilders.api(UploadAPI.class).uploadFile("test-resource/test.txt", params)).andDo(print());
    }
}
