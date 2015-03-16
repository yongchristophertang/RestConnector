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

package com.github.connector.testng;

import com.github.connector.annotations.SqlDB;
import com.github.connector.dependencies.TestDependencyModule;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.List;

/**
 * Load API definitions from class path before testNG run starts
 *
 * @author Yong Tang
 * @since 0.4
 */
public class ApiRepositoryLoader extends TestListenerAdapter {
    private static final Logger logger = LogManager.getLogger();

//    Module module = new TestDependencyModule();


    @Override
    public void onStart(ITestContext context) {
//        context.getCurrentXmlTest().getClasses().stream().forEach(xmlClass->{
//            Reflections reflections = new Reflections(xmlClass.getName());
//            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(SqlDB.class);
//            if (classSet.contains(xmlClass.getSupportClass().))
//        });
//        System.out.println(context.getCurrentXmlTest().getClasses().get(0).getSupportClass().getAnnotations()[2].);
//        context.addGuiceModule(TestDependencyModule.class, module);
//        context.getInjector(Lists.newArrayList(new TestDependencyModule())).injectMembers();
    }

    @Override
    public void onTestStart(ITestResult tr) {
        List<Module> modules = tr.getTestContext().getGuiceModules(TestDependencyModule.class);
//        Reflections reflections = new Reflections(tr.getTestClass().getName());
//        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(SqlDB.class);
//        reflections.
//        if (classSet.contains(tr.getTestClass().getXmlClass().getSupportClass().getAnnotation().getRealClass())) {
//            System.out.println(tr.getTestClass().getName());
//        }


        SqlDB sqlDB = SqlDB.class.cast(tr.getTestClass().getXmlClass().getSupportClass().getAnnotation(SqlDB.class));
        if (StringUtils.isBlank(sqlDB.config()) && StringUtils.isNotBlank(sqlDB.url()) &&
                StringUtils.isNotBlank(sqlDB.userName()) && StringUtils.isNotBlank(sqlDB.password())) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(sqlDB.url());
            ds.setUsername(sqlDB.userName());
            ds.setPassword(sqlDB.password());
        } else if (StringUtils.isNotBlank(sqlDB.config())) {

        } else {
//            logger.error(String.format("This class %s is annotated with SqlDB but without setting DB configurations",
//                    tr.getTestClass().getName()));
            tr.setThrowable(new IllegalArgumentException(
                    String.format("This class %s is annotated with SqlDB but without setting DB configurations",
                            tr.getTestClass().getName())));
        }

    }
}
