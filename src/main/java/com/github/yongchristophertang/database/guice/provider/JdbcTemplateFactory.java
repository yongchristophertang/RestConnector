/*
 *  Copyright 2014-2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.yongchristophertang.database.guice.provider;

import com.github.yongchristophertang.database.annotations.SqlDB;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Factory to create {@link JdbcTemplate} via {@link SqlDB}.
 *
 * @author Yong Tang
 * @since 0.5
 */
public class JdbcTemplateFactory extends AnnotationClientFactory<JdbcTemplate, SqlDB> {

    public JdbcTemplateFactory(SqlDB[] annos) {
        super(annos);
    }

    /**
     * Transfer an annotation to a T object.
     *
     * @param anno Sql annotation
     */
    @Override
    protected JdbcTemplate createClient(SqlDB anno) {
        if (StringUtils.isBlank(anno.url()) || StringUtils.isBlank(anno.userName()) || StringUtils.isBlank(anno.password())) {
            throw new IllegalArgumentException("Not all necessary configuration are specified.");
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(anno.url());
        ds.setUsername(anno.userName());
        ds.setPassword(anno.password());
        return new JdbcTemplate(ds);
    }
}
