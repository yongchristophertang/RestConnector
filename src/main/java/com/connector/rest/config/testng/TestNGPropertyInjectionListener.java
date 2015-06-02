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

package com.connector.rest.config.testng;

import com.connector.rest.config.Property;
import com.connector.rest.config.PropertyConfig;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.TestListenerAdapter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * TestNG listener for property injection
 *
 * @author Yong Tang
 * @since 1.0
 */
public class TestNGPropertyInjectionListener extends TestListenerAdapter {
    @Override
    public void onStart(ITestContext context) {
        super.onStart(context);

        ITestNGMethod[] methods = context.getAllTestMethods();
        if (methods.length < 1) {
            return;
        }
        ITestClass testClass = methods[0].getTestClass();
        Object[] testInstances = testClass.getInstances(true);

        Properties prop = new Properties();
        PropertyConfig[] propertyConfigs = getAnnotations(null, testClass.getRealClass(), PropertyConfig.class);
        Lists.newArrayList(propertyConfigs).stream().filter(pc -> pc.value().endsWith(".properties")).forEach(pc -> {
            try {
                prop.load(testClass.getRealClass().getClassLoader().getResourceAsStream(pc.value()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Field[] fields = testClass.getRealClass().getFields();
        Lists.newArrayList(fields).stream().filter(f -> f.isAnnotationPresent(Property.class))
                .filter(f -> f.getType() == String.class)
                .peek(f -> f.setAccessible(true)).forEach(f -> {
            for (Object instance : testInstances) {
                try {
                    f.set(instance, prop.getProperty(f.getAnnotation(Property.class).value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private <A extends Annotation> A[] getAnnotations(A[] annos, Class<?> testClass, Class<A> annoClass) {
        return testClass == Object.class ? annos :
                getAnnotations(ArrayUtils.addAll(annos, testClass.getAnnotationsByType(annoClass)),
                        testClass.getSuperclass(), annoClass);
    }
}
