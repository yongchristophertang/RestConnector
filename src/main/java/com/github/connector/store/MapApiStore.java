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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable map implementation of {@link com.github.connector.store.ApiStore}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class MapApiStore<K, T> implements ApiStore<K, T> {
    private static final Logger logger = LogManager.getLogger();

    private final Map<K, T> infrastructure;

    /**
     * Create the api store through a map.
     * The underlying map is an immutable copy of the input map.
     */
    public MapApiStore(Map<K, T> apiMap) {
        infrastructure = Collections.unmodifiableMap(apiMap);
        logger.info(infrastructure);
    }

    /**
     * Get the API by Key
     */
    @Override
    public T getApi(K key) {
        return infrastructure.get(key);
    }

    @Override
    public String toString() {
        return infrastructure.toString();
    }
}
