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
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.functions.ParsePagedLists;
import org.jclouds.googlecompute.functions.ToPagedIterable;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptyPagedIterableOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    * @see NetworkApi#get(String, String)
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/projects/{project}/networks/{network}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   public ListenableFuture<Network> get(@PathParam("project") String projectName,
                                     @PathParam("network") String networkName);

   /**
    * @see NetworkApi#insert(String, org.jclouds.googlecompute.domain.Network)
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/projects/{project}/networks")
   @OAuthScopes({COMPUTE_SCOPE})
   public ListenableFuture<Operation> insert(@PathParam("project") String projectName,
                                             @BinderParam(BindToJsonPayload.class) Network network);

   /**
    * @see NetworkApi#delete(String, String)
    */
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/projects/{project}/networks/{network}")
   @OAuthScopes(COMPUTE_SCOPE)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   public ListenableFuture<Operation> delete(@PathParam("project") String projectName,
                                             @PathParam("network") String networkName);

   /**
    * @see NetworkApi#list(String, org.jclouds.googlecompute.options.ListOptions)
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/projects/{project}/networks")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Transform(ToPagedIterable.class)
   @ResponseParser(ParsePagedLists.ParseNetworks.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   public ListenableFuture<? extends PagedIterable<Network>> list(@PathParam("project") String projectName,
                                                               ListOptions options);
}
