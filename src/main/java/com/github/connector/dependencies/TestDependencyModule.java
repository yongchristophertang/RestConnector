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

package com.github.connector.dependencies;

import com.github.connector.store.ApiStore;
import com.github.connector.store.DefaultApiStoreBuilder;
import com.github.connector.store.HttpAPI;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

/**
 * Object graph module for dependency injection
 *
 * @author Yong Tang
 * @since 0.4
 */
public class TestDependencyModule extends AbstractModule {

    /**
     * Provides a default singleton {@link com.github.connector.store.ApiStore} with <{@link java.lang.String},
     * {@link HttpAPI}>.
     */
    @Provides
    @Singleton
    ApiStore<String, HttpAPI> provideApiStore() {
        return new DefaultApiStoreBuilder().buildStore();
    }

    @Override
    protected void configure() {

    }
}
