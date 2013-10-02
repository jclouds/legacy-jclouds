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

/**
 * Provides synchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface AddressApi {

   /**
    * Lists IPAddresses
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return IPAddresses matching query, or empty set, if no IPAddresses are
    *         found
    */
   @Named("listPublicIpAddresses")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPublicIpAddresses", "true" })
   @SelectJson("publicipaddress")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<PublicIPAddress> listPublicIPAddresses(ListPublicIPAddressesOptions... options);

   /**
    * get a specific IPAddress by id
    * 
    * @param id
    *           IPAddress to get
    * @return IPAddress or null if not found
    */
   @Named("listPublicIpAddresses")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPublicIpAddresses", "true" })
   @SelectJson("publicipaddress")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   PublicIPAddress getPublicIPAddress(@QueryParam("id") String id);

   /**
    * Acquires and associates a public IP to an account.
    * 
    * @param zoneId
    *           the ID of the availability zone you want to acquire an public IP
    *           address from
    * @return IPAddress
    */
   @Named("associateIpAddress")
   @GET
   @QueryParams(keys = "command", values = "associateIpAddress")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   AsyncCreateResponse associateIPAddressInZone(@QueryParam("zoneid") String zoneId,
         AssociateIPAddressOptions... options);

   /**
    * Disassociates an ip address from the account.
    * 
    * @param id
    *           the id of the public ip address to disassociate
    */
   @Named("disassociateIpAddress")
   @GET
   @QueryParams(keys = "command", values = "disassociateIpAddress")
   @Fallback(VoidOnNotFoundOr404OrUnableToFindAccountOwner.class)
   void disassociateIPAddress(@QueryParam("id") String id);
   
}
