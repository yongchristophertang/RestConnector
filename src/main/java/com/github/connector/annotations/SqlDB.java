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

package com.github.connector.annotations;

import java.lang.annotation.*;

/**
 * Indicates annotations with a test class can access to sql DBs with defined configurations
 *
 * @author Yong Tang
 * @since 0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(SqlDBs.class)
public @interface SqlDB {
    /**
     * File location for DB configurations, this config can overwrite the configs that constructed by below method.
     */
    String config() default "";

    /**
     * DB url
     */
    String url() default "";

    /**
     * DB userName
     */
    String userName() default "";

    /**
     * DB password
     */
    String password() default "";
}
