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

package com.github.yongchristophertang.engine.web.response;

import com.github.yongchristophertang.engine.web.ResultTransform;

/**
 * Static, factory methods for {@link ResultTransform}-based result actions.
 *
 * @author Yong Tang
 * @since 0.4
 */
public abstract class HttpResultTransformers {

    /**
     * Access to transformation of json result
     */
    public static JsonResultTransformer json() {
        return new JsonResultTransformer();
    }

    /**
     * Access to transformation of json result with result of parsed json string designated by expression
     */
    public static JsonResultTransformer json(String expression) {
        return new JsonResultTransformer(expression);
    }

    /**
     * Access to transformation of xml result
     */
    public static XmlResultTransformer xml() {
        return new XmlResultTransformer();
    }

}
