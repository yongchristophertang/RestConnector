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

package com.github.yongchristophertang.database.guice;

import com.github.yongchristophertang.database.annotations.Mongo;
import com.github.yongchristophertang.database.guice.provider.ClientFactory;
import com.github.yongchristophertang.database.guice.provider.MongoFactory;
import com.github.yongchristophertang.database.guice.provider.MongoTemplateFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Builder to create a module which can provide {@link com.mongodb.MongoClient} injection
 * from all
 * marked {@link Mongo} annotations on {@code Class<?> clazz} and its
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
                Mongo[] mongos = getAnnotations(null, clazz, Mongo.class);

                bind(new TypeLiteral<ClientFactory<MongoClient>>() {
                }).toInstance(new MongoFactory(mongos));
                bind(new TypeLiteral<ClientFactory<MongoTemplate>>() {
                }).toInstance(new MongoTemplateFactory(mongos));
            }

            @Provides
            MongoClient provideMongoClient(ClientFactory<MongoClient> factory) {
                return factory.buildClients().poll();
            }

            @Provides
            MongoTemplate provideMongoTemplate(ClientFactory<MongoTemplate> factory) {
                return factory.buildClients().poll();
            }
        };
    }
}
