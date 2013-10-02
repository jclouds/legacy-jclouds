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
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreateNetworkOptions;
import org.jclouds.cloudstack.options.ListNetworksOptions;
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
public interface NetworkApi {

   /**
    * Lists networks
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return networks matching query, or empty set, if no networks are found
    */
   @Named("listNetworks")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listNetworks", "true" })
   @SelectJson("network")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Network> listNetworks(ListNetworksOptions... options);

   /**
    * get a specific network by id
    * 
    * @param id
    *           network to get
    * @return network or null if not found
    */
   @Named("listNetworks")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listNetworks", "true" })
   @SelectJson("network")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Network getNetwork(@QueryParam("id") String id);

   /**
    * Creates a network
    * 
    * @param zoneId
    *           the Zone ID for the Vlan ip range
    * @param networkOfferingId
    *           the network offering id
    * @param name
    *           the name of the network
    * @param displayText
    *           the display text of the network
    * @param options
    *           optional parameters
    * @return newly created network
    */
   @Named("createNetwork")
   @GET
   @QueryParams(keys = "command", values = "createNetwork")
   @SelectJson("network")
   @Consumes(MediaType.APPLICATION_JSON)
   Network createNetworkInZone(@QueryParam("zoneid") String zoneId,
         @QueryParam("networkofferingid") String networkOfferingId, @QueryParam("name") String name,
         @QueryParam("displaytext") String displayText, CreateNetworkOptions... options);

   /**
    * Deletes a network
    * 
    * @param id
    *           the ID of the network
    * @return job id related to destroying the network, or null if resource was
    *         not found
    */
   @Named("deleteNetwork")
   @GET
   @QueryParams(keys = "command", values = "deleteNetwork")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   String deleteNetwork(@QueryParam("id") String id);
}
