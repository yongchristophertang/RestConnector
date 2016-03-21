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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Logger proxy for an instance
 *
 * @author Yong Tang
 * @since 0.5
 */
public class ClassLoggerProxy<T> implements MethodInterceptor, ProxyFactory<T> {
    private static final Logger logger = LogManager.getLogger();
    private T client;

    ClassLoggerProxy(T client) {
        this.client = client;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return LoggerProxyHelper.addLogger(logger, method, args, client);
    }

    /**
     * Build a {@code T} proxy.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T buildProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(client.getClass());
        enhancer.setCallback(this);
        Constructor<?> constructor = client.getClass().getConstructors()[0];
        return (T) enhancer.create(constructor.getParameterTypes(), new Object[constructor.getParameterCount()]);
    }
}
