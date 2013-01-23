/*
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

package org.jclouds.googlecompute.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.functions.internal.ParseNetworks;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import static org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;

/**
 * Provides asynchronous access to Networks via their REST API.
 *
 * @author David Alves
 * @see NetworkApi
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface NetworkAsyncApi {

   /**
    * @see NetworkApi#get(String)
    */
   @Named("Networks:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks/{network}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Network> get(@PathParam("network") String networkName);

   /**
    * @see NetworkApi#createInIPv4Range(String, String)
    */
   @Named("Networks:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Operation> createInIPv4Range(@PayloadParam("name") String networkName,
                                                 @PayloadParam("IPv4Range") String IPv4Range);

   /**
    * @see NetworkApi#createInIPv4RangeWithGateway(String, String, String)
    */
   @Named("Networks:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Operation> createInIPv4RangeWithGateway(@PayloadParam("name") String networkName,
                                                            @PayloadParam("IPv4Range") String IPv4Range,
                                                            @PayloadParam("gatewayIPv4") String gatewayIPv4);

   /**
    * @see NetworkApi#delete(String)
    */
   @Named("Networks:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks/{network}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Operation> delete(@PathParam("network") String networkName);

   /**
    * @see NetworkApi#listFirstPage()
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Network>> listFirstPage();

   /**
    * @see NetworkApi#listAtMarker(String)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Network>> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Network>> listAtMarker(@QueryParam("pageToken") @Nullable String marker,
                                                    ListOptions options);

   /**
    * @see NetworkApi#list()
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Transform(ParseNetworks.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Network>> list();

   /**
    * @see NetworkApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Networks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseNetworks.class)
   @Transform(ParseNetworks.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Network>> list(ListOptions options);
}
