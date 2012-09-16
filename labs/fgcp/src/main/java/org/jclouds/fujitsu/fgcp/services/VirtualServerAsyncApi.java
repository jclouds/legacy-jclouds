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
package org.jclouds.fujitsu.fgcp.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.binders.BindAlsoToSystemId;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.PerformanceInfo;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Non-blocking API relating to virtual servers.
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface VirtualServerAsyncApi {

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StartVServer")
   ListenableFuture<Void> start(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StopVServer")
   ListenableFuture<Void> stop(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = { "Action", "force" }, values = { "StopVServer", "true" })
   ListenableFuture<Void> stopForcefully(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyVServer")
   ListenableFuture<Void> destroy(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerAttributes")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VServer> get(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerConfiguration")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VServerWithDetails> getDetails(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "UpdateVServerAttribute")
   ListenableFuture<Void> update(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id,
         @QueryParam("attributeName") String name,
         @QueryParam("attributeValue") String value);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerStatus")
   // @Transform(StringToVServerStatus.class)
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VServerStatus> getStatus(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVServerInitialPassword")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<String> getInitialPassword(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String id);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "AttachVDisk")
   ListenableFuture<Void> attachDisk(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("vserverId") String serverId,
         @QueryParam("vdiskId") String diskId);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetPerformanceInformation")
   ListenableFuture<Set<PerformanceInfo>> getPerformanceInformation(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("serverId") String id,
         @QueryParam("interval") String interval);

   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetPerformanceInformation")
   ListenableFuture<Set<PerformanceInfo>> getPerformanceInformation(
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("serverId") String id,
         @QueryParam("dataType") String dataType,
         @QueryParam("interval") String interval);

   @POST
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "RegisterPrivateDiskImage")
   ListenableFuture<Void> registerAsPrivateDiskImage(String xml);
}
