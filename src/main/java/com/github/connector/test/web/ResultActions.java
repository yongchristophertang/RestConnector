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

package com.github.connector.test.web;

/**
 * Allows applying actions, such as expectations, on the result of an executed
 * request.
 * <p>
 * <p>See static factory methods in
 * {@code com.github.connector.test.web.response.HttpResultMatchers}
 * {@code com.github.connector.test.web.response.HttpResultHandlers}
 * {@code com.github.connector.test.web.response.HttpResultTransformers}
 *
 * @author Yong Tang
 * @since 0.4
 */
public interface ResultActions {

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
     */
    ResultActions andExpect(ResultMatcher matcher) throws Exception;

    /**
     * Provide a general action. For example:
     * <pre>
     * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
     *
     * mockMvc.perform(get("/form")).andDo(print());
     * </pre>
     */
    ResultActions andDo(ResultHandler handler) throws Exception;

    /**
     * Provide a transformation of the result. It is a terminal method.
     *
     * @return the transformation of the result
     */
    <T> T andTransform(ResultTransform<T> transformer) throws Exception;

    /**
     * Return the result of the executed request for direct access to the results.
     *
     * @return the result of the request
     */
    HttpResult andReturn() throws Exception;

}