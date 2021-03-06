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

/**
 * Matches the result of an executed request against some expectation.
 *
 * <p>See static factory methods in
 * {@code org.springframework.engine.web.server.result.MockMvcResultMatchers}.
 *
 * <p>Example:
 *
 * <pre>
 * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
 *
 * mockMvc.perform(get("/form"))
 *   .andExpect(status().isOk())
 *   .andExpect(content().mimeType(MediaType.APPLICATION_JSON));
 * </pre>
 *
 * @author Rossen Stoyanchev
 */
public interface ResultMatcher {

    /**
     * Assert the result of an executed request.
     *
     * @param result the result of the executed request
     * @throws Exception if a failure occurs
     */
    void match(HttpResult result) throws Exception;

}
