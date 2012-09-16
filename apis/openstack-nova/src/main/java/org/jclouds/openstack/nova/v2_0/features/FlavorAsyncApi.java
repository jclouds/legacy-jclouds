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
package org.jclouds.openstack.nova.v2_0.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.functions.ReturnEmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseFlavorDetails;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseFlavors;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.functions.ReturnEmptyPagedIterableOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

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
 */
@SkipEncoding({ '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface FlavorAsyncApi {

   /**
    * @see FlavorApi#list()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavors.class)
   @Transform(ParseFlavors.ToPagedIterable.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Resource>> list();

   /** @see FlavorApi#list(PaginationOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavors.class)
   @ExceptionParser(ReturnEmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Resource>> list(PaginationOptions options);

   /**
    * @see FlavorApi#listInDetail()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavorDetails.class)
   @Transform(ParseFlavorDetails.ToPagedIterable.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends Flavor>> listInDetail();

   /** @see FlavorApi#listInDetail(PaginationOptions) */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/detail")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseFlavorDetails.class)
   @ExceptionParser(ReturnEmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends Flavor>> listInDetail(PaginationOptions options);

   /**
    * @see FlavorApi#get
    */
   @GET
   @SelectJson("flavor")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends Flavor> get(@PathParam("id") String id);

}
