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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateDiskOfferingOptions;
import org.jclouds.cloudstack.options.CreateServiceOfferingOptions;
import org.jclouds.cloudstack.options.UpdateDiskOfferingOptions;
import org.jclouds.cloudstack.options.UpdateNetworkOfferingOptions;
import org.jclouds.cloudstack.options.UpdateServiceOfferingOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see GlobalOfferingClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Andrei Savu
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalOfferingAsyncClient extends OfferingAsyncClient {

   /**
    * @see GlobalOfferingClient#createServiceOffering
    */
   @Named("createServiceOffering")
   @GET
   @QueryParams(keys = "command", values = "createServiceOffering")
   @SelectJson("serviceoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ServiceOffering> createServiceOffering(@QueryParam("name") String name, @QueryParam("displaytext") String displayText,
         @QueryParam("cpunumber") int cpuNumber, @QueryParam("cpuspeed") int cpuSpeedInMHz, @QueryParam("memory") int memoryInMB, CreateServiceOfferingOptions... options);


   /**
    * @see GlobalOfferingClient#updateServiceOffering
    */
   @Named("updateServiceOffering")
   @GET
   @QueryParams(keys = "command", values = "updateServiceOffering")
   @SelectJson("serviceoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ServiceOffering> updateServiceOffering(@QueryParam("id") String id, UpdateServiceOfferingOptions... options);

   /**
    * @see GlobalOfferingClient#deleteServiceOffering
    */
   @Named("deleteServiceOffering")
   @GET
   @QueryParams(keys = "command", values = "deleteServiceOffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Void> deleteServiceOffering(@QueryParam("id") String id);

   /**
    * @see GlobalOfferingClient#createDiskOffering
    */
   @Named("createDiskOffering")
   @GET
   @QueryParams(keys = "command", values = "createDiskOffering")
   @SelectJson("diskoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<DiskOffering> createDiskOffering(@QueryParam("name") String name,
         @QueryParam("displaytext") String displayText, CreateDiskOfferingOptions... options);

   /**
    * @see GlobalOfferingClient#updateDiskOffering
    */
   @Named("updateDiskOffering")
   @GET
   @QueryParams(keys = "command", values = "updateDiskOffering")
   @SelectJson("diskoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<DiskOffering> updateDiskOffering(@QueryParam("id") String id, UpdateDiskOfferingOptions... options);

   /**
    * @see GlobalOfferingClient#deleteDiskOffering
    */
   @Named("deleteDiskOffering")
   @GET
   @QueryParams(keys = "command", values = "deleteDiskOffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Void> deleteDiskOffering(@QueryParam("id") String id);

   /**
    * @see GlobalOfferingClient#updateNetworkOffering
    */
   @Named("updateNetworkOffering")
   @GET
   @QueryParams(keys = "command", values ="updateNetworkOffering")
   @SelectJson("networkoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<NetworkOffering> updateNetworkOffering(@QueryParam("id") String id, UpdateNetworkOfferingOptions... options);
}
