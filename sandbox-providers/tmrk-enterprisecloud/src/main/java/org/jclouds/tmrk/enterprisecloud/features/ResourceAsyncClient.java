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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolPerformanceStatistics;
import org.jclouds.tmrk.enterprisecloud.domain.resource.PerformanceStatistics;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummary;
import org.jclouds.tmrk.enterprisecloud.domain.resource.ComputePoolResourceSummaryList;
import org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsage;
import org.jclouds.tmrk.enterprisecloud.domain.resource.memory.ComputePoolMemoryUsageDetail;
import org.jclouds.tmrk.enterprisecloud.domain.resource.storage.ComputePoolStorageUsageDetail;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import java.net.URI;

/**
 * Provides asynchronous access to various Resources via their REST API.
 * <p/>
 * 
 * @see ResourceClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@RequestFilters(BasicAuthentication.class)
@Headers(keys = "x-tmrk-version", values = "{jclouds.api-version}")
public interface ResourceAsyncClient {

   /**
    * @see ResourceClient#getResourceSummary
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolResourceSummary; type=collection")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolResourceSummaryList> getResourceSummaries(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getResourceSummary
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolResourceSummary")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolResourceSummary> getResourceSummary(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getComputePoolCpuUsage
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolCpuUsage")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolCpuUsage> getComputePoolCpuUsage(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getComputePoolCpuUsageDetail
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolCpuUsageDetail")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolCpuUsageDetail> getComputePoolCpuUsageDetail(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getComputePoolMemoryUsage
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolMemoryUsage")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolMemoryUsage> getComputePoolMemoryUsage(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getComputePoolMemoryUsageDetail
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolMemoryUsageDetail")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolMemoryUsageDetail> getComputePoolMemoryUsageDetail(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getComputePoolStorageUsage
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolStorageUsageDetail")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolStorageUsageDetail> getComputePoolStorageUsage(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getComputePoolPerformanceStatistics
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.computePoolPerformanceStatistics")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ComputePoolPerformanceStatistics> getComputePoolPerformanceStatistics(@EndpointParam URI uri);

   /**
    * @see ResourceClient#getPerformanceStatistics
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.performanceStatistics")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<PerformanceStatistics> getPerformanceStatistics(@EndpointParam URI uri);
}
