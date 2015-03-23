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

package com.github.connector.engine.web.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.connector.engine.web.HttpResult;
import com.github.connector.engine.web.ResultHandler;
import com.google.common.collect.Lists;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.RequestLine;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An implementation of {@link com.github.connector.engine.web.ResultHandler} for performing print task
 *
 * @author Yong Tang
 * @since 0.4
 */
public class PrintResultHandler implements ResultHandler {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Accessed via {@link com.github.connector.engine.web.response.HttpResultHandlers#print}
     */
    protected PrintResultHandler() {
    }

    /**
     * Print the given result to log system.
     *
     * @param result the result of the executed request
     * @throws Exception if a failure occurs
     */
    @Override
    public void handle(HttpResult result) throws Exception {
        RequestLine rl = result.getHttpRequest().getRequestLine();
        logger.info("Request URI: " + rl);
        if (rl.getMethod().equals("POST") || rl.getMethod().equals("PUT") || rl.getMethod().equals("PATCH")) {
            if (result.getHttpRequest() instanceof HttpEntityEnclosingRequest) {
                logger.info("Request Body: " +
                        EntityUtils.toString(((HttpEntityEnclosingRequest) result.getHttpRequest()).getEntity()));
            } else {
                logger.info("Request Body: " + "Multipart Body Cannot Be Displayed.");
            }
        }
        logger.info("Request Headers: " + Lists.newArrayList(result.getHttpRequest().getAllHeaders()));
        logger.info("Cost Time(ms): " + result.getCostTime());
        logger.info("Response Status: " + result.getHttpResponse().getStatusLine());
        logger.info("Response Headers: " + Lists.newArrayList(result.getHttpResponse().getAllHeaders()));
        logger.info("Response Content: \n" + getPrettyJsonPrint(result.getResponseStringContent()) + "\n");
    }

    /**
     * Reformat Json string for better look in printing
     *
     * @param rawJson raw json string
     * @return formatted json string
     */
    private String getPrettyJsonPrint(String rawJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(rawJson,
                    Object.class);
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(json);
        } catch (Exception e) {
            return rawJson;
        }
    }
}
