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

package com.github.yongchristophertang.engine.web;

import com.github.yongchristophertang.engine.web.annotations.*;
import com.github.yongchristophertang.engine.web.request.JsonStringConverter;
import com.github.yongchristophertang.engine.web.request.RequestBuilder;

/**
 * Created by YongTang on 2015/3/17.
 *
 * @author Yong Tang
 * @since 0.4
 */
@Host(location = "http_config.properties")
@Path("/v5/categories")
public interface TestAPI {

    @QueryParam("token")
    String token = "accepted";

    @Path("/grades/${id}")
    @GET
    RequestBuilder getBooks(@PathParam("id") String id);

    @Path("/grades/${id}")
    @GET
    RequestBuilder getGrades(@QueryParam("id") String queryId, @PathParam("id") int pathId);

    @Path("/grades")
    @POST
    @Produce("text/plain")
    @Consume("application/json")
    RequestBuilder postGrade(@BodyParam String id);

    @POST
    @Path("/test")
    @GET
    RequestBuilder postTester(@QueryParam("test") String test, @BodyParam(value = "tester", converter = JsonStringConverter.class) Tester tester);

    @Path("/areas")
    @GET
    RequestBuilder getAreas();

    default RequestBuilder testDefaultMethod() {
        return getBooks("123");
    }

    @Path("/files")
    @POST
    RequestBuilder uploadFile(@BodyParam("test") String test, @FileParam("file") String filePath);
}
