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

import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * A helper class that add loggers into intercepted methods.
 *
 * @author Yong Tang
 * @since 0.5
 */
class LoggerProxyHelper {
    static Object addLogger(Logger logger, Method method, Object[] args, Object client) throws Throwable {
        String formatter = "\n\tAPI: " + method.getName() + "\n\n";
        formatter += "\tInput:\n";
        for (int i = 0; i < method.getParameterCount(); i++) {
            formatter += "\t\t" + method.getParameters()[i].getName() + " (" +
                method.getParameters()[i].getType().getSimpleName() + "): ";
            if (args[i] instanceof Iterable) {
                int cnt = 0;
                Iterator iter = ((Iterable) args[i]).iterator();
                while (iter.hasNext()) {
                    formatter += "\n\t\t\t[" + (++cnt) + "]: " + iter.next();
                }
            } else {
                formatter += args[i];
            }
            formatter += "\n";
        }

        long bf = System.nanoTime();
        Object result = method.invoke(client, args);
        long af = System.nanoTime();

        formatter += "\n\tResponse Time(ms): " + (af - bf) / 1000000 + "\n\n\tOutput:\n";

        if (result instanceof Iterable) {
            Iterator iter = ((Iterable) result).iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                formatter += "\t\t[" + (++cnt) + "]: " + iter.next() + "\n";
            }
            if (cnt == 0) {
                formatter += "\t\tEmpty Collection\n";
            }
        } else {
            formatter += "\t\t" + result + "\n";
        }

        formatter += "=======================================================================\n";
        logger.info(formatter);
        return result;
    }
}
