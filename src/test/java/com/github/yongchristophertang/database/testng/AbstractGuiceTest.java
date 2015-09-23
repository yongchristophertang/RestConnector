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
import org.testng.annotations.Guice;

import javax.sql.DataSource;

/**
 * Created by YongTang on 2015/3/16.
 *
 * @author Yong Tang
 * @since 0.4
 */
@Guice(moduleFactory = TestNGDBInjectionModuleFactory.class)
@SqlDB(url = "111", userName = "222", password = "333")
//@PropertyConfig("http_config.properties")
public abstract class AbstractGuiceTest {
//    @Property("http.host")
//    public String httpHost;
    @Inject
    protected DataSource dataSource0;

    @BeforeClass
    public void setUp0() {
        System.out.println(dataSource0);
    }
}
