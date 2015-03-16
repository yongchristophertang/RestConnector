/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.connector.store;


/**
 * Singleton holder for default {@link com.github.connector.store.ApiStore}
 *
 * @author Yong Tang
 * @since 0.4
 */
public enum DefaultApiStoreSingletonHolder {
    INSTANCE(new DefaultApiStoreBuilder().buildStore());

    private final ApiStore<String, HttpAPI> apiStore;

    private DefaultApiStoreSingletonHolder(ApiStore<String, HttpAPI> apiStore) {
        this.apiStore = apiStore;
    }

    /**
     * Get the global {@link com.github.connector.store.ApiStore}
     * @return
     */
    public ApiStore<String, HttpAPI> getApiStore() {
        return apiStore;
    }
}
