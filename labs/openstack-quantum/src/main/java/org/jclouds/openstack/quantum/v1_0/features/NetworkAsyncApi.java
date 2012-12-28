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
package org.jclouds.openstack.quantum.v1_0.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Network operations on the openstack quantum API.
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.quantum.v1_0.features.NetworkApi
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Networks.html">api doc</a>
 */
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/networks")
public interface NetworkAsyncApi {

   /**
    * @see NetworkApi#listReferences
    */
   @GET
   @SelectJson("networks")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends Reference>> listReferences();

   /**
    * @see NetworkApi#list
    */
   @GET
   @SelectJson("networks")
   @Path("/detail")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends Network>> list();

   /**
    * @see NetworkApi#get
    */
   @GET
   @SelectJson("network")
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Network> get(@PathParam("id") String id);

   /**
    * @see NetworkApi#getDetails
    */
   @GET
   @SelectJson("network")
   @Path("/{id}/detail")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends NetworkDetails> getDetails(@PathParam("id") String id);

   /**
    * @see NetworkApi#create
    */
   @POST
   @SelectJson("network")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("network")
   ListenableFuture<? extends Reference> create(@PayloadParam("name") String name);

   /**
    * @see NetworkApi#rename
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @WrapWith("network")
   ListenableFuture<Boolean> rename(@PathParam("id") String id, @PayloadParam("name") String name);

   /**
    * @see NetworkApi#delete
    */
   @DELETE
   @Path("/{id}")
   ListenableFuture<Boolean> delete(@PathParam("id") String id);

}
