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
package org.jclouds.openstack.keystone.v2_0.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.KeystoneFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers.ToPagedIterable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.openstack.v2_0.services.Identity;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to User via their REST API.
 * <p/>
 * 
 * @see UserApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/User_Operations.html"
 *      />
 * @author Adam Lowe
 */
@org.jclouds.rest.annotations.Endpoint(Identity.class)
public interface UserAsyncApi {

   /**
    * @see UserApi#list()
    */
   @Named("user:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseUsers.class)
   @Transform(ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   ListenableFuture<? extends PagedIterable<? extends User>> list();

   /** @see UserApi#list(PaginationOptions) */
   @Named("user:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @ResponseParser(ParseUsers.class)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   ListenableFuture<? extends PaginatedCollection<? extends User>> list(PaginationOptions options);

   /** @see UserApi#get(String) */
   @Named("user:get")
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users/{userId}")
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends User> get(@PathParam("userId") String userId);

   /** @see UserApi#getByName(String) */
   @Named("user:get")
   @GET
   @SelectJson("user")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users")
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<? extends User> getByName(@QueryParam("name") String userName);

   /** @see UserApi#listRolesOfUser(String) */
   @Named("user:listroles")
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/users/{userId}/roles")
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Role>> listRolesOfUser(@PathParam("userId") String userId);

   /** @see UserApi#listRolesOfUserOnTenant(String, String) */
   @Named("user:listroles")
   @GET
   @SelectJson("roles")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/tenants/{tenantId}/users/{userId}/roles")
   @RequestFilters(AuthenticateRequest.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<? extends Role>> listRolesOfUserOnTenant(@PathParam("userId") String userId,
            @PathParam("tenantId") String tenantId);
}
