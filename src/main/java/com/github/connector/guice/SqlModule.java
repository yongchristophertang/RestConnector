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

package com.github.connector.guice;

import com.github.connector.annotations.SqlDB;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Guice module to provide injection of {@link org.springframework.jdbc.core.JdbcTemplate}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class SqlModule extends AbstractModule {
    private static final Map<HikariDataSource, JdbcTemplate> store = new HashMap<>();
    private static Map<SqlDB, HikariDataSource> sqlDbs = new HashMap<>();

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     * bind {@link com.github.connector.guice.DataSourceFactory} to {@link com.github.connector.guice
     * .DataSourceFactoryImpl}.
     * bind {@link com.github.connector.annotations.SqlDB} to a customer provider instance {@link com.github
     * .connector.guice.SqlDbProvider}.
     */
    @Override
    protected void configure() {
        bind(DataSourceFactory.class).to(DataSourceFactoryImpl.class);
    }

    /**
     * Provide injection point of {@link org.springframework.jdbc.core.JdbcTemplate} via {@link com.zaxxer.hikari
     * .HikariDataSource}. Be noticed that the same {@link com.zaxxer.hikari.HikariDataSource} instance will produce
     * the same instance.
     */
    @Provides
    JdbcTemplate provideJdbcTemplate(HikariDataSource dataSource) {
        return Optional.ofNullable(store.get(dataSource)).orElseGet(() -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            store.put(dataSource, jdbcTemplate);
            return jdbcTemplate;
        });
    }

    /**
     * Produce injection point of {@link com.zaxxer.hikari.HikariDataSource} via {@link com.github.connector.guice
     * .DataSourceFactory}. The same {@link com.github.connector.annotations.SqlDB} instance that the {@code factory}
     * possesses will produce the same {@link com.zaxxer.hikari.HikariDataSource} instance.
     */
    @Provides
    HikariDataSource provideHikariDataSource(DataSourceFactory factory) {
        return Optional.ofNullable(sqlDbs.get(factory.getSqlDB())).orElseGet(() -> {
            HikariDataSource ds = factory.toDataSource();
            sqlDbs.put(factory.getSqlDB(), ds);
            return ds;
        });
    }
}
