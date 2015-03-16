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
