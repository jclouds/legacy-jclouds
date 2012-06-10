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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Flavors via their REST API.
 * <p/>
 * 
 * @see FlavorClient
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Flavors-d1e4180.html"
 *      />
 * @author Jeremy Daggett TODO: Need a ListFlavorOptions class
 *         minDisk=minDiskInGB& minRam=minRamInMB& marker=markerID&limit=int
 */
@SkipEncoding({ '/', '=' })
@RequestFilters(AuthenticateRequest.class)
public interface FlavorAsyncClient {

   /**
    * @see FlavorClient#listFlavors
    */
   @GET
   @SelectJson("flavors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Resource>> listFlavors();

   /**
    * @see FlavorClient#listFlavorsInDetail
    */
   @GET
   @SelectJson("flavors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/detail")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Flavor>> listFlavorsInDetail();

   /**
    * @see FlavorClient#getFlavor
    */
   @GET
   @SelectJson("flavor")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/flavors/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Flavor> getFlavor(@PathParam("id") String id);

}
