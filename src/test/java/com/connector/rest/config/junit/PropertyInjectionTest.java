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

package com.connector.rest.config.junit;

import com.connector.rest.config.Property;
import org.junit.Test;

/**
 * Created by YongTang on 2015/6/2.
 *
 * @author Yong Tang
 * @since 1.0
 */
public class PropertyInjectionTest extends AbstractPropertyInjectionTest {
    @Property("http.host")
    public String httpHost;

    @Property("http.port")
    public String httpPort;

    @Property("http")
    public String http;

    @Test
    public void testDemo() {
        System.out.println(httpHost);
        System.out.println(httpPort);
        System.out.println(httpTest);
        System.out.println(http);
    }

}
