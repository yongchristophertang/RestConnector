/*
 *
 *  * Copyright 2014-2015 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.connector.test.web.response;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.connector.test.web.ResultActions;
import com.github.connector.test.web.ResultTransform;
import com.jayway.jsonpath.JsonPath;

import java.util.List;
import java.util.Map;

/**
 * Factory for json result transformation. An instance of this class is typically accessed via {@link com.github
 * .connector.test.web.response.HttpResultTransformers#json}
 *
 * @author Yong Tang
 * @since 0.4
 */
public class JsonResultTransformer {

    /**
     * Accessed via {@link com.github.connector.test.web.response.HttpResultTransformers#json}
     */
    protected JsonResultTransformer() {
    }

    /**
     * Parse the result into a sub part json via evaluation of expression. And create another {@link com.github
     * .connector.test.web.ResultActions} instance which incorporate this json as response for further processing.
     *
     * @param expression the JSON path expression
     */
    public ResultTransform<ResultActions> parse(String expression) {
        return result -> {
            JsonPath jsonPath = JsonPath.compile(expression);
            String content = jsonPath.read(result.getResponseStringContent());
            DefaultHttpResult httpResult = new DefaultHttpResult(result);
            httpResult.setHttpResponse(content);
            return new DefaultResultActions(httpResult);
        };
    }

    /**
     * Transform the json result to an instance of T.
     *
     * @param clazz the type class of transformed object
     */
    public <T> ResultTransform<T> object(Class<T> clazz) {
        return result -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(result.getResponseStringContent(), clazz);
        };
    }

    /**
     * Transform the json result to a list with T as its member class.
     *
     * @param clazz the type class of transformed list member object
     */
    public <T> ResultTransform<List<T>> list(Class<T> clazz) {
        return result -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(result.getResponseStringContent(), TypeFactory.defaultInstance()
                    .constructCollectionType(List.class, clazz));
        };
    }

    /**
     * Transform the json result to a map with K as its key class and V as its value class.
     *
     * @param keyClass   the type class of transformed map key object
     * @param valueClass the type class of transformed map value object
     */
    public <K, V> ResultTransform<Map<K, V>> map(Class<K> keyClass, Class<V> valueClass) {
        return result -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(result.getResponseStringContent(),
                    TypeFactory.defaultInstance().constructMapLikeType(Map.class, keyClass, valueClass));
        };
    }
}
