/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.services;

import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.gogrid.binders.BindIdsToQueryParams;
import org.jclouds.gogrid.binders.BindObjectNameToGetJobsRequestQueryParams;
import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseJobListFromJsonResponse;
import org.jclouds.gogrid.options.GetJobListOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridJobAsyncClient {

   /**
    * @see GridJobClient#getJobList(org.jclouds.gogrid.options.GetJobListOptions...)
    */
   @GET
   @ResponseParser(ParseJobListFromJsonResponse.class)
   @Path("/grid/job/list")
   ListenableFuture<Set<Job>> getJobList(GetJobListOptions... options);

   /**
    * @see GridJobClient#getJobsForObjectName(String)
    */
   @GET
   @ResponseParser(ParseJobListFromJsonResponse.class)
   @Path("/grid/job/list")
   ListenableFuture<Set<Job>> getJobsForObjectName(
            @BinderParam(BindObjectNameToGetJobsRequestQueryParams.class) String objectName);

   /**
    * @see GridJobClient#getJobsById
    */
   @GET
   @ResponseParser(ParseJobListFromJsonResponse.class)
   @Path("/grid/job/get")
   ListenableFuture<Set<Job>> getJobsById(@BinderParam(BindIdsToQueryParams.class) long... ids);

}
