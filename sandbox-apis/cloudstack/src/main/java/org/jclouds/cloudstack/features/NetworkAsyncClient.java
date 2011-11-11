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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.CreateNetworkOptions;
import org.jclouds.cloudstack.options.ListNetworksOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see NetworkClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface NetworkAsyncClient {

   /**
    * @see NetworkClient#listNetworks
    */
   @GET
   @QueryParams(keys = "command", values = "listNetworks")
   @SelectJson("network")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Network>> listNetworks(ListNetworksOptions... options);

   /**
    * @see NetworkClient#getNetwork
    */
   @GET
   @QueryParams(keys = "command", values = "listNetworks")
   @SelectJson("network")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Network> getNetwork(@QueryParam("id") long id);

   /**
    * @see NetworkClient#createNetworkInZone
    */
   @GET
   @QueryParams(keys = "command", values = "createNetwork")
   @SelectJson("network")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Network> createNetworkInZone(@QueryParam("zoneid") long zoneId,
         @QueryParam("networkofferingid") long networkOfferingId, @QueryParam("name") String name,
         @QueryParam("displaytext") String displayText, CreateNetworkOptions... options);

   /**
    * @see NetworkClient#deleteNetwork
    */
   @GET
   @QueryParams(keys = "command", values = "deleteNetwork")
   @SelectJson("network")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Long> deleteNetwork(@QueryParam("id") long id);
}
