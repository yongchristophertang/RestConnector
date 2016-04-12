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

package com.github.yongchristophertang.config;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by YongTang on 2015/6/2.
 *
 * @author Yong Tang
 * @since 1.00
 */
//TODO: To be removed in the future
public abstract class PropertyHandler {

    public static void loadProperties(Class<?> testClass, Object[] testInstances) {
        Properties prop = new Properties();
        PropertyConfig[] propertyConfigs = getAnnotations(null, testClass, PropertyConfig.class);
        Lists.newArrayList(propertyConfigs).stream().filter(pc -> pc.value().endsWith(".properties")).forEach(pc -> {
            try {
                prop.load(testClass.getClassLoader().getResourceAsStream(pc.value()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Field[] fields = testClass.getFields();
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

    private static <A extends Annotation> A[] getAnnotations(A[] annos, Class<?> testClass, Class<A> annoClass) {
        return testClass == Object.class ? annos :
                getAnnotations(ArrayUtils.addAll(annos, testClass.getAnnotationsByType(annoClass)),
                        testClass.getSuperclass(), annoClass);
    }
}
