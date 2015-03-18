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

package com.github.connector.test.web.request;

import org.apache.http.HttpRequest;

/**
 * Extension point for applications or 3rd party libraries that wish to further
 * initialize a {@link org.apache.http.client.methods.HttpRequestBase} instance after it has been built
 * by {@link com.github.connector.test.web.request.HttpRequestBuilders} or its subclass {@link com.github.connector
 * .test.web.request.HttpMultipartRequestBuilders}.
 * <p/>
 * <p>Implementations of this interface can be provided to
 * {@link com.github.connector.test.web.request.HttpRequestBuilders#with(RequestPostProcessor)} at the time
 * when a request is about to be performed.
 *
 * @author Yong Tang
 * @since 0.4
 */
public interface RequestPostProcessor {


    /**
     * Post-factory the given {@code HttpRequestBase} after its creation
     * and initialization through a {@code HttpRequestBuilders}.
     *
     * @param request the request to initialize
     * @return the request to use, either the one passed in or a wrapped one;
     */
    HttpRequest postProcessRequest(HttpRequest request);


}
