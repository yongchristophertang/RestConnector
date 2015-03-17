/*
 *
 *  * Copyright 2014-2015 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.connector.test;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Static assertion lib
 *
 * @author Yong Tang
 * @since 0.4
 */
public abstract class AssertUtils {
    private AssertUtils() {
    }

    /**
     * Assert an object of Type T is not null.
     */
    public static <T> void notNull(T object, String message) {
        if (null == object) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert an array of T instances is not empty.
     */
    public static <T> void arrayNotEmpty(T[] objects, String message) {
        if (objects.length < 1) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a collection of T instances is not empty.
     */
    public static <T> void collectionNotEmpty(Collection<T> collection, String message) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a map of <K, V> is not empty.
     */
    public static <K, V> void mapNotEmpty(Map<K, V> map, String message) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a string is not null or blank.
     */
    public static void stringNotBlank(String s, String message) {
        if (StringUtils.isBlank(s)) {
            throw new IllegalArgumentException(message);
        }
    }
}
