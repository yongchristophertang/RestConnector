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

package com.github.connector.test.web;

import com.github.connector.test.web.annotations.*;
import com.github.connector.test.web.request.HttpRequestBuilders;
import com.github.connector.test.web.request.RequestBuilder;

/**
 * Created by YongTang on 2015/3/17.
 *
 * @author Yong Tang
 * @since 0.4
 */
@Host(value = "http://172.16.79.22", port = 11391)
@Path("/resourceservice/v5/catalogues")
public interface TestAPI {

    @QueryParam("token")
    public String token = "accepted";

    @Path("/grades/${id}")
    @GET
    public RequestBuilder getBooks(@PathParam("id") String id);

    @Path("/grades/${id}")
    @GET
    public HttpRequestBuilders getGrades(@QueryParam("id") String queryId, @PathParam("id") int pathId);

    @Path("/grades")
    @POST
    @Produce("text/plain")
    @Consume("application/json")
    public RequestBuilder postGrade(@BodyParam String id);
}
