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

import com.github.connector.annotations.Mongo;
import com.github.connector.annotations.SqlDB;
import com.google.inject.AbstractModule;

/**
 * Guice module to provide db injection points.
 */
public class DbModule extends AbstractModule {
    private final Mongo[] mongos;
    private final SqlDB[] sqlDBs;

    /**
     * Instantiated via {@link com.github.connector.guice.DbModule.DbModuleBuilder}.
     */
    private DbModule(DbModuleBuilder builder) {
        this.mongos = builder.mongos;
        this.sqlDBs = builder.sqlDBs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        install(new SqlModule());
        install(new MongoModule());
        bind(Mongo.class).toProvider(new MongoDbProvider(mongos));
        bind(SqlDB.class).toProvider(new SqlDbProvider(sqlDBs));
    }

    /**
     * Instantiat {@link com.github.connector.guice.DbModule} through a inner builder {@link com.github.connector.guice.DbModule.DbModuleBuilder} by {@link com.github.connector.testng.TestNGModuleFactory};
     */
    public static class DbModuleBuilder {
        private Mongo[] mongos;
        private SqlDB[] sqlDBs;

        public DbModuleBuilder mongo(Mongo[] mongos) {
            this.mongos = mongos;
            return this;
        }

        public DbModuleBuilder sql(SqlDB[] sqlDBs) {
            this.sqlDBs = sqlDBs;
            return this;
        }

        public DbModule build() {
            return new DbModule(this);
        }
    }
}
