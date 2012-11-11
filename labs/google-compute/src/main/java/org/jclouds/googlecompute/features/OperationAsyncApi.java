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
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.functions.ParsePagedLists;
import org.jclouds.googlecompute.functions.ToPagedIterable;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.functions.ReturnEmptyPagedIterableOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;

/**
 * Provides asynchronous access to Operations via their REST API.
 *
 * @author David Alves
 * @see OperationApi
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface OperationAsyncApi {

   /**
    * @see OperationApi#get(String, String)
    */
   @GET
   @Path("/projects/{project}/operations/{operation}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   public ListenableFuture<Operation> get(@PathParam("project") String projectName,
                                          @PathParam("operation") String operationName);

   /**
    * @see OperationApi#delete(String, String)
    */
   @DELETE
   @Path("/projects/{project}/operations/{operation}")
   @OAuthScopes(COMPUTE_SCOPE)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   public ListenableFuture<Void> delete(@PathParam("project") String projectName,
                                        @PathParam("operation") String operationName);

   /**
    * @see OperationApi#list(String, org.jclouds.googlecompute.options.ListOptions)
    */
   @GET
   @Path("/projects/{project}/operations")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @Transform(ToPagedIterable.class)
   @ResponseParser(ParsePagedLists.ParseOperations.class)
   @ExceptionParser(ReturnEmptyPagedIterableOnNotFoundOr404.class)
   public ListenableFuture<? extends PagedIterable<Operation>> list(@PathParam("project") String project,
                                                                    ListOptions listOptions);

}