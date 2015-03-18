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

import com.sun.istack.internal.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.github.connector.test.AssertUtils.notNull;

/**
 * Factory class to build {@link com.github.connector.store.ApiStore}
 *
 * @author Yong Tang
 * @since 0.4
 */
public class ApiStoreBuilder<K, T> {
    private static final Logger logger = LogManager.getLogger();

    private final Set<String> files;

    private Deserializable<T> deserializer;
    private Function<T, K> keyGenerator;

    /**
     * Produce files that match search pattern.
     */
    public ApiStoreBuilder(String pattern) {
        Reflections reflections = new Reflections(new ResourcesScanner());
        this.files = reflections.getResources(Pattern.compile(pattern));
        notNull(files, "config files must not be null");
    }

    /**
     * Define the search pattern for API config files
     *
     * @param pattern regex search pattern for file names
     */
//    public static <K, T> ApiStoreBuilder filePattern(@NotNull String pattern) {
//        notNull(pattern, "file pattern must not be null");
//        return new ApiStoreBuilder<K, T>(pattern);
//    }

    /**
     * Set the deserializer that implements {@link com.github.connector.store.Deserializable}, which can be used to
     * deserialize API config file to API instance.
     */
    public ApiStoreBuilder<K, T> deserializer(@NotNull Deserializable<T> des) {
        notNull(des, "deserializer must not be null");
        this.deserializer = des;
        return this;
    }

    /**
     * Generate key value from API instance in order to create {@link com.github.connector.store.ApiStore}.
     */
    public ApiStoreBuilder<K, T> keyGenerator(@NotNull Function<T, K> function) {
        notNull(function, "key extractor must not be null");
        keyGenerator = function;
        return this;
    }

    /**
     * Build an {@link com.github.connector.store.ApiStore}, the deserializer and keyGenerator must not be null.
     */
    public ApiStore<K, T> buildStore() {
        notNull(deserializer, "deserializer must not be null");
        notNull(keyGenerator, "key extractor must not be null");

        final Map<K, T> store = new HashMap<>();
        files.stream().map(f -> {
            try {
                return deserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(f));
            } catch (IOException e) {
                logger.warn(f + " can not be deserialized");
                return null;
            }
        }).filter(r -> r != null).forEach(api -> Optional.ofNullable(keyGenerator.apply(api)).ifPresent(
                k -> store.put(k, api)));
        return new MapApiStore<>(store);
    }
}
