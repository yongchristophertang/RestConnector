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

import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;

/**
 * Abstract builder to build a {@link com.google.inject.Module} from an annotation class {@code Class\<A extends
 * Annotation\>}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public abstract class DBAnnotationModuleBuilder implements ModuleBuilder {
    protected final Class<?> clazz;

    DBAnnotationModuleBuilder(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Return all marked {@link java.lang.annotation.Annotation}s from the {@code Class\<\?\> testClass} and its
     * superclasses.
     */
    protected <A extends Annotation> A[] getAnnotations(A[] annos, Class<?> testClass, Class<A> annoClass) {
        return testClass == Object.class ? annos :
                getAnnotations(ArrayUtils.addAll(annos, testClass.getAnnotationsByType(annoClass)),
                        testClass.getSuperclass(), annoClass);
    }
}
