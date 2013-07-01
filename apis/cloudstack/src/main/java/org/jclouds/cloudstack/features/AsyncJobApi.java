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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.functions.ParseAsyncJobFromHttpResponse;
import org.jclouds.cloudstack.functions.ParseAsyncJobsFromHttpResponse;
import org.jclouds.cloudstack.options.ListAsyncJobsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface AsyncJobApi {

   /**
    * Lists asyncJobs
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return asyncJobs matching query, or empty set, if no asyncJobs are found
    */
   @Named("listAsyncJobs")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listAsyncJobs", "true" })
   @ResponseParser(ParseAsyncJobsFromHttpResponse.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<AsyncJob<?>> listAsyncJobs(ListAsyncJobsOptions... options);

   /**
    * get a specific asyncJob by id
    * 
    * @param id
    *           asyncJob to get
    * @return asyncJob or null if not found
    */
   @Named("queryAsyncJobResult")
   @GET
   @QueryParams(keys = "command", values = "queryAsyncJobResult")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseAsyncJobFromHttpResponse.class)
   @Fallback(NullOnNotFoundOr404.class)
   <T> AsyncJob<T> getAsyncJob(@QueryParam("jobid") String id);

}
