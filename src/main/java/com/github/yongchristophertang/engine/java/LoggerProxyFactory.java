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

package com.github.yongchristophertang.engine.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Logger proxy factory to inject necessary descriptive logs for calling methods in {@code client}
 *
 * @author Yong Tang
 * @since 0.4
 */
public class LoggerProxyFactory<T> implements ProxyFactory<T>, InvocationHandler {
    private static final Logger logger = LogManager.getLogger();
    private T client;

    private LoggerProxyFactory(T client) {
        this.client = client;
    }

    /**
     * Create a logger proxy factory for building proxy of {@code client}
     *
     * @param client real client to build proxy on
     */
    public static <T> LoggerProxyFactory<T> newProxyFactory(T client) {
        return new LoggerProxyFactory<>(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T buildProxy() {
        return (T) Proxy.newProxyInstance(client.getClass().getClassLoader(), client.getClass().getInterfaces(), this);
    }

    /**
     * Inject logs to invocation process of {@code method}.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return LoggerProxyHelper.addLogger(logger, method, args, client);
    }
}
