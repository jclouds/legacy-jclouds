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
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.functions.internal.PATCH;
import org.jclouds.googlecompute.functions.internal.ParseFirewalls;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
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
import javax.ws.rs.PUT;
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
 * Provides asynchronous access to Firewalls via their REST API.
 *
 * @author David Alves
 * @see FirewallApi
 *      <p/>
 *      Note: Patch is unsupported, jclouds side, at the moment
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface FirewallAsyncApi {

   /**
    * @see FirewallApi#get(String)
    */
   @Named("Firewalls:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls/{firewall}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Firewall> get(@PathParam("firewall") String firewallName);

   /**
    * @see FirewallApi#create(org.jclouds.googlecompute.domain.Firewall)
    */
   @Named("Firewalls:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/firewalls")
   @OAuthScopes({COMPUTE_SCOPE})
   ListenableFuture<Operation> create(@BinderParam(BindToJsonPayload.class) Firewall firewall);

   /**
    * @see FirewallApi#update(String, org.jclouds.googlecompute.domain.Firewall)
    */
   @Named("Firewalls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/firewalls/{firewall}")
   @OAuthScopes({COMPUTE_SCOPE})
   ListenableFuture<Operation> update(@PathParam("firewall") String firewallName,
                                      @BinderParam(BindToJsonPayload.class) Firewall firewall);

   /**
    * @see FirewallApi#patch(String, org.jclouds.googlecompute.domain.Firewall)
    */
   @Named("Firewalls:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/firewalls/{firewall}")
   @OAuthScopes({COMPUTE_SCOPE})
   ListenableFuture<Operation> patch(@PathParam("firewall") String firewallName,
                                     @BinderParam(BindToJsonPayload.class) Firewall firewall);

   /**
    * @see FirewallApi#delete(String)
    */
   @Named("Firewalls:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls/{firewall}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Operation> delete(@PathParam("firewall") String firewallName);

   /**
    * @see FirewallApi#listFirstPage()
    */
   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Firewall>> listFirstPage();

   /**
    * @see FirewallApi#listAtMarker(String)
    */
   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Firewall>> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * @see FirewallApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Firewall>> listAtMarker(@QueryParam("pageToken") @Nullable String marker,
                                                     ListOptions options);

   /**
    * @see FirewallApi#list()
    */
   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.class)
   @Transform(ParseFirewalls.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Firewall>> list();


   /**
    * @see FirewallApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.class)
   @Transform(ParseFirewalls.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Firewall>> list(ListOptions options);
}
