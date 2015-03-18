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

package com.github.connector.guice;

import com.github.connector.annotations.Mongo;
import com.github.connector.test.AssertUtils;
import com.google.inject.Provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Provides a queue of {@link com.github.connector.annotations.Mongo} which are injected into {@link com.github.connector.guice.MongoModule}.
 */
public class MongoDbProvider implements Provider<Mongo> {
    private final Queue<Mongo> mongos;

    public MongoDbProvider(Mongo[] ms) {
        AssertUtils.notNull(ms, "Mongo array must not be null.");

        mongos = new LinkedList<>();
        Collections.addAll(mongos, ms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mongo get() {
        return mongos.poll();
    }
}
