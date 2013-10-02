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
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.ListDiskOfferingsOptions;
import org.jclouds.cloudstack.options.ListNetworkOfferingsOptions;
import org.jclouds.cloudstack.options.ListServiceOfferingsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface OfferingApi {

   /**
    * Lists service offerings
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return service offerings matching query, or empty set, if no service
    *         offerings are found
    */
   @Named("listServiceOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listServiceOfferings", "true" })
   @SelectJson("serviceoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<ServiceOffering> listServiceOfferings(ListServiceOfferingsOptions... options);

   /**
    * get a specific service offering by id
    * 
    * @param id
    *           offering to get
    * @return service offering or null if not found
    */
   @Named("listServiceOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listServiceOfferings", "true" })
   @SelectJson("serviceoffering")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ServiceOffering getServiceOffering(@QueryParam("id") String id);

   /**
    * Lists disk offerings
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return disk offerings matching query, or empty set, if no disk offerings
    *         are found
    */
   @Named("listDiskOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDiskOfferings", "true" })
   @SelectJson("diskoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<DiskOffering> listDiskOfferings(ListDiskOfferingsOptions... options);

   /**
    * get a specific disk offering by id
    * 
    * @param id
    *           offering to get
    * @return disk offering or null if not found
    */
   @Named("listDiskOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDiskOfferings", "true" })
   @SelectJson("diskoffering")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   DiskOffering getDiskOffering(@QueryParam("id") String id);

   /**
    * Lists service offerings
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return service offerings matching query, or empty set, if no service
    *         offerings are found
    */
   @Named("listNetworkOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listNetworkOfferings", "true" })
   @SelectJson("networkoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<NetworkOffering> listNetworkOfferings(ListNetworkOfferingsOptions... options);

   /**
    * get a specific service offering by id
    * 
    * @param id
    *           offering to get
    * @return service offering or null if not found
    */
   @Named("listNetworkOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listNetworkOfferings", "true" })
   @SelectJson("networkoffering")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkOffering getNetworkOffering(@QueryParam("id") String id);

}
