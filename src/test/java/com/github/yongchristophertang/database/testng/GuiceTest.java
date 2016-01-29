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

package com.github.yongchristophertang.database.testng;

import com.github.yongchristophertang.database.annotations.SqlDB;
import com.google.inject.Inject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by YongTang on 2015/3/16.
 *
 * @author Yong Tang
 * @since 0.4
 */
@Test
//@SqlDB(url = "111", userName = "222", password = "333")
//@SqlDB(url = "333", userName = "222", password = "333")
@SqlDB(url = "jdbc:sqlserver://172.16.79.25:1433;databaseName=usp_center_iflybook_test", userName = "sa", password = "Iflysql@server123")
public class GuiceTest extends AbstractGuiceTest {
//    @Inject
//    private Provider<JdbcTemplate> jdbcTemplateProvider;


//    @Property("http.port")
//    public String httpPort;
    @Inject
    private DataSource dataSource;
//    @Inject
//    private DataSource dataSource2;
//    @Inject
//    private DataSource dataSource3;

    @BeforeClass
    private void setUp() {
//        jdbcTemplate = jdbcTemplateProvider.get();
//        jdbcTemplate = jdbcTemplateProvider.get();
    }
@Test
    private void testDemo() throws SQLException {
    System.out.println(dataSource0.getConnection().getMetaData());
    System.out.println(dataSource0.getConnection().isValid(100));
    System.out.println(dataSource);
//        System.out.println(dataSource2);
//        dataSource.getConnection();
//        System.out.println(httpPort);
//        System.out.println(httpHost);
    }

    @Test
    private void testDemo2() {
        System.out.println(dataSource);
//        System.out.println(dataSource2);
    }

}
