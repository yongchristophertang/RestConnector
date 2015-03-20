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
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by YongTang on 2015/3/20.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JdbcTemplateFactory extends AnnotationClientFactory<JdbcTemplate, SqlDB> {

    public JdbcTemplateFactory(SqlDB[] annos) {
        super(annos);
    }

    @Override
    protected JdbcTemplate createClient(SqlDB anno) {
        String config = anno.config();
        String user = anno.userName();
        String url = anno.url();
        String password = anno.password();

        if (StringUtils.isBlank(config)) {
            if (StringUtils.isBlank(user) || StringUtils.isBlank(url) || StringUtils.isBlank(password)) {
                throw new IllegalArgumentException("Not all necessary configuration are specified.");
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
        return new JdbcTemplate(ds);
    }
}
