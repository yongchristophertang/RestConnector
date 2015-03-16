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
import com.github.connector.test.AssertUtils;
import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Factory implementation of {@link com.github.connector.guice.DataSourceFactory}.
 */
public class DataSourceFactoryImpl implements DataSourceFactory {
    private String config;
    private String user;
    private String password;
    private String url;

    private SqlDB sqlDB;

    @Inject
    public DataSourceFactoryImpl(SqlDB sqlDB) {
        AssertUtils.notNull(sqlDB, "SqlDB must not be null.");
        this.sqlDB = sqlDB;

        this.config = sqlDB.config();
        this.url = sqlDB.url();
        this.user = sqlDB.userName();
        this.password = sqlDB.password();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HikariDataSource toDataSource() {
        if (StringUtils.isBlank(config)) {
            if (StringUtils.isBlank(user) && StringUtils.isBlank(url) && StringUtils.isBlank(password)) {
                throw new IllegalArgumentException("All configurations of SqlDB are blank.");
            }
        } else {
            Properties prop = new Properties();
            try {
                prop.load(this.getClass().getClassLoader().getResourceAsStream(config));
                url = prop.getProperty("sql.jdbc.url");
                user = prop.getProperty("sql.jdbc.userName");
                password = prop.getProperty("sql.jdbc.password");
            } catch (IOException e) {
                return null;
            }
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlDB getSqlDB() {
        return sqlDB;
    }
}
