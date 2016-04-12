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

import com.github.yongchristophertang.engine.web.ResultMatcher;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

/**
 * Factory for assertions on the response content using <a
 * href="http://goessner.net/articles/JsonPath/">JSONPath</a> expressions.
 * An instance of this class is typically accessed via
 * {@link HttpResultMatchers#jsonPath}.
 *
 * @author Yong Tang
 */
public class JsonPathResultMatchers {
    private final JsonPath jsonPath;

    /**
     * Access via {@link HttpResultMatchers#jsonPath}
     */
    protected JsonPathResultMatchers(String expression) {
        jsonPath = JsonPath.compile(expression);
    }

    /**
     * Evaluate the JSONPath and assert the value of the content found with the
     * given Hamcrest {@code Matcher}.
     */
    public <T> ResultMatcher value(final Matcher<T> matcher) {
        return result -> MatcherAssert
                .assertThat("Json Path:", jsonPath.read(result.getResponseStringContent()), matcher);
    }

    /**
     * Evaluate the JSONPath and assert the value of the content found.
     */
    public ResultMatcher value(Object value) {
        return value(equalTo(value));
    }

    /**
     * Evaluate the JSONPath and assert that content exists.
     */
    public ResultMatcher exists() {
        return result -> {
            Object value = jsonPath.read(result.getResponseStringContent());
            MatcherAssert.assertThat("Jason Path Exists:", value, notNullValue());
            if (value instanceof List) {
                MatcherAssert.assertThat("Jason Path List Exists:", ((List<?>) value).isEmpty(), is(false));
            }
        };
    }

    /**
     * Evaluate the JSON path and assert not content was found.
     */
    public ResultMatcher doesNotExist() {
        return result -> {
            Object value;
            try {
                value = jsonPath.read(result.getResponseStringContent());
            } catch (PathNotFoundException e) {
                value = null;
            }
            MatcherAssert.assertThat("Jason Path Not Exists:", value, nullValue());
            if (value instanceof List) {
                MatcherAssert.assertThat("Jason Path List Not Exists:", ((List<?>) value).isEmpty(), is(true));
            }
        };
    }

    /**
     * Evaluate the JSON path and assert the content found is an array.
     */
    public ResultMatcher isArray() {
        return value(instanceOf(List.class));
    }

    /**
     * Evaluate the JSON path and assert the array found has the expected size.
     */
    public ResultMatcher arraySize(int size) {
        return result -> {
            value(instanceOf(List.class));
            MatcherAssert.assertThat("Json Path List Size:",
                    ((List<?>) jsonPath.read(result.getResponseStringContent())).size(), is(size));
        };
    }
}
