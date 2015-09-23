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

import com.github.yongchristophertang.engine.web.*;

/**
 * A default implementation of {@link ResultActions}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class DefaultResultActions implements ResultActions {
    private final HttpResult httpResult;

    public DefaultResultActions(HttpResult httpResult) {
        this.httpResult = httpResult;
    }

    /**
     * Provide an expectation. For example:
     * <pre>
     * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
     *
     * mockMvc.perform(get("/person/1"))
     *   .andExpect(status().isOk())
     *   .andExpect(content().mimeType(MediaType.APPLICATION_JSON))
     *   .andExpect(jsonPath("$.person.name").equalTo("Jason"));
     *
     * mockMvc.perform(post("/form"))
     *   .andExpect(status().isOk())
     *   .andExpect(redirectedUrl("/person/1"))
     *   .andExpect(model().size(1))
     *   .andExpect(model().attributeExists("person"))
     *   .andExpect(flash().attributeCount(1))
     *   .andExpect(flash().attribute("message", "success!"));
     * </pre>
     *
     * @param matcher
     */
    @Override
    public ResultActions andExpect(ResultMatcher matcher) throws Exception {
        matcher.match(httpResult);
        return this;
    }

    /**
     * Provide a general action. For example:
     * <pre>
     * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
     *
     * mockMvc.perform(get("/form")).andDo(print());
     * </pre>
     *
     * @param handler
     */
    @Override
    public ResultActions andDo(ResultHandler handler) throws Exception {
        handler.handle(httpResult);
        return this;
    }

    /**
     * Provide a transformation of the result. It is a terminal method.
     *
     * @return the transformation of the result
     */
    @Override
    public <T> T andTransform(ResultTransform<T> transformer) throws Exception {
        return transformer.transform(httpResult);
    }

    /**
     * Return the result of the executed request for direct access to the results. It is a terminal method.
     *
     * @return the result of the request
     */
    @Override
    public HttpResult andReturn() throws Exception {
        return httpResult;
    }
}
