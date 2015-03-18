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

package com.github.connector.store;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default builder to build an api store with {@link java.lang.String} key and {@link HttpAPI} api object.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class DefaultApiStoreBuilder extends ApiStoreBuilder<String, HttpAPI> {
    /**
     * Produce filePaths from all json files in the classpath.
     */
    public DefaultApiStoreBuilder() {
        super(".+\\.json");
        ObjectMapper mapper = new ObjectMapper();
        super.deserializer(file -> mapper.readValue(file, HttpAPI.class));
        super.keyGenerator(HttpAPI::getApi);
    }

    /**
     * Build default api store.
     */
    @Override
    public ApiStore<String, HttpAPI> buildStore() {
        return super.buildStore();
    }
}
