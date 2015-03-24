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

package com.connector.rest.engine.java;

/**
 * Proxy factories to build different kinds of proxies
 *
 * @author Yong Tang
 * @since 0.4
 */
public class ProxyFactories {
    /**
     * Create a logger proxy for {@code client}
     *
     * @see {@link LoggerProxyFactory}
     */
    public static <T> T createLoggerProxy(T client) {
        return LoggerProxyFactory.newProxyFactory(client).buildProxy();
    }

    /**
     * Create a proxy via a {@link ProxyFactory}
     */
    public static <T> T createLoggerProxy(ProxyFactory<T> factory) {
        return factory.buildProxy();
    }
}
