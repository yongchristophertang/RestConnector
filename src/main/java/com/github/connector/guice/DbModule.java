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
