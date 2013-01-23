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
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.Project;
import org.jclouds.googlecompute.handlers.MetadataBinder;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecompute.GoogleComputeConstants.COMPUTE_SCOPE;

/**
 * Provides asynchronous access to Projects via their REST API.
 *
 * @author David Alves
 * @see ProjectApi
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
public interface ProjectAsyncApi {


   /**
    * @see ProjectApi#get(String)
    */
   @Named("Projects:get")
   @GET
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/projects/{project}")
   ListenableFuture<Project> get(@PathParam("project") String projectName);

   /**
    * @see ProjectApi#setCommonInstanceMetadata(String, java.util.Map)
    */
   @Named("Projects:setCommonInstanceMetadata")
   @POST
   @Path("/projects/{project}/setCommonInstanceMetadata")
   @OAuthScopes(COMPUTE_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   ListenableFuture<Operation> setCommonInstanceMetadata(@PathParam("project") String projectName,
                                                         @BinderParam(MetadataBinder.class)
                                                         Map<String, String> commonInstanceMetadata);
}
