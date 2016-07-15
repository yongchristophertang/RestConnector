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
        Constructor<?> constructor = client.getClass().getConstructors()[0];
        return buildProxy(new Object[constructor.getParameterCount()]);
    }

    /**
     * Build a proxy with constructor arguments {@code args}.
     * This method is used to fix the null pointer exception for constructor arguments
     *
     * @param args constructor arguments
     */
    @SuppressWarnings("unchecked")
    public T buildProxy(Object[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(client.getClass());
        enhancer.setCallback(this);
        Class<?>[] classes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }
        try {
            Constructor<?> constructor = client.getClass().getConstructor(classes);
            return (T) enhancer.create(constructor.getParameterTypes(), args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
