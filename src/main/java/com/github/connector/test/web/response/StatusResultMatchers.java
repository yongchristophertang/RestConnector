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
package com.github.connector.test.web.response;

import com.github.connector.test.web.ResultMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
/**
 * Factory for assertions on the response status. An instance of this class is
 * typically accessed via {@link HttpResultMatchers#status()}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class StatusResultMatchers {


    /**
     * Protected constructor.
     * Use {@link HttpResultMatchers#status()}.
     */
    protected StatusResultMatchers() {
    }

    /**
     * Assert the response status code with the given Hamcrest {@link org.hamcrest.Matcher}.
     */
    public ResultMatcher is(final Matcher<Integer> matcher) {
        return result -> MatcherAssert
                .assertThat("Status: ", result.getHttpResponse().getStatusLine().getStatusCode(), matcher);
    }

    /**
     * Assert the response status code is equal to an integer value.
     */
    public ResultMatcher is(int status) {
        return is(status);
    }


    /**
     * Assert the Servlet response error message with the given Hamcrest {@link org.hamcrest.Matcher}.
     */
    public ResultMatcher reason(final Matcher<? super String> matcher) {
        return result -> MatcherAssert
                .assertThat("Status reason: ", result.getHttpResponse().getStatusLine().getReasonPhrase(), matcher);
    }

    /**
     * Assert the Servlet response error message.
     */
    public ResultMatcher reason(String reason) {
        return reason(CoreMatchers.is(reason));
    }

}
