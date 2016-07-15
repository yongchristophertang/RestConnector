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

package com.github.yongchristophertang.database.guice;

import com.github.yongchristophertang.database.annotations.SqlDB;
import com.github.yongchristophertang.database.guice.provider.ClientFactory;
import com.github.yongchristophertang.database.guice.provider.DataSourceFactory;
import com.github.yongchristophertang.database.guice.provider.JdbcTemplateFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Builder to create a module which can provide {@link javax.sql.DataSource} injection from all
 * marked {@link SqlDB} annotations on {@code Class<?> clazz} and its
 * superclasses.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JdbcModuleBuilder extends DBAnnotationModuleBuilder {
    public JdbcModuleBuilder(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Module buildModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                SqlDB[] sql = getAnnotations(null, clazz, SqlDB.class);

                bind(new TypeLiteral<ClientFactory<DataSource>>() {
                }).toInstance(new DataSourceFactory(sql));
                bind(new TypeLiteral<ClientFactory<JdbcTemplate>>() {
                }).toInstance(new JdbcTemplateFactory(sql));
            }

            @Provides
            DataSource provideJdbcDataSource(ClientFactory<DataSource> factory) {
                return factory.buildClients().poll();
            }

            @Provides
            JdbcTemplate provideJdbcTemplate(ClientFactory<JdbcTemplate> factory) {
                return factory.buildClients().poll();
            }
        };
    }
}
