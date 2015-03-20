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

package com.github.connector.testng;

import com.github.connector.annotations.Mongo;
import com.github.connector.annotations.SqlDB;
import com.github.connector.guice.ClientFactory;
import com.github.connector.guice.JdbcTemplateFactory;
import com.github.connector.guice.MongoTemplateFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Guice Module Factory for TestNG cases
 *
 * @author Yong Tang
 * @since 0.4
 */
public class TestNGModuleFactory implements IModuleFactory {
    /**
     * Check the class annotations, instantiate modules respectively.
     *
     * @param context   The current test context
     * @param testClass The test class
     * @return The Guice module that should be used to get an instance of this
     * test class.
     */
    @Override
    public Module createModule(ITestContext context, Class<?> testClass) {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(new TypeLiteral<ClientFactory<MongoTemplate>>() {
                }).toInstance(
                        new MongoTemplateFactory(getAnnotations(null, testClass, Mongo.class)));
                bind(new TypeLiteral<ClientFactory<JdbcTemplate>>() {
                }).toInstance(
                        new JdbcTemplateFactory(getAnnotations(null, testClass, SqlDB.class)));
            }

            @Provides
            MongoTemplate provideMontoTemplate(ClientFactory<MongoTemplate> factory) {
                MongoTemplate mongoTemplate = factory.buildClients().poll();
                factory.buildClients().add(mongoTemplate);
                return mongoTemplate;
            }

            @Provides
            JdbcTemplate provideJdbcTemplate(ClientFactory<JdbcTemplate> factory) {
                JdbcTemplate jdbcTemplate = factory.buildClients().poll();
                factory.buildClients().add(jdbcTemplate);
                return jdbcTemplate;
            }
        };
    }

    /*
    It is deterministic to return an A[]. Therefore here suppress warnings when converting Object[] to A[].
     */
    @SuppressWarnings("unchecked")
    private <A extends Annotation> A[] getAnnotations(@Nullable A[] annos, Class<?> testClass, Class<A> annoClass) {
        if (testClass == Object.class) {
            return annos;
        } else {
            return getAnnotations((A[]) ArrayUtils.addAll(annos, testClass.getAnnotationsByType(annoClass)),
                    testClass.getSuperclass(), annoClass);
        }
    }
}
