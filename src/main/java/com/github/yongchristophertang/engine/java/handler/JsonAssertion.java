/*
 *  Copyright 2014-2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.yongchristophertang.engine.java.handler;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

/**
 * Utility json assertion library for json string.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JsonAssertion {
    private final String context;

    public JsonAssertion(String context) {
        this.context = context;
    }

    /**
     * Evaluate the JSONPath and assert the value of the content found with the
     * given Hamcrest {@code Matcher}.
     */
    public <T> void pathMatch(String expression, final Matcher<T> matcher) {
        MatcherAssert.assertThat("Json value for " + expression, JsonPath.compile(expression).read(context), matcher);
    }

    /**
     * Evaluate the JSONPath and assert the value of the content found.
     */
    public <T> void pathMatch(String expression, T matcher) {
        pathMatch(expression, equalTo(matcher));
    }

    /**
     * Evaluate the JSONPath and assert that content exists.
     */
    public void pathExist(String expression) {
        Object value = JsonPath.compile(expression).read(context);
        MatcherAssert.assertThat("Jason Path Exists:", value, notNullValue());
        if (value instanceof List) {
            MatcherAssert.assertThat("Jason Path Collection Exists:", ((List<?>) value).isEmpty(), is(false));
        }
    }

    /**
     * Evaluate the JSON path and assert no content was found.
     */
    public void pathNotExist(String expression) {
        Object value = JsonPath.compile(expression).read(context);
        MatcherAssert.assertThat("Jason Path Exists:", value, nullValue());
        if (value instanceof List) {
            MatcherAssert.assertThat("Jason Path Collection Exists:", ((List<?>) value).isEmpty(), is(true));
        }
    }

    /**
     * Evaluate the JSON path and assert the content found is an array.
     */
    public void isArray(String expression) {
        pathMatch(expression, instanceOf(List.class));
    }

    /**
     * Evaluate the JSON path and assert the array found has the expected size.
     */
    public void arraySize(String expression, int size) {
        pathMatch(expression, instanceOf(List.class));
        MatcherAssert.assertThat("Json Path List Size:",
                ((List<?>) JsonPath.compile(expression).read(context)).size(), is(size));
    }
}
