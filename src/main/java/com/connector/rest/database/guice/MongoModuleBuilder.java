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

package com.connector.rest.database.guice;

import com.connector.rest.database.annotations.Mongo;
import com.connector.rest.database.guice.provider.ClientFactory;
import com.connector.rest.database.guice.provider.MongoFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;

/**
 * Builder to create a module which can provide {@link com.mongodb.MongoClient} injection
 * from all
 * marked {@link com.connector.rest.database.annotations.Mongo} annotations on {@code Class<?> clazz} and its
 * superclasses.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class MongoModuleBuilder extends DBAnnotationModuleBuilder {

    public MongoModuleBuilder(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Module buildModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(new TypeLiteral<ClientFactory<MongoClient>>() {
                }).toInstance(new MongoFactory(getAnnotations(null, clazz, Mongo.class)));
            }

            @Provides
            MongoClient provideMongoTemplate(ClientFactory<MongoClient> factory) {
                return factory.buildClients().poll();
            }
        };
    }
}
