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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see OfferingClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface OfferingAsyncClient {

   /**
    * @see OfferingClient#listServiceOfferings
    */
   @Named("listServiceOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listServiceOfferings", "true" })
   @SelectJson("serviceoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<ServiceOffering>> listServiceOfferings(ListServiceOfferingsOptions... options);

   /**
    * @see OfferingClient#getServiceOffering
    */
   @Named("listServiceOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listServiceOfferings", "true" })
   @SelectJson("serviceoffering")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ServiceOffering> getServiceOffering(@QueryParam("id") String id);

   /**
    * @see OfferingClient#listDiskOfferings
    */
   @Named("listDiskOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDiskOfferings", "true" })
   @SelectJson("diskoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<DiskOffering>> listDiskOfferings(ListDiskOfferingsOptions... options);

   /**
    * @see OfferingClient#getDiskOffering
    */
   @Named("listDiskOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listDiskOfferings", "true" })
   @SelectJson("diskoffering")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<DiskOffering> getDiskOffering(@QueryParam("id") String id);

   /**
    * @see NetworkOfferingClient#listNetworkOfferings
    */
   @Named("listNetworkOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listNetworkOfferings", "true" })
   @SelectJson("networkoffering")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<NetworkOffering>> listNetworkOfferings(ListNetworkOfferingsOptions... options);

   /**
    * @see NetworkOfferingClient#getNetworkOffering
    */
   @Named("listNetworkOfferings")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listNetworkOfferings", "true" })
   @SelectJson("networkoffering")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<NetworkOffering> getNetworkOffering(@QueryParam("id") String id);

}
