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

package com.connector.rest.database.junit;

import com.connector.rest.database.guice.JdbcModuleBuilder;
import com.connector.rest.database.guice.MongoModuleBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * A JUnit4 {@link org.junit.Rule} to inject DB related handlers {@link org.springframework.jdbc.core.JdbcTemplate}
 * and {@link org.springframework.data.mongodb.core.MongoTemplate} from the annotations {@link com.connector.rest
 * .database.annotations.SqlDB} and {@link com.connector.rest.database.annotations.Mongo} marked on the test class
 * and its superclasses.
 * <p/>
 * Example:
 * <pre>
 *     @Rule
 *     public JUnit4DBInjectionRule rule = new JUnit4DBInjectionRule();
 * </pre>
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JUnit4DBInjectionRule implements MethodRule {
    // TODO: bad practice, should remove static variables
    private static Map<Class<?>, Injector> guiceInjectors = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        Injector injector;
        Class<?> clazz = method.getDeclaringClass();
        if (!guiceInjectors.keySet().contains(clazz)) {
            Module mongoModule = new MongoModuleBuilder(clazz).buildModule();
            Module jdbcModule = new JdbcModuleBuilder(clazz).buildModule();
            injector = Guice.createInjector(mongoModule, jdbcModule);
            guiceInjectors.put(clazz, injector);
        } else {
            injector = guiceInjectors.get(clazz);
        }
        injector.injectMembers(target);
        return base;
    }

}
