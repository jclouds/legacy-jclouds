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

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Andrei Savu
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalOfferingApi extends OfferingApi {

   /**
    * Create a new service offering
    *
    * @param name
    *          name of the service offering
    * @param displayText
    *          display name
    * @param cpuNumber
    *          number of CPUs
    * @param cpuSpeedInMHz
    *          CPU speed in MHz
    * @param memoryInMB
    *          the total memory of the service offering in MB
    * @param options
    *          optional arguments
    * @return
    *          service offering instance
    */
   @Named("createServiceOffering")
   @GET
   @QueryParams(keys = "command", values = "createServiceOffering")
   @SelectJson("serviceoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ServiceOffering createServiceOffering(@QueryParam("name") String name, @QueryParam("displaytext") String displayText,
         @QueryParam("cpunumber") int cpuNumber, @QueryParam("cpuspeed") int cpuSpeedInMHz, @QueryParam("memory") int memoryInMB, CreateServiceOfferingOptions... options);


   /**
    * Update an existing service offering
    *
    * @param id
    *          service offering ID
    * @param options
    *          optional arguments
    * @return
    *          service offering instance
    */
   @Named("updateServiceOffering")
   @GET
   @QueryParams(keys = "command", values = "updateServiceOffering")
   @SelectJson("serviceoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ServiceOffering updateServiceOffering(@QueryParam("id") String id, UpdateServiceOfferingOptions... options);

   /**
    * Delete service offering
    *
    * @param id
    *       the ID of the service offering
    */
   @Named("deleteServiceOffering")
   @GET
   @QueryParams(keys = "command", values = "deleteServiceOffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   void deleteServiceOffering(@QueryParam("id") String id);

   /**
    * Create a new disk offering
    *
    * @param name
    *          name of the disk offering
    * @param displayText
    *          display text for disk offering
    * @param options
    *          optional arguments
    * @return
    *          disk offering instance
    */
   @Named("createDiskOffering")
   @GET
   @QueryParams(keys = "command", values = "createDiskOffering")
   @SelectJson("diskoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   DiskOffering createDiskOffering(@QueryParam("name") String name,
         @QueryParam("displaytext") String displayText, CreateDiskOfferingOptions... options);

   /**
    * Update a disk offering
    *
    * @param id
    *          disk offering ID
    * @param options
    *          optional arguments
    * @return
    *          disk offering instance
    */
   @Named("updateDiskOffering")
   @GET
   @QueryParams(keys = "command", values = "updateDiskOffering")
   @SelectJson("diskoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   DiskOffering updateDiskOffering(@QueryParam("id") String id, UpdateDiskOfferingOptions... options);

   /**
    * Delete disk offering
    *
    * @param id
    *       the ID of the disk offering
    */
   @Named("deleteDiskOffering")
   @GET
   @QueryParams(keys = "command", values = "deleteDiskOffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   void deleteDiskOffering(@QueryParam("id") String id);

   /**
    * Update network offering
    *
    * @param id
    *       the id of the network offering
    * @param options
    *       optional arguments
    * @return
    *       network offering instance
    */
   @Named("updateNetworkOffering")
   @GET
   @QueryParams(keys = "command", values = "updateNetworkOffering")
   @SelectJson("networkoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkOffering updateNetworkOffering(@QueryParam("id") String id, UpdateNetworkOfferingOptions... options);
}
