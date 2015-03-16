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
import com.google.inject.Provider;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Provides a queue of {@link com.github.connector.annotations.SqlDB} which are injected into {@link com.github
 * .connector.guice.SqlModule}.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class SqlDbProvider implements Provider<SqlDB> {
    private final Queue<SqlDB> sqlDBs;

    public SqlDbProvider(SqlDB[] dbs) {
        AssertUtils.notNull(dbs, "SqlDB array must not be null.");
        AssertUtils.arrayNotEmpty(dbs, "SqlDB array must not be empty.");

        sqlDBs = new LinkedList<>();
        for (SqlDB db : dbs) {
            sqlDBs.add(db);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SqlDB get() {
        return sqlDBs.poll();
    }
}
