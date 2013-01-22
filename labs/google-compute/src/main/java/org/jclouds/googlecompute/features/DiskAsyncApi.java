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
import org.jclouds.googlecompute.domain.Disk;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.functions.internal.ParseDisks;
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
import java.net.URI;

import static org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import static org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;

/**
 * Provides asynchronous access to Disks via their REST API.
 *
 * @author David Alves
 * @see DiskApi
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface DiskAsyncApi {

   /**
    * @see DiskApi#get(String)
    */
   @Named("Disks:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks/{disk}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Disk> get(@PathParam("disk") String diskName);

   /**
    * @see DiskApi#createInZone(String, int, java.net.URI)
    */
   @Named("Disks:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/disks")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Operation> createInZone(@PayloadParam("name") String diskName,
                                            @PayloadParam("sizeGb") int sizeGb,
                                            @PayloadParam("zone") URI zone);

   /**
    * @see DiskApi#delete(String)
    */
   @Named("Disks:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks/{disk}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Operation> delete(@PathParam("disk") String diskName);

   /**
    * @see org.jclouds.googlecompute.features.DiskApi#listFirstPage()
    */
   @Named("Disks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Disk>> listFirstPage();

   /**
    * @see DiskApi#listAtMarker(String)
    */
   @Named("Disks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Disk>> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * @see DiskApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Disks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListenableFuture<ListPage<Disk>> listAtMarker(@QueryParam("pageToken") @Nullable String marker, ListOptions options);

   /**
    * @see DiskApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Disks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Transform(ParseDisks.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Disk>> list();

   /**
    * @see DiskApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   @Named("Disks:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/disks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseDisks.class)
   @Transform(ParseDisks.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<Disk>> list(ListOptions options);
}
