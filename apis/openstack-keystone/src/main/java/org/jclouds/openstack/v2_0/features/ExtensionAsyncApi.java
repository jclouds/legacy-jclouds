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
package org.jclouds.openstack.v2_0.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Extensions via their REST API.
 * <p/>
 * 
 * @see ExtensionApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(AuthenticateRequest.class)
public interface ExtensionAsyncApi {

   /**
    * @see ExtensionApi#list
    */
   @Named("extension:list")
   @GET
   @SelectJson("extensions")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/extensions")
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Extension>> list();

   /**
    * @see ExtensionApi#get
    */
   @Named("extension:get")
   @GET
   @SelectJson("extension")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/extensions/{alias}")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends Extension> get(@PathParam("alias") String id);

}
