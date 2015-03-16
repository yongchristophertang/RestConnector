/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.connector.guice;

import com.github.connector.annotations.SqlDB;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Factory to build {@link com.zaxxer.hikari.HikariDataSource} via {@link com.github.connector.annotations.SqlDB}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public interface DataSourceFactory {

    /**
     * Transfer SqlDB annotation configuration to {@link com.zaxxer.hikari.HikariDataSource}.
     * The config parameter has the highest priority.
     * If all parameters are blank, throw {@link IllegalArgumentException}.
     * If config file has errors to read, return null.
     */
    HikariDataSource toDataSource();

    /**
     * Return the underlying {@link com.github.connector.annotations.SqlDB} object.
     *
     * @return {@see SqlDB}
     */
    SqlDB getSqlDB();
}
