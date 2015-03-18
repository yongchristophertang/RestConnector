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

package com.github.connector.test.java;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The proxy class for java APIs. Insert logs into API process.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JavaRequestProxy<T> implements InvocationHandler {
    private static final Logger logger = LogManager.getLogger();
    private T client;

    private JavaRequestProxy(T client) {
        this.client = client;
    }

    /**
     * Create a {@link com.github.connector.test.java.JavaRequestProxy} via client instance {@code client} and
     * expected interface {@code iface}.
     *
     * @return {@link com.github.connector.test.java.JavaRequestProxy}
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T client, Class<T> iface) {
        return createProxy(client, new Class[]{iface});
    }

    /**
     * Create a {@link com.github.connector.test.java.JavaRequestProxy} via client instance {@code client} and
     * expected interfaces {@code ifaces}.
     *
     * @return {@link com.github.connector.test.java.JavaRequestProxy}
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T client, Class<?>[] ifaces) {
        return (T) Proxy.newProxyInstance(client.getClass().getClassLoader(), ifaces, new JavaRequestProxy<>(client));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("API: " + method.getName());
        List<String> type =
                Lists.newArrayList(method.getParameterTypes()).stream().map(Class::getSimpleName).collect(
                        Collectors.toList());
        IntStream.range(0, type.size()).forEach(i -> logger.info("Input" + i + " (" + type.get(i) + "): " + args[i]));

        long bf = System.nanoTime();
        Object result = method.invoke(client, args);
        long af = System.nanoTime();

        logger.info("Cost Time(ms): " + (af - bf) / 1000000);

        if (result instanceof List && List.class.cast(result).size() > 0) {
            List list = List.class.cast(result);
            for (int i = 0; i < list.size() - 1; i++) {
                logger.info(("OUTPUT[" + (i + 1) + "]: " + list
                        .get(i)));
            }
            logger.info(("OUTPUT[" + list.size() + "]: "
                    + list.get(list.size() - 1) + "\n"));
        } else {
            logger.info("OUTPUT: " + result + "\n");
        }
        return result;
    }
}
