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

package com.github.yongchristophertang.database.guice.provider;

import com.github.yongchristophertang.database.annotations.Mongo;
import com.mongodb.MongoClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Factory to create {@link com.mongodb.MongoClient}s via {@link Mongo}s.
 *
 * @author Yong Tang
 * @since 0.4
 */
public class MongoFactory extends AnnotationClientFactory<MongoClient, Mongo> {
    public MongoFactory(Mongo[] mongos) {
        super((mongos));
    }

    @Override
    protected MongoClient createClient(Mongo mongo) {
        String config = mongo.config();
        String host = mongo.host();
        int port = mongo.port();
        String database = mongo.database();

        if (StringUtils.isBlank(config)) {
            if (StringUtils.isBlank(host) || StringUtils.isBlank(database)) {
                throw new IllegalArgumentException("Necessary config is missing for Mongo connection.");
            }
        } else {
            Properties prop = new Properties();
            try {
                prop.load(this.getClass().getClassLoader().getResourceAsStream(config));
                host = prop.getProperty("mongo.host");
                port = Integer.parseInt(prop.getProperty("mongo.port"));
            } catch (IOException e) {
                return null;
            }
        }

        try {
            return new MongoClient(host, port);
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
