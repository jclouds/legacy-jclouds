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

import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.AddressRange;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.EventLog;
import org.jclouds.fujitsu.fgcp.domain.Information;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.UsageInfo;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemDescriptor;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Non-blocking API relating to the virtual data center.
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
public interface VirtualDCAsyncApi {

   @Named("CreateVSYS")
   // @POST
   @GET
   @JAXBResponseParser
   // @XMLResponseParser(VSYSCreateHandler.class)
   @QueryParams(keys = "Action", values = "CreateVSYS")
   @Transform(SingleElementResponseToElement.class)
   // @PayloadParams(keys = "Action", values = "CreateVSYS")
   // @Produces(MediaType.TEXT_XML)
   // @MapBinder(BindParamsToXmlPayload.class)
   // ListenableFuture<String>
   // createVirtualSystem(@PayloadParam("vsysDescriptorId") String
   // vsysDescriptorId, @PayloadParam("vsysName") String vsysName);
   ListenableFuture<String> createVirtualSystem(
         @QueryParam("vsysDescriptorId") String descriptorId,
         @QueryParam("vsysName") String name);

   @Named("ListVSYS")
   @GET
   @JAXBResponseParser
   // @XMLResponseParser(VSYSListHandler.class)
   @QueryParams(keys = "Action", values = "ListVSYS")
   ListenableFuture<Set<VSystem>> listVirtualSystems();

   @Named("ListServerType")
   @GET
   @JAXBResponseParser
   // according to the manual it takes a 'String diskImageId' but value seems
   // to be ignored
   @QueryParams(keys = { "Action", "diskImageId" }, values = {
         "ListServerType", "dummy" })
   // @XmlJavaTypeAdapter(SetOfServerTypesXMLAdapter.class)
   // @XmlElement(type = ServerType.class)
   ListenableFuture<Set<ServerType>> listServerTypes();

   @Named("ListDiskImage")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListDiskImage")
   ListenableFuture<Set<DiskImage>> listDiskImages();

   @Named("ListDiskImage")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListDiskImage")
   ListenableFuture<Set<DiskImage>> listDiskImages(
         @Nullable @QueryParam("serverCategory") String serverCategory,
         @QueryParam("vsysDescriptorId") String vsysDescriptorId);

   /**
    *
    * @return
    * @see VirtualSystemAsyncApi#listPublicIPs(String)
    */
   @Named("ListPublicIP")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListPublicIP")
   ListenableFuture<Map<PublicIP, String>> listPublicIPs();

   @Named("AddAddressRange")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "AddAddressRange")
   ListenableFuture<Void> addAddressRange(
         @QueryParam("pipFrom") String pipFrom,
         @QueryParam("pipTo") String pipTo);

   @Named("CreateAddressPool")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "CreateAddressPool")
   ListenableFuture<Void> createAddressPool(
         @QueryParam("pipFrom") String pipFrom,
         @QueryParam("pipTo") String pipTo);

   @Named("DeleteAddressRange")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "DeleteAddressRange")
   ListenableFuture<Void> deleteAddressRange(
         @QueryParam("pipFrom") String pipFrom,
         @QueryParam("pipTo") String pipTo);

   @Named("GetAddressRange")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetAddressRange")
   ListenableFuture<Set<AddressRange>> getAddressRange();

   @Named("ListVSYSDescriptor")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListVSYSDescriptor")
   ListenableFuture<Set<VSystemDescriptor>> listVSYSDescriptor();

   @Named("ListVSYSDescriptor")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "ListVSYSDescriptor")
   ListenableFuture<Set<VSystemDescriptor>> listVSYSDescriptor(
         @QueryParam("keyword") String keyword,
         @QueryParam("estimateFrom") int estimateFrom,
         @QueryParam("estimateTo") int estimateTo);

   @Named("GetEventLog")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEventLog")
   ListenableFuture<Set<EventLog>> getEventLogs();

   @Named("GetEventLog")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetEventLog")
   ListenableFuture<Set<EventLog>> getEventLogs(@QueryParam("all") boolean all);

   @Named("GetInformation")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetInformation")
   ListenableFuture<Set<Information>> getInformation();

   @Named("GetInformation")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetInformation")
   ListenableFuture<Set<Information>> getInformation(
         @QueryParam("all") boolean all);

   @Named("GetSystemUsage")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetSystemUsage")
   ListenableFuture<Set<UsageInfo>> getSystemUsage();

   @Named("GetSystemUsage")
   @GET
   @JAXBResponseParser
   @QueryParams(keys = "Action", values = "GetSystemUsage")
   ListenableFuture<Set<UsageInfo>> getSystemUsage(
         @QueryParam("systemIds") String systemIds);

}
