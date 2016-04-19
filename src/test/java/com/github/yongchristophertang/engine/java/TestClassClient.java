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

package com.github.yongchristophertang.engine.java;

/**
 * Created by YongTang on 2016/3/9.
 *
 * @author Yong Tang
 * @since 0.5
 */
public class TestClassClient {
    private String init = "hello,";
    public TestClassClient(String init) {
        this.init = init;
    }

    public String testAPI(String cool) {
        return init + cool;
    }

    public DemoObject testObject(String test) {
        return new DemoObject(111, test, 1.345);
    }
}
