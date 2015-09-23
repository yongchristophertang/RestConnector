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

package com.github.yongchristophertang.database.annotations;

import java.lang.annotation.*;

/**
 * Mongo DB configuration annotated with this interface
 *
 * @author Yong Tang
 * @since 0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(Mongos.class)
public @interface Mongo {
    /**
     * The config file location for Mongo connection, this value can overwrite the subsequent parameters.
     */
    String config() default "";

    /**
     * Mongo DB host.
     */
    String host() default "";

    /**
     * Mongo DB port.
     */
    int port() default 27017;

    /**
     * Mongo DB database name
     */
    String database() default "";

    /**
     * Mongo DB user
     */
    String user() default "";

    /**
     * Mongo DB password
     */
    String password() default "";
}
