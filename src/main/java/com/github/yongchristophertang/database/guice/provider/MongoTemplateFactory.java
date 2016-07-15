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

package com.github.yongchristophertang.database.guice.provider;

import com.github.yongchristophertang.database.annotations.Mongo;
import com.mongodb.MongoClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

/**
 * Factory to create {@link MongoTemplate} via {@link Mongo}.
 *
 * @author Yong Tang
 * @since 0.5
 */
public class MongoTemplateFactory extends AnnotationClientFactory<MongoTemplate, Mongo> {

    public MongoTemplateFactory(Mongo[] annos) {
        super(annos);
    }

    /**
     * Transfer an annotation to a T object.
     *
     * @param anno Mongo annotation
     */
    @Override
    protected MongoTemplate createClient(Mongo anno) {
        if (StringUtils.isBlank(anno.database()) || StringUtils.isBlank(anno.host())) {
            throw new IllegalArgumentException("Mongo annotation must has database argument configured");
        }
        try {
            return new MongoTemplate(new MongoClient(anno.host(), anno.port()), anno.database());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}
