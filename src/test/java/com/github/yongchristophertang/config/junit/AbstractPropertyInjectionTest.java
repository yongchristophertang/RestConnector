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

package com.github.yongchristophertang.config.junit;

import com.github.yongchristophertang.config.Property;
import com.github.yongchristophertang.config.PropertyConfig;
import com.github.yongchristophertang.config.testng.TestNGPropertyInjectionListener;
import org.junit.Rule;
import org.testng.annotations.Listeners;

/**
 * Created by YongTang on 2015/6/2.
 *
 * @author Yong Tang
 * @since 1.0
 */
@Listeners(TestNGPropertyInjectionListener.class)
@PropertyConfig("http_config.properties")
public abstract class AbstractPropertyInjectionTest {
    @Property("http.test")
    public String httpTest;

    @Rule
    public JUnit4PropertyInjectionRule rule = new JUnit4PropertyInjectionRule();
}
