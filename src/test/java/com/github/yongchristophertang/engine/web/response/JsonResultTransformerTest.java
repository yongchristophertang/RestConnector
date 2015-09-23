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

package com.github.yongchristophertang.engine.web.response;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * {@see JsonResultTransformer}
 *
 * @author Yong Tang
 * @since 1.0
 */
public class JsonResultTransformerTest {

    @Test
    public void testList_ShouldReturnAList() throws IOException {
        JsonResultTransformer json = new JsonResultTransformer();
        List<Integer> list = json.list(Integer.class).transform(new DefaultHttpResult() {{
            setHttpResponse("[123,321]");
        }});
        System.out.println(list);
    }

    @Test
    public void testParseAListOut_ShouldReturnTheList() throws Exception {
        JsonResultTransformer json = new JsonResultTransformer();
        List<Integer> list = json.parse("$.test").transform(new DefaultHttpResult() {{
            setHttpResponse("{\"test\":[123,321]}");
        }}).andTransform(HttpResultTransformers.json().list(Integer.class));
        System.out.println(list);
    }

    private class TestObject {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "test='" + test + '\'' +
                    '}';
        }
    }
}