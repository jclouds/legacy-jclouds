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

import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Network operations on the openstack quantum API.
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.quantum.v1_0.features.NetworkClient
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content/Networks.html">api doc</a>
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/networks")
public interface NetworkAsyncClient {

   /**
    * @see NetworkClient#listReferences
    */
   @GET
   @SelectJson("networks")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Reference>> listReferences();

   /**
    * @see NetworkClient#list
    */
   @GET
   @SelectJson("networks")
   @Path("/detail")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Network>> list();

   /**
    * @see NetworkClient#get
    */
   @GET
   @SelectJson("network")
   @Path("/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Network> get(@PathParam("id") String id);

   /**
    * @see NetworkClient#getDetails
    */
   @GET
   @SelectJson("network")
   @Path("/{id}/detail")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<NetworkDetails> getDetails(@PathParam("id") String id);

   /**
    * @see NetworkClient#create
    */
   @POST
   @SelectJson("network")
   @Produces(MediaType.APPLICATION_JSON)
   @WrapWith("network")
   ListenableFuture<Reference> create(@PayloadParam("name") String name);

   /**
    * @see NetworkClient#rename
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{id}")
   @WrapWith("network")
   ListenableFuture<Boolean> rename(@PathParam("id") String id, @PayloadParam("name") String name);

   /**
    * @see NetworkClient#delete
    */
   @DELETE
   @Path("/{id}")
   ListenableFuture<Boolean> delete(@PathParam("id") String id);

}
