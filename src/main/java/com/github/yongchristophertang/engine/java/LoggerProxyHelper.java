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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
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
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(client, args);
        }

        String formatter = "\n\tAPI: " + method.getName() + "\n\n";
        formatter += "\tInput:\n";
        for (int i = 0; i < method.getParameterCount(); i++) {
            formatter += "\t\t" + method.getParameters()[i].getName() + " (" +
                method.getParameters()[i].getType().getSimpleName() + "): ";
            if (args[i] == null) {
                formatter += "NULL";
            } else if (args[i] instanceof Iterable) {
                int cnt = 0;
                Iterator iter = ((Iterable) args[i]).iterator();
                while (iter.hasNext()) {
                    formatter += "\n\t\t\t[" + (++cnt) + "]: " + toPrinterString(iter.next(), false);
                }
            } else {
                formatter += toPrinterString(args[i], false);
            }
            formatter += "\n";
        }

        long bf = System.nanoTime();
        Object result;
        try {
            result = method.invoke(client, args);
        } catch (InvocationTargetException e) {
            formatter += "\n\tException: \n\t\t" + e.getTargetException();
            formatter += "\n=======================================================================\n";
            logger.info(formatter);
            throw e.getTargetException();
        }
        long af = System.nanoTime();

        formatter += "\n\tResponse Time(ms): " + (af - bf) / 1000000 + "\n\n\tOutput:\n";

        if (result == null) {
            formatter += "\t\tNULL\n";
        } else if (result instanceof Iterable) {
            Iterator iter = ((Iterable) result).iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                formatter += "\t\t[" + (++cnt) + "]: " + toPrinterString(iter.next(), true) + "\n";
            }
            if (cnt == 0) {
                formatter += "\t\tEmpty Collection []\n";
            }
        } else {
            formatter += "\t\t" + toPrinterString(result, true) + "\n";
        }

        formatter += "=======================================================================\n";
        logger.info(formatter);
        return result;
    }

    private static String toPrinterString(Object obj, boolean pretty) {
        /*
         * returns true if an object can be converted to a string which matches the regex pattern of ^([a-z]+\.)+[a-zA-Z]+@\w+$ that is
         * the official default object toString implementation
         */
        if (!obj.toString().matches("^([a-z]+\\.)+[a-zA-Z]+@\\w+$")) {
            return obj.toString();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return obj.getClass().getSimpleName() + ": " +
                (pretty ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj) :
                    mapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            return "The object has not implemented toString() method and cannot be serialized to a string either";
        }
    }
}