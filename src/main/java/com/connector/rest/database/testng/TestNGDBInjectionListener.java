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

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.TestListenerAdapter;

/**
 * TestNG DB listener to create db injection module for each test class at the initiation
 * <p/>
 * Example is:
 * <pre>
 *     @Listeners(TestNGDBInjectionListener.class)
 * </pre>
 *
 * @author Yong Tang
 * @since 0.4
 */
public class TestNGDBInjectionListener extends TestListenerAdapter {

    @Override
    public void onStart(ITestContext context) {
        super.onStart(context);

        ITestNGMethod[] methods = context.getAllTestMethods();
        if (methods.length < 1) {
            return;
        }
        ITestClass testClass = methods[0].getTestClass();
        Object[] testInstances = testClass.getInstances(true);

        Module module = new TestNGDBInjectionModuleFactory().createModule(context, testClass.getRealClass());

        Injector injector = Guice.createInjector(module);
        Lists.newArrayList(testInstances).forEach(injector::injectMembers);
        injector.injectMembers(this);

        context.addGuiceModule(Module.class, module);
        context.addInjector(Lists.newArrayList(module), injector);
    }
}
