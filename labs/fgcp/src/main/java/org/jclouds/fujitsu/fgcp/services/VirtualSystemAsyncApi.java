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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.binders.BindAlsoToSystemId;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServer;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.VDisk;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemStatus;
import org.jclouds.fujitsu.fgcp.domain.VSystemWithDetails;
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
 * Non-blocking API relating to virtual systems.
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface VirtualSystemAsyncApi {

   @Named("DestroyVSYS")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DestroyVSYS")
   ListenableFuture<Void> destroy(@QueryParam("vsysId") String id);

   @Named("GetVSYSStatus")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVSYSStatus")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VSystemStatus> getStatus(@QueryParam("vsysId") String id);

   @Named("GetVSYSAttributes")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVSYSAttributes")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VSystem> get(@QueryParam("vsysId") String id);

   @Named("GetVSYSConfiguration")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetVSYSConfiguration")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<VSystemWithDetails> getDetails(
         @QueryParam("vsysId") String id);

   @Named("UpdateVSYSAttribute")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "UpdateVSYSAttribute")
   ListenableFuture<Void> update(@QueryParam("vsysId") String id,
         @QueryParam("attributeName") String name,
         @QueryParam("attributeValue") String value);

   @Named("UpdateVSYSConfiguration")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "UpdateVSYSConfiguration")
   ListenableFuture<Void> updateConfiguration(@QueryParam("vsysId") String id,
         @QueryParam("configurationName") String name,
         @QueryParam("configurationValue") String value);

   @Named("CreateVServer")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "CreateVServer")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<String> createServer(
         @QueryParam("vserverName") String name,
         @QueryParam("vserverType") String type,
         @QueryParam("diskImageId") String diskImageId,
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("networkId") String networkId);

   @Named("ListVServer")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListVServer")
   ListenableFuture<Set<VServer>> listServers(@QueryParam("vsysId") String id);

   @Named("CreateVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "CreateVDisk")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<String> createDisk(@QueryParam("vsysId") String id,
         @QueryParam("vdiskName") String name, @QueryParam("size") int size);

   @Named("ListVDisk")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListVDisk")
   ListenableFuture<Set<VDisk>> listDisks(@QueryParam("vsysId") String id);

   @Named("AllocatePublicIP")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "AllocatePublicIP")
   ListenableFuture<Void> allocatePublicIP(@QueryParam("vsysId") String id);

   /**
    *
    * @return
    * @see VirtualDCAsyncApi#listPublicIPs()
    */
   @Named("ListPublicIP")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListPublicIP")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<Set<PublicIP>> listPublicIPs(
         @QueryParam("vsysId") String id);

   @Named("CreateEFM")
   @GET
   @JAXBResponseParser
   // SLB is the only built-in server that can currently be created so
   // hard-code it
   @QueryParams(keys = { "Action", "efmType" }, values = { "CreateEFM", "SLB" })
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<String> createBuiltinServer(
         @QueryParam("efmName") String name,
         @BinderParam(BindAlsoToSystemId.class) @QueryParam("networkId") String networkId);

   @Named("ListEFM")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListEFM")
   ListenableFuture<Set<BuiltinServer>> listBuiltinServers(
         @QueryParam("vsysId") String id, @QueryParam("efmType") String type);

   @Named("StandByConsole")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "StandByConsole")
   @Transform(SingleElementResponseToElement.class)
   ListenableFuture<String> standByConsole(@QueryParam("vsysId") String id,
         @QueryParam("networkId") String networkId);

   @Named("RegisterPrivateVSYSDescriptor")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "RegisterPrivateVSYSDescriptor")
   ListenableFuture<Void> registerAsPrivateVSYSDescriptor(
         @QueryParam("vsysId") String id,
         @QueryParam("vsysDescriptorXMLFilePath") String vsysDescriptorXMLFilePath);
}
