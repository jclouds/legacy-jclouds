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
package org.jclouds.gogrid.features;

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

/**
 * Manages the customer's jobs.
 * 
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API#Job_Methods" />
 * 
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridJobApi {

   /**
    * Returns all jobs found. The resulting set may be narrowed down by providing
    * {@link GetJobListOptions}.
    *
    * By default, the result is <=100 items from the date range of 4 weeks ago to now.
    *
    * NOTE: this method results in a big volume of data in response
    *
    * @return jobs found by request
    */
   @GET
   @ResponseParser(ParseJobListFromJsonResponse.class)
   @Path("/grid/job/list")
   Set<Job> getJobList(GetJobListOptions... options);

   /**
    * Returns jobs found for an object with a provided name.
    *
    * Usually, in GoGrid a name will uniquely identify the object, or, as the docs state, some API
    * methods will cause errors.
    *
    * @param objectName
    *           name of the object
    * @return found jobs for the object
    */
   @GET
   @ResponseParser(ParseJobListFromJsonResponse.class)
   @Path("/grid/job/list")
   Set<Job> getJobsForObjectName(
           @BinderParam(BindObjectNameToGetJobsRequestQueryParams.class) String objectName);

   /**
    * Returns jobs for the corresponding id(s).
    *
    * NOTE: there is a 1:1 relation between a job and its ID.
    *
    * @param ids
    *           ids for the jobs
    * @return jobs found by the ids
    */
   @GET
   @ResponseParser(ParseJobListFromJsonResponse.class)
   @Path("/grid/job/get")
   Set<Job> getJobsById(@BinderParam(BindIdsToQueryParams.class) long... ids);

}
