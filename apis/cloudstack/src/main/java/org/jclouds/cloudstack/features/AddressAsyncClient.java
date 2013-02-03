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
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.functions.CloudStackFallbacks.VoidOnNotFoundOr404OrUnableToFindAccountOwner;
import org.jclouds.cloudstack.options.AssociateIPAddressOptions;
import org.jclouds.cloudstack.options.ListPublicIPAddressesOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see AddressClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface AddressAsyncClient {

   /**
    * @see AddressClient#listPublicIPAddresses
    */
   @Named("listPublicIpAddresses")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPublicIpAddresses", "true" })
   @SelectJson("publicipaddress")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<PublicIPAddress>> listPublicIPAddresses(ListPublicIPAddressesOptions... options);

   /**
    * @see AddressClient#getPublicIPAddress
    */
   @Named("listPublicIpAddresses")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPublicIpAddresses", "true" })
   @SelectJson("publicipaddress")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PublicIPAddress> getPublicIPAddress(@QueryParam("id") String id);

   /**
    * @see AddressClient#associateIPAddressInZone
    */
   @Named("associateIpAddress")
   @GET
   @QueryParams(keys = "command", values = "associateIpAddress")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> associateIPAddressInZone(@QueryParam("zoneid") String zoneId,
         AssociateIPAddressOptions... options);

   /**
    * @see AddressClient#disassociateIPAddress
    */
   @Named("disassociateIpAddress")
   @GET
   @QueryParams(keys = "command", values = "disassociateIpAddress")
   @Fallback(VoidOnNotFoundOr404OrUnableToFindAccountOwner.class)
   ListenableFuture<Void> disassociateIPAddress(@QueryParam("id") String id);

}
