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
package org.jclouds.openstack.nova.v2_0.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseFlavorDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseFlavors;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Flavors via their REST API.
 * <p/>
 * 
 * @see FlavorApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/List_Flavors-d1e4188.html"
 *      >docs</a>
 * @author Jeremy Daggett TODO: Need a ListFlavorOptions class minDisk=minDiskInGB&
 *         minRam=minRamInMB& marker=markerID&limit=int
 * @author Ilja Bobkevic
 */
@RequestFilters(AuthenticateRequest.class)
public interface FlavorAsyncApi {

   /**
    * @see FlavorApi#list()
    */
   @Named("flavor:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavors.class)
   @Transform(ParseFlavors.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Resource>> list();

   /** @see FlavorApi#list(PaginationOptions) */
   @Named("flavor:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavors.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Resource>> list(PaginationOptions options);

   /**
    * @see FlavorApi#listInDetail()
    */
   @Named("flavor:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavorDetails.class)
   @Transform(ParseFlavorDetails.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Flavor>> listInDetail();

   /** @see FlavorApi#listInDetail(PaginationOptions) */
   @Named("flavor:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavorDetails.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Flavor>> listInDetail(PaginationOptions options);

   /**
    * @see FlavorApi#get
    */
   @Named("flavor:get")
   @GET
   @SelectJson("flavor")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Flavor> get(@PathParam("id") String id);

   
   /**
    * @see FlavorApi#create
    */
   @Named("flavor:create")
   @POST
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/flavors")
   ListenableFuture<? extends Flavor> create(@WrapWith("flavor") Flavor flavor);

   /**
    * @see FlavorApi#delete
    */
   @Named("flavor:delete")
   @DELETE
   @Consumes
   @Path("/flavors/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("id") String id);
}
