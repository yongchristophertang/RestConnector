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

package com.github.yongchristophertang.engine.java.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility json transformation library
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JsonTransformer {
    private final String context;

    public JsonTransformer(String context) {
        this.context = context;
    }

    /**
     * Return the contained json string in this instance.
     *
     * @return contained json string.
     */
    public String getContext() {
        return context;
    }

    /**
     * Parse the result into a sub part json via evaluation of expression. And create another {@link com.github
     * .yongchristophertang.engine.web.ResultActions} instance which incorporate this json as response for further
     * processing.
     *
     * @param expression the JSON path expression
     */
    public JsonTransformer parse(String expression) {
        Objects.nonNull(expression);
        return new JsonTransformer(JsonPath.compile(expression).read(context).toString());
    }

    /**
     * Transform the json result to an instance of T.
     *
     * @param clazz the type class of transformed object
     */
    public <T> T object(String expression, Class<T> clazz) {
        Objects.nonNull(expression);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            return expression == null ? mapper.readValue(context, clazz) :
                    mapper.readValue(JsonPath.compile(expression).read(context).toString(), clazz);
        } catch (Exception e) {
            return clazz.cast(JsonPath.compile(expression).read(context));
        }
    }

    /**
     * Transform the json result to a list with T as its member class.
     *
     * @param clazz the type class of transformed list member object
     */
    public <T> List<T> list(String expression, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return expression == null ? mapper.readValue(context, TypeFactory.defaultInstance().constructCollectionType(
                List.class, clazz)) : mapper.readValue(JsonPath.compile(expression).read(context).toString(),
                TypeFactory.defaultInstance().constructCollectionType(
                        List.class, clazz));
    }

    /**
     * Transform the json result to a map with K as its key class and V as its value class.
     *
     * @param keyClass   the type class of transformed map key object
     * @param valueClass the type class of transformed map value object
     */
    public <K, V> Map<K, V> map(String expression, Class<K> keyClass, Class<V> valueClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return expression == null ? mapper.readValue(context,
                TypeFactory.defaultInstance().constructMapLikeType(Map.class, keyClass, valueClass)) :
                mapper.readValue(JsonPath.compile(expression).read(context).toString(),
                        TypeFactory.defaultInstance().constructMapLikeType(Map.class, keyClass, valueClass));
    }
}
