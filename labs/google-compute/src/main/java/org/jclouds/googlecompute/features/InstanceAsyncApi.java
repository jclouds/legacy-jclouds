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
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.InstanceTemplate;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.functions.internal.ParseInstances;
import org.jclouds.googlecompute.handlers.InstanceBinder;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
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
 * Provides asynchronous access to Instances via their REST API.
 *
 * @author David Alves
 * @see InstanceApi
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface InstanceAsyncApi {

   /**
    * @see InstanceApi#get(String)
    */
   @Named("Instances:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Instance> get(@PathParam("instance") String instanceName);

   /**
    * @see InstanceApi#createInZone(String, org.jclouds.googlecompute.domain.InstanceTemplate, String)
    */
   @Named("Instances:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(InstanceBinder.class)
   ListenableFuture<Operation> createInZone(@PayloadParam("name") String instanceName,
                                            @PayloadParam("template") InstanceTemplate template,
                                            @PayloadParam("zone") String zone);

   /**
    * @see InstanceApi#delete(String)
    */
   @Named("Instances:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Operation> delete(@PathParam("instance") String instanceName);

   /**
    * @see InstanceApi#listFirstPage()
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Instance>> listFirstPage();

   /**
    * @see InstanceApi#listAtMarker(String)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Instance>> listAtMarker(@Nullable String marker);

   /**
    * @see InstanceApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Instance>> listAtMarker(@Nullable String marker, ListOptions options);

   /**
    * @see InstanceApi#list()
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Instance>> list();

   /**
    * @see InstanceApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Instances:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseInstances.class)
   @Transform(ParseInstances.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Instance>> list(ListOptions options);

   /**
    * @see InstanceApi#addAccessConfigToNic(String, Instance.NetworkInterface.AccessConfig, String)
    */
   @Named("Instances:addAccessConfig")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}/addAccessConfig")
   @OAuthScopes({COMPUTE_SCOPE})
   ListenableFuture<Operation> addAccessConfigToNic(@PathParam("instance") String instanceName,
                                                    @BinderParam(BindToJsonPayload.class)
                                                    Instance.NetworkInterface.AccessConfig accessConfig,
                                                    @QueryParam("network_interface") String networkInterfaceName);

   /**
    * @see InstanceApi#deleteAccessConfigFromNic(String, String, String)
    */
   @Named("Instances:deleteAccessConfig")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}/deleteAccessConfig")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Operation> deleteAccessConfigFromNic(@PathParam("instance") String instanceName,
                                                         @QueryParam("access_config") String accessConfigName,
                                                         @QueryParam("network_interface") String networkInterfaceName);

   /**
    * @see InstanceApi#getSerialPortOutput(String)
    */
   @Named("Instances:serialPort")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/instances/{instance}/serialPort")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Instance.SerialPortOutput> getSerialPortOutput(@PathParam("instance") String instanceName);
}
