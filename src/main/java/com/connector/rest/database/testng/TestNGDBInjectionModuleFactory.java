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

package com.connector.rest.database.testng;

import com.connector.rest.database.guice.JdbcModuleBuilder;
import com.connector.rest.database.guice.MongoModuleBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

/**
 * Guice Module Factory for TestNG cases, this can be used in a {@link org.testng.annotations.Guice} annotation
 *
 * Example is:
 * <pre>
 *     @Guice(moduleFactory = TestNGDBModuleFactory.class)
 * </pre>
 *
 * @author Yong Tang
 * @since 0.4
 */
public class TestNGDBInjectionModuleFactory implements IModuleFactory {
    /**
     * Check the class annotations, instantiate modules respectively.
     *
     * @param context   The current engine context
     * @param testClass The engine class
     * @return The Guice module that should be used to get an instance of this
     * engine class.
     */
    @Override
    public Module createModule(ITestContext context, Class<?> testClass) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                install(new MongoModuleBuilder(testClass).buildModule());
                install(new JdbcModuleBuilder(testClass).buildModule());
            }
        };
    }
}
