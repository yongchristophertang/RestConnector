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

package com.github.connector.database.testng;

import com.google.common.collect.Lists;
import com.google.inject.*;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.TestListenerAdapter;

/**
 * TestNG DB listener to create db injection module for each engine class at the initiation and to close all db
 * connections while deconstruction of this engine class.
 *
 * Example is:
 * <pre>
 *     @Listeners(TestNGDBListener.class)
 * </pre>
 *
 * @author Yong Tang
 * @since 0.4
 */
public class TestNGDBListener extends TestListenerAdapter {

    @Inject
    private Provider<JdbcTemplate> jdbcTemplateProvider;

    @Inject
    private Provider<MongoTemplate> mongoTemplateProvider;

    @Override
    public void onStart(ITestContext context) {
        super.onStart(context);

        ITestNGMethod[] methods = context.getAllTestMethods();
        if (methods.length < 1) {
            return;
        }
        ITestClass testClass = methods[0].getTestClass();
        Object[] testInstances = testClass.getInstances(true);

        Module module = new TestNGDBModuleFactory().createModule(context, testClass.getRealClass());

        Injector injector = Guice.createInjector(module);
        Lists.newArrayList(testInstances).forEach(injector::injectMembers);
        injector.injectMembers(this);

        context.addGuiceModule(Module.class, module);
        context.addInjector(Lists.newArrayList(module), injector);
    }

    @Override
    public void onFinish(ITestContext context) {
        while (true) {
            JdbcTemplate jdbcTemplate = jdbcTemplateProvider.get();
            if (jdbcTemplate.getDataSource() == null) {
                break;
            }
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
            jdbcTemplate.setDataSource(null);
        }

        while (true) {
            MongoTemplate mongoTemplate = mongoTemplateProvider.get();
            if (mongoTemplate == null) {
                break;
            }
            mongoTemplate.getDb().getMongo().close();
            mongoTemplate = null;
        }
    }
}
