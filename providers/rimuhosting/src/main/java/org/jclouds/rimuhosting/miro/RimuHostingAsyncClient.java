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
package org.jclouds.rimuhosting.miro;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rimuhosting.miro.binder.CreateServerOptions;
import org.jclouds.rimuhosting.miro.binder.RimuHostingRebootJsonBinder;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.ServerInfo;
import org.jclouds.rimuhosting.miro.fallbacks.ParseRimuHostingException;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.functions.ParseDestroyResponseFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParseImagesFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParsePricingPlansFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParseServerFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParseServerInfoFromJsonResponse;
import org.jclouds.rimuhosting.miro.functions.ParseServersFromJsonResponse;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to RimuHosting via their REST API.
 * <p/>
 * 
 * @author Ivan Meredith
 * @see RimuHostingClient
 * @see <a href="http://apidocs.rimuhosting.com" />
 */
@RequestFilters(RimuHostingAuthentication.class)
@VirtualHost
public interface RimuHostingAsyncClient {

   /**
    * @see RimuHostingClient#getImageList
    */
   @GET
   @Path("/distributions")
   @ResponseParser(ParseImagesFromJsonResponse.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(ParseRimuHostingException.class)
   ListenableFuture<Set<Image>> getImageList();

   /**
    * @see RimuHostingClient#getServerList
    */
   @GET
   @Path("/orders;include_inactive=N")
   @ResponseParser(ParseServersFromJsonResponse.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(ParseRimuHostingException.class)
   ListenableFuture<Set<Server>> getServerList();

   /**
    * @see RimuHostingClient#getPricingPlanList
    */
   @GET
   @Path("/pricing-plans;server-type=VPS")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(ParseRimuHostingException.class)
   @ResponseParser(ParsePricingPlansFromJsonResponse.class)
   ListenableFuture<Set<PricingPlan>> getPricingPlanList();

   /**
    * @see RimuHostingClient#createServer
    */
   @POST
   @Path("/orders/new-vps")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(ParseRimuHostingException.class)
   @Unwrap
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<NewServerResponse> createServer(@PayloadParam("name") String name,
            @PayloadParam("imageId") String imageId, @PayloadParam("planId") String planId,
            CreateServerOptions... options);

   /**
    * @see RimuHostingClient#getServer
    */
   @GET
   @Path("/orders/order-{id}-blah")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseServerFromJsonResponse.class)
   @Fallback(ParseRimuHostingException.class)
   ListenableFuture<Server> getServer(@PathParam("id") Long id);

   /**
    * @see RimuHostingClient#restartServer
    */
   @PUT
   @Path("/orders/order-{id}-blah/vps/running-state")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseServerInfoFromJsonResponse.class)
   @MapBinder(RimuHostingRebootJsonBinder.class)
   @Fallback(ParseRimuHostingException.class)
   ListenableFuture<ServerInfo> restartServer(@PathParam("id") Long id);

   /**
    * @see RimuHostingClient#destroyServer
    */
   @DELETE
   @Path("/orders/order-{id}-blah/vps")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseDestroyResponseFromJsonResponse.class)
   @Fallback(ParseRimuHostingException.class)
   ListenableFuture<List<String>> destroyServer(@PathParam("id") Long id);
}
