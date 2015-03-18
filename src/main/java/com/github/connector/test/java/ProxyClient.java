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

package com.github.connector.test.java;

/**
 * Client wrapper for real client instance and builder for constructing a proxy instance.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class ProxyClient {
    private Object client;

    private ProxyClient(Object client) {
        this.client = client;
    }

    public static ProxyClient newClient(Object client) {
        return new ProxyClient(client);
    }

    /**
     * Get a proxy for the client on interface {@code iface}
     *
     * @param iface Representative interface
     * @throws java.lang.ClassCastException if {@code iface} is not an interface that {@code client} implements.
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxyForInterface(Class<T> iface) {
        return JavaRequestProxy.createProxy(iface.cast(client), iface);
    }

    /**
     * Get a generic proxy for the client on interfaces that client implements
     *
     * @return Object to cast to expected valid interface
     */
    public Object getGenericProxy() {
        return JavaRequestProxy.createProxy(client, client.getClass().getInterfaces());
    }

    /**
     * Get the underlying client on interface {@code iface}
     *
     * @throws java.lang.ClassCastException if {@code iface} is not an interface that {@code client} implements.
     */
    public <T> T getClientForInterface(Class<T> iface) {
        return iface.cast(client);
    }
}
