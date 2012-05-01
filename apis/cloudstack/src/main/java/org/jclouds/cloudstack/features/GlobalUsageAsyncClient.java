/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.cloudstack.features;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.JobResult;
import org.jclouds.cloudstack.domain.UsageRecord;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.functions.DateToYyyyMmDd;
import org.jclouds.cloudstack.options.GenerateUsageRecordsOptions;
import org.jclouds.cloudstack.options.ListUsageRecordsOptions;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to CloudStack usage features.
 * <p/>
 *
 * @see GlobalOfferingAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Richard Downer
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalUsageAsyncClient {

   @GET
   @QueryParams(keys = "command", values = "generateUsageRecords")
   @SelectJson("generateusagerecordsresponse")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<JobResult> generateUsageRecords(@QueryParam("startdate") @ParamParser(DateToYyyyMmDd.class) Date start, @QueryParam("enddate") @ParamParser(DateToYyyyMmDd.class) Date end, GenerateUsageRecordsOptions... options);

   @GET
   @QueryParams(keys = "command", values = "listUsageRecords")
   @SelectJson("usagerecord")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<UsageRecord>> listUsageRecords(@QueryParam("startdate") @ParamParser(DateToYyyyMmDd.class) Date start, @QueryParam("enddate") @ParamParser(DateToYyyyMmDd.class) Date end, ListUsageRecordsOptions... options);

}
